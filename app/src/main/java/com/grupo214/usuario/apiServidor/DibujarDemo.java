package com.grupo214.usuario.apiServidor;


import android.content.Context;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.grupo214.usuario.R;
import com.grupo214.usuario.objetos.Linea;

import org.json.JSONObject;

public class DibujarDemo {

    private final static long REFRESH_TIME = 1000;
    private final Marker mk;
    private Linea linea;
    private GoogleMap googleMap;

    public DibujarDemo(GoogleMap googleMap, Linea linea, Marker mk) {
        this.googleMap = googleMap;
        this.linea = linea;
        this.mk = mk;
    }


    public void execute() {
        Time time = new Time();
        time.execute();
    }

    public void hilo() {
        try {
            Thread.sleep(REFRESH_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void ejecutar() {
        Time time = new Time();
        Log.d("Time: ", "adentro de Time");
        time.execute();
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

    public class Time extends AsyncTask<Void, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            hilo();
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            ejecutar();
            animateMarker(mk, linea.getNextPointDemo(), false, googleMap);
        }
    }
}