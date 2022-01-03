package com.ezstudies.app.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.ezstudies.app.R;
import com.ezstudies.app.services.GpsService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Activity that displays a MapView and a search bar
 */
public class myMapView extends AppCompatActivity implements OnMapReadyCallback{
    /**
     * Intent
     */
    private Intent intent;
    /**
     * Broadcast receiver
     */
    private broadcastReceiver broadcastReceiver;
    /**
     * MapView
     */
    private MapView mapView;
    /**
     * GoogleMap
     */
    private GoogleMap googleMap;
    /**
     * Marker
     */
    private Marker marker;
    /**
     * Type of location
     */
    private String type;

    /**
     * On create
     * @param savedInstanceState Bundle
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapview_layout);
        mapView = findViewById(R.id.mymapview_map);
        mapView.getMapAsync(this);
        mapView.onCreate(savedInstanceState);
        type = getIntent().getStringExtra("type");
        broadcastReceiver = new broadcastReceiver();
    }

    /**
     * On map ready
     * @param googleMap GoogleMap
     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            /**
             * On map click
             * @param latLng Coordinates
             */
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                setMarker(latLng, false);
            }
        });
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(46.227638, 2.213749), 6)); //France
    }

    /**
     * On start
     */
    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    /**
     * On resume
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * On Pause
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * On stop
     */
    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    /**
     * On destroy
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    /**
     * On save instance state
     * @param outState Bundle
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * On low memory
     */
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    /**
     * Set marker on MapView
     * @param latLng Coordinates
     * @param zoom Level of zoom
     */
    public void setMarker(LatLng latLng, boolean zoom) {
        MarkerOptions markerOptions = new MarkerOptions();
        String label = null;
        try {
            label = new Geocoder(this, Locale.getDefault()).getFromLocation(latLng.latitude, latLng.longitude, 1).get(0).getAddressLine(0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IndexOutOfBoundsException e){
            label = getString(R.string.unknown);
        }
        markerOptions.title(label);
        markerOptions.position(latLng);
        if (marker != null) {
            marker.remove();
        }
        marker = googleMap.addMarker(markerOptions);
        marker.showInfoWindow();
        if(zoom) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        }
    }

    /**
     * Get location from GPS
     */
    public void getLocation(){
        intent = new Intent(this, GpsService.class);
        startService(intent);
        registerReceiver(broadcastReceiver, new IntentFilter("GPS"));
    }

    /**
     * Search for a location
     * @param view View
     */
    public void searchLocation(View view){
        EditText editText = findViewById(R.id.mymapview_input);
        String address = editText.getText().toString();
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> coords = geocoder.getFromLocationName(address, 1);
            if(coords.isEmpty())
                Toast.makeText(this, R.string.address_not_found, Toast.LENGTH_SHORT).show();
            else
                setMarker(new LatLng(coords.get(0).getLatitude(), coords.get(0).getLongitude()), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Check for permissions
     * @param view View
     */
    public void locate(View view){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getLocation();
        }
        else{
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, 101);
        }
    }

    /**
     * Select a location
     * @param view View
     */
    public void ok(View view){
        if(marker != null){
            Double longitude = marker.getPosition().longitude;
            Double latitude = marker.getPosition().latitude;
            SharedPreferences sharedPreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(type + "_longitude", String.valueOf(longitude));
            editor.putString(type + "_latitude", String.valueOf(latitude));
            editor.apply();
            finish();
        }
        else {
            Toast.makeText(this, R.string.please_select_location, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * On request permission result
     * @param requestCode Request code
     * @param permissions Permissions
     * @param grantResults Grant results
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 101: //GPS
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, R.string.perm_granted, Toast.LENGTH_SHORT).show();
                    getLocation();
                }
                else {
                    Toast.makeText(this, R.string.perm_denied, Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }

    /**
     * Broadcast receiver
     */
    private class broadcastReceiver extends BroadcastReceiver{
        /**
         * On receive
         * @param context Context
         * @param intent Intent
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            unregisterReceiver(broadcastReceiver);
            double longitude = intent.getDoubleExtra("longitude", -1);
            double latitude = intent.getDoubleExtra("latitude", -1);
            setMarker(new LatLng(latitude, longitude), true);
            Log.d("location", latitude + " " + longitude);
        }
    }
}