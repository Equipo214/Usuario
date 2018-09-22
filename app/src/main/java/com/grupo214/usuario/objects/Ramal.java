package com.grupo214.usuario.objects;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.grupo214.usuario.activities.MainActivity;

import java.util.ArrayList;

public class Ramal {
    private int indexParadas;
    private String idLinea;
    private String idRamal;
    private String descripcion;
    private String linea;
    private ArrayList<Recorrido> recorridosAlternos;
    private Recorrido recorridoPrimario;
    private Boolean checked;
    private Dibujo dibujo;


    public Ramal(String idLinea, String linea, String idRamal, String ramal, Recorrido recorridoPrimario, ArrayList<Recorrido> recorridosAlternos) {
        this.idLinea = idLinea;
        this.idRamal = idRamal;
        this.descripcion = ramal;
        this.recorridoPrimario = recorridoPrimario;
        this.recorridosAlternos = recorridosAlternos;
        this.dibujo = new Dibujo();
        this.checked = false;
        this.linea = linea;
        if (MainActivity.DEMO)
            this.indexParadas = recorridoPrimario.getParadas().size() >= 7 ? 8 : 1;
    }

    public ArrayList<Recorrido> getRecorridosAlternos() {
        return recorridosAlternos;
    }

    @Override
    public String toString() {
        return idRamal;
    }

    public String getIdLinea() {
        return idLinea;
    }

    public String getCode_recorrido() {
        return recorridoPrimario.getRecorridoCompleto();
    }

    public String getIdRamal() {
        return idRamal;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public ArrayList<Parada> getParadas() {
        return recorridoPrimario.getParadas();
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

    public Marker paradaMasCercana(LatLng latLng) {
        return dibujo.paradaMasCercana(latLng);
    }


    public String getLinea() {
        return linea;
    }

    public LatLng[] sigParada() {
        LatLng[] paradasConexas = new LatLng[2];
        paradasConexas[0] = getParadas().get(indexParadas).getLatLng();
        paradasConexas[1] = getParadas().get(++indexParadas >= getParadas().size() ? 1 : indexParadas).getLatLng();
        return paradasConexas;
    }

}
