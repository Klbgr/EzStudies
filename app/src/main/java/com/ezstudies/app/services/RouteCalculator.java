package com.ezstudies.app.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Service that gets travel time between to location, using Bing Route REST API
 */
public class RouteCalculator extends Service implements Runnable {
    /**
     * API key
     */
    private final String KEY = "AnzK_4L_IgHGYge-UleemNLa29Iro40gMPcTPhrI2HX3kZdbw4smKTy434uDNXk5";
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
            url = "https://dev.virtualearth.net/REST/v1/Routes/" + travel_mode + "?waypoint.1=" + homeLat + "," + homeLong + "&waypoint.2=" + schoolLat + "," + schoolLong + "&key=" + KEY;
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
        json = null;
        int duration = 0;
        try {
            InputStream inputStream = new URL(url).openStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
            String line;
            String jsonText = "";
            while ((line = bufferedReader.readLine()) != null) {
                jsonText += line + "\n";
            }
            json = new JSONObject(jsonText);
            inputStream.close();

            duration = json.getJSONArray("resourceSets").getJSONObject(0).getJSONArray("resources").getJSONObject(0).getInt("travelDuration");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(this.intent.getStringExtra("target"));
        intent.putExtra("target", this.intent.getStringExtra("target"));
        intent.putExtra("duration", duration);

        sendBroadcast(intent);
    }
}
