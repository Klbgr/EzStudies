package com.ezstudies.app;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Get a JSON Object from an URL
 */
public class JSONFromURL extends Thread {
    /**
     * URL of JSON stream
     */
    private String url;
    /**
     * JSON
     */
    private JSONObject json;

    /**
     * Constructor
     *
     * @param url URL
     */
    public JSONFromURL(String url) {
        this.url = url;
    }

    /**
     * Start
     */
    @Override
    public void run() {
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
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get JSONObject
     *
     * @return JSONObject
     */
    public JSONObject getJson() {
        return json;
    }
}
