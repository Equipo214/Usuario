package com.grupo214.usuario.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.SparseBooleanArray;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.grupo214.usuario.Util.DatabaseAlarms;
import com.grupo214.usuario.adapters.NotificacionesNombreAdapter;
import com.grupo214.usuario.fragment.NotificacionFragment;
import com.grupo214.usuario.objects.Alarm;
import com.grupo214.usuario.objects.ParadaAlarma;

import java.util.HashMap;


public class DialogoParadaOnInfo extends AppCompatDialogFragment {


    private Marker marker;
    private HashMap<String, Marker> paradasCercanas;
    private HashMap<String, LatLng> paradasConAlarmas;

    public void setParams(Marker marker,
                          HashMap<String, LatLng> paradasConAlarmas,
                          HashMap<String, Marker> paradasCercanas) {
        this.marker = marker;
        this.paradasConAlarmas = paradasConAlarmas;
        this.paradasCercanas = paradasCercanas;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Parada")
                .setMessage("¿Que deseas hacer?")
                .setNegativeButton("Notificacion", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog(getContext());
                    }
                });


        if (paradasCercanas.get(marker.getId()) == null)
            builder.setNeutralButton("Alarma destino", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    DialogoAlarma dialogoAlarma = new DialogoAlarma();
                    dialogoAlarma.setParams(marker, paradasConAlarmas);
                    dialogoAlarma.show(getFragmentManager(), "Dialog NotificationBus");
                }
            });
        return builder.create();
    }

    void dialog(final Context context) {
        final NotificacionesNombreAdapter notificacionesNombreAdapter =
                NotificacionFragment.getNotificacionesNombreAdapter();

        Alarm alarmnueva = null;
        if (NotificacionFragment.getNotificacionesAdapter().getCount() == 0) {
            alarmnueva = new Alarm(0, 0, "Nueva notificacion", new SparseBooleanArray(7), false);
            if (notificacionesNombreAdapter.getCount() == 0)
                notificacionesNombreAdapter.add(alarmnueva);
        }

        final Alarm finalAlarmnueva = alarmnueva;
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(context);
        alt_bld.setTitle("Seleccione una notificacion");
        alt_bld.setSingleChoiceItems(notificacionesNombreAdapter, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                Alarm alarm;
                if (finalAlarmnueva != null) {
                    alarm = finalAlarmnueva;
                    DatabaseAlarms.getInstance(context).addAlarm(alarm);
                    NotificacionFragment.getNotificacionesAdapter().add(alarm);
                    Toast.makeText(context, "Notificacion creada.", Toast.LENGTH_SHORT).show();
                } else {
                    alarm = notificacionesNombreAdapter.getItem(item);
                }
                ParadaAlarma paradaAlarma = (ParadaAlarma) marker.getTag();
                paradaAlarma.setId_alarms(Long.toString(alarm.getId()));
                alarm.putParadaAlarma(paradaAlarma.getIdRamal(), paradaAlarma);
                DatabaseAlarms.getInstance(context).addParadaAlarma(paradaAlarma);
                dialog.dismiss();
                NotificacionFragment.notifyDataSetChange(context);
            }
        });
        AlertDialog alert = alt_bld.create();
        alert.show();
    }


}


