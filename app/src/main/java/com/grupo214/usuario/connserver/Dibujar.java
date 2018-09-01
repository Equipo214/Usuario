package com.grupo214.usuario.connserver;


import android.content.Context;
import android.graphics.Point;
import android.os.Handler;
import android.os.SystemClock;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.grupo214.usuario.R;
import com.grupo214.usuario.objects.Linea;
import com.grupo214.usuario.objects.Ramal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;


// Cuando tenga todas las lineas lo que puedo hacer , es solo animar las que estan dentro del radio
// Sino estan dentro del radio simplemente cambiarle la ubicacion. con eso optimizo bastante la app
//


/**
 * Clase que se conecta al servidor para dibujar en tiempo real la ubicacion del colectivo.
 *
 * @author Daniel Boullon
 */
public class Dibujar implements Runnable {

    private final static long REFRESH_TIME = 1000;
    private final ArrayList<Linea> mLinea;
    private HashMap<String, Marker> servicioss;
    private HashMap<String, Ramal> ramales_seleccionados;
    private Context context;
    private GoogleMap googleMap;


    public Dibujar(GoogleMap googleMap, Context context, ArrayList<Linea> mLinea, HashMap<String,Ramal> ramales_seleccionados) {
        this.googleMap = googleMap;
        this.context = context;
        this.servicioss = new HashMap<>();
        this.mLinea = mLinea;
        this.ramales_seleccionados = ramales_seleccionados;
    }


    public void consumirPosicion() {
        String parameters = "";
        if(ramales_seleccionados.size() == 0)
            return;

        //OPTIMIZAR ESTO con lineas_seleccionadas.
            for (Ramal r : ramales_seleccionados.values() ) {
                if (r.isCheck()) {
                    parameters += "&lineas%5B%5D=" + r.getIdLinea() + "&ramales%5B%5D=" + r.getIdRamal();
                }
            }


        parameters = parameters.replace("?&", "?");

        // https://virginal-way.000webhostapp.com/appPasajero/getUbicacionServicios.php?
        // lineas%5B%5D=3&ramales%5B%5D=2
        // &lineas%5B%5D=3&ramales%5B%5D=1

        String url = "https://virginal-way.000webhostapp.com/appPasajero/getUbicacionServicios.php?" + parameters;
        Log.d("URL ubicaciones: ", url);

        final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray servicios = response.optJSONArray("servicios");
                        Log.d("Dibujar",response.toString());
                        try {
                            for (int i = 0; i < servicios.length(); i++) {
                                String idLinea = servicios.getJSONObject(i).getString("idLinea");
                                String idRamal = servicios.getJSONObject(i).getString("idRamal");
                                Double lat = servicios.getJSONObject(i).getDouble("latitud");
                                Double log = servicios.getJSONObject(i).getDouble("longitud");
                                String idServicio = servicios.getJSONObject(i).getString("idServicio");
                                Marker mk = servicioss.get(idServicio);
                                if (mk == null) {
                                    // buscar la linea
                                    servicioss.put(idServicio,
                                            googleMap.addMarker(
                                                    new MarkerOptions().position(new LatLng(lat, log))
                                                            .title("Servicio de la linea " + idLinea)
                                                            .snippet("Ramal "+ idRamal)));
                                } else {
                                    animateMarker(mk, new LatLng(lat, log), false, googleMap);
                                }

                            }
                        } catch (JSONException e) {
                            Log.d("Json Error", e.toString());
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("JSON:ERROR", error.toString());
            }
        });
        Volley.newRequestQueue(context).add(jsonRequest);
    }

    public void animateMarker(final Marker marker, final LatLng toPosition,
                              final boolean hideMarker, GoogleMap googleMap) {

        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = googleMap.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);

        final Interpolator interpolator = new LinearInterpolator();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / REFRESH_TIME);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));
                if (t < 1.0) {
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
    }

    private void calcularTiempo(Marker mkInicio, final Marker mkDestino) {

        String outputFormat = "origins=" + mkInicio.getPosition().latitude + "," + mkInicio.getPosition().longitude +
                "&destinations=" + mkDestino.getPosition().latitude + "," + mkDestino.getPosition().longitude;
        String parameters = ""; // añadir -> ? <-
        String url = "https://maps.googleapis.com/maps/api/distancematrix/json?" + outputFormat + parameters + "&key=" + context.getString(R.string.google_maps_key);
        Log.d("MapFragment URL: ", url);

        final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Dibujar",response.toString() +" :><: " + response.length());
                        if (response.length() == 2) { //<- no esta funcionando bien :(
                            Log.d("Dibujar", "Sin servicios disponibles");
                        } else {
                            JSONArray rows = response.optJSONArray("rows");
                            try {
                                mkDestino.setSnippet(rows.getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("duration").getString("text"));
                            } catch (JSONException e) {
                                Log.d("Json Error", e.toString());
                            }
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("JSON:ERROR", error.toString());
            }
        });
        Volley.newRequestQueue(context).add(jsonRequest);

    }

    @Override
    public void run() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                consumirPosicion();
            }
        }, 0,2000);
    }

}