package com.grupo214.usuario.Util;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.TimePicker;

import com.android.volley.RequestQueue;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.grupo214.usuario.objects.Alarm;
import com.grupo214.usuario.objects.ParadaAlarma;
import com.grupo214.usuario.objects.Ramal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static com.grupo214.usuario.Util.DatabaseAlarms.COL_FRI;
import static com.grupo214.usuario.Util.DatabaseAlarms.COL_ID_ALARMS;
import static com.grupo214.usuario.Util.DatabaseAlarms.COL_ID_LINEA_ALARMS;
import static com.grupo214.usuario.Util.DatabaseAlarms.COL_ID_PARADA;
import static com.grupo214.usuario.Util.DatabaseAlarms.COL_ID_RAMAL_ALARMS;
import static com.grupo214.usuario.Util.DatabaseAlarms.COL_IS_ENABLED;
import static com.grupo214.usuario.Util.DatabaseAlarms.COL_LABEL;
import static com.grupo214.usuario.Util.DatabaseAlarms.COL_LAT;
import static com.grupo214.usuario.Util.DatabaseAlarms.COL_LNG;
import static com.grupo214.usuario.Util.DatabaseAlarms.COL_MON;
import static com.grupo214.usuario.Util.DatabaseAlarms.COL_SAT;
import static com.grupo214.usuario.Util.DatabaseAlarms.COL_SUN;
import static com.grupo214.usuario.Util.DatabaseAlarms.COL_THURS;
import static com.grupo214.usuario.Util.DatabaseAlarms.COL_TIME;
import static com.grupo214.usuario.Util.DatabaseAlarms.COL_TUES;
import static com.grupo214.usuario.Util.DatabaseAlarms.COL_WED;
import static com.grupo214.usuario.Util.DatabaseAlarms._ID;

/**
 * Clase con metodos de apoyo para el map.
 *
 * @author Daniel Boullon
 */
public class Util {
    private static String TAG = "Util";
    private RequestQueue requestQueue_distanceMatrix;

    /**
     * Ccalcular distancia entre dos puntos en metros.
     *
     * @param StartP punto de inicio.
     * @param EndP   punto de fin.
     * @return
     */
    public static double calculateDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371000;// radio de la tierra en  metros.
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return Radius * c;
    }

    public static void animateMarker(final Marker mk, final LatLng toPosition,
                                     final boolean visible, GoogleMap googleMap, final long refreshTime) {

        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = googleMap.getProjection();
        Point startPoint = proj.toScreenLocation(mk.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / refreshTime);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;
                mk.setPosition(new LatLng(lat, lng));
                if (t < 1.0) {
                    handler.postDelayed(this, 16);
                } else {
                    mk.setVisible(visible);
                }
            }
        });
    }

    public static ContentValues toContentValues(Alarm alarm) {

        final ContentValues cv = new ContentValues(11);
        cv.put(_ID, alarm.getId());
        cv.put(COL_TIME, alarm.getTime());
        cv.put(COL_LABEL, alarm.getLabel());

        final SparseBooleanArray days = alarm.getAllDays();
        cv.put(COL_MON, days.get(Alarm.MON) ? 1 : 0);
        cv.put(COL_TUES, days.get(Alarm.TUES) ? 1 : 0);
        cv.put(COL_WED, days.get(Alarm.WED) ? 1 : 0);
        cv.put(COL_THURS, days.get(Alarm.THURS) ? 1 : 0);
        cv.put(COL_FRI, days.get(Alarm.FRI) ? 1 : 0);
        cv.put(COL_SAT, days.get(Alarm.SAT) ? 1 : 0);
        cv.put(COL_SUN, days.get(Alarm.SUN) ? 1 : 0);

        cv.put(COL_IS_ENABLED, alarm.isEnabled());

        return cv;
    }

    public static ContentValues toContentValues(Ramal ramal) {

        final ContentValues cv = new ContentValues(5);
        cv.put(DatabaseAlarms.COL_ID_LINEA, ramal.getIdLinea());
        cv.put(DatabaseAlarms.COL_LINEA, ramal.getLinea());
        cv.put(DatabaseAlarms.COL_ID_RAMAL, ramal.getIdRamal());
        cv.put(DatabaseAlarms.COL_DESCRIPCION, ramal.getDescripcion());
        cv.put(DatabaseAlarms.COL_CHECKED, ramal.isCheck());
        return cv;
    }

    public static ContentValues toContentValues(Boolean checkRamal) {

        final ContentValues cv = new ContentValues(1);
        cv.put(DatabaseAlarms.COL_CHECKED, checkRamal);
        return cv;
    }

    public static ContentValues toContentValues(ParadaAlarma paradaAlarma) {

        final ContentValues cv = new ContentValues(6);

        cv.put(COL_ID_ALARMS, paradaAlarma.getId_alarms());
        cv.put(COL_ID_PARADA, paradaAlarma.getId_parada());
        cv.put(COL_ID_LINEA_ALARMS, paradaAlarma.getId_linea());
        cv.put(COL_ID_RAMAL_ALARMS, paradaAlarma.getIdRamal());
        cv.put(COL_LAT, paradaAlarma.getPunto().latitude);
        cv.put(COL_LNG, paradaAlarma.getPunto().longitude);

        return cv;
    }


    public static ArrayList<Alarm> buildAlarmList(Cursor c) {

        if (c == null) return new ArrayList<>();

        final int size = c.getCount();

        final ArrayList<Alarm> alarms = new ArrayList<>(size);

        if (c.moveToFirst()) {
            do {

                final int id = c.getInt(c.getColumnIndex(_ID));
                final long time = c.getLong(c.getColumnIndex(COL_TIME));
                final String label = c.getString(c.getColumnIndex(COL_LABEL));
                final boolean mon = c.getInt(c.getColumnIndex(COL_MON)) == 1;
                final boolean tues = c.getInt(c.getColumnIndex(COL_TUES)) == 1;
                final boolean wed = c.getInt(c.getColumnIndex(COL_WED)) == 1;
                final boolean thurs = c.getInt(c.getColumnIndex(COL_THURS)) == 1;
                final boolean fri = c.getInt(c.getColumnIndex(COL_FRI)) == 1;
                final boolean sat = c.getInt(c.getColumnIndex(COL_SAT)) == 1;
                final boolean sun = c.getInt(c.getColumnIndex(COL_SUN)) == 1;
                final boolean isEnabled = c.getInt(c.getColumnIndex(COL_IS_ENABLED)) == 1;
                final SparseBooleanArray dias = new SparseBooleanArray(7);
                dias.put(Alarm.MON, mon);
                dias.put(Alarm.TUES, tues);
                dias.put(Alarm.WED, wed);
                dias.put(Alarm.THURS, thurs);
                dias.put(Alarm.FRI, fri);
                dias.put(Alarm.SAT, sat);
                dias.put(Alarm.SUN, sun);

                final Alarm alarm = new Alarm(id, time, label, dias, isEnabled);

                alarms.add(alarm);

            } while (c.moveToNext());
        }
        return alarms;
    }


    public static HashMap<String, ParadaAlarma> buildParadasList(Cursor c) {

        if (c == null) return new HashMap<>();

        final int size = c.getCount();

        final HashMap<String, ParadaAlarma> paradas = new HashMap<>(size);

        if (c.moveToFirst()) {
            do {

                final String id_alarms = c.getString(c.getColumnIndex(COL_ID_ALARMS));
                final String id_parada = c.getString(c.getColumnIndex(COL_ID_PARADA));
                final String id_linea = c.getString(c.getColumnIndex(COL_ID_LINEA_ALARMS));
                final String id_ramal = c.getString(c.getColumnIndex(COL_ID_RAMAL_ALARMS));
                final double lat = c.getDouble(c.getColumnIndex(COL_LAT));
                final double lng = c.getDouble(c.getColumnIndex(COL_LNG));

                final ParadaAlarma parada = new ParadaAlarma(id_parada, id_linea, id_ramal, new LatLng(lat, lng), id_alarms);

                paradas.put(id_parada, parada);

            } while (c.moveToNext());
        }

        return paradas;
    }


    public static Alarm buildAlarm(Cursor c) {
        if (c == null) return null;

        Alarm alarm = null;
        if (c.moveToFirst()) {

            final int id = c.getInt(c.getColumnIndex(_ID));
            final long time = c.getLong(c.getColumnIndex(COL_TIME));
            final String label = c.getString(c.getColumnIndex(COL_LABEL));
            final boolean mon = c.getInt(c.getColumnIndex(COL_MON)) == 1;
            final boolean tues = c.getInt(c.getColumnIndex(COL_TUES)) == 1;
            final boolean wed = c.getInt(c.getColumnIndex(COL_WED)) == 1;
            final boolean thurs = c.getInt(c.getColumnIndex(COL_THURS)) == 1;
            final boolean fri = c.getInt(c.getColumnIndex(COL_FRI)) == 1;
            final boolean sat = c.getInt(c.getColumnIndex(COL_SAT)) == 1;
            final boolean sun = c.getInt(c.getColumnIndex(COL_SUN)) == 1;
            final boolean isEnabled = c.getInt(c.getColumnIndex(COL_IS_ENABLED)) == 1;
            final SparseBooleanArray dias = new SparseBooleanArray(7);
            dias.put(Alarm.MON, mon);
            dias.put(Alarm.TUES, tues);
            dias.put(Alarm.WED, wed);
            dias.put(Alarm.THURS, thurs);
            dias.put(Alarm.FRI, fri);
            dias.put(Alarm.SAT, sat);
            dias.put(Alarm.SUN, sun);
            alarm = new Alarm(id, time, label, dias, isEnabled);
        }
        return alarm;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static int getTimePickerMinute(TimePicker picker) {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                ? picker.getMinute()
                : picker.getCurrentMinute();
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static int getTimePickerHour(TimePicker picker) {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                ? picker.getHour()
                : picker.getCurrentHour();
    }

    public static Ramal buildRamal(Cursor c) {
        if (c == null) return null;

        Ramal ramal = null;
        if (c.moveToFirst()) {
            final String idLinea = c.getString(c.getColumnIndex(DatabaseAlarms.COL_ID_LINEA));
            final String linea = c.getString(c.getColumnIndex(DatabaseAlarms.COL_LINEA));
            final String idRamal = c.getString(c.getColumnIndex(DatabaseAlarms.COL_ID_RAMAL));
            final String descripcion = c.getString(c.getColumnIndex(DatabaseAlarms.COL_DESCRIPCION));
            final boolean isChecked = c.getInt(c.getColumnIndex(DatabaseAlarms.COL_CHECKED)) == 1;
            ramal = new Ramal(idLinea, linea, idRamal, descripcion, isChecked);
        }
        return ramal;
    }

    public static HashMap<String, Ramal> buildRamales(Cursor c) {
        if (c == null) return null;
        final int size = c.getCount();

        final HashMap<String, Ramal> ramales = new HashMap<>(size);
        if (c.moveToFirst()) {

            do {
                final String idLinea = c.getString(c.getColumnIndex(DatabaseAlarms.COL_ID_LINEA));
                final String linea = c.getString(c.getColumnIndex(DatabaseAlarms.COL_LINEA));
                final String idRamal = c.getString(c.getColumnIndex(DatabaseAlarms.COL_ID_RAMAL));
                final String descripcion = c.getString(c.getColumnIndex(DatabaseAlarms.COL_DESCRIPCION));
                final boolean isChecked = c.getInt(c.getColumnIndex(DatabaseAlarms.COL_CHECKED)) == 1;
                ramales.put(idRamal, new Ramal(idLinea, linea, idRamal, descripcion, isChecked));
            } while (c.moveToNext());
        }
        return ramales;
    }

    public static boolean isNumeric(String cadena) {

        boolean resultado;

        try {
            Integer.parseInt(cadena);
            resultado = true;
        } catch (NumberFormatException excepcion) {
            resultado = false;
        }

        return resultado;
    }

}


