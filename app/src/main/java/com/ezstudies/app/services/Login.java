package com.ezstudies.app.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.FormElement;

import java.util.Calendar;
import java.util.HashMap;

/**
 * Service that logs into Celcat
 */
public class Login extends Service implements Runnable {
    /**
     * User agent of WebView
     */
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.69 Safari/537.36";
    /**
     * URL of login
     */
    private static final String LOGIN_FORM_URL = "https://services-web.u-cergy.fr/calendar/LdapLogin";
    /**
     * Intent
     */
    private Intent intent;

    /**
     * On bind
     *
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
     *
     * @param intent  Intent
     * @param flags   Flags
     * @param startId ID
     * @return Success
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            this.intent = intent;
            Thread thread = new Thread(this);
            thread.start();
        }
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
            Connection.Response loginFormResponse = Jsoup.connect(LOGIN_FORM_URL)
                    .method(Connection.Method.GET)
                    .userAgent(USER_AGENT)
                    .execute();
            FormElement loginForm = (FormElement) loginFormResponse.parse().getElementsByTag("form").get(0);
            Element nameInput = loginForm.getElementById("Name");
            Element passwordInput = loginForm.getElementById("Password");
            nameInput.val(name);
            passwordInput.val(password);
            Connection.Response loginActionResponse = loginForm.submit()
                    .cookies(loginFormResponse.cookies())
                    .userAgent(USER_AGENT)
                    .execute();
            Log.d("url response", loginActionResponse.url().toString());
            responseUrl = loginActionResponse.url().toString();
            cookies = (HashMap<String, String>) loginActionResponse.cookies();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Boolean success;
        if (responseUrl != null && !responseUrl.equals("https://services-web.u-cergy.fr/calendar/LdapLogin/Logon")) {
            success = true;
            Log.d("cookie", cookies.toString());
            url = responseUrl.replace("Unknown", "List");
            url = url.replace("month", "listWeek");
            Calendar now = Calendar.getInstance();
            if (now.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) { //sunday
                int day = now.get(Calendar.DAY_OF_MONTH);
                int month = now.get(Calendar.MONTH) + 1;
                int year = now.get(Calendar.YEAR);
                url = url.replace("01%2F01%2F0001", month + "%2F" + (day + 1) + "%2F" + year); // change date to load next week
            }
            Log.d("url replace", url);
        } else {
            success = false;
            Log.d("error", "connection error");
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
