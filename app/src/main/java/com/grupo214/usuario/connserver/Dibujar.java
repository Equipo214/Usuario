package com.grupo214.usuario.connserver;


import android.content.Context;
import android.graphics.Point;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.grupo214.usuario.R;
import com.grupo214.usuario.adapters.TiempoEstimadoAdapter;
import com.grupo214.usuario.objects.Linea;
import com.grupo214.usuario.objects.Ramal;
import com.grupo214.usuario.objects.Servicio;

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
    private TimerTask calcularTiempoTask;
    private TimerTask obtenerUbicacionTask;
    private HashMap<String, Servicio> serviciosActivos;
    private HashMap<String, Ramal> ramales_seleccionados;
    private HashMap<String, Marker> paradasCercanas;
    private TiempoEstimadoAdapter tiempoEstimadoAdapter;
    private Timer timer;
    private GoogleMap googleMap;
    private Context context;
    private RequestQueue requestQueue_getUbicacion;
    private RequestQueue requestQueue_distanceMatrix;

    public Dibujar(GoogleMap googleMap, Context context, ArrayList<Linea> mLinea, HashMap<String, Ramal> ramales_seleccionados,
                   HashMap<String, Marker> paradasCercanas, final TiempoEstimadoAdapter tiempoEstimadoAdapter, final HashMap<String, Servicio> serviciosActivos) {
        this.googleMap = googleMap;
        this.context = context;
        this.mLinea = mLinea;
        this.ramales_seleccionados = ramales_seleccionados;
        this.paradasCercanas = paradasCercanas;
        this.timer = new Timer();
        this.tiempoEstimadoAdapter = tiempoEstimadoAdapter;
        this.serviciosActivos = serviciosActivos;
        inicializarTasks();
    }

    private void inicializarTasks() {
        obtenerUbicacionTask = new TimerTask() {
            @Override
            public void run() {
                consumirPosicion();
            }
        };
        calcularTiempoTask = new TimerTask() {
            @Override
            public void run() {
                calcularTiempo(serviciosActivos);
            }
        };
    }


    private void consumirPosicion() {
        if(requestQueue_distanceMatrix !=null){
            requestQueue_distanceMatrix.stop();
            requestQueue_distanceMatrix = null;
        }
        String parameters = "";
        if (ramales_seleccionados.size() == 0) {
            stop();
            return;
        }

        for (Ramal r : ramales_seleccionados.values()) {
            if (r.isCheck()) {
                parameters += "&lineas%5B%5D=" + r.getIdLinea() + "&ramales%5B%5D=" + r.getIdRamal();
            }
        }


        parameters = parameters.replace("?&", "?");

        // https://virginal-way.000webhostapp.com/appPasajero/getUbicacionServicios.php?
        // lineas%5B%5D=3&ramales%5B%5D=2
        // &lineas%5B%5D=3&ramales%5B%5D=1

        String url = "https://virginal-way.000webhostapp.com/appPasajero/getUbicacionServicios.php?" + parameters;
        //  Log.d("URL DIBUJAR", url);
        final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray serviciosJson = response.optJSONArray("servicios");

                        try {
                            for (int i = 0; i < serviciosJson.length(); i++) {
                                String idLinea = serviciosJson.getJSONObject(i).getString("idLinea");
                                String idRamal = serviciosJson.getJSONObject(i).getString("idRamal");
                                Double lat = serviciosJson.getJSONObject(i).getDouble("latitud");
                                Double log = serviciosJson.getJSONObject(i).getDouble("longitud");
                                String idServicio = serviciosJson.getJSONObject(i).getString("idServicio");
                                String color = serviciosJson.getJSONObject(i).getString("color");

                                Servicio s = serviciosActivos.get(idServicio);
                                LatLng destino = new LatLng(lat, log);
                                Ramal r = ramales_seleccionados.get(idRamal);
                                if (s == null) {

                                    BitmapDescriptor ico;
                                    if (color.equals("V"))
                                        ico = BitmapDescriptorFactory.fromResource(R.mipmap.ic_bus_verde);
                                    else if (color.equals("A"))
                                        ico = BitmapDescriptorFactory.fromResource(R.mipmap.ic_bus_amarillo);
                                    else if (color.equals("R"))
                                        ico = BitmapDescriptorFactory.fromResource(R.mipmap.ic_bus_rojo);
                                    else
                                        ico = BitmapDescriptorFactory.fromResource(R.mipmap.ic_bus_gris);

                                    Marker mk = googleMap.addMarker(new MarkerOptions()
                                            .position(destino)
                                            .icon(ico));
                                    serviciosActivos.put(idServicio, new Servicio(idServicio, r.getLinea(), r.getDescripcion(), mk, paradasCercanas.get(idRamal).getPosition()));
                                    tiempoEstimadoAdapter.add(serviciosActivos.get(idServicio));
                                } else {


                                    BitmapDescriptor ico;
                                    if (color.equals("V"))
                                        ico = BitmapDescriptorFactory.fromResource(R.mipmap.ic_bus_verde);
                                    else if (color.equals("A"))
                                        ico = BitmapDescriptorFactory.fromResource(R.mipmap.ic_bus_amarillo);
                                    else if (color.equals("R"))
                                        ico = BitmapDescriptorFactory.fromResource(R.mipmap.ic_bus_rojo);
                                    else
                                        ico = BitmapDescriptorFactory.fromResource(R.mipmap.ic_bus_gris);
                                    s.getMk().setIcon(ico);
                                    // si esta en el top 3 de sercanos animar
                                    animateMarker(s.getMk(), destino, false, googleMap);
                                    s.setUbicacionActual(destino);
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
        requestQueue_distanceMatrix = Volley.newRequestQueue(context);
        requestQueue_distanceMatrix.add(jsonRequest);
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

    private void calcularTiempo(HashMap<String, Servicio> serviciosActivos) {

        if(requestQueue_getUbicacion !=null){
            requestQueue_getUbicacion.stop();
            requestQueue_getUbicacion =null;
        }

        for (final Servicio servicio : serviciosActivos.values()) {

            String outputFormat = "origins=" + servicio.getUbicacionActual().latitude + "," + servicio.getUbicacionActual().longitude +
                    "&destinations=" + servicio.getParada().latitude + "," + servicio.getParada().longitude;
            String parameters = "&language=es";
            String url = "https://maps.googleapis.com/maps/api/distancematrix/json?" + outputFormat + parameters + "&key=" + context.getString(R.string.google_maps_key);

            Log.d("URL TE", url);

            final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            JSONArray rows = response.optJSONArray("rows");
                            try {
                                servicio.setTiempoEstimado(rows.getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("duration").getString("text"));
                                tiempoEstimadoAdapter.notifyDataSetChanged();
                            } catch (JSONException e) {
                                Log.d("Json Error", e.toString());
                                tiempoEstimadoAdapter.notifyDataSetChanged();
                            }
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("JSON:ERROR", error.toString());
                }
            });
            RequestQueue v = Volley.newRequestQueue(context);
            v.add(jsonRequest);
        }

    }


    @Override
    public void run() {
        timer.schedule(obtenerUbicacionTask, 0, 5000); // 5000 = 5 segundos
  //     timer.schedule(calcularTiempoTask, 5000, 10000); // 60000 = 1 min
    }

    public void stop() {
        timer.cancel(); // ver si funca esto;
        serviciosActivos.clear();
        tiempoEstimadoAdapter.clear();
    }

    public void reiniciar() {
        stop();
        timer = new Timer();
        inicializarTasks();
        run();
    }
}