package com.ezstudies.app;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.ezstudies.app.activities.Homeworks;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Widget showing next homework
 */
public class HomeworksWidget extends AppWidgetProvider {
    /**
     * On update
     * @param context Context
     * @param appWidgetManager AppWidgetManager
     * @param appWidgetIds AppWidgetIds
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        for (int i=0; i < appWidgetIds.length; i++) {
            int appWidgetId = appWidgetIds[i];
            Intent intent = new Intent(context, Homeworks.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            Calendar now = Calendar.getInstance();
            Database database = new Database(context);
            ArrayList<ArrayList<String>> homeworks = database.toTabHomeworks();
            database.close();
            ArrayList<String> next = null;
            for(ArrayList<String> homework : homeworks){
                String[] date = homework.get(1).split("/");
                Calendar homeworkTime = Calendar.getInstance();
                homeworkTime.set(Integer.parseInt(date[2]), Integer.parseInt(date[1])-1, Integer.parseInt(date[0]), 0, 0);
                if(homeworkTime.getTimeInMillis() > now.getTimeInMillis() && homework.get(3).equals("f")){
                    next = homework;
                    break;
                }
            }

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.homeworks_widget_layout);
            views.setOnClickPendingIntent(R.id.widget_homeworks, pendingIntent);

            if(next != null){
                views.setTextViewText(R.id.homeworks_title, next.get(0));
                views.setTextViewText(R.id.homeworks_date, next.get(1));
                views.setTextViewText(R.id.homeworks_description, next.get(2));
            }
            else{
                views.setTextViewText(R.id.homeworks_title, context.getString(R.string.no_coming_homework));
            }

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}