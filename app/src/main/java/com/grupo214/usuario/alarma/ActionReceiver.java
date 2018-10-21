package com.grupo214.usuario.alarma;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.grupo214.usuario.activities.AMNotificacion;

import static com.grupo214.usuario.activities.AMNotificacion.EXTRA_ID_ALARMA;
import static com.grupo214.usuario.alarma.LocationService.ID_LOCATION_SERVICE;

public class ActionReceiver extends BroadcastReceiver {

    Context context;
    private String TAG = "ActionReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;

        String aceptar = intent.getStringExtra("aceptar");
        String posponer = intent.getStringExtra("posponer");
        String cancelar = intent.getStringExtra("cancelar");

        int idAlarma = intent.getIntExtra(AMNotificacion.EXTRA_ID_ALARMA, 0);

        if (aceptar != null && aceptar.equals("Aceptar")) {
            Toast.makeText(context, "¡Buen viaje!", Toast.LENGTH_LONG).show();
            clearNotification(context, NotificationBus.NOTIFICATION_ID);
        } else if (posponer != null && posponer.equals("Posponer")) {
            posponer(context, idAlarma);
            clearNotification(context, NotificationBus.NOTIFICATION_ID);
        } else if (cancelar != null && cancelar.equals("Cancelar")) {
            context.stopService(new Intent(context,LocationService.class));
        }

        Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        context.sendBroadcast(it);
    }

    public void clearNotification(Context context, int id) {
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(id);
    }

    public void posponer(Context context, int idAlarma) {
        if (!CheckPostsReceiver.checkNotificacion(context, idAlarma)) {
            Intent intent = new Intent(context, CheckPostsReceiver.class);
            intent.putExtra(EXTRA_ID_ALARMA, idAlarma);
            context.startService(intent);
        }
    }
}
