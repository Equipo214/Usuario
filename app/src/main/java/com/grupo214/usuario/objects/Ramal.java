package com.grupo214.usuario.objects;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.grupo214.usuario.Util.UtilMap;

import java.util.ArrayList;
import java.util.HashMap;

public class Ramal {
    private String idLinea;
    private String idRamal;
    private String Descripcion;
    private String code_recorrido;
    private ArrayList<LatLng> paradas;
    private Boolean checked;
    private Dibujo dibujo;
    private HashMap<String,Servicio> serviciosActivos;

    public Ramal(String idLinea, String idRamal, String ramal, String code_recorrido, ArrayList<LatLng> paradas) {
        this.idLinea = idLinea;
        this.idRamal = idRamal;
        this.Descripcion = ramal;
        this.code_recorrido = code_recorrido;
        this.paradas = paradas;
        this.dibujo = new Dibujo();
        this.checked = false;
        this.serviciosActivos = new HashMap<>();
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


    /**
     * Poner en el marker la palabra cercana.
     * @param userStart
     * @return
     */
    public LatLng paradaMasCercanaLatLng(LatLng userStart) {
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

    public Marker paradaMasCercana(LatLng latLng){
        return  dibujo.paradaMasCercana(latLng);
    }

    public boolean esParada(Marker mk) {
        return paradas.contains(mk.getPosition());
    }

}
