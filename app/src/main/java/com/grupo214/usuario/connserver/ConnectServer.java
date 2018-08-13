package com.grupo214.usuario.connserver;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.grupo214.usuario.R;
import com.grupo214.usuario.objects.Linea;
import com.grupo214.usuario.objects.Ramal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ConnectServer extends AsyncTask<Void, Void, Void> {

    private Context context;
    private ArrayList<Linea> mLineas;
    public ConnectServer(Context context,ArrayList<Linea> mLineas) {
        super();
        this.context = context;
        this.mLineas = mLineas;
    }

    @Override
    protected Void doInBackground(Void... vacio) {

        String url = context.getString(R.string.serverRestBase) + "/appPasajero/getRecorridos.php";
        RequestQueue rq1 = Volley.newRequestQueue(context);
        JsonObjectRequest jrq1 = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray lineasJson = response.optJSONArray("lineas");
                        Log.d("JSON:Recorrido",lineasJson.toString());
                        try {
                            Linea l;
                            for (int i = 0; i < lineasJson.length(); i++) {
                                JSONObject lineaJson = lineasJson.getJSONObject(i);
                                JSONArray ramalesJson = lineaJson.getJSONArray("ramales");

                                ArrayList<Ramal> ramales = new ArrayList<>();
                                for (int j = 0; j < ramalesJson.length(); j++) {
                                    JSONObject ramal = ramalesJson.getJSONObject(j);
                                    JSONArray paradasJson = ramal.getJSONObject("recorrido").getJSONArray("puntos");

                                    ArrayList<LatLng> paradas = new ArrayList<>();
                                    for (int k = 0; k < paradasJson.length(); k++) {
                                        JSONObject paradaJson = paradasJson.getJSONObject(j);
                                        if (paradaJson.getString("esParada").equals("1"))
                                            paradas.add(new LatLng(paradaJson.getDouble("longitude"), paradaJson.getDouble("latitude")));
                                    }
                                    ramales.add(new Ramal(ramal.getString("idRamal"),
                                            ramal.getString("descripcion"),
                                            ramal.getJSONObject("recorrido").getString("recorridoCompleto"), paradas));
                                }
                                l = new Linea(lineaJson.getString("idLinea"),
                                        lineaJson.getString("linea"),
                                        ramales);
                                mLineas.add(l);
                            }

                        } catch (JSONException e) {
                            Log.d("JSON:ERROR",e.toString());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("JSON:ERROR", error.toString());
            }
        });
        rq1.add(jrq1);


        return null;
    }

}
