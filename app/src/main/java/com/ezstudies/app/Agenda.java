package com.ezstudies.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class Agenda extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.agenda_layout);
    }

    public void celcat(View view){
        startActivity(new Intent(this, CelcatParser.class));
    }
}
