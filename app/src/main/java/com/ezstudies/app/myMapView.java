package com.ezstudies.app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.ezstudies.app.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

// Implement OnMapReadyCallback.
public class myMapView extends AppCompatActivity implements OnMapReadyCallback{
    public static final int PERMISSION_REQUEST_CODE = 101;
    private MapView mapView;
    private GoogleMap googleMap;
    private Marker marker;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapview_layout);
        mapView = findViewById(R.id.mymapview_map);
        mapView.getMapAsync(this);
        mapView.onCreate(savedInstanceState);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                setMarker("salut", latLng);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    public void setMarker(String text, LatLng latLng) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title(text);
        markerOptions.position(latLng);
        if (marker != null)
            marker.remove();
        marker = googleMap.addMarker(markerOptions);
    }

    public void getLocation(){
        Intent intent = new Intent(this, GpsService.class);
        startService(intent);
        BroadcastReceiver broadcastReceiver = new broadcastReceiver();
        registerReceiver(broadcastReceiver, new IntentFilter("gps"));
        //stopService(intent);
        //unregisterReceiver(broadcastReceiver);
    }

    public void locate(View view){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getLocation();
        }
        else{
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, PERMISSION_REQUEST_CODE);
        }
    }

    public LatLng getPosition(){
        return marker.getPosition();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, R.string.perm_granted, Toast.LENGTH_SHORT).show();
                    getLocation();
                }  else {
                    Toast.makeText(this, R.string.perm_denied, Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }

    private class broadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            double longitude = intent.getDoubleExtra("longitude", -1);
            double latitude = intent.getDoubleExtra("latitude", -1);
            setMarker(null, new LatLng(longitude, latitude));
            Log.d("myloc", longitude + " " + latitude);
        }
    }
}