package com.grupo214.usuario.fragment;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.grupo214.usuario.R;
import com.grupo214.usuario.apiServidor.Dibujar;
import com.grupo214.usuario.apiServidor.DibujarDemo;
import com.grupo214.usuario.objects.Linea;
import com.grupo214.usuario.objects.Punto;
import com.grupo214.usuario.objects.Recorrido;

import java.util.ArrayList;


/**
 * Clase gestiona la pestaña con el mapa.
 * @author  Daniel Boullon
 */
public class MapFragment extends Fragment {

    Button bt_animar;

    Button bt_demo;
    MapView mMapView;
    GoogleMap googleMap;
    ArrayList<Recorrido> recorridos;
    ArrayList<Linea> mLineas;
    byte times = 0;
    private boolean cargadas = false;
    private Marker userMarkerStart;
    private Marker userMarkerDestiny;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        bt_animar = (Button) rootView.findViewById(R.id.button);
        //     bt_demo = (Button) rootView.findViewById(R.id.bt_demo);
        recorridos = new ArrayList<>();
        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();


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
                googleMap.getUiSettings().setMapToolbarEnabled(false);

                googleMap.setBuildingsEnabled(false);
                googleMap.setMapStyle(new MapStyleOptions(getResources()
                        .getString(R.string.style_json)));

                userMarkerStart = googleMap.addMarker(new MarkerOptions()
                        .title("Inicio").position(new LatLng(0, 0)));

                userMarkerDestiny = googleMap.addMarker(new MarkerOptions()
                        .title("Destino").position(new LatLng(0, 0))
                );

                userMarkerStart.setVisible(false);
                userMarkerDestiny.setVisible(false);

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
                        if (marker.getTitle().contains("Linea")) {
                            mensaje("Accesibilidad");
                        } else if (marker.getTitle().contains("Servicio")) {
                            mensaje("Comentario");
                        }
                    }
                });
                googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(LatLng latLng) {
                        if (times == 1) {
                            cleanUserMarkers();

                        }

                        if (times == 0)
                            updateUserMarker(userMarkerStart, latLng);

//                        if (times == 1)
//                            updateUserMarker(userMarkerDestiny, latLng);

                        if (++times == 1) {
                            Boolean flag = false;
                            for (Linea l : mLineas) {
                                if (l.isCheck()) {
                                    new DibujarDemo(googleMap, l, true,
                                            userMarkerStart.getPosition(), userMarkerDestiny.getPosition()).ejecutar();
                                    new DibujarDemo(googleMap, l, false,
                                            userMarkerStart.getPosition(), userMarkerDestiny.getPosition()).ejecutar();
                                    flag = true;
                                }
                            }
                            if (!flag)
                                mensaje("marca alguna gil");
                        }
                    }
                });

                googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                    @Override
                    public boolean onMyLocationButtonClick() {
                        // validar gps. (creo que si no activo no aparece el boton quizas safo=

                        if (times < 2) {
                            LatLng ubicacionActual = new LatLng(0, 0);
                            asd(ubicacionActual);
                        }
                        return true;
                    }
                });
                /*
                 * NET BUTTON {
                 *
                 *       cleanUserMarkers();
                 *
                 *
                 * }
                 *
                 *
                 *
                 *
                 *
                 * */


                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                        new CameraPosition.Builder().target(new LatLng(-34.669997, -58.563181)).zoom(10).build()));


            }
        });

        bt_animar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Marker mk = googleMap.addMarker(new MarkerOptions()
                        .position(mLineas.get(3).getNextPointDemo())
                        .title("Servicio " + mLineas.get(3).getLinea())
                        .snippet(mLineas.get(3).getRamal()));
                Dibujar dibujar = new Dibujar(googleMap, mLineas.get(3), getContext(), mk);
                dibujar.execute();
            }
        });


        /*

        bt_demo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Linea l : mLineas) {
                    if (l.isCheck()) {
                        new DibujarDemo(googleMap, l,true).ejecutar();
                        new DibujarDemo(googleMap, l,false).ejecutar();
                    }
                }
            }
        });
            */
        return rootView;
    }

    private void asd(LatLng latLng) {
        if (times == 2)
            cleanUserMarkers();
        if (times == 0)
            updateUserMarker(userMarkerStart, latLng);
        if (times == 1)
            updateUserMarker(userMarkerDestiny, latLng);
        if (++times == 2)
            mensaje("Aca no se que hacer pero algo tengo que hacer :v");
    }


    private void updateUserMarker(Marker userMarker, LatLng latLng) {
        userMarker.setPosition(latLng);
        userMarker.setVisible(true);
    }

    private void cleanUserMarkers() {
        times = 0;
        userMarkerDestiny.setVisible(false);
        userMarkerStart.setVisible(false);
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

    @Deprecated
    public void drawRoute(Linea l) {
        for (Punto punto : l.getRecorrido()) {
            if (punto.isParada())
                googleMap.addMarker(new MarkerOptions()
                        .position(punto.getLatLng())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_parada))
                        .anchor(0.5f, 0.5f)
                        .flat(true)
                        .title(l.getLinea())
                        .snippet(l.getRamal()));
        }
        googleMap.addPolyline(l.getPolylineOptions());
    }

    public void loadRoutes() {
        BitmapDescriptor icoParada = BitmapDescriptorFactory.fromResource(R.drawable.ic_parada);
        for (Linea l : mLineas) {
            l.setPolyline(googleMap.addPolyline(l.getPolylineOptions()));
            for (Punto punto : l.getRecorrido()) {
                if (punto.isParada())
                    l.agregarParada(googleMap.addMarker(new MarkerOptions()
                            .position(punto.getLatLng())
                            .icon(icoParada)
                            .anchor(0.5f, 0.5f)
                            .flat(true)
                            .title("Linea " + l.getLinea())));
            }

            // si no lo personalizo por coso sacarlo de aca.
            googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                public View getInfoContents(Marker marker) {
                    View view = getLayoutInflater().inflate(R.layout.container_info_windows, null);
                    ((TextView) view.findViewById(R.id.list_text_linea)).setText(marker.getTitle());
                    ((TextView) view.findViewById(R.id.list_text_ramal)).setText(marker.getSnippet());
                    return view;
                }
            });
            if (l.isCheck())
                l.show();
            else
                l.hide();
        }
        cargadas = true;
    }

    public void updateDrawingRoutes() {
        if (!cargadas)
            loadRoutes();
        for (Linea l : mLineas) {
            if (!l.isCheck()) {
                l.hide();
            } else {
                l.show();
            }
        }
    }




}
