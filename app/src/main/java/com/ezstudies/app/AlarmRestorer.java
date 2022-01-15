package com.ezstudies.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.ezstudies.app.activities.Agenda;
import com.ezstudies.app.activities.Homeworks;
import com.ezstudies.app.activities.Overview;

/**
 * Restore alarms for notification after rebooting
 */
public class AlarmRestorer extends BroadcastReceiver {
    /**
     * On receive
     * @param context Context
     * @param intent Intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Agenda.setNotificationsAgenda(context);
        Homeworks.setNotificationsHomeworks(context);
        Overview.setNotificationsOverview(context);
        Toast.makeText(context, context.getString(R.string.on_boot), Toast.LENGTH_SHORT).show();
    }
}
