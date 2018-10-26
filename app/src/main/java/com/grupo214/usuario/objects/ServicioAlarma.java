package com.grupo214.usuario.objects;

import com.google.android.gms.maps.model.LatLng;
import com.grupo214.usuario.Util.Util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

public class ServicioAlarma {

    public static Comparator<ServicioAlarma> COMPARATOR = new Comparator<ServicioAlarma>() {
        @Override
        public int compare(ServicioAlarma o1, ServicioAlarma o2) {
            return o1.getTiempoEstimado() - o2.getTiempoEstimado();
        }
    };

    private LatLng ubicacion;
    private String idServicio;
    private String linea;
    private String ramal;
    private String fecha;
    private int tiempoEstimado;
    private int ico;
    private boolean activo;
    private ArrayList<LatLng> paradasDelRamalSeleccionadas;

    public ServicioAlarma(String idServicio, String fecha, String linea, String ramal,
                          ArrayList<LatLng> paradasDelRamalSeleccionadas) {
        this.idServicio = idServicio;
        this.linea = linea;
        this.ramal = ramal;
        this.fecha = fecha;
        this.activo = true;
        this.paradasDelRamalSeleccionadas = paradasDelRamalSeleccionadas;

    }

    public int compararFechas(String fecha2){
     SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date1 = simpleDateFormat.parse(fecha);
            Date date2 = simpleDateFormat.parse(fecha2);
            return  date1.compareTo(date2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    return 0;
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

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public String getIdServicio() {
        return idServicio;
    }

    public boolean isNearByDistance(int i) {
        for (LatLng parada : paradasDelRamalSeleccionadas) {
            if (Util.calculateDistance(ubicacion, parada) < i)
                return true;
        }
        return false;
    }

    public boolean isNearByTime(int i) {
        if (tiempoEstimado < i)
            return true;
        return false;
    }

    public void setUbicacion(LatLng ubicacion) {
        this.ubicacion = ubicacion;
    }

    public int getIco() {
        return ico;
    }

    public void setIco(int ico) {
        this.ico = ico;
    }
}
