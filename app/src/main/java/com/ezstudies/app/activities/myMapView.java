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

public class myMapView extends AppCompatActivity implements OnMapReadyCallback{
    private Intent intent;
    private broadcastReceiver broadcastReceiver;
    public static final int PERMISSION_REQUEST_CODE = 101;
    private MapView mapView;
    private GoogleMap googleMap;
    private Marker marker;
    private String type;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapview_layout);
        mapView = findViewById(R.id.mymapview_map);
        mapView.getMapAsync(this);
        mapView.onCreate(savedInstanceState);

        type = getIntent().getStringExtra("type");
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                setMarker(latLng, false);
            }
        });
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(46.227638, 2.213749), 6)); //France
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

    public void setMarker(LatLng latLng, boolean zoom) {
        MarkerOptions markerOptions = new MarkerOptions();
        String label = null;
        try {
            label = new Geocoder(this, Locale.getDefault()).getFromLocation(latLng.latitude, latLng.longitude, 1).get(0).getAddressLine(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        markerOptions.title(label);
        markerOptions.position(latLng);
        if (marker != null)
            marker.remove();
        marker = googleMap.addMarker(markerOptions);
        marker.showInfoWindow();
        if(zoom)
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
    }

    public void getLocation(){
        intent = new Intent(this, GpsService.class);
        startService(intent);
        broadcastReceiver = new broadcastReceiver();
        registerReceiver(broadcastReceiver, new IntentFilter("GPS"));
    }

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

    public void locate(View view){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getLocation();
        }
        else{
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, PERMISSION_REQUEST_CODE);
        }
    }

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
        else
            Toast.makeText(this, R.string.please_select_location, Toast.LENGTH_SHORT).show();
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
            setMarker(new LatLng(latitude, longitude), true);
            Log.d("myloc", latitude + " " + longitude);
            unregisterReceiver(broadcastReceiver);
        }
    }
}