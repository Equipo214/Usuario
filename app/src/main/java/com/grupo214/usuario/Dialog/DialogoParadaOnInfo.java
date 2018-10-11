package com.grupo214.usuario.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.SwitchCompat;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.grupo214.usuario.R;
import com.grupo214.usuario.Util.DatabaseAlarms;
import com.grupo214.usuario.adapters.NotificacionesNombreAdapter;
import com.grupo214.usuario.fragment.NotificacionFragment;
import com.grupo214.usuario.objects.Alarm;
import com.grupo214.usuario.objects.ParadaAlarma;

import java.util.ArrayList;
import java.util.HashMap;


public class DialogoParadaOnInfo extends AppCompatDialogFragment {


    private Marker marker;
    private ArrayList<String> paradasAccId;
    private HashMap<String, Marker>  paradasCercanas;
    private HashMap<String, LatLng> paradasConAlarmas;

    public void setParams(Marker marker, ArrayList<String> paradasAccId,
                          HashMap<String, LatLng> paradasConAlarmas,
                          HashMap<String, Marker>  paradasCercanas) {
        this.marker = marker;
        this.paradasAccId = paradasAccId;
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

        SwitchCompat switchCompat = getActivity().findViewById(R.id.accesibilidad_Switch);
        if (switchCompat.isChecked())
            builder.setPositiveButton("Accesibilidad", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    DialogoAccesibilidad dialogoAccesibilidad = new DialogoAccesibilidad();
                    dialogoAccesibilidad.setParams(marker, paradasAccId);
                    dialogoAccesibilidad.show(getFragmentManager(), "Dialog Accesibilidad");
                }
            });

        return builder.create();
    }

    void dialog(final Context context) {
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(context);

        alt_bld.setTitle("Notificaciones");

        final NotificacionesNombreAdapter notificacionesNombreAdapter = NotificacionFragment.getNotificacionesNombreAdapter();
        //  notificacionesNombreAdapter.add(new Alarm(-2, 0, "Nueva notificacion", new SparseBooleanArray(7), false));

        alt_bld.setSingleChoiceItems(notificacionesNombreAdapter, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                Alarm alarm = notificacionesNombreAdapter.getItem(item);
                if (alarm == null) {
                    Toast.makeText(context, "Nuevo", Toast.LENGTH_LONG).show();
                } else {
                    ParadaAlarma paradaAlarma = (ParadaAlarma) marker.getTag();
                    paradaAlarma.setId_alarms(Long.toString(alarm.getId()));
                    alarm.putParadaAlarma(paradaAlarma.getIdRamal(),paradaAlarma);
                    DatabaseAlarms.getInstance(context).addParadaAlarma(paradaAlarma);
                }
                dialog.dismiss();

            }
        });
        AlertDialog alert = alt_bld.create();
        alert.show();
    }


}


