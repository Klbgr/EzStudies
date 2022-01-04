package com.ezstudies.app.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.ezstudies.app.R;
import com.ezstudies.app.services.RouteCalculator;

/**
 * Activity that displays introduction and first setup
 */
public class Welcome extends FragmentActivity {
    /**
     * User agent of WebView
     */
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.69 Safari/537.36";
    /**
     * URL of login
     */
    public static final String LOGIN_FORM_URL  = "https://services-web.u-cergy.fr/calendar/LdapLogin";
    /**
     * Shared preferences
     */
    private SharedPreferences sharedPreferences;
    /**
     * Shared preferences editor
     */
    private SharedPreferences.Editor editor;
    /**
     * ViewPager
     */
    private ViewPager2 viewPager;
    /**
     * Broadcast receiver for RouteCalculator
     */
    private RouteReceiver routeReceiver;

    /**
     * On create
     * @param savedInstanceState Bundle
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        Boolean firstTime = sharedPreferences.getBoolean("first_time", true);

        if(firstTime){
            setContentView(R.layout.welcome_layout);
            viewPager = findViewById(R.id.welcome_viewpager);
            FragmentStateAdapterWelcome fragmentAdapter = new FragmentStateAdapterWelcome(this);
            viewPager.setAdapter(fragmentAdapter);
            viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    ImageView dot0 = findViewById(R.id.welcome_dot0);
                    ImageView dot1 = findViewById(R.id.welcome_dot1);
                    switch (position){
                        case 0:
                            dot0.setImageDrawable(getDrawable(R.drawable.dot_selected));
                            dot1.setImageDrawable(getDrawable(R.drawable.dot));
                            break;
                        case 1:
                            dot0.setImageDrawable(getDrawable(R.drawable.dot));
                            dot1.setImageDrawable(getDrawable(R.drawable.dot_selected));
                            break;
                    }
                }
            });
            if(savedInstanceState != null){ //set current page
                int current_page = savedInstanceState.getInt("current_page", 0);
                viewPager.setCurrentItem(current_page);
            }
        }
        else {
            finish();
            startActivity(new Intent(this, Overview.class));
        }
        routeReceiver = new RouteReceiver();
    }

    /**
     * On save instance state
     * @param outState Bundle
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt("current_page", viewPager.getCurrentItem());
        super.onSaveInstanceState(outState);
    }

    /**
     * Finish setup
     * @param view View
     */
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
                    Intent intent = new Intent(this, RouteCalculator.class);
                    intent.putExtra("mode", mode);
                    intent.putExtra("homeLat", home_latitude);
                    intent.putExtra("homeLong", home_longitude);
                    intent.putExtra("schoolLat", school_latitude);
                    intent.putExtra("schoolLong", school_longitude);
                    intent.putExtra("target", "WelcomeRoute");
                    startService(intent);
                    registerReceiver(routeReceiver, new IntentFilter("WelcomeRoute"));
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
            if(mode == 2){
                finish();
                startActivity(new Intent(this, Overview.class));
            }
        }
        else{
            Toast.makeText(this, R.string.finish_setup, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Create pages of ViewPager
     */
    private class FragmentStateAdapterWelcome extends FragmentStateAdapter {
        /**
         * Constructor
         * @param fragmentActivity FragmentActivity
         */
        public FragmentStateAdapterWelcome(FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        /**
         * Create fragment
         * @param position Position
         * @return Fragment
         */
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

        /**
         * Get number of pages
         * @return Number of pages
         */
        @Override
        public int getItemCount() {
            return 2;
        }
    }

    /**
     * Broadcast receiver for RouteCalculator
     */
    private class RouteReceiver extends BroadcastReceiver{
        /**
         * On receive
         * @param context Context
         * @param intent Intent
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            unregisterReceiver(routeReceiver);
            int duration = intent.getIntExtra("duration", -1);
            editor.putInt("duration", duration);
            editor.apply();
            finish();
            startActivity(new Intent(context, Overview.class));
        }
    }
}

