package com.grupo214.usuario.apiServidor;


import android.content.Context;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.grupo214.usuario.R;
import com.grupo214.usuario.objects.LineaDemo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



// Cuando tenga todas las lineas lo que puedo hacer , es solo animar las que estan dentro del radio
// Sino estan dentro del radio simplemente cambiarle la ubicacion. con eso optimizo bastante la app
//


/**
 * Clase que se conecta al servidor para dibujar en tiempo real la ubicacion del colectivo.
 * @author  Daniel Boullon
 */
public class Dibujar implements Response.Listener<JSONObject>, Response.ErrorListener {

    private final static long REFRESH_TIME = 1000;
    private final Marker mk;

    private Double lat;
    private Double log;
    private LineaDemo lineaDemo;
    private RequestQueue rq;
    private Context context;
    private JsonRequest jrq;
    private GoogleMap googleMap;

    public Dibujar(GoogleMap googleMap, LineaDemo lineaDemo, Context context, Marker mk) {
        this.googleMap = googleMap;
        this.lineaDemo = lineaDemo;
        this.rq = Volley.newRequestQueue(context);
        this.context = context;
        this.mk = mk;
    }

    @Override
    public void onResponse(JSONObject response) {

        JSONArray jsonArray = response.optJSONArray("coordenadas");
        JSONObject jsonObject = null;
        try {

            jsonObject = jsonArray.getJSONObject(0);
            lat = (jsonObject.optDouble("latitud"));
            log = (jsonObject.optDouble("longitud"));

            animateMarker(mk, new LatLng(lat, log), false, googleMap);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Toast.makeText(context, "OK", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(context, "Error: " + error.toString(), Toast.LENGTH_LONG).show();
    }

    public void execute() {
        Time time = new Time();
        time.execute();
    }

    public void hilo() {
        try {
            Thread.sleep(REFRESH_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void ejecutar() {
        Time time = new Time();
        Log.d("Time: ", "adentro de Time");
        time.execute();
    }

    private void consumirPosicion() {

        String url = context.getString(R.string.SERVIDOR_LOCAL);
        Log.d("url1", url);
        jrq = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        Log.d("url2", url);
        rq.add(jrq);
    }

    public void animateMarker(final Marker marker, final LatLng toPosition,
                              final boolean hideMarker, GoogleMap googleMap) {

        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = googleMap.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);

        final Interpolator interpolator = new LinearInterpolator();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / REFRESH_TIME);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));
                if (t < 1.0) {
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
    }

    public class Time extends AsyncTask<Void, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            hilo();
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            ejecutar();
            consumirPosicion();
            Toast.makeText(context, "consumiendo..", Toast.LENGTH_SHORT).show();
        }
    }
}