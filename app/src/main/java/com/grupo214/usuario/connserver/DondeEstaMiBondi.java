package com.grupo214.usuario.connserver;


import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.widget.SwitchCompat;
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
import com.grupo214.usuario.activities.AMNotificacion;
import com.grupo214.usuario.adapters.TiempoEstimadoAdapter;
import com.grupo214.usuario.alarma.NotificationBus;
import com.grupo214.usuario.objects.Linea;
import com.grupo214.usuario.objects.ParadaAlarma;
import com.grupo214.usuario.objects.Ramal;
import com.grupo214.usuario.objects.Servicio;
import com.grupo214.usuario.objects.ServicioAlarma;

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
    private static final String TAG = "DondeEstaMiBondi";
    private final ArrayList<Linea> mLinea;
    //  private TimerTask calcularTiempoTask;
    private TimerTask obtenerUbicacionTask;
    private TimerTask accTask;
    private Timer timer;
    private HashMap<String, Servicio> serviciosActivos;

    private HashMap<String, Ramal> ramales_seleccionados;
    private TiempoEstimadoAdapter tiempoEstimadoAdapter;
    private GoogleMap googleMap;
    private Context context;
    private RequestQueue requestQueue_getUbicacion;
    private boolean flag = true;
    private float tilt = 45;
    private SwitchCompat switchAcc;
    private HashMap<String, ParadaAlarma> paradasCercanas;

    public DondeEstaMiBondi(GoogleMap googleMap, Context context, ArrayList<Linea> mLinea, HashMap<String, Ramal> ramales_seleccionados,
                            final TiempoEstimadoAdapter tiempoEstimadoAdapter, final HashMap<String, Servicio> serviciosActivos) {
        this.googleMap = googleMap;
        this.context = context;
        this.mLinea = mLinea;
        this.ramales_seleccionados = ramales_seleccionados;
        this.timer = new Timer();
        this.tiempoEstimadoAdapter = tiempoEstimadoAdapter;
        this.serviciosActivos = serviciosActivos;
        //    this.serviciosActivos_ID = new ArrayList<>(serviciosActivos.keySet());
        inicializarTasks();
    }

    private static void crearNotificacion(final Context context, Servicio s, int idAlarma) {
        Intent intent = new  Intent(context, NotificationBus.class);
        intent.putExtra("linea", s.getLinea());
        intent.putExtra("ramal", s.getRamal());
        intent.putExtra("tiempo", s.getTiempoEstimado());
        intent.putExtra("color", s.getIco());
        intent.putExtra(AMNotificacion.EXTRA_ID_ALARMA, idAlarma);
        context.startService(intent);
    }

    private void inicializarTasks() {
        obtenerUbicacionTask = new TimerTask() {
            @Override
            public void run() {
                consumirPosicion();
            }
        };
        accTask = new TimerTask() {
            @Override
            public void run() {
                RequestQueue requestQueueSolicitarAcc = Volley.newRequestQueue(context);

                String parametros = "";
                for (Servicio s : serviciosActivos.values())
                    parametros = "&asistencias%5B%5D=" + s.getIdServicio() + "," + s.getFecha() + "," + paradasCercanas.get(s.getIdRamal()).getId_parada();

                String url = "http://dondeestamibondi.online/appPasajero/solicitarAsistenciaEnParada.php?" + parametros;
                url.replace("?&", "&");

                Log.d("DONDEESTAMIBONDI", url);
                JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                String idAcc = response.toString();
                                Log.d(TAG, "ACC: " + idAcc);
                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("JSON:ERROR", error.toString());
                    }
                });
                requestQueueSolicitarAcc.add(jsonRequest);
            }
        };
    }

    private void consumirPosicion() {
        // limpiar memoria.
        if (requestQueue_getUbicacion != null) {
            requestQueue_getUbicacion.stop();
            requestQueue_getUbicacion = null;
        }
        String parameters = "";
        if (ramales_seleccionados.size() == 0) {
            //serviciosActivos.clear();
            limpiar();
            //stop();
            return;
        }

        for (Ramal r : ramales_seleccionados.values()) {
            if (r.isCheck()) {
                String idParada = r.getParadaCercana();
                parameters += "&puntos%5B%5D=" + idParada;
                Log.d(TAG, r.getDescripcion());
            }
        }

        String url = "http://dondeestamibondi.online/appPasajero/getUbicacionServicios.php?" + parameters + "&top=5";
        url = url.replace("?&", "?");

        Log.d("URL", url);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null,
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
                                String fecha = serviciosJson.getJSONObject(i).getString("fecha");
                                String color = serviciosJson.getJSONObject(i).getString("color");
                                int minutos = serviciosJson.getJSONObject(i).getInt("minutos");
                                idServicio += fecha;

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
                                    servicio = new Servicio(idServicio, fecha, r.getIdRamal(), r.getLinea(), r.getDescripcion(), mk, resource, minutos);
                                    serviciosActivos.put(idServicio, servicio);
                                    // Ojo de no buscar el servicio si no tengo la parada mas cercana todavia,
                                    // ver cuando llamo a esta funcoin o quizas deberia hacer que no se pase tan rapido s
                                    // tengo sueño, pero hay que programar pero dejo estas anotaciones para el dani del futuro
                                    // att: dani del pasado :)
                                    // PD: recuerda que la luz que te ilumina te hace mas fuerte attee: QUEWE
                                    tiempoEstimadoAdapter.add(serviciosActivos.get(idServicio));
                                } else{
                                    servicio.setActivo(true);
                                }


                                servicio.getMk().setIcon(BitmapDescriptorFactory.fromResource(resource));
                                servicio.setIco(resource);
                                Log.d("DondeEsta", "id " + idServicio + " tiempo: " + minutos);
                                servicio.setTiempoEstimado(minutos);
                                tiempoEstimadoAdapter.notifyDataSetChanged();

                                if (!destino.equals(servicio.getMk().getPosition()))
                                    animateMarker(servicio.getMk(), destino, googleMap);
                                // MarkerAnimation.animateMarkerToGB(servicio.getMk(),destino,LatLngInterpolator.Spherical());
                            }
                            tiempoEstimadoAdapter.sort(Servicio.COMPARATOR);
                            tiempoEstimadoAdapter.notifyDataSetChanged();
                            limpiar();
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

    public void limpiar() {
        ArrayList<Servicio> serviciosEliminar = new ArrayList<>();
        for (Servicio s : serviciosActivos.values()) {
            Log.d(TAG, s.getIdServicio());
            if (!s.isActivo()) {
                serviciosEliminar.add(s);

            } else
                s.setActivo(false);
        }
        for (Servicio s : serviciosEliminar) {
            serviciosActivos.get(s.getIdServicio()).getMk().remove();
            serviciosActivos.remove(s.getIdServicio());
            tiempoEstimadoAdapter.remove(s);
        }

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

    public void setSwitchAcc(SwitchCompat switchAcc) {
        this.switchAcc = switchAcc;
    }

    @Override
    public void run() {

        timer.schedule(obtenerUbicacionTask, 0, TIME_OBTENER_UBICACIONES);
        if (switchAcc.isChecked()) {
            //    icoMakerParadaCercana = BitmapDescriptorFactory.fromResource(R.mipmap.ic_parada_acc);
            //  armar link en 5 segundos.
            timer.schedule(accTask, 10000);
        }

    }

    public void stop() {
        timer.cancel(); // ver si funca esto;
        for (Servicio s : serviciosActivos.values())
            s.getMk().remove();

        serviciosActivos.clear();
        tiempoEstimadoAdapter.clear();
    }

    public void reiniciar() {
        stop();
        timer = new Timer();
        inicializarTasks();
        run();
    }

    public void setParadasCercanas(HashMap<String, ParadaAlarma> paradasCercanas) {
        this.paradasCercanas = paradasCercanas;
    }


}