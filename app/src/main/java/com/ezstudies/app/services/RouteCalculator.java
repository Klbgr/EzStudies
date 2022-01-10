package com.ezstudies.app.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.ezstudies.app.BuildConfig;
import com.ezstudies.app.JSONFromURL;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Service that gets travel time between to location, using Bing Route REST API
 */
public class RouteCalculator extends Service implements Runnable {
    /**
     * JSON
     */
    private JSONObject json;
    /**
     * URL
     */
    private String url;
    /**
     * Intent
     */
    private Intent intent;

    /**
     * On bind
     * @param intent Intent
     * @return IBinder
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * On start command
     * @param intent Intent
     * @param flags Flags
     * @param startId ID
     * @return Success
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null){
            this.intent = intent;
            int mode = intent.getIntExtra("mode", -1);
            String homeLat = intent.getStringExtra("homeLat");
            String homeLong = intent.getStringExtra("homeLong");
            String schoolLat = intent.getStringExtra("schoolLat");
            String schoolLong = intent.getStringExtra("schoolLong");
            String travel_mode = null;
            switch (mode){
                case 0: //driving
                    travel_mode = "driving";
                    break;
                case 1: //walking
                    travel_mode = "walking";
                    break;
                default:
                    break;
            }
            url = "https://dev.virtualearth.net/REST/v1/Routes/" + travel_mode + "?waypoint.1=" + homeLat + "," + homeLong + "&waypoint.2=" + schoolLat + "," + schoolLong + "&key=" + BuildConfig.BING_API_KEY;
            Log.d("url", url);
            Thread thread = new Thread(this);
            thread.start();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * Start
     */
    public void run(){
        JSONFromURL jsonFromURL = new JSONFromURL(url);
        jsonFromURL.start();
        try {
            jsonFromURL.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        json = jsonFromURL.getJson();
        int duration = 0;
        try {
            duration = json.getJSONArray("resourceSets").getJSONObject(0).getJSONArray("resources").getJSONObject(0).getInt("travelDuration");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(this.intent.getStringExtra("target"));
        intent.putExtra("target", this.intent.getStringExtra("target"));
        intent.putExtra("duration", duration);

        sendBroadcast(intent);
    }
}
