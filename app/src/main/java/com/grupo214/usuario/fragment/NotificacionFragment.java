package com.grupo214.usuario.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.grupo214.usuario.R;
import com.grupo214.usuario.Util.DatabaseHelper;
import com.grupo214.usuario.activities.AMNotificacion;
import com.grupo214.usuario.adapters.NotificacionesAdapter;
import com.grupo214.usuario.objects.Alarm;

/**
 * Clase gestiona la pestaña "inicio" quizas esto muera como los sentimientos de ella hacia mi.
 *
 * @author Daniel Boullon
 */
public final class NotificacionFragment extends Fragment {
    private static final String TAG = "NotificacionFragment";
    private static NotificacionesAdapter notificacionesAdapter;
    private ListView lv_listNotificaciones;

    public static String AGREGAR = "AGREGAR";
    public static String EDITAR = "EDITAR";
    public static String ELIMINAR = "ELIMINAR";


    public static void addNotificacion(Alarm alarm) {
        notificacionesAdapter.add(alarm);
    }

    public static int getCountNotificacion() {
        return notificacionesAdapter.getCount();
    }

    public static void notifyDataSetChange(){ notificacionesAdapter.notifyDataSetChanged();}

    @Nullable
    @Override
    public View onCreateView(@Nullable LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_notificaciones, container, false);
        FloatingActionButton bt_float = view.findViewById(R.id.floatingActionButton);
        notificacionesAdapter = new NotificacionesAdapter(getContext(), android.R.layout.simple_list_item_2, (TextView) view.findViewById(R.id.tx_notificaciones_back));
        lv_listNotificaciones = (ListView) view.findViewById(R.id.listaNotificaciones);
        notificacionesAdapter.setLv(lv_listNotificaciones);
        lv_listNotificaciones.setAdapter(notificacionesAdapter);

        notificacionesAdapter.addAll(DatabaseHelper.getInstance(getContext()).getAlarms());
        notificacionesAdapter.notifyDataSetChanged();

        bt_float.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //modo nuevo
                startActivity(new Intent(getContext(), AMNotificacion.class).putExtra("modo",AGREGAR));
            }
        });

        lv_listNotificaciones.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //modo editar + ID
                Alarm item = notificacionesAdapter.getItem(position);
                Toast.makeText(getContext(), "Editar " + item.getLabel(), Toast.LENGTH_LONG).show();
                startActivity(new Intent(getContext(), AMNotificacion.class)
                        .putExtra("modo",EDITAR)
                        .putExtra("id",item.getId()));
            }
        });



        return view;
    }
}