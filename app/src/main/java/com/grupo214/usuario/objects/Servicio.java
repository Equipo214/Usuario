package com.grupo214.usuario.objects;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.grupo214.usuario.Util.UtilMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

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
    private String tiempoEstimado;
    private Marker mk;
    private LatLng paradaCercanaAlPasajero;
    private LatLng ubicacionActual;

    public Servicio(String idServicio, String linea, String ramal, Marker mk, LatLng paradaCercanaAlPasajero, int ico) {
        this.idServicio = idServicio;
        this.linea = linea;
        this.ramal = ramal;
        this.paradaCercanaAlPasajero = paradaCercanaAlPasajero;
        this.ubicacionActual = mk.getPosition();
        this.tiempoEstimado = "-";
        this.mk = mk;
        this.ico = ico;
    }

    public static void ordenar(HashMap<String, Servicio> serviciosActivos) {
        List<Servicio> serviciosList = new ArrayList<Servicio>(serviciosActivos.values());
        Collections.sort(serviciosList, new Comparator<Servicio>() {

            public int compare(Servicio s1, Servicio s2) {
                if (UtilMap.calculateDistance(s1.getUbicacionActual(), s1.getParadaCercanaAlPasajero())
                        >= UtilMap.calculateDistance(s2.getUbicacionActual(), s2.getParadaCercanaAlPasajero()))
                    return -1;
                else
                    return 1;
            }
        });
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

    public int getIco() {
        return ico;
    }

    public void setIco(int ico) {
        this.ico = ico;

    }
}
