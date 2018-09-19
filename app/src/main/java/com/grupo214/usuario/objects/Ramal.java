package com.grupo214.usuario.objects;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.HashMap;

public class Ramal {
    private int indexParadas;
    private String idLinea;
    private String idRamal;
    private String descripcion;
    private String linea;
    private String code_recorrido;
    private ArrayList<Parada> paradas;
    private Boolean checked;
    private Dibujo dibujo;
    private HashMap<String,Servicio> serviciosActivos;

    public Ramal(String idLinea,String linea, String idRamal, String ramal, String code_recorrido, ArrayList<Parada> paradas) {
        this.idLinea = idLinea;
        this.idRamal = idRamal;
        this.descripcion = ramal;
        this.code_recorrido = code_recorrido;
        this.paradas = paradas;
        this.dibujo = new Dibujo();
        this.checked = false;
        this.linea = linea;
        this.serviciosActivos = new HashMap<>();
        this.indexParadas = paradas.size()>=7?8:1;
    }

    public HashMap<String, Servicio> getServiciosActivos() {
        return serviciosActivos;
    }

    public void setServiciosActivos(HashMap<String, Servicio> serviciosActivos) {
        this.serviciosActivos = serviciosActivos;
    }

    @Override
    public String toString() {
        return idRamal;
    }

    public String getIdLinea() {
        return idLinea;
    }

    public String getCode_recorrido() {
        return code_recorrido;
    }

    public String getIdRamal() {
        return idRamal;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public ArrayList<Parada> getParadas() {
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

    public Marker paradaMasCercana(LatLng latLng){
        return  dibujo.paradaMasCercana(latLng);
    }

    public boolean esParada(Marker mk) {
        return paradas.contains(mk.getPosition());
    }

    public String getLinea() {
        return linea;
    }

    public LatLng[] sigParada() {
        LatLng[] paradasConexas = new LatLng[2];
            paradasConexas[0] = paradas.get(indexParadas).getLatLng();
            paradasConexas[1] = paradas.get(++indexParadas>=paradas.size()?1:indexParadas).getLatLng();
        return  paradasConexas;
    }

}
