package com.ezstudies.app.activities;

import android.app.AlertDialog;
import android.content.Context;
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
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);
        loadPrefs();

        Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.travel_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences.Editor editor = getSharedPreferences("prefs", MODE_PRIVATE).edit();
                editor.putInt("travel_mode", position);
                editor.commit();

                LinearLayout group1 = findViewById(R.id.group1);
                LinearLayout group2 = findViewById(R.id.group2);
                LinearLayout group3 = findViewById(R.id.group3);
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
    }

    public void openSpinner(View view){
        Spinner spinner = findViewById(R.id.spinner);
        spinner.performClick();
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

    public void travel_time(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.travel_time));

        EditText editText = new EditText(this);
        editText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
        builder.setView(editText);

        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try{
                    int time = Integer.parseInt(editText.getText().toString().replace(" ", ""));
                    SharedPreferences.Editor editor = getSharedPreferences("prefs", Context.MODE_PRIVATE).edit();
                    editor.putInt("travel_time", time);
                    editor.commit();
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

    public void prep_time(View view){
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
                    editor.putInt("prep_time", time);
                    editor.commit();
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

        int ptime = sharedPreferences.getInt("prep_time", -1);
        TextView textView = findViewById(R.id.settings_prep_time);
        String prep_time;
        if(ptime != -1){
            prep_time = ptime + " " + getString(R.string.minutes);
        }
        else{
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("prep_time", 30);
            editor.commit();
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
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("travel_time", 30);
            editor.commit();
            travel_time = 30 + " " + getString(R.string.minutes);
        }
        textView.setText(travel_time);

        int travel_mode = sharedPreferences.getInt("travel_mode", 0);
        Spinner spinner = findViewById(R.id.spinner);
        spinner.setSelection(travel_mode);
    }
}
