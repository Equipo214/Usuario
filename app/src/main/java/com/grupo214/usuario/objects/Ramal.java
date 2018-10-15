package com.grupo214.usuario.objects;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;

public class Ramal {
    private String idLinea;
    private String idRamal;
    private String descripcion;
    private String linea;
    private ArrayList<Recorrido> recorridosAlternos;
    private Recorrido recorridoPrimario;
    private Boolean checked;
    private Dibujo dibujo;
    private String paradaCercana;

    public Ramal(String idLinea, String linea, String idRamal, String ramal, Recorrido recorridoPrimario, ArrayList<Recorrido> recorridosAlternos) {
        this.idLinea = idLinea;
        this.linea = linea;
        this.idRamal = idRamal;
        this.descripcion = ramal;
        this.dibujo = new Dibujo();
        this.recorridoPrimario = recorridoPrimario;
        this.recorridosAlternos = recorridosAlternos;
        this.checked = false;
    }

    public Ramal(String idLinea, String linea, String idRamal, String ramal, boolean isChecked) {
        this.idLinea = idLinea;
        this.linea = linea;
        this.idRamal = idRamal;
        this.descripcion = ramal;
        this.checked = isChecked;
    }


    public String getParadaCercana() {
        return paradaCercana;
    }

    public void setParadaCercana(String paradaCercana) {
        this.paradaCercana = paradaCercana;
    }

    public ArrayList<Recorrido> getRecorridosAlternos() {
        return recorridosAlternos;
    }

    @Override
    public String toString() {
        return "Linea " + idLinea + " Ramal " + idRamal + " descripcion: " + descripcion;
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

    public void setDibujo(Dibujo dibujo) {
        this.dibujo = dibujo;
    }

}
