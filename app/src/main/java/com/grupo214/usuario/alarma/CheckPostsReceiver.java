package com.grupo214.usuario.alarma;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.grupo214.usuario.activities.AMNotificacion;

public class CheckPostsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context,NotificationBus.class));
        Toast.makeText(context,"Entro"  ,Toast.LENGTH_LONG).show();
        if (!intent.hasExtra(AMNotificacion.EXTRA_ID_ALARMA)) {
            return;
        }
        int idAlarma = intent.getIntExtra(AMNotificacion.EXTRA_ID_ALARMA, 0);
        Toast.makeText(context,"ID ALARMA: " + idAlarma ,Toast.LENGTH_LONG).show();
        context.startService(new Intent(context,NotificationBus.class));

    }
}