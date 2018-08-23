package com.grupo214.usuario.objects;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class Dibujo {
    private Polyline polyline;
    private List<Marker> paradas;

    public  Dibujo(){
        paradas = new ArrayList<Marker>();

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
    }

    public void show() {
        polyline.setVisible(true);
        for (Marker mk : paradas) {
            mk.setVisible(true);
        }
    }

    public Polyline getPolyline() {
        return polyline;
    }
}
