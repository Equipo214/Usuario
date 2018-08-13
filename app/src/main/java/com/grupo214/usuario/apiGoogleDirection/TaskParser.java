package com.grupo214.usuario.apiGoogleDirection;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.grupo214.usuario.R;
import com.grupo214.usuario.objects.LineaDemo;
import com.grupo214.usuario.objects.Ramal;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Clase asicronica para armar la ruta para dibujar
 * desde un JSON.
 * @author  Daniel Boullon
 */
public class TaskParser extends AsyncTask<String, Void, List<List<HashMap<String, String>>>> {

    private Ramal ramal;

    public void setRamal(Ramal ramal) {
        this.ramal = ramal;
    }

    @Override
    protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
        JSONObject jsonObject = null;
        List<List<HashMap<String, String>>> routes = null;
        try {
            jsonObject = new JSONObject(strings[0]);
            DirectionsParser directionsParser = new DirectionsParser();
            routes = directionsParser.parse(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return routes;
    }

    @Override
    protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
        //Get list route and display it into the map
        ArrayList points = null;
        PolylineOptions polylineOptions = null;

        for (List<HashMap<String, String>> path : lists) {
            points = new ArrayList();

            polylineOptions = new PolylineOptions();
            for (HashMap<String, String> point : path) {
                double lat = Double.parseDouble(point.get("lat"));
                double lon = Double.parseDouble(point.get("lon"));

                points.add(new LatLng(lat, lon));
            }

            polylineOptions.addAll(points);
            polylineOptions.width(15);
            polylineOptions.color(R.color.colorDanger); //COLOR
            polylineOptions.geodesic(true);

        }

        if (polylineOptions != null) {
            ramal.getDibujo().setPolylineOptions(polylineOptions);
        }
    }
}