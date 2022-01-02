package com.ezstudies.app.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ezstudies.app.Database;
import com.ezstudies.app.R;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Activity that displays important information
 */
public class Overview extends AppCompatActivity {
    /**
     * Shared preferences
     */
    private SharedPreferences sharedPreferences;

    /**
     * On create
     * @param savedInstanceState Bundle
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.overview_layout);
        sharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE);
    }

    /**
     * On create OptionMenu
     * @param menu Menu
     * @return Success
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.overview_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * On OptionItem selected
     * @param item MenuItem
     * @return Success
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.overview_agenda:
                startActivity(new Intent(this, Agenda.class));
                break;
            case R.id.overview_homework:
                startActivity(new Intent(this, Homeworks.class));
                break;
            case R.id.overview_settings:
                startActivity(new Intent(this, Settings.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * On resume
     */
    @Override
    protected void onResume() {
        super.onResume();
        route();
        reload();
    }

    /**
     * Reload agenda and homeworks
     */
    public void reload(){
        Calendar now = Calendar.getInstance();
        Database database = new Database(this);
        ArrayList<ArrayList<String>> dataAgenda = database.toTabAgendaDay(now.get(Calendar.DAY_OF_MONTH), now.get(Calendar.MONTH)+1, now.get(Calendar.YEAR));
        ArrayList<ArrayList<String>> dataHomeworks = database.toTabHomeworks();
        database.close();

        if(dataAgenda.size() == 0){
            dataAgenda = new ArrayList<>();
            ArrayList<String> empty = new ArrayList<>();
            empty.add(getString(R.string.no_agenda));
            empty.add("");
            empty.add("");
            empty.add("");
            empty.add("");
            dataAgenda.add(empty);
        }
        else{
            ArrayList<ArrayList<String>> coursList = new ArrayList<>();
            for(ArrayList<String> row : dataAgenda){
                ArrayList<String> courseData = new ArrayList<String>();
                courseData.add(row.get(1));
                courseData.add(row.get(2) + " - " + row.get(3));
                if (row.get(4).contains(" / ")){
                    courseData.add(row.get(4).split(" / ")[0]);
                    courseData.add(row.get(4).split(" / ")[1]);
                }
                else{
                    courseData.add(row.get(4));
                }
                coursList.add(courseData);

            }
            dataAgenda = coursList;
        }

        if(dataHomeworks.size() == 0){
            dataHomeworks = new ArrayList<>();
            ArrayList<String> empty = new ArrayList<>();
            empty.add(getString(R.string.no_homeworks));
            empty.add("");
            empty.add("");
            empty.add("");
            dataHomeworks.add(empty);
        }

        RecyclerView listAgenda = findViewById(R.id.overview_agenda);
        recyclerAdapterAgenda recyclerAdapterAgenda = new recyclerAdapterAgenda(dataAgenda);
        listAgenda.setLayoutManager(new LinearLayoutManager(this));
        listAgenda.setAdapter(recyclerAdapterAgenda);

        RecyclerView listHomeworks = findViewById(R.id.overview_homeworks);
        recyclerAdapterHomeworks recyclerAdapterHomeworks = new recyclerAdapterHomeworks(dataHomeworks);
        listHomeworks.setLayoutManager(new LinearLayoutManager(this));
        listHomeworks.setAdapter(recyclerAdapterHomeworks);
    }

    /**
     * Display wake up time and time of travel for tomorrow
     */
    public void route(){
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.setTimeInMillis(tomorrow.getTimeInMillis() + 1000*60*60*24);
        Database database = new Database(this);
        ArrayList<ArrayList<String>> agenda = database.toTabAgendaDay(tomorrow.get(Calendar.DAY_OF_MONTH), tomorrow.get(Calendar.MONTH)+1, tomorrow.get(Calendar.YEAR));
        database.close();
        if(agenda.size() != 0){
            int mode = sharedPreferences.getInt("travel_mode", 0);
            int duration;
            if(mode != 2){ //not transit
                duration = sharedPreferences.getInt("duration", -1)/60;
            }
            else{
                duration = sharedPreferences.getInt("travel_time", -1);
            }

            String courseBegin = agenda.get(0).get(2);
            int hour = Integer.parseInt(courseBegin.split(":")[0]);
            int minute = Integer.parseInt(courseBegin.split(":")[1]);
            int total = duration + sharedPreferences.getInt("prep_time", 0);
            int diffHour = total/60;
            int diffMin = total - diffHour*60;
            hour -= diffHour;
            minute -= diffMin;
            if(minute<0){
                minute += 60;
                hour--;
            }
            String time;
            if(minute < 10){
                time = hour + ":0" + minute;
            }
            else{
                time = hour + ":" + minute;
            }

            TextView textView = findViewById(R.id.overview_duration);
            textView.setText(getString(R.string.duration, time, duration));
        }
        else{
            TextView textView = findViewById(R.id.overview_duration);
            textView.setText(getString(R.string.nothing_tomorrow));
        }
    }

    /**
     * RecyclerView Adapter for agenda
     */
    public class recyclerAdapterAgenda extends RecyclerView.Adapter<recyclerAdapterAgenda.ViewHolder>{
        /**
         * Data
         */
        private ArrayList<ArrayList<String>> data;

        /**
         * Constructor
         * @param data Data
         */
        public recyclerAdapterAgenda(ArrayList<ArrayList<String>> data){
            this.data = data;
        }

        /**
         * On create ViewHolder
         * @param parent ViewGroup
         * @param viewType Type of view
         * @return
         */
        @NonNull
        @Override
        public recyclerAdapterAgenda.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View listItem= layoutInflater.inflate(R.layout.list_item_course, parent, false);
            recyclerAdapterAgenda.ViewHolder viewHolder = new recyclerAdapterAgenda.ViewHolder(listItem);
            return viewHolder;
        }

        /**
         * On bind ViewHolder
         * @param holder ViewHolder
         * @param position Position
         */
        @Override
        public void onBindViewHolder(@NonNull recyclerAdapterAgenda.ViewHolder holder, int position) {
            if(!data.get(position).isEmpty()){
                holder.course.setText(data.get(position).get(0));
                holder.hour.setText(data.get(position).get(1));
                holder.place.setText(data.get(position).get(2));
                holder.info.setText(data.get(position).get(3));

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    /**
                     * On click
                     * @param v View
                     */
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(Overview.this, Agenda.class));
                    }
                });
            }
        }

        /**
         * Get number of items
         * @return Number of items
         */
        @Override
        public int getItemCount() {
            return data.size();
        }

        /**
         * ViewHolder
         */
        public class ViewHolder extends RecyclerView.ViewHolder {
            /**
             * Name of course
             */
            public TextView course;
            /**
             * Hour of course
             */
            public TextView hour;
            /**
             * Info of course
             */
            public TextView info;
            /**
             * Place of course
             */
            public TextView place;

            /**
             * Constructor
             * @param itemView View
             */
            public ViewHolder(View itemView) {
                super(itemView);
                course = itemView.findViewById(R.id.agenda_course);
                hour = itemView.findViewById(R.id.agenda_hour);
                info = itemView.findViewById(R.id.agenda_info);
                place = itemView.findViewById(R.id.agenda_place);
            }
        }
    }

    /**
     * RecyclerView Adapter for homeworks
     */
    private class recyclerAdapterHomeworks extends RecyclerView.Adapter<recyclerAdapterHomeworks.ViewHolder>{
        /**
         * Data
         */
        private ArrayList<ArrayList<String>> data;

        /**
         * Constructor
         * @param data Data
         */
        public recyclerAdapterHomeworks(ArrayList<ArrayList<String>> data) {
            this.data = data;
        }

        /**
         * On create ViewHolder
         * @param parent ViewGroup
         * @param viewType Type of view
         * @return View Holder
         */
        @NonNull
        @Override
        public recyclerAdapterHomeworks.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View listItem= layoutInflater.inflate(R.layout.list_item_homeworks, parent, false);
            recyclerAdapterHomeworks.ViewHolder viewHolder = new recyclerAdapterHomeworks.ViewHolder(listItem);
            return viewHolder;
        }

        /**
         * On bind ViewHolder
         * @param holder ViewHolder
         * @param p Position
         */
        @Override
        public void onBindViewHolder(@NonNull recyclerAdapterHomeworks.ViewHolder holder, int p) {
            int position = p;
            if(!data.get(position).isEmpty()) {
                String status;
                holder.title.setText(data.get(position).get(0));
                holder.date.setText(data.get(position).get(1));
                holder.description.setText(data.get(position).get(2));
                status = data.get(position).get(3);
                if (status.equals("f")) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        holder.itemView.setBackgroundColor(getColor(R.color.homework_red));
                    }
                    else{
                        holder.itemView.setBackgroundColor(Color.RED);
                    }
                }
                else if(status.equals("t")) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        holder.itemView.setBackgroundColor(getColor(R.color.homework_green));
                    }
                    else{
                        holder.itemView.setBackgroundColor(Color.GREEN);
                    }
                }

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    /**
                     * On click
                     * @param v View
                     */
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(Overview.this, Homeworks.class));
                    }
                });
            }
        }

        /**
         * Get number of items
         * @return Number of items
         */
        @Override
        public int getItemCount() {
            return data.size();
        }

        /**
         * ViewHolder
         */
        public class ViewHolder extends RecyclerView.ViewHolder {
            /**
             * Title of homework
             */
            public TextView title;
            /**
             * Due date of homework
             */
            public TextView date;
            /**
             * Description of homework
             */
            public TextView description;

            /**
             * Constructor
             * @param itemView View
             */
            public ViewHolder(View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.homeworks_title);
                date = itemView.findViewById(R.id.homeworks_date);
                description = itemView.findViewById(R.id.homeworks_description);
            }
        }
    }
}
