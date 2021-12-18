package com.ezstudies.app.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ezstudies.app.R;
import com.ezstudies.app.myMapView;

import java.io.IOException;
import java.util.Locale;

public class Settings extends AppCompatActivity {
    private Spinner spinner;
    private  SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);

        sharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE);
        editor = sharedPreferences.edit();


        spinner = findViewById(R.id.settings_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.travel_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        LinearLayout group1 = findViewById(R.id.settings_group1);
        LinearLayout group2 = findViewById(R.id.settings_group2);
        LinearLayout group3 = findViewById(R.id.settings_group3);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                editor.putInt("travel_mode", position);
                editor.apply();

                switch (position){
                    case 0:
                    case 1:
                        group1.setVisibility(View.VISIBLE);
                        group2.setVisibility(View.VISIBLE);
                        group3.setVisibility(View.GONE);
                        break;
                    case 2:
                        group1.setVisibility(View.GONE);
                        group2.setVisibility(View.GONE);
                        group3.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        setOnClickListeners();
        loadPrefs();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPrefs();
    }

    public void loadPrefs(){
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

        int ptime = sharedPreferences.getInt("prep_time", -1);
        TextView textView = findViewById(R.id.settings_prep_time);
        String prep_time;
        if(ptime != -1){
            prep_time = ptime + " " + getString(R.string.minutes);
        }
        else{
            editor.putInt("prep_time", 30);
            editor.apply();
            prep_time = 30 + " " + getString(R.string.minutes);
        }
        textView.setText(prep_time);

        int ttime = sharedPreferences.getInt("travel_time", -1);
        textView = findViewById(R.id.settings_travel_time);
        String travel_time;
        if(ttime != -1){
            travel_time = ttime + " " + getString(R.string.minutes);
        }
        else{
            editor.putInt("travel_time", 30);
            editor.apply();
            travel_time = 30 + " " + getString(R.string.minutes);
        }
        textView.setText(travel_time);

        int travel_mode = sharedPreferences.getInt("travel_mode", 0);
        spinner.setSelection(travel_mode);
    }

    public void setOnClickListeners(){
        LinearLayout click0 = findViewById(R.id.settings_click0);
        click0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinner.performClick();
            }
        });

        LinearLayout click1 = findViewById(R.id.settings_click1);
        click1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.this, myMapView.class);
                intent.putExtra("type", "home");
                startActivity(intent);
            }
        });

        LinearLayout click2 = findViewById(R.id.settings_click2);
        click2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.this, myMapView.class);
                intent.putExtra("type", "school");
                startActivity(intent);
            }
        });

        LinearLayout click3 = findViewById(R.id.settings_click3);
        click3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
                builder.setTitle(getString(R.string.travel_time));

                EditText editText = new EditText(Settings.this);
                editText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                builder.setView(editText);

                builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try{
                            int time = Integer.parseInt(editText.getText().toString().replace(" ", ""));
                            editor.putInt("travel_time", time);
                            editor.apply();
                            TextView textView = findViewById(R.id.settings_travel_time);
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
        });

        LinearLayout click4 = findViewById(R.id.settings_click4);
        click4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
                builder.setTitle(getString(R.string.prep_time));

                EditText editText = new EditText(Settings.this);
                editText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                builder.setView(editText);

                builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try{
                            int time = Integer.parseInt(editText.getText().toString().replace(" ", ""));
                            editor.putInt("prep_time", time);
                            editor.apply();
                            TextView textView = findViewById(R.id.settings_prep_time);
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
        });

        LinearLayout click5 = findViewById(R.id.settings_click5);
        click5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}
