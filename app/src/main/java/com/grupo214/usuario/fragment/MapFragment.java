package com.grupo214.usuario.fragment;


import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import com.grupo214.usuario.Dialog.DialogoParadaOnInfo;
import com.grupo214.usuario.R;
import com.grupo214.usuario.activities.MainActivity;
import com.grupo214.usuario.adapters.TiempoEstimadoAdapter;
import com.grupo214.usuario.alarma.NotificationBus;
import com.grupo214.usuario.connserver.Dibujar;
import com.grupo214.usuario.objects.Linea;
import com.grupo214.usuario.objects.Parada;
import com.grupo214.usuario.objects.ParadaAlarma;
import com.grupo214.usuario.objects.Ramal;
import com.grupo214.usuario.objects.Recorrido;
import com.grupo214.usuario.objects.Servicio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.LOCATION_SERVICE;
import static com.grupo214.usuario.activities.MainActivity.puntoPartida;


/**
 * Clase gestiona la pestaña con el mapa.
 *
 * @author Daniel Boullon
 */
public class MapFragment extends Fragment {

    public static final LatLng posInicial = new LatLng(-34.681496, -58.559774);
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
    private HashMap<String, Servicio> serviciosActivos;
    private LocationManager locationManager;
    private TextView bottomSheetTextView;
    private NotificationBus notificationBus;
    private GoogleMap.InfoWindowAdapter infoWindowAdapterParadas;

    public GoogleMap getGoogleMap() {
        return googleMap;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        paradasAccId = new ArrayList<String>();
        paradasCercanasId = new ArrayList<String>();
        paradasCercanas = new HashMap<>();
        paradasConAlarmas = new HashMap<>();
        serviciosActivos = new HashMap<>();
        adaptador = new TiempoEstimadoAdapter(getContext(), android.R.layout.simple_list_item_2, (TextView) rootView.findViewById(R.id.tx_servicio_back));
        locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);
        infoWindowAdapterParadas = new GoogleMap.InfoWindowAdapter() {
            public View getInfoWindow(Marker marker) {
                return null;
            }

            public View getInfoContents(Marker marker) {
                View view = getLayoutInflater().inflate(R.layout.container_info_windows, null);
                ((TextView) view.findViewById(R.id.list_text_linea)).setText(marker.getTitle());
                ((TextView) view.findViewById(R.id.list_text_ramal)).setText(marker.getSnippet());
                return view;
            }
        };
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
                dibujar = new Dibujar(googleMap, getContext(), mLinea, ramalesSeleccionados, paradasCercanas, adaptador, serviciosActivos);

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
                        if (marker.getTitle() == null)
                            return true;

                        if (marker.getTitle().contains("Parada")) {
                            return false; // quizas mueva todo aca() AHRE LOCO TODO CAMBIA DE COLOR
                        }
                        return false;
                    }
                });


                googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        if (marker.getTitle() == null)
                            return;
                        if (marker.getTitle().contains("Parada")) {
                            DialogoParadaOnInfo dialogoParadaOnInfo = new DialogoParadaOnInfo();
                            dialogoParadaOnInfo.setParams(marker, paradasAccId, paradasConAlarmas, paradasCercanasId);
                            dialogoParadaOnInfo.show(getFragmentManager(), "Parada");
                            // setear un adapter info para paradas si esta activado el swicht mostrar el boton de accesibliidad.
                        }
                        if (marker.getTitle().contains("Servicio")) {
                            // setar un adapter info para servicio, asi puede activar las alarmas de ese ramal con ese servicio.
                        }


                    }
                });


                // prueba de notificationBus :) (solo deberia sonar)
                // despues hacer distancia de ( ubicacion y paradaCercana) < distancia predefinida.
                googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                    @Override
                    public boolean onMyLocationButtonClick() {
                        if (MainActivity.DEMO) {
                            dibujar.DEMO();
                            return true;
                        }
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

                Location location = getLastKnownLocation();

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
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_user_marker))
                        .flat(true)
                        .anchor(0.5f, 0.5f)
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
            //mk.showInfoWindow();
            if (paradasConAlarmas.get(mk.getId()) != null)
                paradasConAlarmas.remove(mk.getId());
        }
        if (MainActivity.DEMO) {
            dibujar.DEMO();
            return;
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
                Polyline p = googleMap.addPolyline(new PolylineOptions()
                        .color(R.color.tabSelect)
                        .addAll(PolyUtil.decode(r.getCode_recorrido())));
                r.getDibujo().setPolyline(p);

                for (Parada parada : r.getParadas()) {
                    Marker mk = googleMap.addMarker(new MarkerOptions()
                            .position(parada.getLatLng())
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_parada_bondi))
                            .alpha(0.9f)
                            .title("Parada línea " + l.getLinea())
                            .anchor(0.5f, 0.5f)
                            .snippet("Ramal: " + r.getDescripcion()));
                    r.getDibujo().agregarParada(mk);
                    ParadaAlarma paradaAlarma = new ParadaAlarma(parada.getIdParda(), l.getLinea(), r.getDescripcion(),parada.getLatLng());
                    mk.setTag(paradaAlarma);
                }

                for (Recorrido recorridoAlterno : r.getRecorridosAlternos()) {
                    Log.d("MapFragment", "Recorrido alterno: " + recorridoAlterno.getRecorridoCompleto());
                    Polyline pa = googleMap.addPolyline(new PolylineOptions()
                            .color(Color.RED)
                            .addAll(
                                    PolyUtil.decode(recorridoAlterno.getRecorridoCompleto())));
                    r.getDibujo().addPolylineAlternative(pa);
                    for (Parada parada : recorridoAlterno.getParadas()) {
                        Marker mk = googleMap.addMarker(new MarkerOptions()
                                .position(parada.getLatLng())
                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_parada_bondi))
                                .alpha(0.9f)
                                .title("Parada línea " + l.getLinea())
                                .anchor(0.5f, 0.5f)
                                .snippet("Ramal: " + r.getDescripcion()));
                        r.getDibujo().addParadasAlternas(mk);

                    }
                }
                googleMap.setInfoWindowAdapter(infoWindowAdapterParadas);
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
        //    startMenuDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        startMenuDialog.setTitle("Selecciona el punto de partida");
        startMenuDialog.setCancelable(true);
        //establecemos el contenido de nuestro dialog
        startMenuDialog.setContentView(R.layout.start_menu_route);

        ((Button) startMenuDialog.findViewById(R.id.bt_ubicacion)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                startMenuDialog.dismiss();
                Toast.makeText(getContext(), "Ubicacion", Toast.LENGTH_SHORT).show();

                //puntoPartida = new LatLng(location.getLatitude(), location.getLongitude());
                dondeEstaMiBondi(posInicial);

                /*
                if (location != null) {
                    puntoPartida = new LatLng(location.getLatitude(), location.getLongitude());
                    dondeEstaMiBondi(puntoPartida);
                } else {
                    Toast.makeText(getContext(), "GPS error (cambiar esto anita)", Toast.LENGTH_SHORT).show();
                    puntoPartida = posInicial;
                    dondeEstaMiBondi(puntoPartida); // hardCode aca no debo hacer esto sino avisar que no se puede geolocalizar al usuario.
                }*/

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


    //   Location myLocation = getLastKnownLocation();

    private Location getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            mensaje("Amigo no tenes permisos.");
        } else {
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
                mensaje("Sin permisos");
            return null;
        }
        locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }

        return bestLocation;
    }

    public void camare(final LatLng punto) {
        getmMapView().getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                        new CameraPosition.Builder().target(punto).zoom(15).build()));

            }
        });
    }
}
