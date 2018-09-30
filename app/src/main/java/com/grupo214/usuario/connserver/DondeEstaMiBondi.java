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
public class DondeEstaMiBondi implements Runnable {

    private final static long REFRESH_TIME = 5000; // dibujar.
    // 1 segundo de animacion
    private static final long TIME_OBTENER_UBICACIONES = 5000;// cada 5 segundo llamara
    private final ArrayList<Linea> mLinea;
    //  private TimerTask calcularTiempoTask;
    private TimerTask obtenerUbicacionTask;
    private HashMap<String, Servicio> serviciosActivos;
    private HashMap<String, Ramal> ramales_seleccionados;
    private HashMap<String, Marker> paradasCercanas;
    private TiempoEstimadoAdapter tiempoEstimadoAdapter;
    private Timer timer;
    private GoogleMap googleMap;
    private Marker markerDemo;
    private Marker markerDemoUser;
    private Context context;
    //    private RequestQueue requestQueue_distanceMatrix;
    private RequestQueue requestQueue_getUbicacion;
    private boolean flag = true;
    private float tilt = 45;

    public DondeEstaMiBondi(GoogleMap googleMap, Context context, ArrayList<Linea> mLinea, HashMap<String, Ramal> ramales_seleccionados,
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
        /*
        calcularTiempoTask = new TimerTask() {
            @Override
            public void run() {
                calcularTiempo(serviciosActivos);
            }
        };*/
    }

    private void consumirPosicion() {
        // limpiar memoria.
        if (requestQueue_getUbicacion != null) {
            requestQueue_getUbicacion.stop();
            requestQueue_getUbicacion = null;
        }
        String parameters = "";
        if (ramales_seleccionados.size() == 0) {
            //stop();
            return;
        }

        for (Ramal r : ramales_seleccionados.values()) {
            if (r.isCheck()) {
                String idParada = r.getParadaCercana();
                parameters += "&puntos%5B%5D=" + idParada;
            }
        }


        // https://virginal-way.000webhostapp.com/appPasajero/getUbicacionServicios.php?
        // lineas%5B%5D=3&ramales%5B%5D=2
        // &lineas%5B%5D=3&ramales%5B%5D=1

        String url = "https://virginal-way.000webhostapp.com/appPasajero/getUbicacionServicios.php?" + parameters;
        url = url.replace("?&", "?");
        Log.d("URL DIBUJAR", url);
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        for (Servicio s : serviciosActivos.values()) {
                            s.setActivo(false);
                        }
                        JSONArray serviciosJson = response.optJSONArray("servicios");
                        try {
                            for (int i = 0; i < serviciosJson.length(); i++) {
                                String idLinea = serviciosJson.getJSONObject(i).getString("idLinea");
                                String idRamal = serviciosJson.getJSONObject(i).getString("idRamal");
                                Double lat = serviciosJson.getJSONObject(i).getDouble("latitud");
                                Double log = serviciosJson.getJSONObject(i).getDouble("longitud");
                                String idServicio = serviciosJson.getJSONObject(i).getString("idServicio");
                                String color = serviciosJson.getJSONObject(i).getString("color");
                                int minutos = serviciosJson.getJSONObject(i).getInt("minutos");

                                LatLng destino = new LatLng(lat, log);
                                Ramal r = ramales_seleccionados.get(idRamal);
                                Servicio servicio = serviciosActivos.get(idServicio);

                                int resource;
                                if (color.equals("V")) {
                                    resource = R.mipmap.ic_bus_verde;
                                } else if (color.equals("A")) {
                                    resource = R.mipmap.ic_bus_amarillo;
                                } else if (color.equals("R")) {
                                    resource = R.mipmap.ic_bus_rojo;
                                } else {
                                    resource = (R.mipmap.ic_bus_gris);
                                }

                                if (servicio == null) {
                                    Marker mk = googleMap.addMarker(new MarkerOptions()
                                            .position(destino)
                                            .visible(true)
                                            .icon(BitmapDescriptorFactory.fromResource(resource)));
                                    servicio = new Servicio(idServicio, r.getLinea(), r.getDescripcion(), mk, paradasCercanas.get(idRamal).getPosition(), resource, minutos);
                                    serviciosActivos.put(idServicio, servicio);
                                    // Ojo de no buscar el servicio si no tengo la parada mas cercana todavia,
                                    // ver cuando llamo a esta funcoin o quizas deberia hacer que no se pase tan rapido s
                                    // tengo sueño, pero hay que programar pero dejo estas anotaciones para el dani del futuro
                                    // att: dani del pasado :)
                                    // PD: recuerda que la luz que te ilumina te hace mas fuerte QUEWE
                                    tiempoEstimadoAdapter.add(serviciosActivos.get(idServicio));
                                } else {
                                    servicio.setActivo(true);
                                }

                                servicio.getMk().setIcon(BitmapDescriptorFactory.fromResource(resource));
                                servicio.setIco(resource);
                                servicio.setTiempoEstimado(minutos);
                                tiempoEstimadoAdapter.notifyDataSetChanged();

                                // si esta en el top 3 de cercanos animar ESTO CAMBIAR.
                                if (servicio.getMk().isVisible())
                                    animateMarker(servicio.getMk(), destino, googleMap);
                                else
                                    servicio.getMk().setPosition(destino);
                                servicio.setUbicacionActual(destino);
                            }
                            for (Servicio s : serviciosActivos.values()) {
                                if (!s.isActivo()){
                                    serviciosActivos.get(s.getIdServicio()).getMk().remove();
                                    serviciosActivos.remove(s.getIdServicio());
                                }
                            }
                            tiempoEstimadoAdapter.sort(Servicio.COMPARATOR);
                            tiempoEstimadoAdapter.notifyDataSetChanged();
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
        requestQueue_getUbicacion = Volley.newRequestQueue(context);
        requestQueue_getUbicacion.add(jsonRequest);

    }

    private void animateMarker(final Marker marker, final LatLng toPosition, GoogleMap googleMap) {

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
                }

            }
        });
    }

    @Override
    public void run() {
        timer.schedule(obtenerUbicacionTask, 0, TIME_OBTENER_UBICACIONES);
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