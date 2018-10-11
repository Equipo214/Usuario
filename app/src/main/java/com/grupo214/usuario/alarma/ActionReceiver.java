package com.grupo214.usuario.alarma;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.grupo214.usuario.activities.AMNotificacion;
import com.grupo214.usuario.objects.Alarm;
import com.grupo214.usuario.objects.ServicioAlarma;

import java.util.ArrayList;
import java.util.List;

import static com.grupo214.usuario.activities.AMNotificacion.EXTRA_ID_ALARMA;

public class ActionReceiver extends BroadcastReceiver {

    Context context;
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;

        String aceptar = intent.getStringExtra("aceptar");
        String posponer = intent.getStringExtra("posponer");
        int idAlarma = intent.getIntExtra(AMNotificacion.EXTRA_ID_ALARMA,0);

        if (aceptar != null && aceptar.equals("Aceptar")) {
            Toast.makeText(context, "¡Buen viaje!", Toast.LENGTH_LONG).show();
            clearNotification(context);
        } else if (posponer != null && posponer.equals("Posponer")) {
            posponer(context,idAlarma);
            clearNotification(context);
        }

        Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        context.sendBroadcast(it);
    }

    public void clearNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NotificationBus.NOTIFICATION_ID);
    }

    public void posponer(Context context,int idAlarma) {
        if( ! CheckPostsReceiver.checkNotificacion(context,idAlarma) ){
            Intent intent = new Intent(context, CheckPostsReceiver.class);
            intent.putExtra(EXTRA_ID_ALARMA, idAlarma);
            context.startService(intent);
        }

    }

    public void performAction2(Context context) {
        Toast.makeText(context, "Entro como caballo", Toast.LENGTH_LONG);
    }


}
