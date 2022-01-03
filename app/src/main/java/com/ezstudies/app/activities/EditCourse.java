package com.ezstudies.app.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.ezstudies.app.Database;
import com.ezstudies.app.R;

public class EditCourse extends AppCompatActivity {

    EditText hour;
    EditText place;
    EditText info;
    EditText course;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_course_layout);
        hour = findViewById(R.id.agenda_hour);
        place = findViewById(R.id.agenda_place);
        info = findViewById(R.id.agenda_info);
        course = findViewById(R.id.agenda_course);

        course.setText(getIntent().getStringExtra("course"));
        hour.setText(getIntent().getStringExtra("hour"));
        place.setText(getIntent().getStringExtra("place"));
        info.setText(getIntent().getStringExtra("info"));
    }

    public void save(View view){
        String date = getIntent().getStringExtra("hour").split(" : ")[0];
        String startAt = getIntent().getStringExtra("hour").substring(getIntent().getStringExtra("hour").indexOf(" : ") + 3, getIntent().getStringExtra("hour").indexOf(" - "));
        String endAt = getIntent().getStringExtra("hour").substring(getIntent().getStringExtra("hour").indexOf(" - ") + 3);
        String title = getIntent().getStringExtra("course");
        Log.d("date", date);
        Log.d("startAt", startAt);
        Log.d("endAt", endAt);
        Log.d("title", title);

        String newDate = String.valueOf(hour.getText()).split(" : ")[0];
        String newStartAt = String.valueOf(hour.getText()).substring(String.valueOf(hour.getText()).indexOf(" : ") + 3, String.valueOf(hour.getText()).indexOf(" - "));
        String newEndAt = String.valueOf(hour.getText()).substring(String.valueOf(hour.getText()).indexOf(" - ") + 3);
        String newTitle = String.valueOf(course.getText());
        String newDesc = String.valueOf(place.getText()) + " / " + String.valueOf(info.getText());
        Log.d("newdate", newDate);
        Log.d("newstartAt", newStartAt);
        Log.d("newendAt", newEndAt);
        Log.d("newtitle", newTitle);

        Database db = new Database(this);
        db.editAgenda(date, title, startAt, endAt, newDate,newTitle, newStartAt, newEndAt, newDesc, this);
        startActivity(new Intent(this, Agenda.class));
        finish();
    }
}