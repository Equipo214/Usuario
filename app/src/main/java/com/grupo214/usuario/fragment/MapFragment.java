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
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import com.grupo214.usuario.R;
import com.grupo214.usuario.alarma.Alarma;
import com.grupo214.usuario.connserver.Dibujar;
import com.grupo214.usuario.objects.Linea;
import com.grupo214.usuario.objects.LineaDemo;
import com.grupo214.usuario.objects.Punto;
import com.grupo214.usuario.objects.Ramal;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Clase gestiona la pestaña con el mapa.
 *
 * @author Daniel Boullon
 */
public class MapFragment extends Fragment {

    HashMap<String, Marker> paradasConAlarmas;
    private MapView mMapView;
    private GoogleMap googleMap;
    private ArrayList<Linea> mLinea;
    private ArrayList<Linea> lineas_seleccionadas;


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        paradasConAlarmas = new HashMap<>();
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


                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(final Marker marker) {
                        // tiempo estimado al abrir el info view
                        // if( Servicio ) calcular tiempo return ;
                        // if( paradaMasCercana ) menu accecibilidad.
                        if (marker.getTitle().contains("cercana")) {
                            mensaje("cercana");
                        }
                        //ONMARKERCLICK
                        if (marker.getTitle().contains("Servicio")) {
                            mensaje("Servicio");
                        }
                        if (marker.getTitle().contains("Parada")) {
                            Snackbar mySnackbar;
                            if (paradasConAlarmas.get(marker.getId()) == null) {
                                mySnackbar = Snackbar.make(rootView,
                                        "", Snackbar.LENGTH_LONG);
                                mySnackbar.setActionTextColor(getResources().getColor(R.color.colorAccent));
                                mySnackbar.setAction("¿Desea activar la alarma?", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        marker.setIcon(BitmapDescriptorFactory
                                                .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)); // ICONO ALARMA
                                        paradasConAlarmas.put(marker.getId(), marker);
                                    }
                                });
                            } else {
                                mySnackbar = Snackbar.make(rootView,
                                        "", Snackbar.LENGTH_SHORT);
                                mySnackbar.setActionTextColor(getResources().getColor(R.color.colorWarning));
                                mySnackbar.setAction("¿Desea desactivar la alarma?", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        marker.setIcon(BitmapDescriptorFactory
                                                .defaultMarker(BitmapDescriptorFactory.HUE_RED)); // ICONO COMUN
                                        paradasConAlarmas.remove(marker.getId());
                                    }
                                });
                            }
                            mySnackbar.show();
                        }
                        return false;
                    }
                });
                googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        if (marker.getTitle().contains(getString(R.string.linea))) {
                            mensaje("2");
                        } else if (marker.getTitle().contains("Servicio")) {
                            mensaje("3");
                        }
                    }
                });


                // prueba de alarma :) (solo deberia sonar)
                // despues hacer distancia de ( ubicacion y paradaCercana) < distancia predefinida.
                googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                    @Override
                    public boolean onMyLocationButtonClick() {
                        new Alarma(getContext(), paradasConAlarmas).run();
                        return false;
                    }
                });

                googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(LatLng latLng) {
                        googleMap.addMarker(new MarkerOptions()
                                .title("Inicio")
                                .position(latLng)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                    }
                });

                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                        new CameraPosition.Builder().target(new LatLng(-34.669997, -58.563181)).zoom(13).build()));

                loadRoutes();

            }
        });

        Dibujar d = new Dibujar(googleMap, getContext(), mLinea);
        d.consumirPosicion();
        return rootView;
    }


    void mensaje(String msj) {
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

    @Deprecated
    public void drawRoute(LineaDemo l) {
        for (Punto punto : l.getRecorrido()) {
            if (punto.isParada())
                googleMap.addMarker(new MarkerOptions()
                        .position(punto.getLatLng())
                        .icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_RED))
                        .title(l.getLinea())
                        .snippet(l.getRamal()));
        }
        googleMap.addPolyline(l.getPolylineOptions());
    }

    public void updateDrawingRoutes() {
        for (Linea l : mLinea) {
            for (Ramal r : l.getRamales())
                if (r.isCheck())
                    r.getDibujo().show();
                else
                    r.getDibujo().hide();

        }
    }

    public void loadRoutes() {

        for (Linea l : mLinea) {
            for (Ramal r : l.getRamales()) {
                Polyline p = googleMap.addPolyline(new PolylineOptions().addAll(PolyUtil.decode(r.getCode_recorrido())));
                r.getDibujo().setPolyline(p);

                for (LatLng parada : r.getParadas()) {
                    Marker mk = googleMap.addMarker(new MarkerOptions()
                            .position(parada)
                            .icon(BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_RED))
                            .title("Parada de la linea" + l.getLinea())
                            .snippet("Ramal: " + r.getDescripcion()));
                    r.getDibujo().agregarParada(mk);
                }

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
            }
        }

        updateDrawingRoutes();
    }

    public ArrayList<Linea> getmLinea() {
        return mLinea;
    }

    public void setLineas(ArrayList<Linea> mLinea, ArrayList<Linea> lineas_seleccionadas) {
        this.mLinea = mLinea;
        this.lineas_seleccionadas = lineas_seleccionadas;
    }

}
