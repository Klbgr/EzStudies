package com.ezstudies.app.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.ezstudies.app.Database;
import com.ezstudies.app.R;

import java.util.Calendar;

/**
 * Activity to edit a course
 */
public class EditCourse extends AppCompatActivity {

    /**
     *  EditText to display the start hour
     */
    EditText startHour;
    /**
     *  EditText to display the end hour
     */
    EditText endHour;
    /**
     *  EditText to display the date
     */
    EditText date;
    /**
     *  EditText to display the place
     */
    EditText place;
    /**
     *  EditText to display the info
     */
    EditText info;
    /**
     *  EditText to display the title of the course
     */
    EditText course;

    /**
     * Initiate the activity
     * @param savedInstanceState Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_course_layout);
        date = findViewById(R.id.agenda_date);
        startHour = findViewById(R.id.agenda_startHour);
        endHour = findViewById(R.id.agenda_endHour);
        place = findViewById(R.id.agenda_place);
        info = findViewById(R.id.agenda_info);
        course = findViewById(R.id.agenda_course);

        course.setText(getIntent().getStringExtra("course"));
        date.setText(getIntent().getStringExtra("hour").split(" : ")[0]);
        startHour.setText(getIntent().getStringExtra("hour").substring(getIntent().getStringExtra("hour").indexOf(" : ") + 3, getIntent().getStringExtra("hour").indexOf(" - ")));
        endHour.setText(getIntent().getStringExtra("hour").substring(getIntent().getStringExtra("hour").indexOf(" - ") + 3));
        place.setText(getIntent().getStringExtra("place"));
        info.setText(getIntent().getStringExtra("info"));

        startHour.setInputType(InputType.TYPE_NULL);
        startHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog tp = new TimePickerDialog(EditCourse.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                        startHour.setText(hourOfDay + ":" + minute);
                    }
                }, Integer.parseInt(String.valueOf(startHour.getText()).split(":")[0]), Integer.parseInt(String.valueOf(startHour.getText()).split(":")[1]), true);
            tp.show();
            }
        });

        endHour.setInputType(InputType.TYPE_NULL);
        endHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog tp = new TimePickerDialog(EditCourse.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                        endHour.setText(hourOfDay + ":" + minute);
                    }
                }, Integer.parseInt(String.valueOf(endHour.getText()).split(":")[0]), Integer.parseInt(String.valueOf(endHour.getText()).split(":")[1]), true);
                tp.show();
            }
        });

        date.setInputType(InputType.TYPE_NULL);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dp = new DatePickerDialog(EditCourse.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        if (dayOfMonth < 10){
                            date.setText("0" + dayOfMonth + "/" + month+1 + "/" + year);
                        }
                        else{
                            date.setText(dayOfMonth + "/" + month+1 + "/" + year);
                        }

                    }
                }, Integer.parseInt(String.valueOf(date.getText()).split("/")[2]), Integer.parseInt(String.valueOf(date.getText()).split("/")[1]), Integer.parseInt(String.valueOf(date.getText()).split("/")[0]));
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                dp.getDatePicker().setMinDate(calendar.getTimeInMillis());
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
                dp.getDatePicker().setMaxDate(calendar.getTimeInMillis());
                dp.show();
            }
        });
    }

    /**
     * Save the changes on the course
     * @param view view
     */
    public void save(View view){
        String date = getIntent().getStringExtra("hour").split(" : ")[0];
        String startAt = getIntent().getStringExtra("hour").substring(getIntent().getStringExtra("hour").indexOf(" : ") + 3, getIntent().getStringExtra("hour").indexOf(" - "));
        String endAt = getIntent().getStringExtra("hour").substring(getIntent().getStringExtra("hour").indexOf(" - ") + 3);
        String title = getIntent().getStringExtra("course");
        Log.d("date", date);
        Log.d("startAt", startAt);
        Log.d("endAt", endAt);
        Log.d("title", title);

        String newDate = String.valueOf(this.date.getText());
        String newStartAt = String.valueOf(startHour.getText());
        String newEndAt = String.valueOf(endHour.getText());
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

    /**
     * Delete a course
     * @param view view
     */
    public void delete(View view){
        String date = getIntent().getStringExtra("hour").split(" : ")[0];
        String startAt = getIntent().getStringExtra("hour").substring(getIntent().getStringExtra("hour").indexOf(" : ") + 3, getIntent().getStringExtra("hour").indexOf(" - "));
        String endAt = getIntent().getStringExtra("hour").substring(getIntent().getStringExtra("hour").indexOf(" - ") + 3);
        String title = getIntent().getStringExtra("course");
        Log.d("date", date);
        Log.d("startAt", startAt);
        Log.d("endAt", endAt);
        Log.d("title", title);
        Database db = new Database(this);
        db.deleteAgenda(date, title, startAt, endAt, this);
        startActivity(new Intent(this, Agenda.class));
        finish();
    }
}