package com.grupo214.usuario.objects;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.Comparator;

/**
 * Clase que representa un servicio de colectivo en tiempo real.
 *
 * @author Daniel Boullon
 */
public class Servicio {

    private int ico;
    private String idServicio;
    private String linea;
    private String ramal;
    private int tiempoEstimado;
    private Marker mk;
    private LatLng paradaCercanaAlPasajero;
    private LatLng ubicacionActual;
    public static Comparator<Servicio> COMPARATOR =  new Comparator<Servicio>() {
        @Override
        public int compare(Servicio o1, Servicio o2) {
            return o1.getTiempoEstimado() - o2.getTiempoEstimado();
        }
    };

    public Servicio(String idServicio, String linea, String ramal, Marker mk, LatLng paradaCercanaAlPasajero, int ico) {
        this.idServicio = idServicio;
        this.linea = linea;
        this.ramal = ramal;
        this.paradaCercanaAlPasajero = paradaCercanaAlPasajero;
        this.ubicacionActual = mk.getPosition();
        this.tiempoEstimado = -1;
        this.mk = mk;
        this.ico = ico;
        this.mk.setTitle("Servicio");
        this.mk.setSnippet("Linea " + linea + "\nRamal " + ramal);
    }


    public LatLng getParadaCercanaAlPasajero() {
        return paradaCercanaAlPasajero;
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

    public int getTiempoEstimado() {
        return tiempoEstimado;
    }

    public void setTiempoEstimado(int tiempoEstimado) {
        this.tiempoEstimado = tiempoEstimado;
    }

    public String getIdServicio() {
        return idServicio;
    }

    public int getIco() {
        return ico;
    }

    public void setIco(int ico) {
        this.ico = ico;

    }
}
