package com.grupo214.usuario.objects;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Clase que representa un servicio de colectivo ent iempo real.
 * @author  Daniel Boullon
 */
public class Servicio {

    private LatLng ubicacionActual;
    private ArrayList<LatLng> recorrido;
    private Linea linea;

    public LatLng actualizarUbicacionActual(){
        recorrido.add(ubicacionActual); // guardo
        ubicacionActual = new LatLng(0,0); // ir a base de datos y agarrar el proximo
        return ubicacionActual;
    }
}
