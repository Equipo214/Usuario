package com.grupo214.usuario.alarma;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.grupo214.usuario.R;
import com.grupo214.usuario.activities.MainActivity;

//                getContext().startService(new Intent(getContext(),Alarma.class)); <- forma de crear
public class Alarma extends Service {

    private NotificationManager mNotificationManager;
    private String YES_ACTION = "se";


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
        Toast.makeText(this, "Servicio: " + startId, Toast.LENGTH_LONG).show();

        Context mContext = getBaseContext();
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext.getApplicationContext(), "notify_001");
        Intent ii = new Intent(mContext.getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, ii, 0);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText("Anda saliendo wachin");
        bigText.setBigContentTitle("Linea 242 A - Liners");
        bigText.setSummaryText("Donde esta mi bondi");


        Intent okReceive = new Intent();
        okReceive.setAction(YES_ACTION);
        PendingIntent pendingIntentOk= PendingIntent.getBroadcast(this, 12345, okReceive, PendingIntent.FLAG_UPDATE_CURRENT);

         Intent posPonerReceive = new Intent();
        posPonerReceive.setAction(YES_ACTION);
        PendingIntent pendingIntentPosponer = PendingIntent.getBroadcast(this, 12345, posPonerReceive, PendingIntent.FLAG_UPDATE_CURRENT);

        //mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.drawable.ic_buss);
        mBuilder.setContentTitle("¿Donde esta mi bondi?");
        mBuilder.setContentText("Linea 242 A - Liners");
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        mBuilder.addAction(R.drawable.ic_directions_bus_black_24dp, "Aceptar", pendingIntentOk);
        mBuilder.addAction(R.drawable.ic_sync_black_24dp, "Posponer", pendingIntentPosponer);

        mBuilder.setVibrate(new long[]{100, 250, 100, 500});
        mBuilder.setStyle(bigText);

        mNotificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("notify_001",
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            mNotificationManager.createNotificationChannel(channel);
        }

        mNotificationManager.notify(0, mBuilder.build());

        return super.onStartCommand(intent, flags, startId);
    }
/*
    public Alarma(Context context, HashMap<String, LatLng> paradasConAlarmas) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.context = context;
        this.parametro = preferences.getString("list_preference_parameter", "nope");
        this.paradasConAlarmas = paradasConAlarmas;
    }
*/

}

