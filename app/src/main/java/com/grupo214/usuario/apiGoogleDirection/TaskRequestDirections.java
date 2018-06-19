package com.grupo214.usuario.apiGoogleDirection;

import android.os.AsyncTask;

import com.grupo214.usuario.objetos.Linea;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class TaskRequestDirections extends AsyncTask<String, Void, String> {

    private Linea linea;

    public void setLinea(Linea linea) {
        this.linea = linea;
    }

    @Override
    protected String doInBackground(String... strings) {
        String responseString = "";
        try {
            responseString = requestDirection(strings[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseString;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        //Parse json
        TaskParser taskParser = new TaskParser();
        taskParser.setLinea(linea);
        taskParser.execute(s);
    }


    private String requestDirection(String reqUrl) throws IOException {
        String responseString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(reqUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            //Get the response result
            inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }

            responseString = stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            httpURLConnection.disconnect();
        }
        return responseString;
    }
}