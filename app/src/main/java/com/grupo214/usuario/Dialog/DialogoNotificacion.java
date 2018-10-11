package com.grupo214.usuario.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.grupo214.usuario.objects.ParadaAlarma;

import java.util.HashMap;

public class DialogoNotificacion extends AppCompatDialogFragment {


    private Marker marker;

    public void setParams(Marker marker) {
        this.marker = marker;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());


        builder.setTitle("NotificationBus")
                .setMessage("¿Desea agregar parada a notificacion?")
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //combo box

                        ParadaAlarma paradaAlarma = (ParadaAlarma) marker.getTag();


                        //alarm.putParadaAlarma(paradaAlarma);
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                });
        return builder.create();
    }
    /*public Dialog listaNotificaciones(){
        Dialog listaNotificaciones = new Dialog(getContext());
        listaNotificaciones.
    }*/
}