package com.ezstudies.app;

import android.util.Log;

import com.ezstudies.app.activities.Welcome;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.FormElement;

import java.io.IOException;
import java.util.Map;

public class Login extends Thread {

    private final String login_url = Welcome.LOGIN_FORM_URL;
    private final String user_agent = Welcome.USER_AGENT;
    private final String name;
    private final String password;
    private Boolean success = false;
    private String url;
    private String responseUrl = null;
    private Map<String, String> cookies;

    public Login(String name, String password){
        this.name = name;
        this.password = password;
    }

    @Override
    public void run() {
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
            cookies = loginActionResponse.cookies();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(responseUrl != null && !responseUrl.equals("https://services-web.u-cergy.fr/calendar/LdapLogin/Logon")) {
            success = true;
            Log.d("cookie", cookies.toString());
            url = responseUrl.replace("Unknown", "List");
            url = url.replace("month", "listWeek");
            Log.d("url replace", url);
        }
        else{
            success = false;
            Log.d("erreur", "erreur de connexion ! ");
        }

    }

    public String getResponseUrl() {
        return responseUrl;
    }

    public Map<String, String> getCookies() {
        return cookies;
    }

    public Boolean isSuccess(){
        return success;
    }

    public String getUrl(){
        return url;
    }
}
