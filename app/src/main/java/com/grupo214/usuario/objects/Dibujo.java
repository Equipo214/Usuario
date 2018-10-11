package com.grupo214.usuario.objects;


import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.grupo214.usuario.Util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Dibujo {
    private Polyline polyline;
    private ArrayList<Polyline> polylinesAlternos;
    private List<Marker> paradas;
    private List<Marker> paradasAlternas;
    private HashMap<String, Marker> servicios;

    public Dibujo() {
        paradas = new ArrayList<>();
        paradasAlternas = new ArrayList<>();
        polylinesAlternos = new ArrayList<>();
        servicios = new HashMap<>();
    }

    public void setPolyline(Polyline polyline) {
        this.polyline = polyline;
    }

    public void addPolylineAlternative(Polyline polyline) {
        polylinesAlternos.add(polyline);
    }

    public void addParadasAlternas(Marker mk) {
        paradasAlternas.add(mk);
    }

    public void agregarParada(Marker marker) {
        this.paradas.add(marker);
    }

    /**
     *  quizas lo hago de otra forma mucho mas optima si me sobra el tiempo
     */
    @Deprecated
    public void hide() {
        polyline.setVisible(false);
        for (Polyline polylineAlterna : polylinesAlternos)
            polylineAlterna.setVisible(false);
        for (Marker mk : paradas)
            mk.setVisible(false);
        for (Marker mk : paradasAlternas)
            mk.setVisible(false);
        for (Marker mk : servicios.values()) {
            mk.setVisible(false);
        }
    }

    @Deprecated
    public void show() {
        polyline.setVisible(true);
        for (Polyline polylineAlterna : polylinesAlternos)
            polylineAlterna.setVisible(true);
        for (Marker mk : paradas)
            mk.setVisible(true);
        for (Marker mk : paradasAlternas)
            mk.setVisible(true);
        for (Marker mk : servicios.values())
            mk.setVisible(true);
    }



    public Marker paradaMasCercana(LatLng userStart) {
        Marker paradaMasCercana = paradas.get(0); // guardo el primero como minimo
        double distancia = Util.calculateDistance(userStart, paradaMasCercana.getPosition());
        double distancia_aux;
        for (Marker parada : paradas) {
            distancia_aux = Util.calculateDistance(userStart, parada.getPosition());
            if (distancia_aux < distancia) { // si no hay otro minimo es este
                paradaMasCercana = parada;
                distancia = distancia_aux;
            }
        }
        return paradaMasCercana;
    }

    public void setAlpah(Boolean visible) {

        for (int i = 1; i < paradas.size() - 1; i++) {
            paradas.get(i).setVisible(visible);
        }
    }
}
