package com.ezstudies.app;

import android.content.Context;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.FormElement;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

public class Login implements Runnable{

    private final String login_url;
    private final String user_agent;
    private final String name;
    private final String password;

    public String getResponseUrl() {
        return responseUrl;
    }

    public Map<String, String> getCookies() {
        return cookies;
    }

    private String responseUrl = null;
    private Map<String, String> cookies;

    public Login(String url, String agent, String name, String password){
        login_url = url;
        this.name = name;
        user_agent = agent;
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
    }
}
