package com.grupo214.usuario.alarma;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.grupo214.usuario.Util.UtilMap;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class Alarma extends Timer {
    private String parametro;
    private HashMap<String, LatLng> paradasConAlarmas;
    private Context context;

    public Alarma(Context context, HashMap<String, LatLng> paradasConAlarmas) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.context = context;
        this.parametro = preferences.getString("list_preference_parameter", "nope");
        this.paradasConAlarmas = paradasConAlarmas;
    }

    public void run() {
        this.schedule(new TimerTask() {
            @Override
            public void run() {
                // verificar alarma
                /*Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Ringtone r = RingtoneManager.getRingtone(context, notification);
                r.play();*/
                for (LatLng parada : paradasConAlarmas.values())
                    //if (UtilMap.calculateDistance(parada,new LatLng(0.0,0.0)) < 5000)
                      Log.d("Alarma", "Distancia: " + UtilMap.calculateDistance(parada,new LatLng(0.0,0.0)));
            }
        }, 0, 1000);
    }
}
