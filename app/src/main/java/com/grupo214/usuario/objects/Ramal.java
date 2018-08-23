package com.grupo214.usuario.objects;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import com.grupo214.usuario.Util.UtilMap;

import java.util.ArrayList;
import java.util.List;

public class Ramal {
    @Override
    public String toString() {
        return idRamal;
    }

    private String idRamal;
    private String Descripcion;
    private String code_recorrido;
    private ArrayList<LatLng> paradas;
    private Boolean  checked;
    private Dibujo dibujo;


    public Ramal(String idRamal, String ramal,String code_recorrido, ArrayList<LatLng> paradas) {
        this.idRamal = idRamal;
        this.Descripcion = ramal;
        this.code_recorrido = code_recorrido;
        this.paradas = paradas;
        this.dibujo = new Dibujo();
        this.checked = false;
    }

    public String getCode_recorrido() {
        return code_recorrido;
    }

    public String getIdRamal() {
        return idRamal;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public ArrayList<LatLng> getParadas() {
        return paradas;
    }

    public Boolean isCheck() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    public Dibujo getDibujo() {
        return dibujo;
    }

    public LatLng paraMasCercana(LatLng userStart) {
        LatLng paradaMasCercana = paradas.get(0); // guardo el primero como minimo
        double distancia = UtilMap.calculateDistance(userStart, paradaMasCercana);

        double distancia_aux;
        for (LatLng parada : paradas) {
                distancia_aux = UtilMap.calculateDistance(userStart, parada);
                if (distancia_aux < distancia) { // si no hay otro minimo es este
                    paradaMasCercana = parada;
                    distancia = distancia_aux;
            }
        }
        return paradaMasCercana;
    }

}
