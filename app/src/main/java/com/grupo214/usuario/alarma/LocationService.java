package com.grupo214.usuario.alarma;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.grupo214.usuario.R;
import com.grupo214.usuario.Util.Util;
import com.grupo214.usuario.activities.MainActivity;
import com.grupo214.usuario.activities.WarnActivity;

import static android.app.NotificationManager.IMPORTANCE_HIGH;


/**
 * Created by devdeeds.com on 27-09-2017.
 */

public class LocationService extends Service implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    public static final int LOCATION_INTERVAL = 10000;
    public static final int FASTEST_LOCATION_INTERVAL = 5000;
    public static final String ACTION_LOCATION_BROADCAST = LocationService.class.getName() + "LocationBroadcast";
    public static final String EXTRA_LATITUDE = "extra_latitude";
    public static final String EXTRA_LONGITUDE = "extra_longitude";
    private static final String TAG = LocationService.class.getSimpleName();
    private static final int ID_LOCATION_SERVICE = 214;
    GoogleApiClient mLocationClient;
    LocationRequest mLocationRequest = new LocationRequest();
    /**
     * Variable para testear la alarma destino.-
     */
    int test = 0;
    private FusedLocationProviderClient mFusedLocationClient;
    private LatLng destino;

    private double lat;
    private double lng;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String CHANNEL_ONE_ID = "Where is my bondi";
        String CHANNEL_ONE_NAME = "Channel One";

        lat = intent.getDoubleExtra("lat", 0);
        lng = intent.getDoubleExtra("lng", 0);
        Log.d(TAG, "LAT: " + lat + "LNG: " + lng);


        NotificationChannel notificationChannel = null;
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        Notification notification = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(CHANNEL_ONE_ID,
                    CHANNEL_ONE_NAME, IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setShowBadge(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(notificationChannel);
            notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ONE_ID)
                    .setContentTitle("Gota fast")
                    .setContentText("Duerme tranquilo, nosotros te avisamos para bajar.")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(icon)
                    .build();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {

            notification = new Notification.Builder(getApplicationContext())
                    .setContentTitle("Gota fast")
                    .setContentText("Duerme tranquilo, nosotros te avisamos para bajar.")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(icon)
                    .build();
        } else{
            notification = new Notification.Builder(getApplicationContext())
                    .setContentTitle("Gota fast")
                    .setContentText("Duerme tranquilo, nosotros te avisamos para bajar.")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(icon)
                    .getNotification();
        }


        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        notification.contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);

        startForeground(ID_LOCATION_SERVICE, notification);
        ///////////////
        mLocationClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();


        mLocationRequest.setInterval(LOCATION_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_LOCATION_INTERVAL);


        int priority = LocationRequest.PRIORITY_HIGH_ACCURACY; //by default
        //PRIORITY_BALANCED_POWER_ACCURACY, PRIORITY_LOW_POWER, PRIORITY_NO_POWER are the other priority modes


        mLocationRequest.setPriority(priority);
        mLocationClient.connect();

        //Make it stick to the notification panel so it is less prone to get cancelled by the Operating System.
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /*
     * LOCATION CALLBACKS
     */
    @Override
    public void onConnected(Bundle dataBundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "== Error On onConnected() Permission not granted");
            return;
        }
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        // do work here
                        onLocationChanged(locationResult.getLastLocation());
                    }
                },
                Looper.myLooper()
        );
        Log.d(TAG, "Connected to Google API");
    }

    /*
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Connection suspended");
    }

    //to get the location change
    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Location changed");


        if (location != null) {
            Log.d(TAG, "location not null and: " + location.getLatitude() + " , " + location.getLongitude()); // > usuario
            destino = new LatLng(lat, lng);
            //  Toast.makeText(getBaseContext(), location.getLatitude() + " , " + location.getLongitude(), Toast.LENGTH_SHORT).show();
            //  Toast.makeText(getBaseContext(), "destino: " + destino.toString(), Toast.LENGTH_SHORT).show();
            //  Send result to activities
            if (test++ == 1) {
                //&& < 200
                Log.d(TAG, "Distancia: " + Util.calculateDistance(new LatLng(location.getLatitude(), location.getLongitude()), destino) + " metros.");
                Intent intent = new Intent(this, WarnActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    stopForeground(STOP_FOREGROUND_REMOVE);
                }
            }

            //   sendMessageToUI(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
        }

    }

    private void sendMessageToUI(String lat, String lng) {

        Log.d(TAG, "Sending info...");

        Intent intent = new Intent(ACTION_LOCATION_BROADCAST);
        intent.putExtra(EXTRA_LATITUDE, lat);
        intent.putExtra(EXTRA_LONGITUDE, lng);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Failed to connect to Google API");

    }

}