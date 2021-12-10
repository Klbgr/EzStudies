package com.ezstudies.app.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ezstudies.app.R;
import com.ezstudies.app.RouteCalculator;

public class Overview extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.overview_layout);
        route();
    }

    @Override
    protected void onResume() {
        super.onResume();
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
        try {
            SharedPreferences sharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE);
            int mode = sharedPreferences.getInt("travel_mode", 0);
            int duration;
            if(mode != 2){ //not transit
                String homeLat = sharedPreferences.getString("home_latitude", null);
                String homeLong = sharedPreferences.getString("home_longitude", null);
                String schoolLat = sharedPreferences.getString("school_latitude", null);
                String schoolLong = sharedPreferences.getString("school_longitude", null);
                RouteCalculator routeCalculator = new RouteCalculator(homeLat, homeLong, schoolLat, schoolLong, mode);
                routeCalculator.start();
                routeCalculator.join();
                duration = routeCalculator.getDuration();
            }
            else{
                duration = sharedPreferences.getInt("travel_time", -1)*60;
            }
            TextView textView = findViewById(R.id.overview_duration);
            textView.setText(getString(R.string.duration) + " " + duration + "s");

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
