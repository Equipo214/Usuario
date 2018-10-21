package com.grupo214.usuario.alarma;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.grupo214.usuario.R;

//                getContext().startService(new Intent(getContext(),NotificationBus.class)); <- forma de crear
public class NotificationBus extends Service {

    public static int NOTIFICATION_ID = 1;
    private String TAG = "NotificationBus";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Context mContext = getBaseContext();
        Intent okReceive = new Intent(getBaseContext(), ActionReceiver.class);

        int tiempoEstimado = intent.getIntExtra("tiempo", 0);
        String ramal = intent.getStringExtra("ramal");
        String linea = intent.getStringExtra("linea");
        int ico = intent.getIntExtra("color", Color.GREEN);

        String detalle = "Linea " + linea + " - " + ramal;

        okReceive.putExtra("aceptar", "Aceptar");
        PendingIntent pendingIntentOk = PendingIntent.getBroadcast(this, 12345, okReceive, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager mNotificationManager;
        String NOTIFICATION_CHANNEL_ID = "Notificacion_ColectivoCerca";


        Intent posPonerReceive = new Intent(getBaseContext(), ActionReceiver.class);
        posPonerReceive.putExtra("posponer", "Posponer");
        PendingIntent pendingIntentPosponer =
                PendingIntent.getBroadcast(this, 12346, posPonerReceive, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.BigTextStyle mensaje = new NotificationCompat.BigTextStyle();
        if (tiempoEstimado == 0)
            mensaje.bigText("El colectivo esta en la parada.");
        else
            mensaje.bigText("El colectivo esta a " + tiempoEstimado + " minutos de la parada.");
        mensaje.setBigContentTitle(detalle);
        mensaje.setSummaryText("¡Hay un bondi cerca!");


        mNotificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        Log.d(TAG, "uri: " + Uri.parse(pref.getString("notifications_new_message_ringtone", "content://settings/system/notification_sound")));


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            Log.d(TAG, "entro a la version " + android.os.Build.VERSION.SDK_INT);
            NotificationCompat.Action actionOk
                    = new NotificationCompat.Action.Builder(0,
                    "Aceptar", pendingIntentOk).build();

            NotificationCompat.Action actionPosponer
                    = new NotificationCompat.Action.Builder(0,
                    "Posponer", pendingIntentPosponer).build();

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext.getApplicationContext(),
                    "notify_001")
                    .setSmallIcon(R.mipmap.ic_buss)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(detalle)
                    .setVisibility(Notification.VISIBILITY_PUBLIC)
                    .addAction(actionOk)
                    .addAction(actionPosponer)
                    .setDeleteIntent(pendingIntentPosponer)
                    .setStyle(mensaje);
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "¡Bondi cerca! v2", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableVibration(false);
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            mNotificationManager.createNotificationChannel(notificationChannel);
            mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

        } else {
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext.getApplicationContext(),
                    "notify_001")
                    .setSmallIcon(R.mipmap.ic_buss)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(detalle)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .addAction(0, "Aceptar", pendingIntentOk)
                    .addAction(0, "Posponer", pendingIntentPosponer)
                    .setDeleteIntent(pendingIntentPosponer)
                    .setStyle(mensaje);
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            if (mNotificationManager != null) {
                mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
            }
        }


        Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createWaveform(new long[]{0, 400, 800, 600, 800, 800, 800, 1000, 400, 800, 600, 800, 800, 800, 400, 800, 600, 800, 800, 800},
                    VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(500);
        }

        return super.onStartCommand(intent, flags, startId);
    }
}

