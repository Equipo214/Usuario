package com.grupo214.usuario.objetos;

import com.google.android.gms.maps.model.LatLng;

import java.sql.Time;

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
