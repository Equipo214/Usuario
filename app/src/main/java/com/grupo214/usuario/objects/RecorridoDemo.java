package com.grupo214.usuario.objects;

import android.support.annotation.NonNull;


import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Collection;


/**
 * Clase que representa el recorrido de un determinado ramal.
 * es el recorrido habitual del un ramal de una linea de un colectivo.
 * @author  Daniel Boullon
 */
@Deprecated
public class RecorridoDemo extends ArrayList<Punto> {




    public RecorridoDemo(@NonNull Collection<? extends Punto> c) {
        super(c);
    }

    public RecorridoDemo(){
        super();
    }



    @Override
    public String toString() {
        String aux = "";
        for (Punto p: this) {
            aux += p.toString();
        }
        return aux;
    }



}

