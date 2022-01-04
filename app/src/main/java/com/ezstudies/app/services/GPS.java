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

/**
 * Service that gets location from GPS sensor
 */
public class GPS extends Service {
    /**
     * On bind
     * @param intent Intent
     * @return IBinder
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * On start command
     * @param intent Intent
     * @param flags Flags
     * @param startId ID
     * @return Success
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new GpsListener();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 10, locationListener);
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * LocationListener
     */
    private class GpsListener implements LocationListener{
        /**
         * On location changed
         * @param location Location
         */
        @Override
        public void onLocationChanged(@NonNull Location location) {
            Intent intent = new Intent("MyMapView");
            intent.putExtra("latitude", location.getLatitude());
            intent.putExtra("longitude", location.getLongitude());
            sendBroadcast(intent);
        }
    }
}
