package com.ezstudies.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Overview extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.overview_layout);
    }

    public void agenda(View view){
        startActivity(new Intent(this, Agenda.class));
    }

    public void homework(View view){
        startActivity(new Intent(this, Homework.class));
    }

    public void settings(View view){
        startActivity(new Intent(this, Settings.class));
    }
}
