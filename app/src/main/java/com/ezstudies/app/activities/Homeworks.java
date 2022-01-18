package com.ezstudies.app.activities;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ezstudies.app.Database;
import com.ezstudies.app.R;
import com.ezstudies.app.widgets.HomeworksWidget;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Activity that displays homeworks
 */
public class Homeworks extends AppCompatActivity {
    /**
     * ID of notification channel
     */
    private static final String CHANNEL_ID_HOMEWORKS = "EzStudies_Homeworks";
    /**
     * Number of pending notifications
     */
    private static int nbNotifPendingHomeworks;

    /**
     * Schedule a notification
     *
     * @param context Context
     * @param time    Time
     * @param title   Title
     * @param text    Text
     */
    public static void scheduleNotificationHomeworks(Context context, long time, String title, String text) {
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra("title", title);
        intent.putExtra("text", text);
        intent.putExtra("nb", nbNotifPendingHomeworks);
        PendingIntent pending = PendingIntent.getBroadcast(context, nbNotifPendingHomeworks, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        // Schedule notification
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        manager.setExact(AlarmManager.RTC_WAKEUP, time, pending);
        Log.d("homeworks notification", "created notification");
        nbNotifPendingHomeworks++;
    }

    /**
     * Cancel a notification
     *
     * @param context Context
     */
    public static void cancelNotificationsHomeworks(Context context) {
        while (nbNotifPendingHomeworks >= 100) {
            Intent intent = new Intent(context, NotificationReceiver.class);
            intent.putExtra("title", "title");
            intent.putExtra("text", "text");
            PendingIntent pending = PendingIntent.getBroadcast(context, nbNotifPendingHomeworks, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            // Cancel notification
            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            manager.cancel(pending);
            nbNotifPendingHomeworks--;
        }
        nbNotifPendingHomeworks = 100;
    }

    /**
     * Set notifications
     *
     * @param context Context
     */
    public static void setNotificationsHomeworks(Context context) {
        Database database = new Database(context);
        cancelNotificationsHomeworks(context);
        Calendar now = Calendar.getInstance();
        now.set(Calendar.HOUR_OF_DAY, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        now.setTimeInMillis(now.getTimeInMillis() + (1000 * 60 * 60 * 24));
        ArrayList<ArrayList<String>> data = database.toTabHomeworks();
        database.close();
        for (ArrayList<String> row : data) {
            String date[] = row.get(1).split("/");
            Calendar calendar = Calendar.getInstance();
            calendar.set(Integer.parseInt(date[2]), Integer.parseInt(date[1]) - 1, Integer.parseInt(date[0]), 0, 0, 0);
            if (now.getTimeInMillis() < calendar.getTimeInMillis() && row.get(3).equals("f")) { //do not notify if due date exceeded or if it's already done
                calendar.set(Calendar.HOUR_OF_DAY, 19);
                calendar.setTimeInMillis(calendar.getTimeInMillis() - (1000 * 60 * 60 * 24));

                scheduleNotificationHomeworks(context, calendar.getTimeInMillis(), context.getString(R.string.homework_tomorrow), row.get(0) + "\n" + row.get(2));
                Log.d("new notification", calendar.get(Calendar.DAY_OF_MONTH) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.YEAR) + " at 19:00");
            }
        }
    }

    /**
     * On create
     *
     * @param savedInstanceState Bundle
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homeworks_layout);
        createNotificationChannelHomeworks();

        RecyclerView list = findViewById(R.id.homeworks_list);
        Database database = new Database(this);
        ArrayList<ArrayList<String>> data = database.toTabHomeworks();
        database.close();

        RecyclerAdapterHomeworks recyclerAdapterHomeworks = new RecyclerAdapterHomeworks(data);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(recyclerAdapterHomeworks);

        nbNotifPendingHomeworks = 100;
    }

    /**
     * Add a homework
     *
     * @param view View
     */
    public void add(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.add_homework));

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        DatePicker datePicker = new DatePicker(this);
        datePicker.setMinDate(Calendar.getInstance().getTimeInMillis() + 1000 * 60 * 60 * 24);

        EditText title = new EditText(this);
        title.setHint(getString(R.string.title));

        EditText description = new EditText(this);
        description.setHint(getString(R.string.description));

        linearLayout.addView(datePicker);
        linearLayout.addView(title);
        linearLayout.addView(description);

        builder.setView(linearLayout);

        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Database database = new Database(Homeworks.this);
                String date = datePicker.getDayOfMonth() + "/" + (datePicker.getMonth() + 1) + "/" + datePicker.getYear();
                database.addHomework(title.getText().toString(), date, description.getText().toString());
                Log.d("database homeworks", database.toStringHomeworks());
                database.close();

                reload();
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    /**
     * Reload page with new values
     */
    public void reload() {
        Database database = new Database(this);
        RecyclerView list = findViewById(R.id.homeworks_list);
        ArrayList<ArrayList<String>> data = database.toTabHomeworks();
        database.close();

        setNotificationsHomeworks(this);
        updateWidget();

        RecyclerAdapterHomeworks recyclerAdapterHomeworks = new RecyclerAdapterHomeworks(data);
        list.setLayoutManager(new LinearLayoutManager(Homeworks.this));
        list.setAdapter(recyclerAdapterHomeworks);
    }

    /**
     * Update widget
     */
    public void updateWidget() {
        Intent intent = new Intent(this, HomeworksWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), HomeworksWidget.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        sendBroadcast(intent);
    }

    /**
     * Create a notification channel
     */
    public void createNotificationChannelHomeworks() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name) + " " + getString(R.string.homeworks);
            String description = getString(R.string.reminders);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID_HOMEWORKS, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * Broadcast receiver for notifications
     */
    public static class NotificationReceiver extends BroadcastReceiver {
        /**
         * On receive
         *
         * @param context Context
         * @param intent  Intent
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("receiver", "received ! id = " + intent.getIntExtra("nb", 1));
            Intent agenda = new Intent(context, Homeworks.class);
            agenda.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, agenda, PendingIntent.FLAG_IMMUTABLE);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID_HOMEWORKS)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(intent.getStringExtra("text")))
                    .setContentTitle(intent.getStringExtra("title"))
                    .setContentText(intent.getStringExtra("text"))
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(intent.getIntExtra("nb", -1), builder.build());
        }
    }

    /**
     * RecyclerView Adapter
     */
    private class RecyclerAdapterHomeworks extends RecyclerView.Adapter<RecyclerAdapterHomeworks.ViewHolder> {
        /**
         * Data
         */
        private ArrayList<ArrayList<String>> data;

        /**
         * Constructor
         *
         * @param data Data
         */
        public RecyclerAdapterHomeworks(ArrayList<ArrayList<String>> data) {
            this.data = data;
        }

        /**
         * On create ViewHolder
         *
         * @param parent   ViewGroup
         * @param viewType Type of view
         * @return ViewHolder
         */
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View listItem = layoutInflater.inflate(R.layout.list_item_homeworks, parent, false);
            RecyclerAdapterHomeworks.ViewHolder viewHolder = new RecyclerAdapterHomeworks.ViewHolder(listItem);
            return viewHolder;
        }

        /**
         * On bind ViewHolder
         *
         * @param holder ViewHolder
         * @param p      Position
         */
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int p) {
            int position = p;
            if (!data.get(position).isEmpty()) {
                String status;
                holder.title.setText(data.get(position).get(0));
                holder.date.setText(data.get(position).get(1));
                holder.description.setText(data.get(position).get(2));
                status = data.get(position).get(3);
                if (status.equals("f")) { //red if not done
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        holder.itemView.findViewById(R.id.homeworks_card).setBackgroundColor(getColor(R.color.homework_red));
                    } else {
                        holder.itemView.findViewById(R.id.homeworks_card).setBackgroundColor(Color.RED);
                    }
                } else { //green if done
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        holder.itemView.findViewById(R.id.homeworks_card).setBackgroundColor(getColor(R.color.homework_green));
                    } else {
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                        builder.setTitle(v.getContext().getString(R.string.action));
                        builder.setMessage(getString(R.string.action_message));

                        builder.setNegativeButton(getString(R.string.remove), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Database database = new Database(Homeworks.this);
                                database.removeHomework(data.get(position).get(0), data.get(position).get(1), data.get(position).get(2));
                                Log.d("database homeworks", database.toStringHomeworks());
                                database.close();

                                reload();
                            }
                        });

                        builder.setNeutralButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
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

                        builder.setPositiveButton(getString(R.string.toggle), new DialogInterface.OnClickListener() {
                            /**
                             * On click
                             * @param dialog DialogInterface
                             * @param which ID of DialogInterface
                             */
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Database database = new Database(Homeworks.this);
                                if (status.equals("f")) {
                                    database.setHomeworkDone(data.get(position).get(0), data.get(position).get(1), data.get(position).get(2), "t");
                                } else {
                                    database.setHomeworkDone(data.get(position).get(0), data.get(position).get(1), data.get(position).get(2), "f");
                                }
                                Log.d("database homeworks", database.toStringHomeworks());
                                database.close();

                                reload();
                            }
                        });

                        builder.show();
                    }
                });
            }
        }

        /**
         * Get number of items
         *
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
             *
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
