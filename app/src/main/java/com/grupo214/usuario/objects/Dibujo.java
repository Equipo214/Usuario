package com.grupo214.usuario.objects;


import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.grupo214.usuario.Util.UtilMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Dibujo {
    private Polyline polyline;
    private List<Marker> paradas;
    private HashMap<String,Marker> servicios;

    public  Dibujo(){
        paradas = new ArrayList<>();
        servicios = new HashMap<>();
    }
    public void setPolyline(Polyline polyline) {
        this.polyline = polyline;
    }

    public void agregarParada(Marker marker){
        this.paradas.add(marker);
    }

    public void hide() {
        polyline.setVisible(false);
        for (Marker mk : paradas) {
            mk.setVisible(false);
        }
        for( Marker mk : servicios.values()){
            mk.setVisible(false);
        }
    }

    public void show() {
        polyline.setVisible(true);
        for (Marker mk : paradas) {
            mk.setVisible(true);
        }
        for( Marker mk : servicios.values()){
            mk.setVisible(false);
        }
    }


    public Marker paradaMasCercana(LatLng userStart) {
        Marker paradaMasCercana = paradas.get(0); // guardo el primero como minimo
        double distancia = UtilMap.calculateDistance(userStart, paradaMasCercana.getPosition());

        double distancia_aux;
        for (Marker parada : paradas) {
            distancia_aux = UtilMap.calculateDistance(userStart, parada.getPosition());
            if (distancia_aux < distancia) { // si no hay otro minimo es este
                paradaMasCercana = parada;
                distancia = distancia_aux;
            }
        }
        return paradaMasCercana;
    }
}
