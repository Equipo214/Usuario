package com.grupo214.usuario.objects;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

/**
 * Clase que representa un servicio de colectivo en tiempo real.
 *
 * @author Daniel Boullon
 */
public class Servicio {

    public static Comparator<Servicio> COMPARATOR = new Comparator<Servicio>() {
        @Override
        public int compare(Servicio o1, Servicio o2) {
            return o1.getTiempoEstimado() - o2.getTiempoEstimado();
        }
    };
    private int ico;
    private String idServicio;
    private String linea;
    private String ramal;
    private int tiempoEstimado;
    private Marker mk;
    private boolean activo;
    private ArrayList<LatLng> paradas;
    private String fecha;

    public Servicio(String idServicio,String fecha, String linea, String ramal, Marker mk, int ico, int minutos) {
        this.idServicio = idServicio;
        this.linea = linea;
        this.ramal = ramal;
        this.tiempoEstimado = minutos;
        this.mk = mk;
        this.fecha = fecha;
        this.ico = ico;
        this.activo = true;
        this.mk.setTitle("Servicio");
        this.mk.setSnippet("Linea " + linea + "\nRamal " + ramal);
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public Marker getMk() {
        return mk;
    }


    public String getLinea() {
        return linea;
    }

    public String getRamal() {
        return ramal;
    }

    public int getTiempoEstimado() {
        return tiempoEstimado;
    }

    public void setTiempoEstimado(int tiempoEstimado) {
        this.tiempoEstimado = tiempoEstimado;
    }

    public int compararFechas(String fecha2){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date1 = sdf.parse(fecha);
            Date date2 = sdf.parse(fecha2);
            return  date1.compareTo(date2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public String getIdServicio() {
        return idServicio;
    }

    public int getIco() {
        return ico;
    }


    public void setIco(int ico) {
        this.ico = ico;

    }

    public void setParadas(ArrayList<LatLng> paradas) {
        this.paradas = paradas;
    }
}
