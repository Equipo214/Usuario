package com.grupo214.usuario.objects;

import com.google.android.gms.maps.model.LatLng;

/**
 * Clase que define un punto de una ruta de colectivo.
 * define si es parada o es un punto de la ruta.
 * @author  Daniel Boullon
 */
public class Punto {

    private Boolean parada;
    private LatLng punto;

    public Punto(Boolean parada, LatLng punto) {
        this.parada = parada;
        this.punto = punto;
    }

    public Boolean isParada() {
        return parada;
    }

    public LatLng getLatLng() {
        return punto;
    }

    @Override
    public String toString() {
        return "( "+ punto.latitude + " , " + punto.longitude + " )";
    }
}
