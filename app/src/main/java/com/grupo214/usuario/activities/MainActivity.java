package com.grupo214.usuario.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.grupo214.usuario.R;
import com.grupo214.usuario.SettingsActivity;
import com.grupo214.usuario.connserver.ConnectServer;
import com.grupo214.usuario.adapters.SectionsPageAdapter;
import com.grupo214.usuario.alarma.Alarma;
import com.grupo214.usuario.fragment.LineasFragment;
import com.grupo214.usuario.fragment.MapFragment;
import com.grupo214.usuario.objects.Linea;

import java.util.ArrayList;

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
    public final static int TAB_INICIO = 2; // esto despues tengo que revisar.

    /**
     * Constante con el link hacia la pagina de tarifas del gobierno
     */
    private final static String LINK_TARIFAS
            = "https://www.argentina.gob.ar/redsube/tarifas-de-transporte-publico-amba-2018";

    /**
     * Constante con el link hacia la pagina SUBE
     */
    private final static String LINK_SUBE = "https://www.argentina.gob.ar/sube";
    /**
     * Variable que contiene todas las lineas traidas desde el servidor o que esten guardadas
     * en el telefono (si no hay actualizacion)
     */
    private ArrayList<Linea> mLineas;
    private SectionsPageAdapter mSectionsPageAdapter;
    private MapFragment mapFragment;
    private LineasFragment lineasFragment;
    private TabLayout tabLayout;
    private int flagg = 0;
    private Alarma alarma;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alarma = new Alarma(this);

        //Seteo de variables:
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // final AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        final ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        cargarLineas();

        //Set Tab:
        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());
        mSectionsPageAdapter.addFragment(lineasFragment, "Lineas");   // 0
        mSectionsPageAdapter.addFragment(mapFragment, "Mapa");        // 1
        mViewPager.setAdapter(mSectionsPageAdapter);
        mViewPager.setCurrentItem(TAB_MAPA); // para que inicie la tab de Mapas
        tabLayout.setupWithViewPager(mViewPager);


        tabLayout.getTabAt(TAB_LINEA).setIcon(R.drawable.ic_directions_bus_unselect_24dp);
        tabLayout.getTabAt(TAB_MAPA).setIcon(R.drawable.ic_map_unselect_24dp);
        int tabIconColor = ContextCompat.getColor(getApplicationContext(), R.color.tabSelect);
        tabLayout.getTabAt(TAB_MAPA).getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);

        // Acciones al tocar las pestañas // estetica.
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int tabIconColor = ContextCompat.getColor(getApplicationContext(), R.color.tabSelect);
                tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int tabIconColor = ContextCompat.getColor(getApplicationContext(), R.color.tabUnSelect);
                tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case TAB_MAPA:
                        lineasFragment.setmLineas(mLineas);
                      //  mensaje(alarma.getParametro());
                        /*

                        if (flagg == 0) {
                            mapFragment.loadRoutes();
                            flagg++;
                        } else
                            mapFragment.updateDrawingRoutes();
                        */
                        break;
                    case TAB_LINEA:
                        break;
                }
            }
        });

        //Barra superior
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
        mLineas = new ArrayList<>();
        new ConnectServer(this,mLineas).execute();

        mapFragment = new MapFragment();
        lineasFragment = new LineasFragment();
        lineasFragment.setmLineas(mLineas);
        lineasFragment.setTabLayout(tabLayout);

        mapFragment.setmLinea(mLineas);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //  mensaje("Guardar en base de datos SQL");
            Thread.interrupted();
            super.onBackPressed();
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

            case R.id.nav_ajustes:
                mensaje("Ajustes");
                startActivity(new Intent(this, SettingsActivity.class));
                break;

            case R.id.nav_comentario:
                startActivity(new Intent(this, CommentaryActivity.class));
                break;

            case R.id.nav_acc:
                mensaje("Solicitar Accecibilidad");
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