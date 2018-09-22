package com.grupo214.usuario.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.grupo214.usuario.R;
import com.grupo214.usuario.activities.EditarNotificacion;
import com.grupo214.usuario.adapters.NotificacionesAdapter;
import com.grupo214.usuario.objects.Notificacion;

/**
 * Clase gestiona la pestaña "inicio" quizas esto muera como los sentimientos de ella hacia mi.
 *
 * @author Daniel Boullon
 */
public final class NotificacionFragment extends Fragment {
    private static final String TAG = "NotificacionFragment";
    public static NotificacionesAdapter adaptador;
    private ListView lv_listNotificaciones;
    private NotificacionEditFragment notificacionEditFragment;

    public static void addNotificacion(Notificacion notificacion) {
        adaptador.add(notificacion);
    }

    public static int getCountNotificacion() {
        return adaptador.getCount();
    }

    @Nullable
    @Override
    public View onCreateView(@Nullable LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_notificaciones, container, false);
        FloatingActionButton bt_float = view.findViewById(R.id.floatingActionButton);
        notificacionEditFragment = new NotificacionEditFragment();
        adaptador = new NotificacionesAdapter(getContext(), android.R.layout.simple_list_item_2, (TextView) view.findViewById(R.id.tx_notificaciones_back));

        lv_listNotificaciones = (ListView) view.findViewById(R.id.listaNotificaciones);
        adaptador.setLv(lv_listNotificaciones);
        lv_listNotificaciones.setAdapter(adaptador);

        final SparseBooleanArray array = new SparseBooleanArray(7);
        array.put(1, false);
        array.put(2, false);
        array.put(3, false);
        array.put(4, false);
        array.put(5, false);
        array.put(6, false);
        array.put(7, false);
        bt_float.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //modo nuevo
                startActivity(new Intent(getContext(), EditarNotificacion.class));
            }
        });

        lv_listNotificaciones.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //modo editar + ID
                Notificacion item = adaptador.getItem(position);
                Toast.makeText(getContext(), "Editar " + item.getLabel(), Toast.LENGTH_LONG).show();
                startActivity(new Intent(getContext(), EditarNotificacion.class));
            }
        });
        return view;
    }
}