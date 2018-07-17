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
import com.grupo214.usuario.objetos.Punto;

public class DibujarDemo {

    private final Boolean sentido;
    private final Marker mk;
    private long refreshTime = 1000;
    private Linea linea;
    private GoogleMap googleMap;
    private Boolean visible;
    private LatLng paradaInicio;
    private LatLng userDestiny;

    public DibujarDemo(GoogleMap googleMap, Linea linea, Boolean sentido, LatLng userStart, LatLng userDestiny) {
        this.googleMap = googleMap;
        this.linea = linea;
        this.sentido = sentido;


        this.paradaInicio = paraMasCercana(userStart);

        this.userDestiny = userDestiny;
        LatLng inicio;

        if (sentido)
            inicio = linea.getNextPointDemo();
        else
            inicio = linea.getPreviousPointDemo();

        this.mk = googleMap.addMarker(new MarkerOptions()
                .position(inicio)
                .title("Servicio " + linea.getLinea())
                .snippet(linea.getRamal()));
    }

    private LatLng paraMasCercana(LatLng userStart) {
        LatLng parada = linea.getRecorrido().get(0).getLatLng();
        double distancia = calculateDistance(userStart,parada);

        for (Punto punto: linea.getRecorrido() ){
            if ( calculateDistance(userStart,punto.getLatLng()) < distancia){
                parada = punto.getLatLng();
            }
        }
        return  parada;
    }


    public void hilo() {
        while (System.currentTimeMillis() % refreshTime != 0) ;
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
                    marker.setVisible(hideMarker);
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
            LatLng siguiente;
            if (sentido)
                siguiente = linea.getNextPointDemo();
            else
                siguiente = linea.getPreviousPointDemo();

            visible = (calculateDistance(siguiente, paradaInicio) < 600);

            animateMarker(mk, siguiente, visible, googleMap);
        }
    }
    /**
     * Ccalcular distancia entre dos puntos.
     *
     * @param StartP punto de inicio.
     * @param EndP   punto de fin.
     * @return
     */
    private double calculateDistance(LatLng StartP, LatLng EndP) {
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