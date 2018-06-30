package com.grupo214.usuario.apiServidor;

import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.grupo214.usuario.objetos.Linea;

public class DibujarDemo {

    private long refreshTime = 1000;
    private final Marker mk;
    private Linea linea;
    private GoogleMap googleMap;

    public DibujarDemo(GoogleMap googleMap, Linea linea) {
        this.googleMap = googleMap;
        this.linea = linea;
        this.mk = googleMap.addMarker(new MarkerOptions()
                .position(linea.getNextPointDemo())
                .title("Servicio " + linea.getLinea())
                .snippet(linea.getRamal()));
    }



    public void hilo() {
        while (System.currentTimeMillis()%refreshTime!=0);
    }

    public void ejecutar() {
        Time time = new Time();
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
                        / refreshTime);
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








/*
package com.grupo214.usuario.apiServidor;


import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.grupo214.usuario.objetos.Linea;

public class DibujarDemo {

    private static long refreshTime = 1000;
    private final Marker mk;
    private Linea linea;
    private GoogleMap googleMap;

    public DibujarDemo(GoogleMap googleMap, Linea linea, int cantidad) {
        this.googleMap = googleMap;
        this.linea = linea;
        this.refreshTime /= cantidad;
        this.mk = googleMap.addMarker(new MarkerOptions()
                .position(linea.getNextPointDemo())
                .title("Servicio " + linea.getLinea())
                .snippet(linea.getRamal())
        );
    }

    public void dibujar() {
        Time time = new Time();
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
                        / refreshTime );
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
            while (System.currentTimeMillis()%refreshTime==0);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            dibujar();
            animateMarker(mk, linea.getNextPoint(), false, googleMap);
        }
    }


}
*/