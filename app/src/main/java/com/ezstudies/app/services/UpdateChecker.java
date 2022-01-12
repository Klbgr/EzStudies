package com.ezstudies.app.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.ezstudies.app.BuildConfig;
import com.ezstudies.app.JSONFromURL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Check for updates from GitHub
 */
public class UpdateChecker extends Service{
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
        try {
            JSONFromURL jsonFromURL = new JSONFromURL("https://api.github.com/repos/Klbgr/EzStudies/releases/latest");
            jsonFromURL.start();
            jsonFromURL.join();
            JSONObject json = jsonFromURL.getJson();
            String currentVersion = BuildConfig.VERSION_NAME;
            String remoteVersion = json.getString("name");
            Intent intent1 = new Intent(intent.getStringExtra("target"));
            if(isGreater(remoteVersion, currentVersion)){
                JSONArray array = json.getJSONArray("assets");
                for(int i = 0 ; i < array.length() ; i++){
                    String type = array.getJSONObject(0).getString("content_type");
                    if(type.equals("application/vnd.android.package-archive")){ //contains apk
                        String name = array.getJSONObject(0).getString("name");
                        String url = array.getJSONObject(0).getString("browser_download_url");
                        String changelog = json.getString("body");
                        intent1.putExtra("update", true);
                        intent1.putExtra("url", url);
                        intent1.putExtra("name", name);
                        intent1.putExtra("changelog", changelog);
                        intent1.putExtra("version", remoteVersion);
                        break;
                    }
                }
            }
            sendBroadcast(intent1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException e){
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * Check if one version is greater than another version
     * @param version1 Version 1
     * @param version2 Version 2
     * @return Is greater
     */
    public boolean isGreater(String version1, String version2){
        int[] v1 = stringToInt(version1.split("\\."));
        int[] v2 = stringToInt(version2.split("\\."));
        if(v1[0] > v2[0]){
            return true;
        }
        else if(v1[0] == v2[0]){
            if(v1[1] > v2[1]){
                return true;
            }
            else if(v1[1] == v2[1]){
                if(v1[2] > v2[2]){
                    return true;
                }
                else if(v1[2] == v2[2]){
                    return false;
                }
                else{
                    return false;
                }
            }
            else{
                return false;
            }
        }
        else{
            return false;
        }
    }

    /**
     * Convert an array of String of an array of Integer
     * @param string Array of String
     * @return Array of Integer
     */
    public int[] stringToInt(String[] string){
        int[] integer = new int[3];
        for(int i = 0 ; i < string.length ; i++){
            integer[i] = Integer.parseInt(string[i]);
        }
        return integer;
    }
}
