package com.grupo214.usuario.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.grupo214.usuario.R;
import com.grupo214.usuario.Util.DatabaseAlarms;
import com.grupo214.usuario.objects.ColorRuta;
import com.grupo214.usuario.objects.Linea;
import com.grupo214.usuario.objects.Parada;
import com.grupo214.usuario.objects.Ramal;
import com.grupo214.usuario.objects.Recorrido;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class SplashScreen extends AppCompatActivity {

    public static ArrayList<Linea> mLineas;
    private static String TAG = "SplashScreen";
    final Context context = this;
    private RequestQueue requestQueue_getRecorrido;
    private ColorRuta colorRuta = new ColorRuta();

    public void setmLineas(ArrayList<Linea> mLineas) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mLineas.sort(Linea.COMPARATOR);
        }
        SplashScreen.mLineas = mLineas;

        if (SplashScreen.mLineas != null) {
            starttt();
        } else
            getRecorrido();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.WHITE, android.graphics.PorterDuff.Mode.MULTIPLY);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            // Si hay conexión a Internet en este momento
            getRecorrido();
        } else {
            // No hay conexión a Internet en este momento
            mensaje("Sin conexion a Internet. (Conectar y reitentar)");
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
                                    JSONArray recorridosJson = ramal.getJSONArray("recorrido");
                                    ArrayList<Recorrido> recorridosAlternos = new ArrayList<>();
                                    Recorrido recorridoPrimario = null;

                                    for (int k = 0; k < recorridosJson.length(); k++) {
                                        JSONObject recorrido = recorridosJson.getJSONObject(k);
                                        int esPrimario = recorrido.getInt("esPrimario");
                                        String idRecorrido = recorrido.getString("idRecorrido");
                                        String code = recorrido.getString("recorridoCompleto");
                                        // Fix \\ -> \
                                        code = code.replace("\\\\", "\\");
                                        JSONArray paradasJson = recorrido.getJSONArray("puntos");
                                        ArrayList<Parada> paradas = new ArrayList<>();
                                        for (int l = 0; l < paradasJson.length(); l++) {
                                            JSONObject paradaJson = paradasJson.getJSONObject(l);
                                            paradas.add(new Parada(new LatLng(paradaJson.getDouble("latitude"), paradaJson.getDouble("longitude")),
                                                    paradaJson.getString("idPunto"),
                                                    paradaJson.getInt("orden")));
                                        }
                                        if (esPrimario == 1) { // inicializar recorridoPrimario
                                            recorridoPrimario = new Recorrido(idRecorrido, code, paradas);
                                        } else { // addRecorridoAlterno
                                            recorridosAlternos.add(new Recorrido(idRecorrido, code, paradas));
                                        }
                                    }
                                    Ramal r = new Ramal(lineaJson.getString("idLinea"), linea, ramal.getString("idRamal"),
                                            ramal.getString("descripcion"),
                                            recorridoPrimario, recorridosAlternos,colorRuta.nextColor());
                                    ramales.add(r);
                                    if (DatabaseAlarms.getInstance(context).exist(r)) {
                                        Log.d(TAG, "Existe: " + r.getDescripcion() + "Check: " + r.isCheck());
                                        r.setChecked(DatabaseAlarms.getInstance(context).getRamal(r.getIdRamal()).isCheck());
                                    } else {
                                        Log.d(TAG, "No existe: " + r.getDescripcion() + "Check: " + r.isCheck());
                                        DatabaseAlarms.getInstance(context).addRamal(r);
                                    }
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
                mensaje("Error en la conexion a Internet.");
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

