package com.ezstudies.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Overview extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.overview_layout);
        route();
    }

    public void agenda(View view){
        startActivity(new Intent(this, Agenda.class));
    }

    public void homework(View view){
        startActivity(new Intent(this, Homework.class));
    }

    public void settings(View view){
        startActivity(new Intent(this, Settings.class));
    }

    public void route(){
        //navitia
        SharedPreferences sharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE);
        sharedPreferences.getString("home_latitude", null);
        String key = "AnzK_4L_IgHGYge-UleemNLa29Iro40gMPcTPhrI2HX3kZdbw4smKTy434uDNXk5";
        String wp0 = sharedPreferences.getString("home_latitude", null) + "," + sharedPreferences.getString("home_longitude", null);
        String wp1 = sharedPreferences.getString("school_latitude", null) + "," + sharedPreferences.getString("school_longitude", null);
        String mode = "Driving";
        String url = "https://dev.virtualearth.net/REST/v1/Routes/" + mode + "?wayPoint.1=" + wp0 + "&waypoint.2=" + wp1 + "&key=" + key;
        Log.d("url", url);
    }
}
