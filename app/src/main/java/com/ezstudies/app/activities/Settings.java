package com.ezstudies.app.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
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

import com.ezstudies.app.Login;
import com.ezstudies.app.R;
import com.ezstudies.app.myMapView;

import java.io.IOException;
import java.net.URI;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Settings extends AppCompatActivity {
    private Spinner travel_spinner;
    private Spinner agenda_spinner;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Date date = null;
    private int count = 0;
    private Toast toast;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);

        sharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        LinearLayout group0 = findViewById(R.id.settings_group0);
        LinearLayout group1 = findViewById(R.id.settings_group1);
        LinearLayout group2 = findViewById(R.id.settings_group2);
        LinearLayout group3 = findViewById(R.id.settings_group3);

        agenda_spinner = findViewById(R.id.settings_agenda_spinner);
        ArrayAdapter<CharSequence> agenda_spinner_adapter = ArrayAdapter.createFromResource(this, R.array.agenda_array, android.R.layout.simple_spinner_item);
        agenda_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        agenda_spinner.setAdapter(agenda_spinner_adapter);
        agenda_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                editor.putInt("import_mode", position);
                editor.apply();

                switch (position){
                    case 0:
                        group0.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        group0.setVisibility(View.GONE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        travel_spinner = findViewById(R.id.settings_travel_spinner);
        ArrayAdapter<CharSequence> travel_spinner_adapter = ArrayAdapter.createFromResource(this, R.array.travel_array, android.R.layout.simple_spinner_item);
        travel_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        travel_spinner.setAdapter(travel_spinner_adapter);
        travel_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPrefs();
    }

    @Override
    public void onBackPressed() {
        Boolean condition1 = false;
        Boolean condition2 = false;
        int prep_time;
        int mode = sharedPreferences.getInt("travel_mode", -1);
        switch (mode){
            case 0: //driving
            case 1: //walking
                String home_latitude = sharedPreferences.getString("home_latitude", null);
                String home_longitude = sharedPreferences.getString("home_longitude", null);
                String school_latitude = sharedPreferences.getString("school_latitude", null);
                String school_longitude = sharedPreferences.getString("school_longitude", null);
                prep_time = sharedPreferences.getInt("prep_time", -1);
                if(home_latitude != null && home_longitude != null && school_latitude != null && school_longitude != null && prep_time != -1) {
                    condition1 = true;
                }
                break;
            case 2: // transit
                prep_time = sharedPreferences.getInt("prep_time", -1);
                int travel_time = sharedPreferences.getInt("travel_time", -1);
                if (prep_time != -1 && travel_time != -1){
                    condition1 = true;
                }
                break;
            case -1: //no mode
                break;
        }
        int import_mode = sharedPreferences.getInt("import_mode", -1);
        switch (import_mode){
            case 0: //celcat
                Boolean connected = sharedPreferences.getBoolean("connected", false);
                if(connected){
                    condition2 = true;
                }
                else {
                    condition2 = false;
                }
                break;
            case 1: //ics
                condition2 = true;
                break;
            case -1:
                break;
        }

        Boolean conditions = condition1 && condition2;
        if(conditions){
            super.onBackPressed();
        }
        else{
            Toast.makeText(this, R.string.invalid_settings, Toast.LENGTH_SHORT).show();
        }
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
            prep_time = getString(R.string.minutes, ptime);
        }
        else{
            editor.putInt("prep_time", 30);
            editor.apply();
            prep_time = getString(R.string.minutes, 30);
        }
        textView.setText(prep_time);

        int ttime = sharedPreferences.getInt("travel_time", -1);
        textView = findViewById(R.id.settings_travel_time);
        String travel_time;
        if(ttime != -1){
            travel_time = getString(R.string.minutes, ttime);
        }
        else{
            editor.putInt("travel_time", 30);
            editor.apply();
            travel_time = getString(R.string.minutes, 30);
        }
        textView.setText(travel_time);

        int travel_mode = sharedPreferences.getInt("travel_mode", 0);
        travel_spinner.setSelection(travel_mode);

        int import_mode = sharedPreferences.getInt("import_mode", 0);
        agenda_spinner.setSelection(import_mode);

        boolean connected = sharedPreferences.getBoolean("connected", false);
        textView = findViewById(R.id.settings_status);
        String text;
        if(connected){
            String name = sharedPreferences.getString("name", null);
            text = getString(R.string.connected_as, name);
        }
        else{
            text = getString(R.string.not_connected);
        }
        textView.setText(text);
    }

    public void setOnClickListeners(){
        LinearLayout click0 = findViewById(R.id.settings_click0);
        click0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                agenda_spinner.performClick();
            }
        });

        LinearLayout click1 = findViewById(R.id.settings_click1);
        click1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
                builder.setTitle(getString(R.string.connect_celcat));

                LinearLayout linearLayout = new LinearLayout(Settings.this);
                linearLayout.setOrientation(LinearLayout.VERTICAL);

                EditText name = new EditText(Settings.this);
                name.setHint(R.string.name_hint);
                linearLayout.addView(name);

                EditText password = new EditText(Settings.this);
                password.setHint(R.string.password_hint);
                password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                linearLayout.addView(password);

                builder.setView(linearLayout);

                builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try{
                            String nameText = name.getText().toString();
                            String passwordText = password.getText().toString();
                            Login login = new Login(nameText, passwordText);
                            login.start();
                            login.join();

                            if(login.isSuccess()) {
                                editor.putString("name", nameText);
                                editor.putString("password", passwordText);
                                editor.putBoolean("connected", true);
                                editor.apply();

                                Toast.makeText(Settings.this, getString(R.string.login_succes), Toast.LENGTH_SHORT).show();

                                TextView textView = findViewById(R.id.settings_status);
                                textView.setText(getString(R.string.connected_as, nameText));
                            }
                            else{
                                String response = login.getResponseUrl();
                                String text;
                                if (response.equals("https://services-web.u-cergy.fr/calendar/LdapLogin/Logon")) {
                                    text = getString(R.string.login_fail_credentials);
                                }
                                else {
                                    text = getString(R.string.login_fail_network);
                                }
                                Toast.makeText(Settings.this, text, Toast.LENGTH_SHORT).show();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
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

        LinearLayout click2 = findViewById(R.id.settings_click2);
        click2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                travel_spinner.performClick();
            }
        });

        LinearLayout click3 = findViewById(R.id.settings_click3);
        click3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.this, myMapView.class);
                intent.putExtra("type", "home");
                startActivity(intent);
            }
        });

        LinearLayout click4 = findViewById(R.id.settings_click4);
        click4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.this, myMapView.class);
                intent.putExtra("type", "school");
                startActivity(intent);
            }
        });

        LinearLayout click5 = findViewById(R.id.settings_click5);
        click5.setOnClickListener(new View.OnClickListener() {
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
                            textView.setText(getString(R.string.minutes, time));
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

        LinearLayout click6 = findViewById(R.id.settings_click6);
        click6.setOnClickListener(new View.OnClickListener() {
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
                            textView.setText(getString(R.string.minutes, time));
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

        LinearLayout click7 = findViewById(R.id.settings_click7);
        click7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date now = Calendar.getInstance().getTime();
                if(toast != null) {
                    toast.cancel();
                }
                if(date == null || now.getTime() - date.getTime() > 5*1000){ //5s
                    date = Calendar.getInstance().getTime();
                    count = 1;
                    toast = Toast.makeText(Settings.this, getString(R.string.easter_egg_count, 5-count), Toast.LENGTH_SHORT);
                }
                else if(count == 4){
                    toast = Toast.makeText(Settings.this, getString(R.string.easter_egg_enjoy), Toast.LENGTH_SHORT);
                    date = null;
                    count = 0;
                    Uri uri = Uri.parse("https://youtu.be/dQw4w9WgXcQ");
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
                else{
                    count ++;
                    toast = Toast.makeText(Settings.this, getString(R.string.easter_egg_count, 5-count), Toast.LENGTH_SHORT);
                }
                toast.show();
            }
        });
    }
}
