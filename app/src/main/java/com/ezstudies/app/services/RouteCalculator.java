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

public class RouteCalculator extends Service implements Runnable {
    private final String KEY = "AnzK_4L_IgHGYge-UleemNLa29Iro40gMPcTPhrI2HX3kZdbw4smKTy434uDNXk5";
    private JSONObject json;
    private String url;

    public int getDuration(){
        int duration = -1;
        try {
            duration = json.getJSONArray("resourceSets").getJSONObject(0).getJSONArray("resources").getJSONObject(0).getInt("travelDuration");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        catch (NullPointerException e){ //no internet
            return duration;
        }
        return duration;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int mode = intent.getIntExtra("mode", -1);
        String homeLat = intent.getStringExtra("homeLat");
        String homeLong = intent.getStringExtra("homeLong");
        String schoolLat = intent.getStringExtra("schoolLat");
        String schoolLong = intent.getStringExtra("schoolLong");
        String travel_mode = null;
        switch (mode){
            case 0: //driving
                travel_mode = "Driving";
                break;
            case 1: //walking
                travel_mode = "Walking";
                break;
            case -1:
                break;
            default:
                break;
        }
        url = "https://dev.virtualearth.net/REST/v1/Routes/" + travel_mode + "?wayPoint.1=" + homeLat + "," + homeLong + "&wayPoint.2=" + schoolLat + "," + schoolLong + "&key=" + KEY;
        Log.d("url", url);
        Thread thread = new Thread(this);
        thread.start();
        return super.onStartCommand(intent, flags, startId);
    }

    public void run(){
        json = null;
        int duration = -1;
        try {
            InputStream inputStream = new URL(url).openStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
            String line;
            String jsonText = "";
            while ((line = bufferedReader.readLine()) != null){
                jsonText += line + "\n";
            }
            json = new JSONObject(jsonText);
            inputStream.close();


            duration = json.getJSONArray("resourceSets").getJSONObject(0).getJSONArray("resources").getJSONObject(0).getInt("travelDuration");



            
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent("route");
        intent.putExtra("duration", duration);

        sendBroadcast(intent);
    }
}
