package com.grupo214.usuario.objetos;

import android.graphics.Color;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.grupo214.usuario.R;


public class ColorRuta {

    public static final int[] coloresRuta = {
            Color.argb(150, 255, 0, 0),
            Color.argb(150, 0, 0, 255),
            Color.argb(150, 0, 255, 0),
            Color.argb(150, 255, 127, 0),
            Color.argb(150, 255, 0, 127),
            Color.argb(150, 127, 255, 0),
            Color.argb(150, 0,255, 127),
            Color.argb(150, 0, 127, 127),
            Color.argb(150, 127, 0, 127),
            Color.argb(150, 127, 127, 0),
            Color.argb(150, 127, 0, 255),
            Color.argb(150, 0, 127, 255),
            Color.argb(150, 0, 127, 127),
            Color.argb(150, 127, 0, 127),
            Color.argb(150, 127, 127, 0),
            Color.argb(150, 127, 127, 127)
    };

    private int i;


    public int nextColor() {
        if (i == coloresRuta.length)
            i = 0;
        return coloresRuta[i++];
    }

}

