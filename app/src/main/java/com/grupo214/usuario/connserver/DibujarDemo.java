package com.grupo214.usuario.connserver;

import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.grupo214.usuario.Util.UtilMap;
import com.grupo214.usuario.objects.LineaDemo;


/**
 * Clase demo para dibujar la ubicacion del colectivo simulada.
 * @author  Daniel Boullon
 */
public class DibujarDemo extends Thread {

    private static final double DISTANCIA = 1000;
    private final Boolean sentido;
    private final Marker mk;
    private long refreshTime = 1000;
    private LineaDemo lineaDemo;
    private GoogleMap googleMap;
    private Marker paradaInicio;
    private LatLng userStart;
    private LatLng userDestiny;

    public DibujarDemo(GoogleMap googleMap, LineaDemo lineaDemo, Boolean sentido, LatLng userStart, LatLng userDestiny)  {
        this.googleMap = googleMap;
        this.lineaDemo = lineaDemo;
        this.sentido = sentido;
        this.userStart = lineaDemo.paraMasCercana(userStart);
        this.userDestiny = userDestiny;
        LatLng inicio;

        if (sentido)
            inicio = lineaDemo.getNextPointDemo();
        else
            inicio = lineaDemo.getPreviousPointDemo();

        this.mk = googleMap.addMarker(new MarkerOptions()
                .position(inicio)
                .title("Servicio " + lineaDemo.getLinea())
                .snippet(lineaDemo.getRamal())
                .visible(

                        UtilMap.calculateDistance(this.userStart,inicio) < DISTANCIA

                ));


        this.paradaInicio = googleMap.addMarker(new MarkerOptions()
                    .position(this.userStart)
                    .title("Parada mas cercana")
                    .snippet( String.format("%.2f",( UtilMap.calculateDistance(userStart,this.userStart) / 1000) ) + " Km."));

        paradaInicio.showInfoWindow();
    }



    public void esperar() {
        while (System.currentTimeMillis() % refreshTime != 0) ;
    }

    public void ejecutar() {
        Time time = new Time();
        time.execute();
    }

    public class Time extends AsyncTask<Void, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                Thread.sleep(refreshTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
                avanzarUnPunto();
                ejecutar();
        }
    }

    private void avanzarUnPunto() {
        LatLng siguiente;
        if (sentido)
            siguiente = lineaDemo.getNextPointDemo();
        else
            siguiente = lineaDemo.getPreviousPointDemo();
        double distancia = UtilMap.calculateDistance(siguiente, userStart);

        if(distancia < DISTANCIA ){
            UtilMap.animateMarker(mk, siguiente, true, googleMap, refreshTime);
        }else{
            mk.setPosition(siguiente);
            mk.setVisible(false);
        }
    }

}


