package com.grupo214.usuario.alarma;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
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
import com.grupo214.usuario.activities.NotificacionManagerActivity;
import com.grupo214.usuario.objects.Alarm;
import com.grupo214.usuario.objects.ParadaAlarma;
import com.grupo214.usuario.objects.Ramal;
import com.grupo214.usuario.objects.ServicioAlarma;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.grupo214.usuario.activities.NotificacionManagerActivity.EXTRA_ID_ALARMA;

public class CheckPostsReceiver extends BroadcastReceiver {

    private static final String TAG = "CheckPostReceiver";
    private static final HashMap<String, ServicioAlarma> serviciosActivos = new HashMap<>();
    private static final List<String> serviciosPospuestos = new ArrayList<>();
    private static Timer timer;
    final long TIME_OBTENER_UBICACIONES_NOTIFICACIONES = 60000; // un minuto <-
    private Context context;

    /**
     * @param context
     * @param idAlarma
     * @return
     */
    static boolean checkNotificacion(Context context, int idAlarma) {
        List<ServicioAlarma> serviciosOrdenados = new ArrayList<>(serviciosActivos.values());
        Collections.sort(serviciosOrdenados, ServicioAlarma.COMPARATOR);
        for (ServicioAlarma s : serviciosOrdenados) {
            if (s.isActivo()) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                String tipo = preferences.getString("list_preference_parameter", "time");
                if (tipo.equals("distance")) {
                    int distacia = Integer.parseInt(preferences.getString("list_preference_distance", "300"));
                    if (s.isNearByDistance(distacia)) {// 300 metros.
                        crearNotificacion(context, s, idAlarma);
                        return true;
                    }
                } else {
                    int tiempo = Integer.parseInt(preferences.getString("list_preference_time", "5"));
                    if (s.isNearByTime(tiempo)) {  // 15 minutos levantar configuracion usuario -1
                        crearNotificacion(context, s, idAlarma);
                        return true;
                    }
                }
            } else
                serviciosActivos.remove(s.getIdServicio());
        }
        return false;
    }

    private static void crearNotificacion(final Context context, ServicioAlarma s, final int idAlarma) {
        serviciosPospuestos.add(s.getIdServicio());
        serviciosActivos.remove(s.getIdServicio());
        Intent intent = new Intent(context, NotificationBus.class);
        intent.putExtra("linea", s.getLinea());
        intent.putExtra("ramal", s.getRamal());
        intent.putExtra("tiempo", s.getTiempoEstimado());
        intent.putExtra("color", s.getIco());
        intent.putExtra(NotificacionManagerActivity.EXTRA_ID_ALARMA, idAlarma);
        context.startService(intent);
        timer.cancel();
        timer.purge();
        timer = null;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                clearNotification(context);
                posponer(context, idAlarma);
            }
        }, 1000 * 60);
    }

    public static void clearNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NotificationBus.NOTIFICATION_ID);
    }

    public static void posponer(Context context, int idAlarma) {
        if (!CheckPostsReceiver.checkNotificacion(context, idAlarma)) {
            Intent intent = new Intent(context, CheckPostsReceiver.class);
            intent.putExtra(EXTRA_ID_ALARMA, idAlarma);
            context.startService(intent);
        }
    }


    @Override
    public void onReceive(final Context context, Intent intent) {
        TimerTask obtenerUbicacionTask;
        this.context = context;
        timer = new Timer();

        if (!intent.hasExtra(NotificacionManagerActivity.EXTRA_ID_ALARMA)) {
            Toast.makeText(context, "¿Donde esta mi bondi? Nunca deberia aparecer esto.", Toast.LENGTH_LONG).show();
            return;
        }
        int idAlarma = intent.getIntExtra(NotificacionManagerActivity.EXTRA_ID_ALARMA, 0);
        final Alarm alarm = DatabaseAlarms.getInstance(context).getAlarm(idAlarma);
        final HashMap<String, Ramal> ramales = new HashMap<>();

        for (ParadaAlarma paradaAlarma : alarm.getParadaAlarmas().values()) {
            ramales.put(paradaAlarma.getIdRamal(),
                    DatabaseAlarms.getInstance(context).getRamal(paradaAlarma.getIdRamal())
            );
        }

        obtenerUbicacionTask = new TimerTask() {
            @Override
            public void run() {
                getUbicacion(ramales, alarm, serviciosActivos);
            }
        };
        if (alarm.getParadaAlarmas().size() > 0) {
            timer.schedule(obtenerUbicacionTask, 0, TIME_OBTENER_UBICACIONES_NOTIFICACIONES);
        } else {
            Toast.makeText(context, "No hay paradas para crear las notificaciones.", Toast.LENGTH_LONG).show();
        }
    }

    private void getUbicacion(final HashMap<String, Ramal> ramales, final Alarm alarm,
                              final HashMap<String, ServicioAlarma> serviciosActivos) {
        RequestQueue requestQueue_getUbicacion;
        String parameters = "";
        final HashMap<String, ParadaAlarma> paradaAlarmas = alarm.getParadaAlarmas();
        for (ParadaAlarma paradaAlarma : paradaAlarmas.values()) {
            parameters += "&puntos%5B%5D=" + paradaAlarma.getId_parada();
        }
        String url = "http://dondeestamibondi.online/appPasajero/getUbicacionServicios.php?" + parameters + "&top=5";
        url = url.replace("?&", "?");
        Log.d("Check", url);
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        limpiar();

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

                                LatLng ubicacion = new LatLng(lat, log);
                                ServicioAlarma servicio = serviciosActivos.get(idServicio);
                                int resource;
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");

                                if (color.equals("V")) {
                                    resource = R.mipmap.ic_bus_verde;
                                } else if (color.equals("A")) {
                                    resource = R.mipmap.ic_bus_amarillo;
                                } else if (color.equals("R")) {
                                    resource = R.mipmap.ic_bus_rojo;
                                } else {
                                    resource = (R.mipmap.ic_bus_gris);
                                }

                                Ramal r = ramales.get(idRamal);

                                ArrayList<LatLng> paradasDelRamal = new ArrayList<>();
                                for (ParadaAlarma paradaAlarmas : paradaAlarmas.values())
                                    if (paradaAlarmas.getIdRamal().equals(r.getIdRamal()))
                                        paradasDelRamal.add(paradaAlarmas.getPunto());

                                if (!serviciosPospuestos.contains(idServicio)) {
                                    if (servicio == null) {
                                        servicio = new ServicioAlarma(idServicio, fecha, r.getLinea(), r.getDescripcion(), paradasDelRamal);
                                        serviciosActivos.put(idServicio, servicio);
                                    } else
                                        servicio.setActivo(true);


                                    servicio.setIco(resource);
                                    servicio.setTiempoEstimado(minutos);
                                    servicio.setUbicacion(ubicacion);
                                }

                            }
                            checkNotificacion(context, alarm.getId());

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

    private void limpiar() {
        ArrayList<ServicioAlarma> serviciosEliminar = new ArrayList<>();
        for (ServicioAlarma s : serviciosActivos.values())
            if (!s.isActivo())
                serviciosEliminar.add(s);
            else
                s.setActivo(false);
        for (ServicioAlarma s : serviciosEliminar)
            serviciosActivos.remove(s.getIdServicio());
    }
}