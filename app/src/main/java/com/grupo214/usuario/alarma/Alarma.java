package com.grupo214.usuario.alarma;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.maps.model.Marker;
import com.grupo214.usuario.objects.Servicio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;

public class Alarma implements Runnable  {
    private String parametro;
    private HashMap<String, Marker> paradasConAlarmas;
    private Context context;

    public Alarma(Context context, HashMap<String,Marker> paradasConAlarmas) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.context = context;
        parametro = preferences.getString("list_preference_parameter", "nope");
        Log.d("Alarma","wholaa: " + parametro);
        this.paradasConAlarmas  = paradasConAlarmas;
    }



    @Override
    public void run() {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(context, notification);
        r.play();

        // despues hacer distancia de ( ubicacion y paradaCercana) < distancia predefinida cada 5 segundos-.
        /*
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                // verificar alarma
                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Ringtone r = RingtoneManager.getRingtone(context, notification);
                r.play();
                Log.d("Alarma","5 segundos.");

            }
        }, 5000); */
    }
}
