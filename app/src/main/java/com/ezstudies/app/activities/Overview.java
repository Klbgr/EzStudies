package com.ezstudies.app.activities;

import android.app.AlarmManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ezstudies.app.R;

public class Overview extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.overview_layout);
        sharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE);
        LinearLayout group0 = findViewById(R.id.settings_group0);
        LinearLayout group1 = findViewById(R.id.settings_group1);

        setInfo();
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
        int mode = sharedPreferences.getInt("travel_mode", 0);
        int duration;
        if(mode != 2){ //not transit
            duration = sharedPreferences.getInt("duration", -1);
        }
        else{
            duration = sharedPreferences.getInt("travel_time", -1)*60;
        }
        /*TextView textView = findViewById(R.id.overview_duration);
        textView.setText(getString(R.string.duration, duration));*/
    }

    public void setInfo() {
        //Show next alarm
        /*AlarmManager m = (AlarmManager)Overview.getSystemService(Overview.ALARM_SERVICE);
        AlarmManager.AlarmClockInfo aci = m.getNextAlarmClock();
        TextView textView = findViewById(R.id.overview_wake_up);
        textView.setText(aci.toString());*/

        //Show Travel time from Settings line 285
        Intent intent = getIntent();
        String name = intent.getStringExtra("travel_time");
    }
}
