package com.grupo214.usuario.objects;

import com.google.android.gms.maps.model.LatLng;

public class Parada {
    private LatLng latLng;
    private String idParda;
    private int orden;


    public Parada(LatLng latLng, String idParda, int orden) {
        this.latLng = latLng;
        this.idParda = idParda;
        this.orden = orden;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public String getIdParda() {
        return idParda;
    }

    public int getOrden() {
        return orden;
    }
}
