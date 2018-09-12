package com.grupo214.usuario.fragment;


import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.grupo214.usuario.Dialog.DialogoAccesibilidad;
import com.grupo214.usuario.Dialog.DialogoAlarma;
import com.grupo214.usuario.R;
import com.grupo214.usuario.activities.MainActivity;
import com.grupo214.usuario.adapters.TiempoEstimadoAdapter;
import com.grupo214.usuario.alarma.Alarma;
import com.grupo214.usuario.connserver.Dibujar;
import com.grupo214.usuario.objects.Linea;
import com.grupo214.usuario.objects.Parada;
import com.grupo214.usuario.objects.Ramal;
import com.grupo214.usuario.objects.Servicio;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.Context.LOCATION_SERVICE;
import static com.grupo214.usuario.activities.MainActivity.puntoPartida;


/**
 * Clase gestiona la pestaña con el mapa.
 *
 * @author Daniel Boullon
 */
public class MapFragment extends Fragment {

    private HashMap<String, LatLng> paradasConAlarmas;
    private HashMap<String, Ramal> ramalesSeleccionados;
    private Dialog startMenuDialog;
    private Dibujar dibujar;
    private MapView mMapView;
    private GoogleMap googleMap;
    private ArrayList<Linea> mLinea;

    private Marker startMakerUser;
    private boolean selecionar = false; // por si las moscas.
    private HashMap<String, Marker> paradasCercanas;
    private ArrayList<String> paradasCercanasId;
    private ArrayList<String> paradasAccId;
    private boolean dondeEstaMiBondi = false;
    private ListView lv_listTiempoEstimado;
    private TiempoEstimadoAdapter adaptador;
    private boolean accesibilidad = false;
    private boolean alarmaDestino = false;
    private HashMap<String, Servicio> servicios;
    private LocationManager locationManager;
    private LatLng posInicial;
    private TextView bottomSheetTextView;
    private Alarma alarma;


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        paradasAccId = new ArrayList<String>();
        paradasCercanasId = new ArrayList<String>();
        paradasCercanas = new HashMap<>();
        paradasConAlarmas = new HashMap<>();
        servicios = new HashMap<>();
        adaptador = new TiempoEstimadoAdapter(getContext(), android.R.layout.simple_list_item_2);

        locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);

        lv_listTiempoEstimado = (ListView) rootView.findViewById(R.id.listaTiempoEstimado);
        adaptador.setLv(lv_listTiempoEstimado);
        lv_listTiempoEstimado.setAdapter(adaptador);

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
                adaptador.setGoogleMap(mMap);

                // Probar esto.
                dibujar = new Dibujar(googleMap, getContext(), mLinea, ramalesSeleccionados, paradasCercanas, adaptador, servicios);

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
                        if (marker.getTitle().contains("Parada")) {
                            if (accesibilidad) {
                                DialogoAccesibilidad dialogoAccesibilidad = new DialogoAccesibilidad();
                                dialogoAccesibilidad.setParams(marker, paradasAccId);
                                dialogoAccesibilidad.show(getFragmentManager(), "Dialog Accesibilidad");
                                accesibilidad = false;
                                return true;
                            }
                            if (alarmaDestino) {
                                if (!paradasCercanasId.contains(marker.getId())) {
                                    DialogoAlarma dialogoAlarma = new DialogoAlarma();
                                    dialogoAlarma.setParams(marker, paradasConAlarmas);
                                    dialogoAlarma.show(getFragmentManager(), "Dialog Alarma");
                                } else {
                                    Toast.makeText(getContext(), "No permitido alarma en parada cercana", Toast.LENGTH_SHORT).show();
                                }
                                alarmaDestino = false;
                                return true;
                            }
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

                        return false;
                    }
                });

                googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                    @Override
                    public void onMarkerDragStart(Marker marker) {
                        //stop() <->
                    }

                    @Override
                    public void onMarkerDrag(Marker marker) {

                    }

                    @Override
                    public void onMarkerDragEnd(Marker marker) {
                        puntoPartida = marker.getPosition();
                        dondeEstaMiBondi(puntoPartida);
                    }
                });

                Location location = location();
                posInicial = new LatLng(-34.669997, -58.563181);

                if (location != null)
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder().target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(15).build()));
                else
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder().target(posInicial).zoom(12).build()));

                MarkerOptions markerOptions = new MarkerOptions()
                        .visible(false)
                        .position(new LatLng(0, 0))
                        .title("Punto de partida")
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_person_pin))
                        .draggable(true);

                startMakerUser = googleMap.addMarker(markerOptions);
                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        if (selecionar) {
                            if (!startMakerUser.isVisible())
                                startMakerUser.setVisible(true);
                            startMakerUser.setPosition(latLng);
                            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                                    new CameraPosition.Builder().target(latLng).zoom(15).build()));
                            puntoPartida = latLng;
                            dondeEstaMiBondi(puntoPartida);
                            selecionar = false;
                        }
                    }
                });

                loadRoutes();
                dialogDondeEstaMiBondi();

            }
        });


        cargarMenuInferior(rootView);


        return rootView;
    }

    private void cargarMenuInferior(View rootView) {

    }


    public void dondeEstaMiBondi(LatLng latLng) {

        for (Marker mk : paradasCercanas.values()) {
            mk.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_parada_bondi));
        }
        paradasCercanas.clear();
        paradasCercanasId = new ArrayList<String>();

        for (Ramal r : ramalesSeleccionados.values()) {
            Log.d("MapFragment", "ramal numero: " + r.toString());
            Marker mk = r.paradaMasCercana(latLng);
            paradasCercanas.put(r.getIdRamal(), mk);
            paradasCercanasId.add(mk.getId());
            mk.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_parada_cercana));
            mk.showInfoWindow();
            if (paradasConAlarmas.get(mk.getId()) != null)
                paradasConAlarmas.remove(mk.getId());
        }
        if (!dondeEstaMiBondi) {
            dibujar.run();
            dondeEstaMiBondi = true;
        } else {
            dibujar.reiniciar();
        }

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

                for (Parada parada : r.getParadas()) {
                    Marker mk = googleMap.addMarker(new MarkerOptions()
                            .position(parada.getLatLng())
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_parada_bondi))
                            .title("Parada de la linea " + l.getLinea())
                            .anchor(0.5f, 0.5f)
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

    public void setLineas(ArrayList<Linea> mLinea, HashMap<String, Ramal> ramales_seleccionados) {
        this.mLinea = mLinea;
        this.ramalesSeleccionados = ramales_seleccionados;
    }

    public void dialogDondeEstaMiBondi() {

        //deshabilitamos el título por defecto
        startMenuDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        startMenuDialog.setCancelable(true);
        //establecemos el contenido de nuestro dialog
        startMenuDialog.setContentView(R.layout.start_menu_route);

        ((Button) startMenuDialog.findViewById(R.id.bt_ubicacion)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                startMenuDialog.dismiss();
                Toast.makeText(getContext(), "Ubicacion", Toast.LENGTH_SHORT).show();
                Location location = location();
                if (location != null) {
                    puntoPartida = new LatLng(location.getLatitude(), location.getLongitude());
                    dondeEstaMiBondi(puntoPartida);
                } else {
                    Toast.makeText(getContext(), "GPS error (cambiar esto anita)", Toast.LENGTH_SHORT).show();
                    puntoPartida = posInicial;
                    dondeEstaMiBondi(puntoPartida); // hardCode aca no debo hacer esto sino avisar que no se puede geolocalizar al usuario.
                }

            }
        });

        ((Button) startMenuDialog.findViewById(R.id.bt_loc_map)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                startMenuDialog.dismiss();
                mensaje("Seleciona el punto de partida en el mapa");
                selecionar = true;
            }
        });

    }

    private Location location() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            mensaje("Amigo no tenes permisos.");
        } else {
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))

                mensaje("No tenes ni el GPS ni el Internet Rata");

            return null;

        }
        return locationManager
                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }


    public void setAccesibilidad(boolean accesibilidad) {
        mensaje("Selecione la parada donde solicite accesibilidad");
        this.accesibilidad = accesibilidad;

    }

    public void setStartMenuDialog(Dialog startMenuDialog) {
        this.startMenuDialog = startMenuDialog;
    }

    public void setAlarmaDestino(boolean alarmaDestino) {
        this.alarmaDestino = alarmaDestino;
    }

}
