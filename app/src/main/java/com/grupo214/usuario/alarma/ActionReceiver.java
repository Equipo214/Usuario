package com.grupo214.usuario.alarma;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class ActionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        //  Toast.makeText(context, "recieved", Toast.LENGTH_SHORT).show();

        String aceptar = intent.getStringExtra("aceptar");
        String posponer = intent.getStringExtra("posponer");


        if (aceptar != null && aceptar.equals("Aceptar")) {
            // que no suena mas alarma
            Toast.makeText(context, "Entro como caballo Aceptar", Toast.LENGTH_LONG).show();
            clearNotification(context);
        } else if (posponer != null && posponer.equals("Posponer")) {
            // que busqueotro servicio
            Toast.makeText(context, "Entro como caballo Posponer", Toast.LENGTH_LONG).show();
            clearNotification(context);
        }
        //This is used to close the notification tray
        Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        context.sendBroadcast(it);
    }

    public void clearNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NotificationBus.NOTIFICATION_ID);
    }

    public void performAction1(Context context) {
        Toast.makeText(context, "Entro como caballo", Toast.LENGTH_LONG);
    }

    public void performAction2(Context context) {
        Toast.makeText(context, "Entro como caballo", Toast.LENGTH_LONG);
    }

}
