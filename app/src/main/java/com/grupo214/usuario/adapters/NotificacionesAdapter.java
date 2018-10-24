package com.grupo214.usuario.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.grupo214.usuario.Dialog.DialogoEliminarNotificacion;
import com.grupo214.usuario.R;
import com.grupo214.usuario.Util.DatabaseAlarms;
import com.grupo214.usuario.activities.AMNotificacion;
import com.grupo214.usuario.fragment.LineasFragment;
import com.grupo214.usuario.fragment.MapFragment;
import com.grupo214.usuario.fragment.NotificacionFragment;
import com.grupo214.usuario.objects.Alarm;

import java.text.SimpleDateFormat;
import java.util.Locale;


public class NotificacionesAdapter extends ArrayAdapter<Alarm> {

    private static final SimpleDateFormat TIME_FORMAT =
            new SimpleDateFormat("h:mm", Locale.getDefault());
    private static final SimpleDateFormat AM_PM_FORMAT =
            new SimpleDateFormat("a", Locale.getDefault());
    private final TextView txServicioBack;
    private FragmentManager fragmentManager;
    private ViewPager tabViewPager;
    private MapFragment mapFragment;
    private LineasFragment lineasFragment;

    public NotificacionesAdapter(@NonNull Context context, int resource, TextView txServicioBack, FragmentManager fragmentManager, ViewPager tabViewPager) {
        super(context, resource);
        this.txServicioBack = txServicioBack;
        this.fragmentManager = fragmentManager;
        this.tabViewPager = tabViewPager;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Alarm curAlarm = getItem(position);

        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.notif_row, parent, false);

        final TextView tx_hora = (TextView) convertView.findViewById(R.id.ar_time);
        tx_hora.setText(TIME_FORMAT.format(curAlarm.getTime()));

        final TextView tx_am_pm = (TextView) convertView.findViewById(R.id.ar_am_pm);
        tx_am_pm.setText(AM_PM_FORMAT.format(curAlarm.getTime()));

        final TextView tx_label = (TextView) convertView.findViewById(R.id.ar_label);
        tx_label.setText(curAlarm.getLabel());

        final TextView tx_dias = (TextView) convertView.findViewById(R.id.ar_days);
        SparseBooleanArray diasActivos = curAlarm.getAllDays();
        String dias = "";
        dias += diasActivos.get(Alarm.MON) ? "Lu · " : "";
        dias += diasActivos.get(Alarm.TUES) ? "Ma · " : "";
        dias += diasActivos.get(Alarm.WED) ? "Mi · " : "";
        dias += diasActivos.get(Alarm.THURS) ? "Ju · " : "";
        dias += diasActivos.get(Alarm.FRI) ? "Vi · " : "";
        dias += diasActivos.get(Alarm.SAT) ? "Sa · " : "";
        dias += diasActivos.get(Alarm.SUN) ? "Do · " : "";
        int tamano = dias.length();
        if (tamano > 4)
            dias = dias.substring(0, tamano - 3);

        tx_dias.setText(dias);

        ImageView bt_onOff = (ImageButton) convertView.findViewById(R.id.ar_icon);

        bt_onOff.setImageResource(curAlarm.isEnabled() ? R.drawable.ic_alarm_on_black_24dp : R.drawable.ic_alarm_off_black_24dp);
        bt_onOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // editar o eliminar
                curAlarm.setEnabled(!curAlarm.isEnabled());
                /*if(curAlarm.isEnabled()){

                }else {

                }*/
                // al final de guardar o quitar de la bbd
                DatabaseAlarms.getInstance(v.getContext()).updateAlarm(curAlarm);
                notifyDataSetChanged();
            }
        });
        ImageView bt_delete_not = (ImageView) convertView.findViewById(R.id.bt_delete_not);
        bt_delete_not.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogoEliminarNotificacion dialogoEliminarNotificacion = new DialogoEliminarNotificacion();
                dialogoEliminarNotificacion.setParams(curAlarm.getId());
                dialogoEliminarNotificacion.show(fragmentManager, "Eliminar Notificacion");

            }
        });

        ImageButton bt_edit_not = (ImageButton) convertView.findViewById(R.id.bt_edit_not);
        bt_edit_not.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Editar " + curAlarm.getLabel(), Toast.LENGTH_LONG).show();
                getContext().startActivity(new Intent(getContext(), AMNotificacion.class)
                        .putExtra("modo", NotificacionFragment.EDITAR)
                        .putExtra("id", curAlarm.getId()));
            }
        });


        final TextView tx_paradas_not = (TextView) convertView.findViewById(R.id.tx_paradas_not);

        if (curAlarm.getParadaAlarmas().size() == 0)
            tx_paradas_not.setVisibility(View.INVISIBLE);
        else
            tx_paradas_not.setVisibility(View.VISIBLE);

        RecyclerView rw_paradas = (RecyclerView) convertView.findViewById(R.id.horizontalList_paradas_noti);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rw_paradas.setLayoutManager(layoutManager);
        ParadasListaSimpleAdapter paradasListaSimpleAdapter = new ParadasListaSimpleAdapter(getContext(), curAlarm.getParadaAlarmas());
        paradasListaSimpleAdapter.setMapFragment(mapFragment);
        paradasListaSimpleAdapter.setLineasFragment(lineasFragment);
        paradasListaSimpleAdapter.setTabViewPager(tabViewPager);
        rw_paradas.setAdapter(paradasListaSimpleAdapter);

    /*
    *   bt_not_parada1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // aca que vaya al mapa si la para ya esta setiada sino mensaje.
                ParadaAlarma paradaAlarma = curAlarm.getParadaAlarma(0);
                if( paradaAlarma != null ){
                    mapFragment.camare(paradaAlarma.getPunto());
                    tabViewPager.setCurrentItem(MainActivity.TAB_MAPA);
                }
            }
        });*/

//        AnimationFactory.doGrow(convertView);
        return convertView;
    }


    @Override
    public void notifyDataSetChanged() {
        if (this.getCount() > 0) {
            txServicioBack.setVisibility(View.INVISIBLE);
        } else if (this.getCount() == 0) {
            txServicioBack.setVisibility(View.VISIBLE);
        }
        super.notifyDataSetChanged();
    }


    public void setMapFragment(MapFragment mapFragment) {
        this.mapFragment = mapFragment;
    }

    public void setLineasFragment(LineasFragment lineasFragment) {
        this.lineasFragment = lineasFragment;
    }
}

