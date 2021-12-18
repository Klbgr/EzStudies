package com.ezstudies.app.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.ezstudies.app.R;
import com.ezstudies.app.WelcomeFragment;

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
