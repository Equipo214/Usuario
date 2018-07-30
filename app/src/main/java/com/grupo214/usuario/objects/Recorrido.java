package com.grupo214.usuario.objects;

import android.support.annotation.NonNull;


import java.util.ArrayList;
import java.util.Collection;


/**
 * Clase que representa el recorrido de un determinado ramal.
 * es el recorrido habitual del un ramal de una linea de un colectivo.
 * @author  Daniel Boullon
 */
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

