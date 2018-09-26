package com.grupo214.usuario.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;

import com.grupo214.usuario.Util.DatabaseAlarms;
import com.grupo214.usuario.adapters.NotificacionesAdapter;
import com.grupo214.usuario.fragment.NotificacionFragment;

public class DialogoEliminarNotificacion extends AppCompatDialogFragment {

    private long id;
    private NotificacionesAdapter notificacionesAdapter;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Eliminar Notificacion")
                .setMessage("¿Desea eliminar notificacion?")
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NotificacionFragment.delNotificacion(DatabaseAlarms.getInstance(getContext()).getAlarm(id));
                        DatabaseAlarms.getInstance(getContext()).deleteAlarm(id);
                        NotificacionFragment.notifyDataSetChange(getContext());
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

    public void setParams(long id) {
        this.id = id;
    }
}