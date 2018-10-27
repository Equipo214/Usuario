package com.grupo214.usuario.fragment;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.grupo214.usuario.R;
import com.grupo214.usuario.Util.DatabaseAlarms;
import com.grupo214.usuario.activities.CrearYEditarNotificacionActivity;
import com.grupo214.usuario.adapters.NotificacionesAdapter;
import com.grupo214.usuario.adapters.NotificacionesNombreAdapter;
import com.grupo214.usuario.objects.Alarm;
import com.grupo214.usuario.objects.Ramal;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Clase gestiona la pestaña "inicio" quizas esto muera como los sentimientos de ella hacia mi.
 *
 * @author Daniel Boullon
 */
public final class NotificacionFragment extends Fragment  {
    private static final String TAG = "NotificacionFragment";
    public static String AGREGAR = "AGREGAR";
    public static String EDITAR = "EDITAR";

    @SuppressLint("StaticFieldLeak")
    private static NotificacionesAdapter notificacionesAdapter;
    private static NotificacionesNombreAdapter notificacionesNombreAdapter;
    private ListView lv_listNotificaciones;
    private ViewPager tabViewPage;
    private MapFragment mapFragment;
    private AlarmManager alarmManager;
    private HashMap<String, Ramal> ramalesSeleccionados;
    private LineasFragment lineasFragment;

    public static NotificacionesNombreAdapter getNotificacionesNombreAdapter() {
        return notificacionesNombreAdapter;
    }
    public static NotificacionesAdapter getNotificacionesAdapter() {
        return notificacionesAdapter;
    }

    public static void addNotificacion(Alarm alarm) {
        notificacionesAdapter.add(alarm);
    }

    public static void delNotificacion(Alarm alarm) {
        notificacionesAdapter.remove(alarm);
    }

    public static int getCountNotificacion() {
        return notificacionesAdapter.getCount();
    }

    public static void notifyDataSetChange(Context context) {
        notificacionesAdapter.clear();
        notificacionesNombreAdapter.clear();
        ArrayList<Alarm> listAlarms = DatabaseAlarms.getInstance(context).getAlarms();
        notificacionesAdapter.addAll(listAlarms);
        notificacionesNombreAdapter.addAll(listAlarms);
        notificacionesAdapter.notifyDataSetChanged();
    }

    public void setMapFragment(MapFragment mapFragment) {
        this.mapFragment = mapFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@Nullable LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_notificaciones, container, false);
        FloatingActionButton bt_float = view.findViewById(R.id.agregarNotificacion);
        notificacionesAdapter = new NotificacionesAdapter(getContext(),
                android.R.layout.simple_list_item_2,
                (TextView) view.findViewById(R.id.tx_notificaciones_back),
                getFragmentManager(), tabViewPage);

        notificacionesNombreAdapter = new NotificacionesNombreAdapter(getContext(),
                android.R.layout.simple_list_item_2);

        lv_listNotificaciones = (ListView) view.findViewById(R.id.listaNotificaciones);
        notificacionesAdapter.setMapFragment(mapFragment);
        notificacionesAdapter.setLineasFragment(lineasFragment);
        lv_listNotificaciones.setAdapter(notificacionesAdapter);
        ArrayList<Alarm> listAlarms = DatabaseAlarms.getInstance(getContext()).getAlarms();
        notificacionesAdapter.addAll(listAlarms);
        notificacionesNombreAdapter.addAll(listAlarms);
        notifyDataSetChange(getContext());

        bt_float.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), CrearYEditarNotificacionActivity.class).putExtra("modo", AGREGAR));
            }
        });

        return view;
    }

    public void setTabViewPage(ViewPager tabViewPage) {
        this.tabViewPage = tabViewPage;
    }

    public void setAlarmManager(AlarmManager alarmManager) {
        this.alarmManager = alarmManager;
    }


    public void setLineasFragment(LineasFragment lineasFragment) {
        this.lineasFragment = lineasFragment;
    }
}