package com.grupo214.usuario.activities;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.grupo214.usuario.R;
import com.grupo214.usuario.Util.DatabaseAlarms;
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
    private static final String TAG = "MainActivity";
    public static LatLng puntoPartida;

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
    private AlarmManager manager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Seteo de variables:
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // final AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        tabLayout = (SmartTabLayout) findViewById(R.id.tabs);
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setOffscreenPageLimit(3);
        manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        inicializarFragments();

        //Set Tab:
        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());
        final LayoutInflater inflater = LayoutInflater.from(this);
        final Resources res = getResources();

        tabLayout.setCustomTabView(new SmartTabLayout.TabProvider() {
            @Override
            public View createTabView(ViewGroup container, int position, PagerAdapter adapter) {
                View itemView = inflater.inflate(R.layout.tab_item, container, false);
                ImageView icon = (ImageView) itemView.findViewById(R.id.custom_tab_icon);
                switch (position) {
                    case 0:
                        icon.setImageDrawable(res.getDrawable(R.drawable.ic_tab_linea));
                        break;
                    case 1:
                        icon.setImageDrawable(res.getDrawable(R.drawable.ic_tab_map));
                        break;
                    case 2:
                        icon.setImageDrawable(res.getDrawable(R.drawable.ic_tab_notf));
                        break;
                    default:
                        throw new IllegalStateException("Invalid position: " + position);
                }
                return itemView;
            }
        });


        tabLayout.setDividerColors(Color.BLUE);

        mSectionsPageAdapter.addFragment(lineasFragment, "LINEAS");     // 0
        mSectionsPageAdapter.addFragment(mapFragment, "MAPA");          // 1
        mSectionsPageAdapter.addFragment(notificacionFragment, "NOTIF.");       // 2


        mViewPager.setAdapter(mSectionsPageAdapter);
        mViewPager.setCurrentItem(TAB_LINEA); // para que inicie la tab de Mapas --> temporalmente cambiado.

        tabLayout.setViewPager(mViewPager);

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
        navigationView.setItemIconTintList(null);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == TAB_MAPA) {
                    if (lineasFragment.isChange() && mapFragment.isDondeEstaMiBondiActive()) {
                        mapFragment.dondeEstaMiBondi(puntoPartida);
                    }
                } else if (position == TAB_LINEA) {
                    lineasFragment.setChange(false);
                }
            }

            @Override
            public void onPageSelected(int position) {
                if (position == TAB_MAPA) {
                    if (lineasFragment.isChange() && mapFragment.isDondeEstaMiBondiActive()) {
                        mapFragment.dondeEstaMiBondi(puntoPartida);
                    }
                } else if (position == TAB_LINEA) {
                    lineasFragment.setChange(false);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    private void inicializarFragments() {
        mLineas = SplashScreen.mLineas;  // cambiar por SQLite o algo mas objetoso
        ramales_seleccionados = DatabaseAlarms.getInstance(this).getRamalesSeleccionados();
        startMenuDialog = new Dialog(this, R.style.Theme_AppCompat_Dialog_Alert);
        notificacionFragment = new NotificacionFragment();
        lineasFragment = new LineasFragment();
        mapFragment = new MapFragment();
        lineasFragment.setStartMenuDialog(startMenuDialog);
        lineasFragment.setLineas(mLineas, ramales_seleccionados);
        lineasFragment.setParams(mViewPager, mapFragment);
        mapFragment.setStartMenuDialog(startMenuDialog);
        mapFragment.setLineas(mLineas, ramales_seleccionados);
        notificacionFragment.setTabViewPage(mViewPager);
        notificacionFragment.setMapFragment(mapFragment);
        notificacionFragment.setLineasFragment(lineasFragment);
        notificacionFragment.setAlarmManager(manager);
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

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_likSube:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(LINK_SUBE)));

                //    startActivity(new Intent(this, WebActivity.class)
                //             .putExtra("URL", LINK_SUBE));
                break;

            case R.id.nav_linkTarifas:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(LINK_TARIFAS)));

                // startActivity(new Intent(this, WebActivity.class)
                //         .putExtra("URL", LINK_TARIFAS));
                break;

            case R.id.nav_ajustes:
                startActivity(new Intent(this, SettingActivity.class));
                break;

            case R.id.nav_comentario:
                startActivity(new Intent(this, CommentaryActivity.class));
                break;

            case R.id.nav_facebook:
                String facebookId = "fb://page/295459871051427";
                String urlPage = "http://www.facebook.com/dondeestamibondi";
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(facebookId)));
                } catch (Exception e) {
                    Log.e(TAG, "Aplicación no instalada.");
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(urlPage)));
                }
                break;
            case R.id.nav_instagram:
                Uri uri = Uri.parse("http://instagram.com/_u/dondeestamibondi");
                Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);
                likeIng.setPackage("com.instagram.android");
                try {
                    startActivity(likeIng);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://instagram.com/dondeestamibondi")));
                }
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
        Toast.makeText(this, msj, Toast.LENGTH_SHORT).show();
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