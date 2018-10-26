package com.grupo214.usuario.fragment;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.SphericalUtil;
import com.grupo214.usuario.Dialog.DialogoParadaOnInfo;
import com.grupo214.usuario.R;
import com.grupo214.usuario.adapters.TiempoEstimadoAdapter;
import com.grupo214.usuario.connserver.DondeEstaMiBondi;
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
    public static final int ZOOM = 14;
    private static final String TAG = "MapFragment";
    private static final String TITLE_USER_MAKER = "Punto de partida";
    private SwitchCompat switchAcc;
    private HashMap<String, LatLng> paradasConAlarmas;
    private HashMap<String, Ramal> ramalesSeleccionados;
    private Dialog startMenuDialog;
    private DondeEstaMiBondi dondeEstaMiBondi;
    private boolean dondeEstaMiBondiActive;
    private MapView mMapView;
    private GoogleMap googleMap;
    private ArrayList<Linea> mLinea;
    private Marker startMakerUser;
    private boolean selecionar = false; // por si las moscas.
    private HashMap<String, Marker> markerCercanos;
    private HashMap<String, ParadaAlarma> paradasCercanas;
    private ListView lv_listTiempoEstimado;
    private TiempoEstimadoAdapter adaptador;
    private HashMap<String, Servicio> serviciosActivos;
    private LocationManager locationManager;
    private GoogleMap.InfoWindowAdapter infoWindowAdapterParadas;
    private Boolean visible = false;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        mMapView = (MapView) rootView.findViewById(R.id.mapView);


        markerCercanos = new HashMap<>();
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
                dondeEstaMiBondi = new DondeEstaMiBondi(googleMap, getContext(), ramalesSeleccionados, adaptador, serviciosActivos);

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
                            return false; // quizas mueva todo aca() AHRE LOCO CUANDO ESCRIBO "TODO" CAMBIA DE COLOR
                        }
                        if (marker.getTitle().contains(TITLE_USER_MAKER)) {
                            return false;
                        }
                        return false;
                    }
                });

                googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                    @Override
                    public void onCameraIdle() {
                        actulizarMarkers();
                    }
                });

                googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        if (marker.getTitle() == null)
                            return;
                        if (marker.getTitle().contains("Parada")) {
                            DialogoParadaOnInfo dialogoParadaOnInfo = new DialogoParadaOnInfo();
                            dialogoParadaOnInfo.setParams(marker, paradasConAlarmas, markerCercanos);
                            dialogoParadaOnInfo.show(getFragmentManager(), "Parada");
                            // setear un adapter info para paradas si esta activado el swicht mostrar el boton de accesibliidad.
                        }
                        if (marker.getTitle().contains(TITLE_USER_MAKER)) {
                            startMakerUser.setVisible(false);
                            selecionar = true;
                            mensaje("Seleccioné una nueva ubicación para esperar el colectivo.");
                        }

                    }
                });


                // prueba de notificationBus :) (solo deberia sonar)
                // despues hacer distancia de ( ubicacion y paradaCercana) < distancia predefinida.
                googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                    @Override
                    public boolean onMyLocationButtonClick() {
                        // Dialog que te haga habilitar el mapa.-
                        return false;
                    }
                });

       /*         googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                    @Override
                    public void onMarkerDragStart(Marker marker) {
                        dondeEstaMiBondi.stop();
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
        */
                final LatLng pos = getLastKnownLocation();

                if (pos != null)
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder().target(pos).zoom(15).build()));
                else
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder().target(posInicial).zoom(12).build()));

                MarkerOptions markerOptions = new MarkerOptions()
                        .visible(false)
                        .position(new LatLng(0, 0))
                        .title(TITLE_USER_MAKER)
                        .snippet("Haga click para cambiar la posición.")
                        //.draggable(true)
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_user_map_demo))
                        .anchor(0.5f, 0.5f);

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

                //loadRoutes();
                googleMap.setInfoWindowAdapter(infoWindowAdapterParadas);
                updateDrawingRoutes();

                dialogDondeEstaMiBondi();
                switchAcc = (SwitchCompat) getActivity().findViewById(R.id.accesibilidad_Switch);

                switchAcc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (puntoPartida != null)
                            dondeEstaMiBondi(puntoPartida);
                    }
                });
            }
        });

        return rootView;
    }

    private void actulizarMarkers() {
        float zoom = googleMap.getCameraPosition().zoom;

        if (!visible && zoom < ZOOM) {
            for (Linea l : mLinea) {
                for (Ramal r : l.getRamales()) {
                    if (r.isCheck()) {
                        r.getDibujo().setAlpah(false);
                        if (markerCercanos.get(r.getIdRamal()) != null)
                            markerCercanos.get(r.getIdRamal()).setVisible(true);
                    }
                }
            }
            visible = !visible;
        } else if (visible && zoom > ZOOM) {
            for (Linea l : mLinea) {
                for (Ramal r : l.getRamales()) {
                    if (r.isCheck())
                        r.getDibujo().setAlpah(true);
                }
            }
            visible = !visible;
        }

    }

    public void dondeEstaMiBondi(LatLng latLng) {

        for (Ramal r : ramalesSeleccionados.values()) {
            if (markerCercanos.get(r.getIdRamal()) != null)
                markerCercanos.get(r.getIdRamal())
                        .setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_parada_bondi));
        }
        actulizarMarkers();
        markerCercanos.clear();
        paradasCercanas.clear();

        BitmapDescriptor icoMakerParadaCercana;
        if (switchAcc.isChecked())
            icoMakerParadaCercana = BitmapDescriptorFactory.fromResource(R.mipmap.ic_parada_acc);
        else
            icoMakerParadaCercana = BitmapDescriptorFactory.fromResource(R.mipmap.ic_parada_cercana);

        for (Ramal r : ramalesSeleccionados.values()) {
            Marker mk = r.paradaMasCercana(latLng);
            markerCercanos.put(r.getIdRamal(), mk);
            paradasCercanas.put(r.getIdRamal(), ((ParadaAlarma) mk.getTag()));
            mk.setVisible(true);
            r.setParadaCercana(((ParadaAlarma) mk.getTag()).getId_parada());
            mk.setIcon(icoMakerParadaCercana);
            mk.showInfoWindow();
            if (paradasConAlarmas.get(mk.getId()) != null)
                paradasConAlarmas.remove(mk.getId());
        }
        if (!dondeEstaMiBondiActive) {
            dondeEstaMiBondi.setSwitchAcc(switchAcc);
            if (switchAcc.isChecked())
                dondeEstaMiBondi.setParadasCercanas(paradasCercanas);
            dondeEstaMiBondi.run();
            dondeEstaMiBondiActive = true;
        } else {
            if (switchAcc.isChecked())
                dondeEstaMiBondi.setParadasCercanas(paradasCercanas);
            dondeEstaMiBondi.reiniciar();
        }
    }

    void mensaje(String msj) {
        Toast toast = Toast.makeText(getContext(), msj, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
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
                    cargarRamal(r);

        }
    }

    @Deprecated
    public void loadRoutes() {
        for (Linea l : mLinea) {
            for (Ramal r : l.getRamales()) {
                cargarRamal(r);
            }
        }
    }

    public boolean cargarRamal(Ramal r) {
        List<LatLng> points = PolyUtil.decode(r.getCode_recorrido());
        int salto = (points.size() / 10) > 1 ? 10 : 1;
        for (int i = 1; i < points.size() - 1; i = i + salto) {
            float headingRotation = (float) SphericalUtil.computeHeading(points.get(i), points.get(i + 1));
            Marker mk = googleMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_arrow_up))
                    .flat(true)
                    .anchor(0.5f, 0.5f)
                    .position(points.get(i))
                    .rotation(headingRotation)
            );
            r.getDibujo().addArrows(mk);
        }
        Polyline p = googleMap.addPolyline(new PolylineOptions()
                .color(r.getColor())
                .geodesic(true)
                .addAll(points));
        r.getDibujo().setPolyline(p);

        if (ramalesSeleccionados.get(r.getIdRamal()) != null)
            ramalesSeleccionados.get(r.getIdRamal()).setDibujo(r.getDibujo());

        for (Parada parada : r.getParadas()) {
            Marker mk = googleMap.addMarker(new MarkerOptions()
                    .position(parada.getLatLng())
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_parada_bondi))
                    .alpha(0.9f)
                    .title("Parada línea " + r.getLinea())
                    .anchor(0.5f, 0.5f)
                    .snippet("Ramal: " + r.getDescripcion()));
            r.getDibujo().agregarParada(mk);
            ParadaAlarma paradaAlarma = new ParadaAlarma(parada.getIdParda(), r.getIdLinea(), r.getIdRamal(), parada.getLatLng());
            mk.setTag(paradaAlarma);
        }


        boolean alternativo = false;
        for (Recorrido recorridoAlterno : r.getRecorridosAlternos()) {
            alternativo = true;
            Log.d("MapFragment", "Recorrido alterno: " + recorridoAlterno.getRecorridoCompleto());
            Polyline pa = googleMap.addPolyline(new PolylineOptions()
                    .color(Color.RED)
                    .addAll(
                            PolyUtil.decode(recorridoAlterno.getRecorridoCompleto())));
            r.getDibujo().addPolylineAlternative(pa);
            for (Parada parada : recorridoAlterno.getParadas()) {
                Marker mk = googleMap.addMarker(new MarkerOptions()
                        .position(parada.getLatLng())
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_parada_alterna))
                        .alpha(0.9f)
                        .title("Parada alterna - Línea " + r.getLinea())
                        .anchor(0.5f, 0.5f)
                        .snippet("Ramal: " + r.getDescripcion()));
                r.getDibujo().addParadasAlternas(mk);
                ParadaAlarma paradaAlarma = new ParadaAlarma(parada.getIdParda(), r.getIdLinea(), r.getIdRamal(), parada.getLatLng());
                mk.setTag(paradaAlarma);
            }
        }
        return alternativo;
    }

    public void setLineas(ArrayList<Linea> mLinea, HashMap<String, Ramal> ramalesSeleccionados) {
        this.mLinea = mLinea;
        this.ramalesSeleccionados = ramalesSeleccionados;
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
                puntoPartida = getLastKnownLocation();
                if (puntoPartida != null)
                    dondeEstaMiBondi(puntoPartida);

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

    /*
    void test() {
        //       dondeEstaMiBondi(posInicial);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getContext(), "No se han concedidos los permisos del GPS.", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            puntoPartida = new LatLng(location.getLatitude(), location.getLongitude());
                        }
                    }
                });
    }*/

    private LatLng getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
            return null;
        } else {
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                requerirGPS();
                return null;
            }
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
        if (bestLocation != null)
            return new LatLng(bestLocation.getLatitude(), bestLocation.getLongitude());
        else
            return null;
    }

    private void requerirGPS() {
        final Activity activity = getActivity();
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                activity);
        alertDialogBuilder
                .setMessage("GPS está deshabilitado en su dispositivo. ¿Desea habilitarlo?")
                .setCancelable(false)
                .setPositiveButton("Activar GPS",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                activity.startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }


    public void setStartMenuDialog(Dialog startMenuDialog) {
        this.startMenuDialog = startMenuDialog;
    }


    public void camare(final LatLng punto) {
        getmMapView().getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                        new CameraPosition.Builder().target(punto)
                                .zoom(15)
                                .tilt(0)
                                .build()));
            }
        });
    }

    public boolean isDondeEstaMiBondiActive() {
        return dondeEstaMiBondiActive;
    }
}

