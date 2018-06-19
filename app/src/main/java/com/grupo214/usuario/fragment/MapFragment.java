package com.grupo214.usuario.fragment;


import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import com.grupo214.usuario.R;
import com.grupo214.usuario.objetos.Linea;
import com.grupo214.usuario.objetos.Punto;
import com.grupo214.usuario.objetos.Recorrido;


import java.text.DecimalFormat;
import java.util.ArrayList;

public class MapFragment extends Fragment {

    Button bt;
    MapView mMapView;
    GoogleMap googleMap;
    ArrayList<Recorrido> recorridos;
    ArrayList<Linea> mLineas;
    Marker mk;
    Boolean flag = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        bt = (Button) rootView.findViewById(R.id.button);
        recorridos = new ArrayList<>();
        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {

            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                } else {
                    googleMap.setMyLocationEnabled(true);
                    googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                }

                googleMap.getUiSettings().setZoomControlsEnabled(true);
                googleMap.getUiSettings().setIndoorLevelPickerEnabled(false);
                // googleMap.getUiSettings().setMapToolbarEnabled(false);
                googleMap.setBuildingsEnabled(false);
                googleMap.setMapStyle(new MapStyleOptions(getResources()
                        .getString(R.string.style_json)));


                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        mensaje("markerClick");
                        return false;
                    }
                });
                googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        mensaje("infoWindowsClick");
                    }
                });

                LatLng ubicacionUniversidad = new LatLng(-34.669997, -58.563181);
                //   googleMap.addMarker(new MarkerOptions().position(ubicacionUniversidad).title("Linea 406").snippet("Lomas de zamora"));
                googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                    @Override
                    public boolean onMyLocationButtonClick() {
                        mensaje("Button apretado");
                        return false;
                    }
                });

                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                        new CameraPosition.Builder().target(ubicacionUniversidad).zoom(10).build()));


            }
        });

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag) {
                    mk = googleMap.addMarker(new MarkerOptions()
                            .position(mLineas.get(0).getNextPoint())
                            .title("Linea  4")
                            .snippet("Macri bus"));
                    flag = false;
                } else
                    animateMarker(mk, mLineas.get(0).getNextPoint(), false);
            }
        });

        return rootView;
    }

    private void mensaje(String msj) {
        Snackbar.make(getActivity().getWindow().getDecorView().findViewById(android.R.id.content)
                , msj, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    public MapView getmMapView() {
        return mMapView;
    }

    public void setmLineas(ArrayList<Linea> mLineas) {
        this.mLineas = mLineas;
    }

    public void dibujarRuta(Linea linea) {
        for (Punto punto : linea.getRecorrido()) {
            if (punto.isParada())
                googleMap.addMarker(new MarkerOptions()
                        .position(punto.getLatLng())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_parada))
                        .anchor(0.5f, 0.5f)
                        .flat(true)
                        .title(linea.getLinea()));

        }

        googleMap.addPolyline(linea.getPolylineOptions());
    }

    // funcion para animar que recibe puntos.

    /**
     * Borro todas las rutas y vuelvo a dibujar.
     */
    public void actualizarDibujoRutas() {
        googleMap.clear();
        for (int i = 0; i < mLineas.size(); i++) {
            Linea linea = mLineas.get(i);
            if (linea.isCheck()) {
                //    if (linea.getPolylineOptions() == null)
                //        cargarPolylineOptions(linea);
                dibujarRuta(linea);
            }

        }
    }

    public void animateMarker(final Marker marker, final LatLng toPosition,
                              final boolean hideMarker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = googleMap.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        int distancia = (int) calcularDistancia(mk.getPosition(),toPosition);

        distancia = distancia==0?1:distancia;

        // Lo que me devuelve la funcion es la distancia en metros entre los dos puntos
        // entonces segun yo, 100 metros lo hace en 12 segundos
        // (distancia/100)*12 (cada doce segundos hace 100 metros)
        // pero como la duracion esta en mili lo debo multiplicar por 1000
        // entonces me queda (distancia*12*10) -> distancia*120
        final long duration = distancia*120; // dependiendo la distancia entre los puntos.
        final Interpolator interpolator = new LinearInterpolator();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));
                if (t < 1.0) {
                    // Post again 16ms later.
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
    private double calcularDistancia(LatLng StartP, LatLng EndP) {
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

        Log.i("Radius Value", "" + Radius * c + "   Metros");

        return Radius * c;
    }
}
