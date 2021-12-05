package com.ezstudies.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

public class Welcome extends AppCompatActivity {
    final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.69 Safari/537.36";
    final String LOGIN_FORM_URL  = "https://services-web.u-cergy.fr/calendar/LdapLogin";
    EditText eName;
    EditText ePassword;
    public static Map<String, String> cookies;
    public static String url;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_layout);
        eName = (EditText) findViewById(R.id.editTextName);
        ePassword = (EditText) findViewById(R.id.editTextPassword);
        SharedPreferences sharedPreferences = this.getSharedPreferences("info", Context.MODE_PRIVATE);
        if(sharedPreferences.getString("password", null) != null && sharedPreferences.getString("name", null) != null){
            Log.d("shared", "on a des info !");
            ePassword.setText(sharedPreferences.getString("password", "null"), TextView.BufferType.EDITABLE);
            eName.setText(sharedPreferences.getString("name", "null"), TextView.BufferType.EDITABLE);
        }
    }

    public void submit (View view) throws InterruptedException {
        Intent intent = new Intent(this, Overview.class);
        String name = eName.getText().toString();
        String password = ePassword.getText().toString();

        Login login = new Login(LOGIN_FORM_URL, USER_AGENT, name, password);
        Thread thread = new Thread(login);
        thread.start();
        thread.join();
        String response = login.getResponseUrl();
        if(response != null) {
            if (response.equals("https://services-web.u-cergy.fr/calendar/LdapLogin/Logon")) {
                Context context = getApplicationContext();
                CharSequence text = "Login ou password incorrect !";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            } else {
                SharedPreferences sharedPreferences = this.getSharedPreferences("info", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("name", name);
                editor.putString("password", password);
                editor.apply();
                cookies = login.getCookies();
                Log.d("cookie", cookies.toString());
                url = response;
                url = url.replace("Unknown", "List");
                url = url.replace("month", "listWeek");
                Log.d("url replace", url);
                startActivity(intent);
            }
        }else{
            Context context = getApplicationContext();
            CharSequence text = "error de connexion !";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            Log.d("erreur", "erreur de connexion ! ");
        }
    }
}
