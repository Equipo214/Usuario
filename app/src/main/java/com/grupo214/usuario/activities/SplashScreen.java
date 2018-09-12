package com.grupo214.usuario.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
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
import com.grupo214.usuario.objects.Parada;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class SplashScreen extends AppCompatActivity {
    public static ArrayList<Linea> mLineas;
    private RequestQueue requestQueue_getRecorrido;

    public void setmLineas(ArrayList<Linea> mLineas) {
        SplashScreen.mLineas = mLineas;
        if (SplashScreen.mLineas != null)
            starttt();
        else
            getRecorrido();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            // Si hay conexión a Internet en este momento
            getRecorrido();
        } else {
            // No hay conexión a Internet en este momento
            mensaje("Sin conexion a Internet. (Conectar y reiniciar)");
        }


    }


    public void starttt() {
        Intent main = new Intent(SplashScreen.this, MainActivity.class);
        startActivity(main);
        requestQueue_getRecorrido = null;
        finish();
    }


    public void getRecorrido() {

        String url = getString(R.string.GET_RECORRIDOS);

        requestQueue_getRecorrido = Volley.newRequestQueue(this);
        final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray lineasJson = response.optJSONArray("lineas");
                        ArrayList<Linea> listaLinea = new ArrayList<>();

                        try {
                            for (int i = 0; i < lineasJson.length(); i++) {
                                JSONObject lineaJson = lineasJson.getJSONObject(i);
                                JSONArray ramalesJson = lineaJson.getJSONArray("ramales");
                                ArrayList<Ramal> ramales = new ArrayList<>();
                                String linea = lineaJson.getString("linea");
                                String idLinea = lineaJson.getString("idLinea");
                                for (int j = 0; j < ramalesJson.length(); j++) {
                                    JSONObject ramal = ramalesJson.getJSONObject(j);
                                    JSONArray paradasJson = ramal.getJSONObject("recorrido").getJSONArray("puntos");

                                    ArrayList<Parada> paradas = new ArrayList<>();
                                    for (int k = 0; k < paradasJson.length(); k++) {
                                        JSONObject paradaJson = paradasJson.getJSONObject(k);
                                        paradas.add(new Parada(new LatLng(paradaJson.getDouble("latitude"), paradaJson.getDouble("longitude")),
                                                paradaJson.getString("idPunto"),
                                                paradaJson.getInt("orden")));

                                    }

                                    String code = ramal.getJSONObject("recorrido").getString("recorridoCompleto");
                                    // Fix \\ -> \
                                    code = code.replace("\\\\", "\\");

                                    ramales.add(new Ramal(lineaJson.getString("idLinea"), linea, ramal.getString("idRamal"),
                                            ramal.getString("descripcion"),
                                            code, paradas));

                                }
                                listaLinea.add(new Linea(idLinea, linea, ramales));
                            }
                        } catch (JSONException e) {
                            Log.d("Json Error", e.toString());
                            getRecorrido();
                        }
                        setmLineas(listaLinea);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("JSON:ERROR", error.toString());
                getRecorrido();
            }
        });

        requestQueue_getRecorrido.add(jsonRequest);
    }

    private void mensaje(String msj) {
        Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content)
                , msj, Snackbar.LENGTH_INDEFINITE)
                .setAction("Action", null).show();
    }
}

