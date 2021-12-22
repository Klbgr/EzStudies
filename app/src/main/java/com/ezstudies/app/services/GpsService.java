package com.ezstudies.app.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class GpsService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new GpsListener();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 10, locationListener);
        return super.onStartCommand(intent, flags, startId);
    }

    private class GpsListener implements LocationListener{

        @Override
        public void onLocationChanged(@NonNull Location location) {
            Intent intent = new Intent("GPS");
            intent.putExtra("latitude", location.getLatitude());
            intent.putExtra("longitude", location.getLongitude());
            sendBroadcast(intent);
        }
    }
}
