package com.grupo214.usuario.apiGoogleDirection;

import android.util.Log;

import com.grupo214.usuario.objects.Linea;
import com.grupo214.usuario.objects.Recorrido;

import java.util.List;

/**
 * Clase para armar endpoint para obtener
 * rutas desde la API de google
 * (no deberia usarla en la version final para cargar rutas sino para caminos a pie hasta la parada).
 * @author  Daniel Boullon
 */
public class  GoogleMapsDirectionsAPI {


    /**
     * Obtener todas las rutas de google teniendo solo puntos.
     */
    public static void loadPolylineOptions(List<Linea> lineas){
        for (Linea l : lineas) {
                loadPolylineOptions(l);
        }
    }

    public static boolean checkNull(List<Linea> lineas){
        for (Linea l : lineas){
            if(l.getPolylineOptions() == null)
                return true;
        }
        return false;
    }

    /**
     * Disparar el evento asyncronico para obtener las rutas de google.
     * @param linea
     */
    public static void loadPolylineOptions(Linea linea) {
        String url = getRequestUrl(linea.getRecorrido());
        TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
        taskRequestDirections.setLinea(linea);
        taskRequestDirections.execute(url);
    }


    /**
     * Armar url.
     *
     * @param puntos
     * @return
     */
    private static String getRequestUrl(Recorrido puntos) {
        //Value of origin
        int i = 0;
        String str_org = "origin=" + puntos.get(i).getLatLng().latitude + "," + puntos.get(i).getLatLng().longitude;
        //Values of waypoints
        String str_wayp = "waypoints=";
        while (i < puntos.size() - 1) {
            str_wayp += puntos.get(i).getLatLng().latitude + "," + puntos.get(i).getLatLng().longitude + "|";
            i++;
        }
        //Value of destination
        String str_dest = "destination=" + puntos.get(i).getLatLng().latitude + "," + puntos.get(i).getLatLng().longitude;
        //Set value enable the sensor
        String sensor = "sensor=false";
        //Mode for find direction
        String mode = "mode=driving";
        //Build the full param
        String param = str_org + "&" + str_dest + "&" + str_wayp + "&" + sensor + "&" + mode;
        param.replace("|&", "&");
        //Output format
        String output = "json";
        //Create url to request
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param;
        Log.d("URL",url);
        return url;
    }
}
