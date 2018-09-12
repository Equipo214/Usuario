package com.grupo214.usuario.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.grupo214.usuario.R;

import java.util.ArrayList;

public class DialogoAccesibilidad extends AppCompatDialogFragment {


    private Marker marker;
    private ArrayList<String> paradasAccId;


    public void setParams(Marker marker, ArrayList<String> paradasAccId) {
        this.marker = marker;
        this.paradasAccId = paradasAccId;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final Boolean modo;
        modo = !paradasAccId.contains(marker.getId());

        builder.setTitle("Solicitar Accesibilidad")
                .setMessage("¿" + (modo ? "Solicitar" : "Cancelar") + " parada con accesibilidad?")
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (modo) {
                            marker.setIcon(BitmapDescriptorFactory
                                    .fromResource(R.mipmap.ic_parada_acc)); // ICONO ACC
                            // solicitaraccesibilidad.
                            paradasAccId.add(marker.getId());
                        } else {
                            marker.setIcon(BitmapDescriptorFactory
                                    .fromResource(R.mipmap.ic_parada_bondi)); // ICONO COMUN
                            paradasAccId.remove(marker.getId());
                            // ! solicitaraccesibilidad
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
