package com.grupo214.usuario.objects;

import com.google.android.gms.maps.model.LatLng;

public class ParadaAlarma {

    private String id_parada;
    private String id_alarms;
    private String id_linea;
    private String id_ramal;
    private LatLng punto;


    public ParadaAlarma(String id_parada, String id_linea, String id_ramal, LatLng punto) {
        this.id_linea = id_linea;
        this.id_parada = id_parada;
        this.id_ramal = id_ramal;
        this.punto = punto;
    }

    public ParadaAlarma(String id_parada, String id_linea, String id_ramal, LatLng punto, String id_alarms) {
        this.id_linea = id_linea;
        this.id_parada = id_parada;
        this.id_ramal = id_ramal;
        this.id_parada = id_parada;
        this.punto = punto;
        this.id_alarms = id_alarms;

    }

    public LatLng getPunto() {
        return punto;
    }

    public String getId_parada() {
        return id_parada;
    }

    public String getId_linea() {
        return id_linea;
    }

    public String getIdRamal() {
        return id_ramal;
    }

    public String getId_alarms() {
        return id_alarms;
    }

    public void setId_alarms(String id_alarms) {
        this.id_alarms = id_alarms;
    }


}
