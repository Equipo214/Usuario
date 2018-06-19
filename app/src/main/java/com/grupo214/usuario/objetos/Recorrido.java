package com.grupo214.usuario.objetos;

import android.support.annotation.NonNull;


import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collection;

public class Recorrido extends ArrayList<Punto> {

    private Boolean  checked;

    public Recorrido( @NonNull Collection<? extends Punto> c) {
        super(c);
        checked = false;
    }

    public Recorrido(){
        super();
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
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

