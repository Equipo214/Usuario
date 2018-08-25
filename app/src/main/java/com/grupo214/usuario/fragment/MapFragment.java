package com.grupo214.usuario.fragment;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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
import com.grupo214.usuario.connserver.Dibujar;
import com.grupo214.usuario.objects.Linea;
import com.grupo214.usuario.objects.LineaDemo;
import com.grupo214.usuario.objects.Punto;
import com.grupo214.usuario.objects.Ramal;
import com.grupo214.usuario.objects.Recorrido;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Clase gestiona la pestaña con el mapa.
 *
 * @author Daniel Boullon
 */
public class MapFragment extends Fragment {

    private MapView mMapView;
    private GoogleMap googleMap;
    private ArrayList<Recorrido> recorridos;
    private ArrayList<Linea> mLinea;
    private byte times = 0;
    private int contarClicks = 0;
    private Marker userMarkerStart;
    private Marker userMarkerDestiny;
    private Marker mkInicio;
    private Marker mkDestino;


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_map, container, false);
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
                    public boolean onMarkerClick(final Marker marker) {
                        //ONMARKERCLICK
                        Snackbar mySnackbar = Snackbar.make(rootView,
                                "wachin", Snackbar.LENGTH_SHORT);
                        mySnackbar.setAction("Activar Alarma", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                marker.setIcon(BitmapDescriptorFactory
                                        .defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                                marker.setFlat(false);
                            }
                        });
                        mySnackbar.show();


                        //       desplegarMenuAlarma(marker);
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


                googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(LatLng latLng) {

                        if (++contarClicks == 1) {
                            mkInicio = googleMap.addMarker(new MarkerOptions()
                                    .title("Inicio")
                                    .position(latLng)
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                        } else if (contarClicks == 2) {
                            mkDestino = googleMap.addMarker(new MarkerOptions()
                                    .title("Destino")
                                    .position(latLng)
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                            calcularTiempo(mkInicio,mkDestino);
                        } else if (contarClicks == 3) {
                            contarClicks = 0;
                        }

                    }
                });


                /*
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
                            for (Linea l : mLinea) {
                                for (Ramal r : l.getRamales()) {
                                    if (r.isCheck()) {
                                        flag = true;
                                    }
                                }
                            }
                            if (!flag)
                                mensaje("marca alguna gil");
                        }
                    }
                }); */

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


                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                        new CameraPosition.Builder().target(new LatLng(-34.669997, -58.563181)).zoom(13).build()));

                loadRoutes();

            }
        });

        /*bt_animar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Marker mk = googleMap.addMarker(new MarkerOptions()
                        .position(mLineaDemos.get(3).getNextPointDemo())
                        .title("Servicio " + mLineaDemos.get(3).getLinea())
                        .snippet(mLineaDemos.get(3).getDescripcion()));
                Dibujar dibujar = new Dibujar(googleMap, mLineaDemos.get(3), getContext(), mk);
                dibujar.execute();
            }
        });*/


        /*

        bt_demo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (LineaDemo l : mLineaDemos) {
                    if (l.isCheck()) {
                        new DibujarDemo(googleMap, l,true).ejecutar();
                        new DibujarDemo(googleMap, l,false).ejecutar();
                    }
                }
            }
        });
            */

        Dibujar d = new Dibujar(googleMap, getContext());
        d.consumirPosicion(); //<- cambiar nombre y pasar por parametro la array de lineas y ramales. _:)
        return rootView;
    }

    private void calcularTiempo(Marker mkInicio, final Marker mkDestino) {

        String outputFormat = "origins=" +mkInicio.getPosition().latitude +"," + mkInicio.getPosition().longitude+
                "&destinations="+ mkDestino.getPosition().latitude +"," + mkDestino.getPosition().longitude;
        String parameters = ""; // añadir ?
        String url = "https://maps.googleapis.com/maps/api/distancematrix/json?"+outputFormat+parameters + "&key=" + getString(R.string.google_maps_key);
        Log.d("MapFragment URL: ", url);

        final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray rows = response.optJSONArray("rows");
                        try {
                            mkDestino.setSnippet(rows.getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("duration").getString("text"));
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
        Volley.newRequestQueue(getContext()).add(jsonRequest);

    }

    private void desplegarMenuAlarma(Marker marker) {
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
                            .title("Linea " + l.getLinea())
                            .snippet(r.getDescripcion()));
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

    public void setmLinea(ArrayList<Linea> mLinea) {
        this.mLinea = mLinea;
    }

}
