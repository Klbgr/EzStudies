package com.ezstudies.app.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.ezstudies.app.activities.Welcome;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.FormElement;

import java.util.HashMap;

/**
 * Service that logs into Celcat
 */
public class Login extends Service implements Runnable{
    /**
     * URL of login
     */
    private final String login_url = Welcome.LOGIN_FORM_URL;
    /**
     * User agent of WebView
     */
    private final String user_agent = Welcome.USER_AGENT;
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
        this.intent = intent;
        Thread thread = new Thread(this);
        thread.start();
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * Start
     */
    @Override
    public void run() {
        String name = intent.getStringExtra("name");
        String password = intent.getStringExtra("password");
        String responseUrl = null;
        HashMap<String, String> cookies = null;
        String url = null;
        try {
            Connection.Response loginFormResponse = Jsoup.connect(login_url)
                    .method(Connection.Method.GET)
                    .userAgent(user_agent)
                    .execute();
            FormElement loginForm = (FormElement)loginFormResponse.parse().getElementsByTag("form").get(0);
            Element nameInput = loginForm.getElementById("Name");
            Element passwordInput = loginForm.getElementById("Password");
            nameInput.val(name);
            passwordInput.val(password);
            Connection.Response loginActionResponse = loginForm.submit()
                    .cookies(loginFormResponse.cookies())
                    .userAgent(user_agent)
                    .execute();
            System.out.println(loginActionResponse.url());
            responseUrl = loginActionResponse.url().toString();
            cookies = (HashMap<String, String>) loginActionResponse.cookies();
        } catch (Exception e){
            e.printStackTrace();
        }
        Boolean success;
        if(responseUrl != null && !responseUrl.equals("https://services-web.u-cergy.fr/calendar/LdapLogin/Logon")) {
            success = true;
            Log.d("cookie", cookies.toString());
            url = responseUrl.replace("Unknown", "List");
            url = url.replace("month", "listWeek");
            url = url.replace("01%2F01%2F0001", "01%2F03%2F2022"); // change la date pour test
            Log.d("url replace", url);
        }
        else{
            success = false;
            Log.d("erreur", "erreur de connexion ! ");
        }

        String target = intent.getStringExtra("target");
        Intent intent1 = new Intent(target);
        intent1.putExtra("success", success);
        intent1.putExtra("name", name);
        intent1.putExtra("password", password);
        intent1.putExtra("responseUrl", responseUrl);
        intent1.putExtra("url", url);
        intent1.putExtra("cookies", cookies);
        sendBroadcast(intent1);
    }
}
