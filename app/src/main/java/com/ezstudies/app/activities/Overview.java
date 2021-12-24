package com.ezstudies.app.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ezstudies.app.R;
import com.ezstudies.app.services.RouteCalculator;

public class Overview extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.overview_layout);
        /*
        Intent intent1 = new Intent(AlarmClock.ACTION_SET_ALARM);
        intent1.putExtra(AlarmClock.EXTRA_HOUR, 14);
        intent1.putExtra(AlarmClock.EXTRA_MINUTES, 17);
        intent1.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
        intent1.putExtra(AlarmClock.EXTRA_MESSAGE, "coucou");
        startActivity(intent1);

         */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.overview_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.overview_agenda:
                startActivity(new Intent(this, Agenda.class));
                break;
            case R.id.overview_homework:
                startActivity(new Intent(this, Homeworks.class));
                break;
            case R.id.overview_settings:
                startActivity(new Intent(this, Settings.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        route();
    }

    public void route(){
        SharedPreferences sharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE);
        int mode = sharedPreferences.getInt("travel_mode", 0);
        if(mode != 2){ //not transit
            String homeLat = sharedPreferences.getString("home_latitude", null);
            String homeLong = sharedPreferences.getString("home_longitude", null);
            String schoolLat = sharedPreferences.getString("school_latitude", null);
            String schoolLong = sharedPreferences.getString("school_longitude", null);

            Intent intent = new Intent(this, RouteCalculator.class);
            intent.putExtra("mode", mode);
            intent.putExtra("homeLat", homeLat);
            intent.putExtra("homeLong", homeLong);
            intent.putExtra("schoolLat", schoolLat);
            intent.putExtra("schoolLong", schoolLong);
            startService(intent);
            broadcastReceiver broadcastReceiver = new broadcastReceiver();
            registerReceiver(broadcastReceiver, new IntentFilter("route"));
        }
        else{
            int duration = sharedPreferences.getInt("travel_time", -1)*60;
            TextView textView = findViewById(R.id.overview_duration);
            textView.setText(getString(R.string.duration, duration));
        }
    }

    private class broadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            int duration = intent.getIntExtra("duration", -1);
            TextView textView = findViewById(R.id.overview_duration);
            textView.setText(getString(R.string.duration, duration));
        }
    }
}
