package com.ezstudies.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Welcome extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = this.getSharedPreferences("firstTime", Context.MODE_PRIVATE);
        Boolean firstTime = sharedPreferences.getBoolean("firstTime", true);

        if(firstTime){
            setContentView(R.layout.welcome_layout);
        }
        else{
            finish();
            startActivity(new Intent(this, Overview.class));
        }
    }

    public void enter(View view){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("firstTime", false);
        editor.commit();
        finish();
        startActivity(new Intent(this, Overview.class));
    }
}
