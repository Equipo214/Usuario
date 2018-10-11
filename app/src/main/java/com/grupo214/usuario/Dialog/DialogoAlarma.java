package com.grupo214.usuario.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;


import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.grupo214.usuario.R;
import com.grupo214.usuario.alarma.LocationService;

import java.util.HashMap;

public class DialogoAlarma extends AppCompatDialogFragment {


    private Marker marker;
    private HashMap<String, LatLng> paradasConAlarmas;

    public void setParams(Marker marker, HashMap<String, LatLng> paradasConAlarmas) {
        this.marker = marker;
        this.paradasConAlarmas = paradasConAlarmas;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final Boolean modo;
        modo = paradasConAlarmas.get(marker.getId()) == null;


        builder.setTitle("Alarma")
                .setMessage("¿Desea " + (modo ? "activar" : "desactivar") + " alarma en esta parada?")
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (modo) {
                            marker.setIcon(BitmapDescriptorFactory
                                    .fromResource(R.mipmap.ic_parada_alarma_iv));
                            marker.setAlpha(1f);// ICONO ALARMA
                            paradasConAlarmas.put(marker.getId(), marker.getPosition());
                            Intent intent = new Intent(getContext(), LocationService.class);
                            intent.putExtra("lat",marker.getPosition().latitude);
                            intent.putExtra("lng",marker.getPosition().longitude);
                            getActivity().startService(intent);
                        } else {
                            marker.setIcon(BitmapDescriptorFactory
                                    .fromResource(R.mipmap.ic_parada_bondi)); // ICONO COMUN
                            paradasConAlarmas.remove(marker.getId());
                            Intent intent = new Intent(getContext(), LocationService.class);
                            getActivity().stopService(intent);
                        }
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
}