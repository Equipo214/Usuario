package com.grupo214.usuario.Util;

import android.graphics.Point;
import android.os.Handler;
import android.os.SystemClock;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.android.volley.RequestQueue;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

/**
 * Clase con metodos de apoyo para el map.
 *
 * @author Daniel Boullon
 */
public class UtilMap {

    private RequestQueue requestQueue_distanceMatrix;

    /**
     * Ccalcular distancia entre dos puntos en metros.
     *
     * @param StartP punto de inicio.
     * @param EndP   punto de fin.
     * @return
     */
    public static double calculateDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371000;// radio de la tierra en  metros.

        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return Radius * c;
    }

    public static void animateMarker(final Marker mk, final LatLng toPosition,
                                     final boolean visible, GoogleMap googleMap, final long refreshTime) {

        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = googleMap.getProjection();
        Point startPoint = proj.toScreenLocation(mk.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / refreshTime);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;
                mk.setPosition(new LatLng(lat, lng));
                if (t < 1.0) {
                    handler.postDelayed(this, 16);
                } else {
                    mk.setVisible(visible);
                }
            }
        });
    }
    /*
    public Parada calcularTiempoEstimado(LatLng ubicacion, final ArrayList<Parada> paradas, Parada proximaParada, Context context) {
        // esto hay que afinarlo un poco mas.
        if (calculateDistance(ubicacion, proximaParada.getLatLng()) < 50) {  // llegue a la proximaParada
            if (paradas.size() == proximaParada.getOrden()) // si llegue a la ultima parada
                return null; // termino el viaje
            proximaParada = paradas.get(proximaParada.getOrden() + 1); // si no es la ultima agarra la siguiente.
        }
        if (requestQueue_distanceMatrix != null) {
            requestQueue_distanceMatrix.stop();
            requestQueue_distanceMatrix = null;
        }
        LatLng origen;
        LatLng destino = null;
        for (int i = 1; i <= paradas.size(); i++) { // lo hago aproposito para asegurar que vaya de forma ordenada.
            final Parada parada = paradas.get(i);
            final int finaI;
            // solo agarro las paradas posteriores a que estoy. desde la prox para delante.
            if (parada.getOrden() < proximaParada.getOrden())
                continue; // pasa al siguiente elemento del for.
            else if (parada.getOrden() == proximaParada.getOrden()) {
                origen = ubicacion;
                destino = parada.getLatLng();
                finaI = -1;
            } else { // if parada.getOrden() > proximaParada.getOrden()
                origen = destino;
                destino = parada.getLatLng();
                finaI = i;
            }

            // armo el URL para llamar a google ( hay que agregar mas parametros VER ESTO JUANPI O ALFRED )
            String outputFormat = "origins=" + origen.latitude + "," + origen.longitude +
                    "&destinations=" + destino.latitude + "," + destino.longitude;
            String parameters = "&language=es";
            String url = "https://maps.googleapis.com/maps/api/distancematrix/json?" + outputFormat + parameters + "&key=" + context.getString(R.string.google_maps_key);


            final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            JSONArray rows = response.optJSONArray("rows");
                            try {
                                long tiempoAux;
                                if (finaI == -1)
                                    tiempoAux = 0;
                                else
                                    tiempoAux = paradas.get(finaI - 1).getTiempoEstimado();

                                // quizas vos en parada vas a tener un atributo nuevo llamado tiempo esperado que sea un value en segundos.
                                parada.setTiempoEstimado(rows.getJSONObject(0).getJSONArray("elements")
                                        .getJSONObject(0)
                                        .getJSONObject("duration")
                                        .getLong("value")
                                        + tiempoAux );
                            } catch (JSONException e) {
                                Log.d("Json Error", e.toString());
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("JSON:ERROR", error.toString());
                }
            });
            requestQueue_distanceMatrix = Volley.newRequestQueue(context);
            requestQueue_distanceMatrix.add(jsonRequest);
        }

        return proximaParada; // proxima parada debe estar inicializado en 2.
        // luego de esto hacer un update de la tabla.
    }*/

}
