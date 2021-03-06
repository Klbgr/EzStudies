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
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.ezstudies.app.R;
import com.ezstudies.app.services.Login;

import java.io.IOException;
import java.util.Locale;

/**
 * Fragments of Welcome
 */
public class WelcomeFragment extends Fragment {
    /**
     * Page of ViewPager
     */
    private int page;
    /**
     * Shared preferences
     */
    private SharedPreferences sharedPreferences;
    /**
     * Shared preferences editor
     */
    private SharedPreferences.Editor editor;
    /**
     * View to be displayed
     */
    private View view;
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
     * Theme spinner
     */
    private Spinner theme_spinner;
    /**
     * Loading dialog
     */
    private ProgressDialog progressDialog;
    /**
     * Broadcast receiver for Login
     */
    private LoginReceiver loginReceiver;
    /**
     * Switch
     */
    private SwitchCompat s;

    /**
     * Constructor
     */
    public WelcomeFragment() {
    }

    /**
     * Constructor
     *
     * @param page Requested page
     */
    public WelcomeFragment(int page) {
        this.page = page;
    }

    /**
     * On create view
     *
     * @param inflater           LayoutInflater
     * @param container          ViewGroup
     * @param savedInstanceState Bundle
     * @return View
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        switch (page) {
            case 1:
                view = inflater.inflate(R.layout.welcome_page1, container, false);
                break;
            case 2:
                view = inflater.inflate(R.layout.welcome_page2, container, false);

                sharedPreferences = getActivity().getSharedPreferences(Settings.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
                editor = sharedPreferences.edit();

                LinearLayout group0 = view.findViewById(R.id.settings_group0);
                LinearLayout group1 = view.findViewById(R.id.settings_group1);
                LinearLayout group2 = view.findViewById(R.id.settings_group2);
                LinearLayout group3 = view.findViewById(R.id.settings_group3);
                LinearLayout group4 = view.findViewById(R.id.settings_group4);
                LinearLayout group5 = view.findViewById(R.id.settings_group5);
                LinearLayout click11 = view.findViewById(R.id.settings_click11);

                group4.setVisibility(View.GONE);
                group5.setVisibility(View.GONE);
                click11.setVisibility(View.GONE);

                //theme
                theme_spinner = view.findViewById(R.id.settings_theme_spinner);
                ArrayAdapter<CharSequence> theme_spinner_adapter = ArrayAdapter.createFromResource(getActivity(), R.array.theme_array, android.R.layout.simple_spinner_item);
                theme_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                theme_spinner.setAdapter(theme_spinner_adapter);
                theme_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    /**
                     * On item selected
                     * @param parent AdapterView
                     * @param view View
                     * @param position Position
                     * @param id ID
                     */
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        editor.putInt("theme", position);
                        editor.apply();
                        switch (position) {
                            case 0:
                                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                                break;
                            case 1:
                                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                                break;
                            case 2:
                                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                                break;
                        }
                    }

                    /**
                     * On nothing selected
                     * @param parent AdapterView
                     */
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

                //agenda
                agenda_spinner = view.findViewById(R.id.settings_agenda_spinner);
                ArrayAdapter<CharSequence> agenda_spinner_adapter = ArrayAdapter.createFromResource(getActivity(), R.array.agenda_array, android.R.layout.simple_spinner_item);
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

                        switch (position) {
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
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

                //travel
                travel_spinner = view.findViewById(R.id.settings_travel_spinner);
                ArrayAdapter<CharSequence> travel_spinner_adapter = ArrayAdapter.createFromResource(getActivity(), R.array.travel_array, android.R.layout.simple_spinner_item);
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

                        switch (position) {
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

                    /**
                     * On nothing selected
                     * @param parent AdapterView
                     */
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

                //alarm
                alarm_spinner = view.findViewById(R.id.settings_alarm_spinner);
                ArrayAdapter<CharSequence> alarm_spinner_adapter = ArrayAdapter.createFromResource(getActivity(), R.array.alarm_array, android.R.layout.simple_spinner_item);
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
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

                //switch
                s = view.findViewById(R.id.settings_switch);
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
                        TextView textView = view.findViewById(R.id.settings_alarm);
                        String text;
                        if (isChecked) {
                            text = getString(R.string.enabled);
                            group4.setVisibility(View.VISIBLE);
                        } else {
                            text = getString(R.string.disabled);
                            group4.setVisibility(View.GONE);
                        }
                        textView.setText(text);
                    }
                });

                setOnClickListeners();

                loadPrefs();
                break;
            default:
                break;
        }
        return view;
    }

    /**
     * On resume
     */
    @Override
    public void onResume() {
        super.onResume();
        if (page == 2) {
            updateLocation();
        }
    }

    /**
     * Update location names
     */
    public void updateLocation() {
        //home
        String home_longitude = sharedPreferences.getString("home_longitude", null);
        String home_latitude = sharedPreferences.getString("home_latitude", null);
        if (home_longitude != null && home_latitude != null) {
            TextView textView = view.findViewById(R.id.settings_home);
            String address = home_latitude + "\n" + home_longitude;
            try {
                address = new Geocoder(getActivity(), Locale.getDefault()).getFromLocation(Double.parseDouble(home_latitude), Double.parseDouble(home_longitude), 1).get(0).getAddressLine(0);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
            textView.setText(address);
        }

        //school
        String school_longitude = sharedPreferences.getString("school_longitude", null);
        String school_latitude = sharedPreferences.getString("school_latitude", null);
        if (school_longitude != null && school_latitude != null) {
            TextView textView = view.findViewById(R.id.settings_school);
            String address = school_latitude + "\n" + school_longitude;
            try {
                address = new Geocoder(getActivity(), Locale.getDefault()).getFromLocation(Double.parseDouble(school_latitude), Double.parseDouble(school_longitude), 1).get(0).getAddressLine(0);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
            textView.setText(address);
        }
    }

    /**
     * Load preferences
     */
    public void loadPrefs() {
        //preparation time
        int ptime = sharedPreferences.getInt("prep_time", -1);
        TextView textView = view.findViewById(R.id.settings_prep_time);
        String prep_time;
        if (ptime != -1) {
            prep_time = getString(R.string.minutes, ptime);
        } else {
            editor.putInt("prep_time", 30);
            editor.apply();
            prep_time = getString(R.string.minutes, 30);
        }
        textView.setText(prep_time);

        //travel time
        int ttime = sharedPreferences.getInt("travel_time", -1);
        textView = view.findViewById(R.id.settings_travel_time);
        String travel_time;
        if (ttime != -1) {
            travel_time = getString(R.string.minutes, ttime);
        } else {
            editor.putInt("travel_time", 30);
            editor.apply();
            travel_time = getString(R.string.minutes, 30);
        }
        textView.setText(travel_time);

        //theme
        int theme = sharedPreferences.getInt("theme", 0);
        theme_spinner.setSelection(theme);

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
        textView = view.findViewById(R.id.settings_status);
        String text;
        if (connected) {
            String name = sharedPreferences.getString("name", null);
            text = getString(R.string.connected_as, name);
        } else {
            text = getString(R.string.not_connected);
        }
        textView.setText(text);

        //alarms
        boolean alarm = sharedPreferences.getBoolean("alarm", false);
        s.setChecked(alarm);
        textView = view.findViewById(R.id.settings_alarm);
        LinearLayout group4 = view.findViewById(R.id.settings_group4);
        if (alarm) {
            text = getString(R.string.enabled);
            group4.setVisibility(View.VISIBLE);
        } else {
            text = getString(R.string.disabled);
            group4.setVisibility(View.GONE);
        }
        textView.setText(text);
    }

    /**
     * Set OnClickListeners
     */
    public void setOnClickListeners() {
        view.findViewById(R.id.settings_click0).setOnClickListener(new View.OnClickListener() {
            /**
             * On click
             * @param view View
             */
            @Override
            public void onClick(View view) {
                theme_spinner.performClick();
            }
        });

        view.findViewById(R.id.settings_click1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                agenda_spinner.performClick();
            }
        });

        view.findViewById(R.id.settings_click2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(getString(R.string.connect_celcat));

                LinearLayout linearLayout = new LinearLayout(getContext());
                linearLayout.setOrientation(LinearLayout.VERTICAL);

                EditText name = new EditText(getContext());
                name.setHint(R.string.name_hint);
                linearLayout.addView(name);

                EditText password = new EditText(getContext());
                password.setHint(R.string.password_hint);
                password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                linearLayout.addView(password);

                builder.setView(linearLayout);

                builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progressDialog = ProgressDialog.show(getContext(), getString(R.string.connecting), getString(R.string.loading), true);
                        String nameText = name.getText().toString();
                        String passwordText = password.getText().toString();

                        loginReceiver = new LoginReceiver();
                        getContext().registerReceiver(loginReceiver, new IntentFilter("WelcomeLogin"));
                        Intent intent = new Intent(getContext(), Login.class);
                        intent.putExtra("name", nameText);
                        intent.putExtra("password", passwordText);
                        intent.putExtra("target", "WelcomeLogin");
                        getContext().startService(intent);
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

        view.findViewById(R.id.settings_click3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                travel_spinner.performClick();
            }
        });

        view.findViewById(R.id.settings_click4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WelcomeFragment.this.getActivity(), MyMapView.class);
                intent.putExtra("type", "home");
                startActivity(intent);
            }
        });

        view.findViewById(R.id.settings_click5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WelcomeFragment.this.getActivity(), MyMapView.class);
                intent.putExtra("type", "school");
                startActivity(intent);
            }
        });

        view.findViewById(R.id.settings_click6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(WelcomeFragment.this.getActivity());
                builder.setTitle(getString(R.string.travel_time));

                EditText editText = new EditText(WelcomeFragment.this.getActivity());
                editText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                editText.setHint(R.string.minute_hint);
                builder.setView(editText);

                builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            int time = Integer.parseInt(editText.getText().toString().replace(" ", ""));
                            editor.putInt("travel_time", time);
                            editor.apply();
                            TextView textView = view.findViewById(R.id.settings_travel_time);
                            textView.setText(getString(R.string.minutes, time));
                        } catch (NumberFormatException e) {
                            Toast.makeText(getActivity(), R.string.invalid_input, Toast.LENGTH_SHORT).show();
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

        view.findViewById(R.id.settings_click7).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(WelcomeFragment.this.getActivity());
                builder.setTitle(getString(R.string.prep_time));

                EditText editText = new EditText(WelcomeFragment.this.getActivity());
                editText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                editText.setHint(R.string.minute_hint);
                builder.setView(editText);

                builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            int time = Integer.parseInt(editText.getText().toString().replace(" ", ""));
                            editor.putInt("prep_time", time);
                            editor.apply();
                            TextView textView = view.findViewById(R.id.settings_prep_time);
                            textView.setText(getString(R.string.minutes, time));
                        } catch (NumberFormatException e) {
                            Toast.makeText(WelcomeFragment.this.getActivity(), R.string.invalid_input, Toast.LENGTH_SHORT).show();
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

        view.findViewById(R.id.settings_click8).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s.toggle();
            }
        });

        view.findViewById(R.id.settings_click9).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarm_spinner.performClick();
            }
        });
    }

    /**
     * Broadcast receiver for Login
     */
    private class LoginReceiver extends BroadcastReceiver {
        /**
         * On receive
         *
         * @param context Context
         * @param intent  Intent
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            context.unregisterReceiver(loginReceiver);
            progressDialog.cancel();
            Boolean success = intent.getBooleanExtra("success", false);
            String name = intent.getStringExtra("name");
            String password = intent.getStringExtra("password");
            String responseUrl = intent.getStringExtra("responseUrl");
            if (success) {
                editor.putString("name", name);
                editor.putString("password", password);
                editor.putBoolean("connected", true);
                editor.apply();

                Toast.makeText(context, getString(R.string.login_succes), Toast.LENGTH_SHORT).show();

                TextView textView = view.findViewById(R.id.settings_status);
                textView.setText(getString(R.string.connected_as, name));
            } else {
                String response = responseUrl;
                String text;
                if (response != null && response.equals("https://services-web.u-cergy.fr/calendar/LdapLogin/Logon")) {
                    text = getString(R.string.login_fail_credentials);
                } else {
                    text = getString(R.string.login_fail_network);
                }
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
