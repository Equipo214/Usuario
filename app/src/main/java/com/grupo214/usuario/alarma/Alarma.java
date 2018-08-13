package com.grupo214.usuario.alarma;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

public class Alarma extends AsyncTask<Void, Void, Void> {
    private String parametro;

    public Alarma(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        parametro = preferences.getString("list_preference_parameter", "nope");
        Log.d("Alarma","wholaa: " + parametro);
    }
    public String getParametro() {
        return parametro;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        return null;
    }
}
