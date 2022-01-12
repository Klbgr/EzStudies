package com.ezstudies.app.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ezstudies.app.BuildConfig;
import com.ezstudies.app.Database;
import com.ezstudies.app.R;
import com.ezstudies.app.services.UpdateChecker;

import java.io.File;
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
     * Broadcast receiver for UpdateChecker
     */
    private UpdateCheckerReceiver updateCheckerReceiver;
    /**
     * Broadcast receiver for DownloadManager
     */
    private DownloadManagerReceiver downloadManagerReceiver;
    /**
     * Loading dialog
     */
    private ProgressDialog progressDialog;
    /**
     * File name
     */
    private String name;

    /**
     * On create
     * @param savedInstanceState Bundle
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences(Settings.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        Boolean firstTime = sharedPreferences.getBoolean("first_time", true);
        if(firstTime) {
            finish();
            startActivity(new Intent(this, Welcome.class));
        }
        else{
            switch(sharedPreferences.getInt("theme", 0)){
                case 0:
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                    break;
                case 1:
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    break;
                case 2:
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    break;
            }
            setContentView(R.layout.overview_layout);
            getSupportActionBar().setTitle(getString(R.string.overview));
            checkUpdate();
        }
    }

    /**
     * Start UpdateChecker service
     */
    public void checkUpdate(){
        updateCheckerReceiver = new UpdateCheckerReceiver();
        registerReceiver(updateCheckerReceiver, new IntentFilter("OverviewUpdateChecker"));
        Intent intent = new Intent(this, UpdateChecker.class);
        intent.putExtra("target", "OverviewUpdateChecker");
        startService(intent);
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
                courseData.add(row.get(0) + " : " + row.get(2) + " - " + row.get(3));
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
        RecyclerAdapterAgenda recyclerAdapterAgenda = new RecyclerAdapterAgenda(dataAgenda);
        listAgenda.setLayoutManager(new LinearLayoutManager(this));
        listAgenda.setAdapter(recyclerAdapterAgenda);

        RecyclerView listHomeworks = findViewById(R.id.overview_homeworks);
        RecyclerAdapterHomeworks recyclerAdapterHomeworks = new RecyclerAdapterHomeworks(dataHomeworks);
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
    private class RecyclerAdapterAgenda extends RecyclerView.Adapter<RecyclerAdapterAgenda.ViewHolder>{
        /**
         * Data
         */
        private ArrayList<ArrayList<String>> data;

        /**
         * Constructor
         * @param data Data
         */
        public RecyclerAdapterAgenda(ArrayList<ArrayList<String>> data){
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
        public RecyclerAdapterAgenda.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View listItem= layoutInflater.inflate(R.layout.list_item_course, parent, false);
            RecyclerAdapterAgenda.ViewHolder viewHolder = new RecyclerAdapterAgenda.ViewHolder(listItem);
            return viewHolder;
        }

        /**
         * On bind ViewHolder
         * @param holder ViewHolder
         * @param position Position
         */
        @Override
        public void onBindViewHolder(@NonNull RecyclerAdapterAgenda.ViewHolder holder, int position) {
            if(!data.get(position).isEmpty()){
                try{
                    holder.course.setText(data.get(position).get(0));
                    holder.hour.setText(data.get(position).get(1));
                    holder.place.setText(data.get(position).get(2));
                    holder.info.setText(data.get(position).get(3));
                } catch (IndexOutOfBoundsException e){
                    e.printStackTrace();
                }

                if(data.get(position).get(0).equals(getString(R.string.no_agenda))){
                    holder.itemView.findViewById(R.id.agenda_img0).setVisibility(View.GONE);
                    holder.itemView.findViewById(R.id.agenda_img1).setVisibility(View.GONE);
                    holder.itemView.findViewById(R.id.agenda_img2).setVisibility(View.GONE);
                }

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
    private class RecyclerAdapterHomeworks extends RecyclerView.Adapter<RecyclerAdapterHomeworks.ViewHolder>{
        /**
         * Data
         */
        private ArrayList<ArrayList<String>> data;

        /**
         * Constructor
         * @param data Data
         */
        public RecyclerAdapterHomeworks(ArrayList<ArrayList<String>> data) {
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
        public RecyclerAdapterHomeworks.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View listItem= layoutInflater.inflate(R.layout.list_item_homeworks, parent, false);
            RecyclerAdapterHomeworks.ViewHolder viewHolder = new RecyclerAdapterHomeworks.ViewHolder(listItem);
            return viewHolder;
        }

        /**
         * On bind ViewHolder
         * @param holder ViewHolder
         * @param p Position
         */
        @Override
        public void onBindViewHolder(@NonNull RecyclerAdapterHomeworks.ViewHolder holder, int p) {
            int position = p;
            if(!data.get(position).isEmpty()) {
                String status;
                holder.title.setText(data.get(position).get(0));
                holder.date.setText(data.get(position).get(1));
                holder.description.setText(data.get(position).get(2));
                status = data.get(position).get(3);
                if (status.equals("f")) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        holder.itemView.findViewById(R.id.homeworks_card).setBackgroundColor(getColor(R.color.homework_red));
                    }
                    else{
                        holder.itemView.findViewById(R.id.homeworks_card).setBackgroundColor(Color.RED);
                    }
                }
                else if(status.equals("t")) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        holder.itemView.findViewById(R.id.homeworks_card).setBackgroundColor(getColor(R.color.homework_green));
                    }
                    else{
                        holder.itemView.findViewById(R.id.homeworks_card).setBackgroundColor(Color.GREEN);
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

    /**
     * Broadcast receiver for UpdateChecker
     */
    private class UpdateCheckerReceiver extends BroadcastReceiver {
        /**
         * On receive
         * @param context Context
         * @param intent Intent
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            unregisterReceiver(updateCheckerReceiver);
            Boolean update = intent.getBooleanExtra("update", false);
            if(update){
                String url = intent.getStringExtra("url");
                name = intent.getStringExtra("name");
                String changelog = intent.getStringExtra("changelog");
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(getString(R.string.update));
                builder.setMessage(getString(R.string.update_message, changelog));
                builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    /**
                     * On click
                     * @param dialog DialogInterface
                     * @param which ID of DialogInterface
                     */
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progressDialog = new ProgressDialog(context);
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                        progressDialog.setTitle(getString(R.string.downloading));
                        progressDialog.setMessage(getString(R.string.loading));
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.setCancelable(false);
                        progressDialog.show();

                        downloadManagerReceiver = new DownloadManagerReceiver();
                        registerReceiver(downloadManagerReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

                        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), name);
                        if(file.exists()){
                            file.delete();
                        }

                        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                        long DOWNLOAD_ID = downloadManager.enqueue(new DownloadManager.Request(Uri.parse(url))
                            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                            .setTitle(getString(R.string.downloading))
                            .setDescription(getString(R.string.loading))
                            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, name));

                        //download progress
                        Thread thread = new Thread(new Runnable() {
                            /**
                             * Start
                             */
                            @SuppressLint("Range")
                            @Override
                            public void run() {
                                Boolean running = true;
                                long total;
                                long downloaded;
                                while(running) {
                                    Cursor cursor = downloadManager.query(new DownloadManager.Query().setFilterById(DOWNLOAD_ID));
                                    cursor.moveToFirst();
                                    total = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                                    downloaded = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                                    progressDialog.setProgress((int) ((downloaded*100)/total));
                                    try {
                                        Thread.sleep(100);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    if (downloaded >= total && total != -1){
                                        running = false;
                                    }
                                }
                            }
                        });
                        thread.start();
                    }
                });
                builder.setNegativeButton(R.string.later, new DialogInterface.OnClickListener() {
                    /**
                     * On click
                     * @param dialog DialogInterface
                     * @param which ID of DialogInterface
                     */
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        }
    }

    /**
     * Broadcast receiver for DownloadManager
     */
    private class DownloadManagerReceiver extends BroadcastReceiver{
        /**
         * On receive
         * @param context Context
         * @param intent Intent
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            unregisterReceiver(downloadManagerReceiver);
            progressDialog.cancel();
            Intent intent1 = new Intent(Intent.ACTION_VIEW);
            intent1.setDataAndType(FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), name)), "application/vnd.android.package-archive");
            intent1.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent1);
        }
    }
}
