package com.grupo214.usuario.alarma;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class CheckPostsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context,NotificationBus.class));
        Toast.makeText(context,"SONIC HEROEEESSS",Toast.LENGTH_LONG).show();
    }
}