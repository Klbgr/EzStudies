package com.ezstudies.app;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.util.Locale;

public class WelcomeFragment extends Fragment {
    private int page;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private View view;
    private Spinner spinner;

    public WelcomeFragment(int page){
        this.page = page;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        switch (page){
            case 1:
                view = inflater.inflate(R.layout.welcome_page1, container, false);
                break;
            case 2:
                view = inflater.inflate(R.layout.welcome_page2, container, false);

                sharedPreferences = getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);
                editor = sharedPreferences.edit();

                spinner = view.findViewById(R.id.settings_spinner);
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.travel_array, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);

                LinearLayout group1 = view.findViewById(R.id.settings_group1);
                LinearLayout group2 = view.findViewById(R.id.settings_group2);
                LinearLayout group3 = view.findViewById(R.id.settings_group3);
                LinearLayout group5 = view.findViewById(R.id.settings_click5);

                group5.setVisibility(View.GONE);

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

                break;
            case 3:
                view = inflater.inflate(R.layout.welcome_page3, container, false);

                sharedPreferences = getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);
                editor = sharedPreferences.edit();

                RadioGroup radioGroup = view.findViewById(R.id.welcome_radios);
                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        LinearLayout linearLayout = view.findViewById(R.id.welcome_celcat);
                        switch (group.getCheckedRadioButtonId()){
                            case R.id.welcome_radio1: //celcat
                                linearLayout.setVisibility(View.VISIBLE);
                                editor.putInt("import_mode", 0);
                                editor.apply();
                                break;
                            case R.id.welcome_radio2: //ics
                                linearLayout.setVisibility(View.INVISIBLE);
                                editor.putInt("import_mode", 1);
                                editor.apply();
                                break;
                        }
                    }
                });
                RadioButton radioButton1 = view.findViewById(R.id.welcome_radio1);
                radioButton1.setChecked(true);
                break;
            default:
                break;
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(page == 2){
            loadPrefs();
        }
    }

    public void loadPrefs(){
        String home_longitude = sharedPreferences.getString("home_longitude", null);
        String home_latitude = sharedPreferences.getString("home_latitude", null);
        if(home_longitude != null && home_latitude != null){
            TextView textView = view.findViewById(R.id.settings_home);
            String address = home_latitude + "\n" + home_longitude;
            try {
                address = new Geocoder(getActivity(), Locale.getDefault()).getFromLocation(Double.parseDouble(home_latitude), Double.parseDouble(home_longitude), 1).get(0).getAddressLine(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
            textView.setText(address);
        }

        String school_longitude = sharedPreferences.getString("school_longitude", null);
        String school_latitude = sharedPreferences.getString("school_latitude", null);
        if(school_longitude != null && school_latitude != null){
            TextView textView = view.findViewById(R.id.settings_school);
            String address = school_latitude + "\n" + school_longitude;
            try {
                address = new Geocoder(getActivity(), Locale.getDefault()).getFromLocation(Double.parseDouble(school_latitude), Double.parseDouble(school_longitude), 1).get(0).getAddressLine(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
            textView.setText(address);
        }

        int ptime = sharedPreferences.getInt("prep_time", -1);
        TextView textView = view.findViewById(R.id.settings_prep_time);
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
        textView = view.findViewById(R.id.settings_travel_time);
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
        LinearLayout click0 = view.findViewById(R.id.settings_click0);
        click0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinner.performClick();
            }
        });

        LinearLayout click1 = view.findViewById(R.id.settings_click1);
        click1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WelcomeFragment.this.getActivity(), myMapView.class);
                intent.putExtra("type", "home");
                startActivity(intent);
            }
        });

        LinearLayout click2 = view.findViewById(R.id.settings_click2);
        click2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WelcomeFragment.this.getActivity(), myMapView.class);
                intent.putExtra("type", "school");
                startActivity(intent);
            }
        });

        LinearLayout click3 = view.findViewById(R.id.settings_click3);
        click3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(WelcomeFragment.this.getActivity());
                builder.setTitle(getString(R.string.travel_time));

                EditText editText = new EditText(WelcomeFragment.this.getActivity());
                editText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                builder.setView(editText);

                builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try{
                            int time = Integer.parseInt(editText.getText().toString().replace(" ", ""));
                            editor.putInt("travel_time", time);
                            editor.apply();
                            TextView textView = view.findViewById(R.id.settings_travel_time);
                            textView.setText(time + " " + getString(R.string.minutes));
                        }
                        catch (NumberFormatException e){
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

        LinearLayout click4 = view.findViewById(R.id.settings_click4);
        click4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(WelcomeFragment.this.getActivity());
                builder.setTitle(getString(R.string.prep_time));

                EditText editText = new EditText(WelcomeFragment.this.getActivity());
                editText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                builder.setView(editText);

                builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try{
                            int time = Integer.parseInt(editText.getText().toString().replace(" ", ""));
                            editor.putInt("prep_time", time);
                            editor.apply();
                            TextView textView = view.findViewById(R.id.settings_prep_time);
                            textView.setText(time + " " + getString(R.string.minutes));
                        }
                        catch (NumberFormatException e){
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
    }
}
