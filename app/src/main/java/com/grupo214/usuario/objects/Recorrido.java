package com.grupo214.usuario.objects;

import java.util.ArrayList;

public class Recorrido {
    private String idRecorrido;
    private String recorridoCompleto;
    private ArrayList<Parada> paradas;

    public Recorrido(String idRecorrido, String recorridoCompleto, ArrayList<Parada> paradas) {
        this.idRecorrido = idRecorrido;
        this.recorridoCompleto = recorridoCompleto;
        this.paradas = paradas;
    }

    public String getRecorridoCompleto() {
        return recorridoCompleto;
    }

    public ArrayList<Parada> getParadas() {
        return paradas;
    }
}
