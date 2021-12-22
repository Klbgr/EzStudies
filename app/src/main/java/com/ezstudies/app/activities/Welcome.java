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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.ezstudies.app.R;
import com.ezstudies.app.services.Login;

import java.io.IOException;
import java.util.Locale;

public class Welcome extends FragmentActivity {
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.69 Safari/537.36";
    public static final String LOGIN_FORM_URL  = "https://services-web.u-cergy.fr/calendar/LdapLogin";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private ViewPager2 viewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        Boolean firstTime = sharedPreferences.getBoolean("first_time", true);

        if(firstTime){
            viewPager = new ViewPager2(this);
            FragmentStateAdapter pagerAdapter = new ScreenSlidePagerAdapter(this);
            viewPager.setAdapter(pagerAdapter);
            setContentView(viewPager);
            if(savedInstanceState != null){
                int current_page = savedInstanceState.getInt("current_page", 0);
                viewPager.setCurrentItem(current_page);
            }
        }
        else {
            finish();
            startActivity(new Intent(this, Overview.class));
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt("current_page", viewPager.getCurrentItem());
        super.onSaveInstanceState(outState);
    }

    public void enter(View view){
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
            editor.putBoolean("first_time", false);
            editor.apply();
            finish();
            startActivity(new Intent(this, Overview.class));
        }
        else{
            Toast.makeText(this, R.string.finish_setup, Toast.LENGTH_SHORT).show();
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentStateAdapter {
        public ScreenSlidePagerAdapter(FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @Override
        public Fragment createFragment(int position) {
            Fragment page = null;
            switch (position){
                case 0:
                    page =  new WelcomeFragment(1);
                    break;
                case 1:
                    page =  new WelcomeFragment(2);
                    break;
                default:
                    break;
            }
            return page;
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }
}

class WelcomeFragment extends Fragment {
    private int page;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private View view;
    private Spinner travel_spinner;
    private Spinner agenda_spinner;
    private ProgressDialog progressDialog;

    public WelcomeFragment(){}

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

                LinearLayout group0 = view.findViewById(R.id.settings_group0);
                LinearLayout group1 = view.findViewById(R.id.settings_group1);
                LinearLayout group2 = view.findViewById(R.id.settings_group2);
                LinearLayout group3 = view.findViewById(R.id.settings_group3);
                LinearLayout group5 = view.findViewById(R.id.settings_click7);
                group5.setVisibility(View.GONE);

                agenda_spinner = view.findViewById(R.id.settings_agenda_spinner);
                ArrayAdapter<CharSequence> agenda_spinner_adapter = ArrayAdapter.createFromResource(getActivity(), R.array.agenda_array, android.R.layout.simple_spinner_item);
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

                travel_spinner = view.findViewById(R.id.settings_travel_spinner);
                ArrayAdapter<CharSequence> travel_spinner_adapter = ArrayAdapter.createFromResource(getActivity(), R.array.travel_array, android.R.layout.simple_spinner_item);
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
            prep_time = getString(R.string.minutes, ptime);
        }
        else{
            editor.putInt("prep_time", 30);
            editor.apply();
            prep_time = getString(R.string.minutes, 30);
        }
        textView.setText(prep_time);

        int ttime = sharedPreferences.getInt("travel_time", -1);
        textView = view.findViewById(R.id.settings_travel_time);
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
        textView = view.findViewById(R.id.settings_status);
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
        LinearLayout click0 = view.findViewById(R.id.settings_click0);
        click0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                agenda_spinner.performClick();
            }
        });

        LinearLayout click1 = view.findViewById(R.id.settings_click1);
        click1.setOnClickListener(new View.OnClickListener() {
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
                        String target = "WelcomeLogin";
                        Intent intent = new Intent(getContext(), Login.class);
                        intent.putExtra("name", nameText);
                        intent.putExtra("password", passwordText);
                        intent.putExtra("target", target);
                        getActivity().startService(intent);
                        broadcastReceiver broadcastReceiver = new broadcastReceiver();
                        getActivity().registerReceiver(broadcastReceiver, new IntentFilter(target));
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

        LinearLayout click2 = view.findViewById(R.id.settings_click2);
        click2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                travel_spinner.performClick();
            }
        });

        LinearLayout click3 = view.findViewById(R.id.settings_click3);
        click3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WelcomeFragment.this.getActivity(), myMapView.class);
                intent.putExtra("type", "home");
                startActivity(intent);
            }
        });

        LinearLayout click4 = view.findViewById(R.id.settings_click4);
        click4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WelcomeFragment.this.getActivity(), myMapView.class);
                intent.putExtra("type", "school");
                startActivity(intent);
            }
        });

        LinearLayout click5 = view.findViewById(R.id.settings_click5);
        click5.setOnClickListener(new View.OnClickListener() {
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
                        try{
                            int time = Integer.parseInt(editText.getText().toString().replace(" ", ""));
                            editor.putInt("travel_time", time);
                            editor.apply();
                            TextView textView = view.findViewById(R.id.settings_travel_time);
                            textView.setText(getString(R.string.minutes, time));
                        }
                        catch (NumberFormatException e){
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

        LinearLayout click6 = view.findViewById(R.id.settings_click6);
        click6.setOnClickListener(new View.OnClickListener() {
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
                        try{
                            int time = Integer.parseInt(editText.getText().toString().replace(" ", ""));
                            editor.putInt("prep_time", time);
                            editor.apply();
                            TextView textView = view.findViewById(R.id.settings_prep_time);
                            textView.setText(getString(R.string.minutes, time));
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

    private class broadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
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

                Toast.makeText(getActivity(), getString(R.string.login_succes), Toast.LENGTH_SHORT).show();

                TextView textView = view.findViewById(R.id.settings_status);
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
                Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
