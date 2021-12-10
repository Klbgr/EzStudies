package com.ezstudies.app;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;

public class RouteCalculator extends Thread {
    private final String KEY = "AnzK_4L_IgHGYge-UleemNLa29Iro40gMPcTPhrI2HX3kZdbw4smKTy434uDNXk5";
    JSONObject json;
    private String url;
    public RouteCalculator(String homeLat, String homeLong, String schoolLat, String schoolLong, int mode){
        String travel_mode = null;
        switch (mode){
            case 0: //driving
                travel_mode = "Driving";
                break;
            case 1: //walking
                travel_mode = "Walking";
                break;
            default:
                break;
        }
        url = "https://dev.virtualearth.net/REST/v1/Routes/" + travel_mode + "?wayPoint.1=" + homeLat + "," + homeLong + "&waypoint.2=" + schoolLat + "," + schoolLong + "&key=" + KEY;
        Log.d("url", url);
    }

    public void run(){
        json = null;
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
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getDuration(){
        int duration = -1;
        try {
            duration = json.getJSONArray("resourceSets").getJSONObject(0).getJSONArray("resources").getJSONObject(0).getInt("travelDuration");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return duration;
    }
}
