package com.grupo214.usuario.objects;

import com.google.android.gms.maps.model.LatLng;

public class Parada {
    private LatLng latLng;
    private String idParda;
    private int orden;
    private long tiempoEstimado;

    public Parada(LatLng latLng, String idParda, int orden) {
        this.latLng = latLng;
        this.idParda = idParda;
        this.orden = orden;
    }


    public long getTiempoEstimado() {
        return tiempoEstimado;
    }

    public void setTiempoEstimado(long tiempoEstimado) {
        this.tiempoEstimado = tiempoEstimado;
    }

    public String getIdParda() {
        return idParda;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public int getOrden() {
        return orden;
    }

}
