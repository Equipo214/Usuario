package com.grupo214.usuario.activities;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ThemedSpinnerAdapter;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.grupo214.usuario.R;
import com.grupo214.usuario.apiGoogleDirection.GoogleMapsDirectionsAPI;
import com.grupo214.usuario.objetos.Linea;
import com.grupo214.usuario.sqlite.ConexionSQLiteHelper;
import com.grupo214.usuario.fragment.LineasFragment;
import com.grupo214.usuario.adapters.SectionsPageAdapter;
import com.grupo214.usuario.fragment.InicioFragment;
import com.grupo214.usuario.fragment.MapFragment;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final static int TAB_INICIO = 0;
    private final static int TAB_MAPA = 1;
    private final static int TAB_LINEA = 2;

    private final static String LINK_TARIFAS = "https://www.argentina.gob.ar/redsube/tarifas-de-transporte-publico-amba-2018";
    private final static String LINK_SUBE = "https://www.argentina.gob.ar/sube";

    private ArrayList<Linea> mLineas;
    private SectionsPageAdapter mSectionsPageAdapter;
    private MapFragment mapFragment;

    private Button btnTEST;
    private LineasFragment lineasFragment;
    private ConexionSQLiteHelper connSQLite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        final AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        final ViewPager mViewPager = findViewById(R.id.container);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);



        appBarLayout.setExpanded(true,true);

        //Levantar Datos
        connSQLite = new ConexionSQLiteHelper(this, "db_lineas", null, 1);

        //Set Tab:
        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        // SI hay cambio true, sino false;
        setupViewPager(mSectionsPageAdapter, mViewPager);

        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case TAB_INICIO:
                        break;
                    case TAB_MAPA:
                        //if hay cambios:
                        mapFragment.updateDrawingRoutes();
                        break;
                    case TAB_LINEA:
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case TAB_INICIO:
                        break;
                    case TAB_MAPA:
                        break;
                    case TAB_LINEA:
                        break;
                }
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case TAB_INICIO:
//                        mapFragment.loadRoutes();
                        break;
                    case TAB_MAPA:
                        break;
                    case TAB_LINEA:
                        break;
                }
            }
        });


        //barra superior
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //Menu Lateral:
        navigationView.setNavigationItemSelectedListener(this);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
                break;

            case R.id.nav_comentario:
                mensaje("Enviar Comentarios");
                break;

            case R.id.nav_acc:
                mensaje("Solicitar Accecibilidad");
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setupViewPager(SectionsPageAdapter adapter, ViewPager viewPager) {

        mapFragment = new MapFragment();
        lineasFragment = new LineasFragment();
        mLineas = loadArray();
        lineasFragment.setmLineas(mLineas);
        mapFragment.setmLineas(mLineas);

        adapter.addFragment(new InicioFragment(), "Inicio");
        adapter.addFragment(mapFragment, "Mapa");
        adapter.addFragment(lineasFragment, "Lineas");

        viewPager.setAdapter(adapter);
    }

    private void mensaje(String msj) {
        Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content)
                , msj, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }


    // primera vez que pide permisos, para que active la location.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content)
                , "El codigo es: " + requestCode, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
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


    //esta funcion debe cargar la lista, este es el lugar final donde quedara
    //Trato de guardar all en SQL LITE.
    private ArrayList<Linea> loadArray() {

        if ( true ){
            mLineas = connSQLite.cargarLineas();
            Log.d("SQLite","cantidad de lineas " +  mLineas.size());
        }else{

        }

        mLineas = Linea.listHardCodeTest();
 //       for (Linea l:mLineas) {
//            connSQLite.insertarLinea(l);
//        }

        /*
        switch (mode) {
            case MODE_HARD:z
                mLineas = Linea.listHardCodeTest();
                break;
            case MODE_SQL:
                // delay hasta que termine
                // mLineas = connSQL.cargarLineas();
                // API GOOGLE CARGAR Rutas.
                // UtilSQLite.guardar(mLineas);
                break;
            case MODE_SQLite:
                // pequeño delay.
                mLineas = connSQLite.cargarLineas();
                break;
        }*/

        // si Alex me trae las cosas, lo que debo hacer es
        // crear por cada polylineOptions un polyline y solo
        // guardar esto en la clase Linea y en la base de datos el polyline
        // esta funcion se debe llamar una vez creado el mapa, al inicio de la app,
        // la funciones loadRoutes();
        GoogleMapsDirectionsAPI.loadPolylineOptions(mLineas);


        //  mapFragment.loadRoutes();

        return mLineas;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Thread.interrupted();
    }
}
