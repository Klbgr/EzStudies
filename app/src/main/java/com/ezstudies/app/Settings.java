package com.ezstudies.app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

    public void home(View view){
        Intent intent = new Intent(this, myMapView.class);
        intent.putExtra("type", "home");
        startActivity(intent);
    }

    public void school(View view){
        Intent intent = new Intent(this, myMapView.class);
        intent.putExtra("type", "school");
        startActivity(intent);
    }

    public void time(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.prep_time));

        EditText editText = new EditText(this);
        editText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
        builder.setView(editText);

        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try{
                    int time = Integer.parseInt(editText.getText().toString().replace(" ", ""));
                    SharedPreferences.Editor editor = getSharedPreferences("prefs", Context.MODE_PRIVATE).edit();
                    editor.putInt("time", time);
                    editor.commit();
                    TextView textView = findViewById(R.id.settings_time);
                    textView.setText(time + " " + getString(R.string.minutes));
                }
                catch (NumberFormatException e){
                    Toast.makeText(Settings.this, R.string.invalid_input, Toast.LENGTH_SHORT).show();
                }

            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPrefs();
    }

    public void loadPrefs(){
        SharedPreferences sharedPreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE);

        String home_longitude = sharedPreferences.getString("home_longitude", null);
        String home_latitude = sharedPreferences.getString("home_latitude", null);
        if(home_longitude != null && home_latitude != null){
            TextView textView = findViewById(R.id.settings_home);
            String address = home_latitude + "\n" + home_longitude;
            try {
                address = new Geocoder(this, Locale.getDefault()).getFromLocation(Double.parseDouble(home_latitude), Double.parseDouble(home_longitude), 1).get(0).getAddressLine(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
            textView.setText(address);
        }

        String school_longitude = sharedPreferences.getString("school_longitude", null);
        String school_latitude = sharedPreferences.getString("school_latitude", null);
        if(school_longitude != null && school_latitude != null){
            TextView textView = findViewById(R.id.settings_school);
            String address = school_latitude + "\n" + school_longitude;
            try {
                address = new Geocoder(this, Locale.getDefault()).getFromLocation(Double.parseDouble(school_latitude), Double.parseDouble(school_longitude), 1).get(0).getAddressLine(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
            textView.setText(address);
        }

        int time = sharedPreferences.getInt("time", -1);
        TextView textView = findViewById(R.id.settings_time);
        String prep_time;
        if(time != -1){
            prep_time = time + " " + getString(R.string.minutes);
        }
        else{
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("time", 30);
            editor.commit();
            prep_time = 30 + " " + getString(R.string.minutes);
        }
        textView.setText(prep_time);
    }
}
