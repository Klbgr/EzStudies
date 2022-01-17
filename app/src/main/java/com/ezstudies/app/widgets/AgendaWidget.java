package com.ezstudies.app.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.ezstudies.app.Database;
import com.ezstudies.app.R;
import com.ezstudies.app.activities.Agenda;

import java.util.ArrayList;
import java.util.Calendar;

public class AgendaWidget extends AppWidgetProvider {
    /**
     * On update
     *
     * @param context          Context
     * @param appWidgetManager AppWidgetManager
     * @param appWidgetIds     AppWidgetIds
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        for (int i = 0; i < appWidgetIds.length; i++) {
            int appWidgetId = appWidgetIds[i];
            Intent intent = new Intent(context, Agenda.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);


            Calendar now = Calendar.getInstance();
            Database database = new Database(context);
            ArrayList<ArrayList<String>> agenda = database.toTabAgenda();
            database.close();
            ArrayList<String> next = null;
            for (ArrayList<String> course : agenda) {
                String[] date = course.get(0).split("/");
                String[] hour = course.get(2).split(":");
                Calendar courseTime = Calendar.getInstance();
                courseTime.set(Integer.parseInt(date[2]), Integer.parseInt(date[1]) - 1, Integer.parseInt(date[0]), Integer.parseInt(hour[0]), Integer.parseInt(hour[1]));
                if (courseTime.getTimeInMillis() >= now.getTimeInMillis()) {
                    next = course;
                    break;
                }
            }

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.agenda_widget_layout);
            views.setOnClickPendingIntent(R.id.widget_agenda, pendingIntent);

            if (next != null) {
                views.setTextViewText(R.id.agenda_course, next.get(1));
                views.setTextViewText(R.id.agenda_hour, next.get(0) + " : " + next.get(2) + " - " + next.get(3));
                String description = next.get(4);
                if (description.contains(" / ")) { //if multiple infos
                    views.setTextViewText(R.id.agenda_place, description.split(" / ")[0]);
                    views.setTextViewText(R.id.agenda_info, description.split(" / ")[1]);
                } else {
                    views.setTextViewText(R.id.agenda_place, description);
                }
            } else {
                views.setTextViewText(R.id.agenda_course, context.getString(R.string.no_coming_course));
            }

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}

