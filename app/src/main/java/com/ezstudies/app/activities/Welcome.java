package com.ezstudies.app.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ezstudies.app.Login;
import com.ezstudies.app.R;

public class Welcome extends AppCompatActivity {
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.69 Safari/537.36";
    public static final String LOGIN_FORM_URL  = "https://services-web.u-cergy.fr/calendar/LdapLogin";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE);

        Boolean firstTime = sharedPreferences.getBoolean("firstTime", true);
        String name = sharedPreferences.getString("name", null);
        String password = sharedPreferences.getString("password", null);

        if(firstTime || name == null || password == null){
            setContentView(R.layout.welcome_layout);
        }
        else {
            finish();
            startActivity(new Intent(this, Overview.class));
        }
    }

    public void submit (View view) throws InterruptedException {
        EditText eName = findViewById(R.id.welcome_name);
        EditText ePassword = findViewById(R.id.welcome_password);
        String name = eName.getText().toString();
        String password = ePassword.getText().toString();

        Login login = new Login(name, password);
        login.start();
        login.join();

        if(login.isSuccess()) {

            SharedPreferences sharedPreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("name", name);
            editor.putString("password", password);
            editor.putBoolean("firstTime", false);
            editor.commit();

            Toast.makeText(this, getString(R.string.login_succes), Toast.LENGTH_SHORT).show();

            startActivity(new Intent(this, Overview.class));
        }
        else{
            String response = login.getResponseUrl();
            String text;
            if (response.equals("https://services-web.u-cergy.fr/calendar/LdapLogin/Logon")) {
                text = getString(R.string.login_fail_credentials);
            }
            else {
                text = getString(R.string.login_fail_network);
            }
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
        }
    }
}
