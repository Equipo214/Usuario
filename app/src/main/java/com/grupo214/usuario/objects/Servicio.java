package com.grupo214.usuario.objects;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

/**
 * Clase que representa un servicio de colectivo en tiempo real.
 * @author Daniel Boullon
 */
public class Servicio {

    private String idServicio;
    private String linea;
    private String ramal;
    private String tiempoEstimado;
    private Marker mk;
    private LatLng parada;
    private LatLng ubicacionActual;

    public Servicio(String idServicio, String linea, String ramal, Marker mk, LatLng parada) {
        this.idServicio = idServicio;
        this.linea = linea;
        this.ramal = ramal;
        this.parada = parada;
        this.ubicacionActual = mk.getPosition();
        this.tiempoEstimado = "-";
        this.mk = mk;

    }

    public LatLng getParada() {
        return parada;
    }

    public void setParada(LatLng parada) {
        this.parada = parada;
    }

    public Marker getMk() {
        return mk;
    }

    public LatLng getUbicacionActual() {
        return ubicacionActual;
    }

    public void setUbicacionActual(LatLng ubicacionActual) {
        this.ubicacionActual = ubicacionActual;
    }

    public String getLinea() {
        return linea;
    }

    public String getRamal() {
        return ramal;
    }


    public String getTiempoEstimado() {
        return tiempoEstimado;
    }

    public void setTiempoEstimado(String tiempoEstimado) {
        Log.d("Servicio", "id " + idServicio + " tiempo " + tiempoEstimado);
        if (tiempoEstimado != null)
            this.tiempoEstimado = tiempoEstimado;
    }


    public String getIdServicio() {
        return idServicio;
    }
}
