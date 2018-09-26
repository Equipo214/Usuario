package com.grupo214.usuario.objects;

import com.google.android.gms.maps.model.LatLng;

public class ParadaAlarma {

    private String id_parada;
    private String id_alarms;
    private String linea;
    private String ramal;
    private LatLng punto;


    public ParadaAlarma(String id_parada, String linea, String ramal, LatLng punto) {
        this.linea = linea;
        this.id_parada = id_parada;
        this.ramal = ramal;
        this.punto = punto;
    }

    public ParadaAlarma(String id_parada, String linea, String ramal, LatLng punto, String id_alarms) {
        this.linea = linea;
        this.id_parada = id_parada;
        this.ramal = ramal;
        this.id_parada = id_parada;
        this.punto = punto;

    }

    public LatLng getPunto() {
        return punto;
    }

    public String getId_parada() {
        return id_parada;
    }

    public String getLinea() {
        return linea;
    }

    public String getRamal() {
        return ramal;
    }

    public String getId_alarms() {
        return id_alarms;
    }

    public void setId_alarms(String id_alarms) {
        this.id_alarms = id_alarms;
    }


}
