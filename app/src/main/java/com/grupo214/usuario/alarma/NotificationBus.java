package com.grupo214.usuario.alarma;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.grupo214.usuario.R;

//                getContext().startService(new Intent(getContext(),NotificationBus.class)); <- forma de crear
public class NotificationBus extends Service {

    public static int NOTIFICATION_ID = 1;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "Servicio Creado", Toast.LENGTH_LONG).show();

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Context mContext = getBaseContext();
        Intent okReceive = new Intent(getBaseContext(), ActionReceiver.class);

        int tiempoEstimado = intent.getIntExtra("tiempo", 0);
        String ramal = intent.getStringExtra("ramal");
        String linea = intent.getStringExtra("linea");
        int ico = intent.getIntExtra("color", Color.GREEN);


        String lineaRamal = "Linea " + linea + " - " + ramal;

        okReceive.putExtra("aceptar", "Aceptar");
        PendingIntent pendingIntentOk = PendingIntent.getBroadcast(this, 12345, okReceive, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager mNotificationManager;
        String NOTIFICATION_CHANNEL_ID = "notify_001";


        Intent posPonerReceive = new Intent(getBaseContext(), ActionReceiver.class);
        posPonerReceive.putExtra("posponer", "Posponer");
        PendingIntent pendingIntentPosponer = PendingIntent.getBroadcast(this, 12346, posPonerReceive, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        if (tiempoEstimado == 0)
            bigText.bigText("El colectivo esta en la parada.");
        else
            bigText.bigText("El colectivo esta a " + tiempoEstimado + " minutos de la parada.");
        bigText.setBigContentTitle(lineaRamal);
        bigText.setSummaryText("¡Hay un bondi cerca!");

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext.getApplicationContext(), "notify_001")
                .setSmallIcon(R.mipmap.ic_buss)
                .setContentTitle("¿Donde está mi bondi?")
                .setContentText(lineaRamal)
                .setColor(ico)
                .setColorized(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .addAction(R.mipmap.ic_bus_1, "Aceptar", pendingIntentOk)
                .addAction(R.drawable.ic_sync_black_24dp, "Posponer", pendingIntentPosponer)
                .setOngoing(false)
                .setDeleteIntent(pendingIntentPosponer)
                .setAutoCancel(true)
                .setVibrate(new long[]{100, 250, 100, 500})
                .setStyle(bigText);

        mNotificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "¡Bondi cerca!", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(R.color.tabSelect);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            assert mNotificationManager != null;
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

        return super.onStartCommand(intent, flags, startId);
    }


/*
    public NotificationBus(Context context, HashMap<String, LatLng> paradasConAlarmas) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.context = context;
        this.parametro = preferences.getString("list_preference_parameter", "nope");
        this.paradasConAlarmas = paradasConAlarmas;
    }
*/

}

