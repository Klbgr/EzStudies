package com.ezstudies.app.services;

import static java.lang.Thread.sleep;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.provider.AlarmClock;

import androidx.annotation.Nullable;

import com.ezstudies.app.R;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Service that sets alarms
 */
public class AlarmSetter extends Service implements Runnable{
    /**
     * List of times
     */
    private ArrayList<String[]> list;

    /**
     * On bind
     * @param intent Intent
     * @return IBinder
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * On start command
     * @param intent Intent
     * @param flags Flags
     * @param startId ID
     * @return Success
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        list = (ArrayList<String[]>) intent.getSerializableExtra("list");
        Thread thread = new Thread(this);
        thread.start();
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * Start
     */
    public void run(){
        for(String infos[] : list){
            int day = Integer.parseInt(infos[0].split("/")[0]);
            int month = Integer.parseInt(infos[0].split("/")[1]);
            int year = Integer.parseInt(infos[0].split("/")[2]);
            int hour = Integer.parseInt(infos[1].split(":")[0]);
            int minute = Integer.parseInt(infos[1].split(":")[1]);
            long now = Calendar.getInstance().getTimeInMillis();
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_MONTH, day);
            calendar.set(Calendar.MONTH, month-1);
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            long then = calendar.getTimeInMillis();
            if(then >= now){
                setAlarm(calendar.get(Calendar.DAY_OF_WEEK), hour, minute);
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Get name of a day
     * @param d Day
     * @return Name of a day
     */
    public String getDayName(int d){
        String name = null;
        switch (d){
            case 2:
                name = getString(R.string.monday);
                break;
            case 3:
                name = getString(R.string.tuesday);
                break;
            case 4:
                name = getString(R.string.wednesday);
                break;
            case 5:
                name = getString(R.string.thursday);
                break;
            case 6:
                name = getString(R.string.friday);
                break;
            case 7:
                name = getString(R.string.saturday);
                break;
        }
        return name;
    }

    /**
     * Set an alarm
     * @param day Day
     * @param hour Hour
     * @param minute Minute
     */
    public void setAlarm(int day, int hour, int minute){
        int alarm = getApplicationContext().getSharedPreferences("prefs", MODE_PRIVATE).getInt("alarm_ringtone", 0)+1;
        ArrayList<Integer> days = new ArrayList<>();
        days.add(day);
        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
        intent.putExtra(AlarmClock.EXTRA_DAYS, days);
        intent.putExtra(AlarmClock.EXTRA_HOUR, hour);
        intent.putExtra(AlarmClock.EXTRA_MINUTES, minute);
        intent.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
        intent.putExtra(AlarmClock.EXTRA_MESSAGE, getString(R.string.app_name) + " " + getDayName(day));
        intent.putExtra(AlarmClock.EXTRA_RINGTONE, "android.resource://com.ezstudies.app/raw/ezstudies" + alarm);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
