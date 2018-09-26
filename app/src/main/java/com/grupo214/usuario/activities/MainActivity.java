package com.grupo214.usuario.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.grupo214.usuario.R;
import com.grupo214.usuario.adapters.SectionsPageAdapter;
import com.grupo214.usuario.fragment.LineasFragment;
import com.grupo214.usuario.fragment.MapFragment;
import com.grupo214.usuario.fragment.NotificacionFragment;
import com.grupo214.usuario.objects.Linea;
import com.grupo214.usuario.objects.Ramal;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Clase Main donde se gestiona toda la app.
 * tambien se gestiona el menu lateral,
 * el boton de ubicacion inicial
 * y la interacion de cada pestaña.
 *
 * @author Daniel Boullon
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    /**
     * Constante que representa la pestaña LINEA
     */
    public final static int TAB_LINEA = 0;
    /**
     * Constante que representa la pestaña MAPA
     */
    public final static int TAB_MAPA = 1;
    /**
     * Constante que representa la pestaña ?
     */
    public final static int TAB_NOTIFICACIONES = 2; // esto despues tengo que revisar.

    /**
     * Constante con el link hacia la pagina de tarifas del gobierno
     */
    private final static String LINK_TARIFAS
            = "https://www.argentina.gob.ar/redsube/tarifas-de-transporte-publico-amba-2018";

    /**
     * Constante con el link hacia la pagina SUBE
     */
    private final static String LINK_SUBE = "https://www.argentina.gob.ar/sube";
    public static LatLng puntoPartida;


    public static Boolean DEMO = false;
    /**
     * Variable que contiene todas las lineas traidas desde el servidor o que esten guardadas
     * en el telefono (si no hay actualizacion)
     */
    private ArrayList<Linea> mLineas;
    /**
     *
     *
     */
    private HashMap<String, Ramal> ramales_seleccionados;
    private SectionsPageAdapter mSectionsPageAdapter;
    private MapFragment mapFragment;
    private LineasFragment lineasFragment;
    private NotificacionFragment notificacionFragment;
    private SmartTabLayout tabLayout;
    private ViewPager mViewPager;
    private Dialog startMenuDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Seteo de variables:
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // final AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        tabLayout = (SmartTabLayout) findViewById(R.id.tabs);
        mViewPager = (ViewPager) findViewById(R.id.container);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        cargarLineas();

        //Set Tab:
        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());
        mSectionsPageAdapter.addFragment(lineasFragment, "Lineas");     // 0
        mSectionsPageAdapter.addFragment(mapFragment, "Mapa");          // 1
        mSectionsPageAdapter.addFragment(notificacionFragment, "Notif.");       // 2
        mViewPager.setAdapter(mSectionsPageAdapter);
        mViewPager.setCurrentItem(TAB_LINEA); // para que inicie la tab de Mapas --> temporalmente cambiado.
        tabLayout.setViewPager(mViewPager);
        tabLayout.setOnTabClickListener(new SmartTabLayout.OnTabClickListener() {
            @Override
            public void onTabClicked(int position) {
                if (position == TAB_MAPA)
                    if (puntoPartida != null)
                        mapFragment.dondeEstaMiBondi(puntoPartida);
            }
        });

        //   tabLayout.getTabAt(TAB_LINEA).setPointerIcon(new PointerIcon());
        //   tabLayout.getTabAt(TAB_MAPA).setIcon(R.drawable.ic_map_unselect_24dp);
        //   int tabIconColor = ContextCompat.getColor(getApplicationContext(), R.color.tabSelect);
        //   tabLayout.getTabAt(TAB_MAPA).getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);

        //Barra superior
        toolbar.setNavigationIcon(R.drawable.ic_parada); //cambiar  
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //Menu Lateral:
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

    }



    private void cargarLineas() {
        // con este tema personalizado evitamos los bordes por defecto
        startMenuDialog = new Dialog(this, R.style.Theme_AppCompat_Dialog_Alert);
        mapFragment = new MapFragment();
        lineasFragment = new LineasFragment();
        notificacionFragment = new NotificacionFragment();
        ramales_seleccionados = new HashMap<>();
        mLineas = SplashScreen.mLineas;
        lineasFragment.setStartMenuDialog(startMenuDialog);
        lineasFragment.setLineas(mLineas, ramales_seleccionados);
        lineasFragment.setTabViewPager(mViewPager);
        mapFragment.setStartMenuDialog(startMenuDialog);
        mapFragment.setLineas(mLineas, ramales_seleccionados);
        notificacionFragment.setTabViewPage(mViewPager);
        notificacionFragment.setMapFragment(mapFragment);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //mensaje("Guardar en base de datos SQL");
            //super.onBackPressed();
            if (tabLayout.getTabAt(TAB_MAPA).isSelected())
                mViewPager.setCurrentItem(TAB_LINEA);
            else
                moveTaskToBack(true);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_likSube:
                startActivity(new Intent(this, WebActivity.class)
                        .putExtra("URL", LINK_SUBE));
                break;

            case R.id.nav_linkTarifas:
                startActivity(new Intent(this, WebActivity.class)
                        .putExtra("URL", LINK_TARIFAS));
                break;

            case R.id.nav_destino:
                mapFragment.setAlarmaDestino(true);
                break;

            case R.id.nav_ajustes:
                mensaje("Ajustes");
                startActivity(new Intent(this, SettingsActivity.class));
                break;

            case R.id.nav_comentario:
                startActivity(new Intent(this, CommentaryActivity.class));
                break;

            case R.id.nav_acc:
                Toast.makeText(this, "hola", Toast.LENGTH_LONG).show();
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Muestra un mensaje en pantalla
     *
     * @param msj
     */
    private void mensaje(String msj) {
        Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content)
                , msj, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }


    // primera vez que pide permisos, para que active la location.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mapFragment.getmMapView().getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    mensaje("No tienes permisos para acceder a la ubicacion.");
                    return;
                }
                googleMap.setMyLocationEnabled(true);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Thread.interrupted();
    }

}