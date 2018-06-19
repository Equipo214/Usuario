package com.grupo214.usuario.objetos;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class Servicio {

    private LatLng ubicacionActual;
    private ArrayList<LatLng> recorrido;



    public LatLng actualizarUbicacionActual(){
        recorrido.add(ubicacionActual); // guardo
        ubicacionActual = new LatLng(0,0); // ir a base de datos y agarrar el proximo
        return ubicacionActual;
    }
}
