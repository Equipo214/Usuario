package com.grupo214.usuario.alarma;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class ActionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Toast.makeText(context, "recieved", Toast.LENGTH_SHORT).show();

        String action = intent.getStringExtra("action");

        if (action.equals("Aceptar")) {
            Toast.makeText(context, "Entro como caballo action1", Toast.LENGTH_LONG).show();
        } else if (action.equals("Posponer")) {
            Toast.makeText(context, "Entro como caballo action2", Toast.LENGTH_LONG).show();

            //This is used to close the notification tray
            Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            context.sendBroadcast(it);
        }
    }

    public void performAction1(Context context) {
        Toast.makeText(context, "Entro como caballo", Toast.LENGTH_LONG);
    }

    public void performAction2(Context context) {
        Toast.makeText(context, "Entro como caballo", Toast.LENGTH_LONG);
    }

}
