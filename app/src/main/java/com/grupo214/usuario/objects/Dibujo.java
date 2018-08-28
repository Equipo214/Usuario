package com.grupo214.usuario.objects;

import android.graphics.Point;
import android.os.Handler;
import android.os.SystemClock;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Dibujo {
    private Polyline polyline;
    private List<Marker> paradas;
    private HashMap<String,Marker> servicios;

    public  Dibujo(){
        paradas = new ArrayList<Marker>();
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



}
