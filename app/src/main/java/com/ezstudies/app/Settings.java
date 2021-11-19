package com.ezstudies.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.Locale;

public class Settings extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);
        loadPrefs();
    }

    public void map(View view){
        startActivity(new Intent(this, myMapView.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPrefs();

    }

    public void loadPrefs(){
        SharedPreferences sharedPreferences = this.getSharedPreferences("location", Context.MODE_PRIVATE);
        String longitude = sharedPreferences.getString("longitude", null);
        String latitude = sharedPreferences.getString("latitude", null);
        if(longitude != null && latitude != null){
            TextView textView = findViewById(R.id.settings_location);
            String address = latitude + "\n" + longitude;
            try {
                address = new Geocoder(this, Locale.getDefault()).getFromLocation(Double.parseDouble(latitude), Double.parseDouble(longitude), 1).get(0).getAddressLine(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
            textView.setText(address);
        }
    }
}
