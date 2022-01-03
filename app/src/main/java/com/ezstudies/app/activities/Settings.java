package com.ezstudies.app.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.ezstudies.app.R;
import com.ezstudies.app.services.Login;
import com.ezstudies.app.services.RouteCalculator;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Activity that displays settings of the app
 */
public class Settings extends AppCompatActivity {
    /**
     * Travel mode spinner
     */
    private Spinner travel_spinner;
    /**
     * Agenda importation mode spinner
     */
    private Spinner agenda_spinner;
    /**
     * Alarm ringtone spinner
     */
    private Spinner alarm_spinner;
    /**
     * Shared preferences
     */
    private SharedPreferences sharedPreferences;
    /**
     * Shared preferences editor
     */
    private SharedPreferences.Editor editor;
    /**
     * Date for easter egg
     */
    private Date date = null;
    /**
     * Click count for easter egg
     */
    private int count = 0;
    /**
     * Toast
     */
    private Toast toast;
    /**
     * Loading dialog
     */
    private ProgressDialog progressDialog;
    /**
     * Broadcast receiver
     */
    private broadcastReceiver broadcastReceiver;
    /**
     * Status of process
     */
    private Boolean wait = false;
    /**
     * Switch
     */
    private SwitchCompat s;

    /**
     * On create
     * @param savedInstanceState Bundle
     */
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
        LinearLayout group4 = findViewById(R.id.settings_group4);

        //agenda
        agenda_spinner = findViewById(R.id.settings_agenda_spinner);
        ArrayAdapter<CharSequence> agenda_spinner_adapter = ArrayAdapter.createFromResource(this, R.array.agenda_array, android.R.layout.simple_spinner_item);
        agenda_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        agenda_spinner.setAdapter(agenda_spinner_adapter);
        agenda_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            /**
             * On item selected
             * @param parent AdapterView
             * @param view View
             * @param position Position
             * @param id ID
             */
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

            /**
             * On nothing selected
             * @param parent AdapterView
             */
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        //travel
        travel_spinner = findViewById(R.id.settings_travel_spinner);
        ArrayAdapter<CharSequence> travel_spinner_adapter = ArrayAdapter.createFromResource(this, R.array.travel_array, android.R.layout.simple_spinner_item);
        travel_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        travel_spinner.setAdapter(travel_spinner_adapter);
        travel_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            /**
             * On item selected
             * @param parent AdapterView
             * @param view View
             * @param position Position
             * @param id ID
             */
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

                        refreshRoute();
                        break;
                    case 2:
                        group1.setVisibility(View.GONE);
                        group2.setVisibility(View.GONE);
                        group3.setVisibility(View.VISIBLE);
                        break;
                }
            }

            /**
             * On nothing selected
             * @param parent AdapterView
             */
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        //alarm
        alarm_spinner = findViewById(R.id.settings_alarm_spinner);
        ArrayAdapter<CharSequence> alarm_spinner_adapter = ArrayAdapter.createFromResource(this, R.array.alarm_array, android.R.layout.simple_spinner_item);
        alarm_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        alarm_spinner.setAdapter(alarm_spinner_adapter);
        alarm_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            /**
             * On item selected
             * @param parent AdapterView
             * @param view View
             * @param position Position
             * @param id ID
             */
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                editor.putInt("alarm_ringtone", position);
                editor.apply();
            }

            /**
             * On nothing selected
             * @param parent AdapterView
             */
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        //switch
        s = findViewById(R.id.settings_switch);
        s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            /**
             * On checked changed
             * @param buttonView CompoundButton
             * @param isChecked Is checked
             */
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean("alarm", isChecked);
                editor.apply();
                TextView textView = findViewById(R.id.settings_alarm);
                String text;
                if(isChecked){
                    text = getString(R.string.enabled);
                    group4.setVisibility(View.VISIBLE);
                }
                else{
                    text = getString(R.string.disabled);
                    group4.setVisibility(View.GONE);
                }
                textView.setText(text);
            }
        });

        setOnClickListeners();

        broadcastReceiver = new broadcastReceiver();

        loadPrefs();
    }

    /**
     * Refresh time of travel
     */
    public void refreshRoute(){
        String homeLat = sharedPreferences.getString("home_latitude", null);
        String homeLong = sharedPreferences.getString("home_longitude", null);
        String schoolLat = sharedPreferences.getString("school_latitude", null);
        String schoolLong = sharedPreferences.getString("school_longitude", null);
        int mode = sharedPreferences.getInt("travel_mode", 0);

        if(homeLat != null && homeLong != null && schoolLat != null && schoolLong != null){
            wait = true;
            Intent intent = new Intent(Settings.this, RouteCalculator.class);
            intent.putExtra("mode", mode);
            intent.putExtra("homeLat", homeLat);
            intent.putExtra("homeLong", homeLong);
            intent.putExtra("schoolLat", schoolLat);
            intent.putExtra("schoolLong", schoolLong);
            intent.putExtra("target", "Settings");
            startService(intent);
            registerReceiver(broadcastReceiver, new IntentFilter("Settings"));
        }
    }

    /**
     * On resume
     */
    @Override
    protected void onResume() {
        super.onResume();
        updateLocation();
    }

    /**
     * On back pressed, check if settings are valid
     */
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

        Boolean conditions = condition1 && condition2 && !wait;
        if(conditions){
            super.onBackPressed();
        }
        else{
            Toast.makeText(this, R.string.invalid_settings, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Update location names
     */
    public void updateLocation(){
        //home
        String home_longitude = sharedPreferences.getString("home_longitude", null);
        String home_latitude = sharedPreferences.getString("home_latitude", null);
        if(home_longitude != null && home_latitude != null){
            TextView textView = findViewById(R.id.settings_home);
            String address = home_latitude + "\n" + home_longitude;
            try {
                address = new Geocoder(this, Locale.getDefault()).getFromLocation(Double.parseDouble(home_latitude), Double.parseDouble(home_longitude), 1).get(0).getAddressLine(0);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (IndexOutOfBoundsException e){
                e.printStackTrace();
            }
            textView.setText(address);
        }

        //school
        String school_longitude = sharedPreferences.getString("school_longitude", null);
        String school_latitude = sharedPreferences.getString("school_latitude", null);
        if(school_longitude != null && school_latitude != null){
            TextView textView = findViewById(R.id.settings_school);
            String address = school_latitude + "\n" + school_longitude;
            try {
                address = new Geocoder(this, Locale.getDefault()).getFromLocation(Double.parseDouble(school_latitude), Double.parseDouble(school_longitude), 1).get(0).getAddressLine(0);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (IndexOutOfBoundsException e){
                e.printStackTrace();
            }
            textView.setText(address);
        }
    }

    /**
     * Load preferences
     */
    public void loadPrefs(){
        //preparation time
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

        //travel time
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

        //travel mode
        int travel_mode = sharedPreferences.getInt("travel_mode", 0);
        travel_spinner.setSelection(travel_mode);

        //import mode
        int import_mode = sharedPreferences.getInt("import_mode", 0);
        agenda_spinner.setSelection(import_mode);

        //alarm ringtone
        int alarm_ringtone = sharedPreferences.getInt("alarm_ringtone", 0);
        alarm_spinner.setSelection(alarm_ringtone);

        //connected to Celcat
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

        //alarms
        boolean alarm = sharedPreferences.getBoolean("alarm", false);
        s.setChecked(alarm);
        textView = findViewById(R.id.settings_alarm);
        LinearLayout group4 = findViewById(R.id.settings_group4);
        if(alarm){
            text = getString(R.string.enabled);
            group4.setVisibility(View.VISIBLE);
        }
        else{
            text = getString(R.string.disabled);
            group4.setVisibility(View.GONE);
        }
        textView.setText(text);
    }

    /**
     * Set OnClickListeners
     */
    public void setOnClickListeners(){
        LinearLayout click0 = findViewById(R.id.settings_click0);
        click0.setOnClickListener(new View.OnClickListener() {
            /**
             * On click
             * @param view View
             */
            @Override
            public void onClick(View view) {
                agenda_spinner.performClick();
            }
        });

        LinearLayout click1 = findViewById(R.id.settings_click1);
        click1.setOnClickListener(new View.OnClickListener() {
            /**
             * On click
             * @param view View
             */
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
                    /**
                     * On click
                     * @param dialog DialogInterface
                     * @param which Type of DialogInterface
                     */
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progressDialog = ProgressDialog.show(Settings.this, getString(R.string.connecting), getString(R.string.loading), true);
                        String nameText = name.getText().toString();
                        String passwordText = password.getText().toString();
                        String target = "Settings";
                        Intent intent = new Intent(Settings.this, Login.class);
                        intent.putExtra("name", nameText);
                        intent.putExtra("password", passwordText);
                        intent.putExtra("target", target);
                        startService(intent);
                        registerReceiver(broadcastReceiver, new IntentFilter("Settings"));
                    }
                });
                builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    /**
                     * On click
                     * @param dialog DialogInterface
                     * @param which Type of DialogInterface
                     */
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
            /**
             * On click
             * @param view View
             */
            @Override
            public void onClick(View view) {
                travel_spinner.performClick();
            }
        });

        LinearLayout click3 = findViewById(R.id.settings_click3);
        click3.setOnClickListener(new View.OnClickListener() {
            /**
             * On click
             * @param view View
             */
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.this, myMapView.class);
                intent.putExtra("type", "home");
                startActivity(intent);
            }
        });

        LinearLayout click4 = findViewById(R.id.settings_click4);
        click4.setOnClickListener(new View.OnClickListener() {
            /**
             * On click
             * @param view View
             */
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.this, myMapView.class);
                intent.putExtra("type", "school");
                startActivity(intent);
            }
        });

        LinearLayout click5 = findViewById(R.id.settings_click5);
        click5.setOnClickListener(new View.OnClickListener() {
            /**
             * On click
             * @param view View
             */
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
                builder.setTitle(getString(R.string.travel_time));

                EditText editText = new EditText(Settings.this);
                editText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                editText.setHint(R.string.minute_hint);
                builder.setView(editText);

                builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    /**
                     * On click
                     * @param dialog DialogInterface
                     * @param which Type of DialogInterface
                     */
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
                    /**
                     * On click
                     * @param dialog DialogInterface
                     * @param which Type of DialogInterface
                     */
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
            /**
             * On click
             * @param view View
             */
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
                builder.setTitle(getString(R.string.prep_time));

                EditText editText = new EditText(Settings.this);
                editText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                editText.setHint(R.string.minute_hint);
                builder.setView(editText);

                builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    /**
                     * On click
                     * @param dialog DialogInterface
                     * @param which Type of DialogInterface
                     */
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
                    /**
                     * On click
                     * @param dialog DialogInterface
                     * @param which Type of DialogInterface
                     */
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
            /**
             * On click
             * @param view View
             */
            @Override
            public void onClick(View view) {
                s.toggle();
            }
        });

        LinearLayout click8 = findViewById(R.id.settings_click8);
        click8.setOnClickListener(new View.OnClickListener() {
            /**
             * On click
             * @param view View
             */
            @Override
            public void onClick(View view) {
                alarm_spinner.performClick();
            }
        });

        LinearLayout click9 = findViewById(R.id.settings_click9);
        click9.setOnClickListener(new View.OnClickListener() {
            /**
             * On click
             * @param view View
             */
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
        click9.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Uri uri = Uri.parse("https://github.com/Klbgr/EzStudies");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                return false;
            }
        });
    }

    /**
     * Broadcast receiver
     */
    private class broadcastReceiver extends BroadcastReceiver {
        /**
         * On receive
         * @param context Context
         * @param intent Intent
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            unregisterReceiver(broadcastReceiver);
            int duration = intent.getIntExtra("duration", -2);
            if(duration != -2){
                editor.putInt("duration", duration);
                editor.apply();
                wait = false;
            }
            else{
                progressDialog.cancel();
                Boolean success = intent.getBooleanExtra("success", false);
                String name = intent.getStringExtra("name");
                String password = intent.getStringExtra("password");
                String responseUrl = intent.getStringExtra("responseUrl");
                if(success) {
                    editor.putString("name", name);
                    editor.putString("password", password);
                    editor.putBoolean("connected", true);
                    editor.apply();

                    Toast.makeText(context, getString(R.string.login_succes), Toast.LENGTH_SHORT).show();

                    TextView textView = findViewById(R.id.settings_status);
                    textView.setText(getString(R.string.connected_as, name));
                }
                else{
                    String response = responseUrl;
                    String text;
                    if (response != null && response.equals("https://services-web.u-cergy.fr/calendar/LdapLogin/Logon")) {
                        text = getString(R.string.login_fail_credentials);
                    }
                    else {
                        text = getString(R.string.login_fail_network);
                    }
                    Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
