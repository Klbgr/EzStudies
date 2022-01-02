package com.ezstudies.app.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ezstudies.app.Database;
import com.ezstudies.app.R;

import java.util.ArrayList;

/**
 * Activity that displays homeworks
 */
public class Homeworks extends AppCompatActivity {
    /**
     * On create
     * @param savedInstanceState Bundle
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homework_layout);

        RecyclerView list = findViewById(R.id.homeworks_list);
        Database database = new Database(this);
        ArrayList<ArrayList<String>> data = database.toTabHomeworks();
        database.close();

        recyclerAdapter recyclerAdapter = new recyclerAdapter(data);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(recyclerAdapter);
    }

    /**
     * Add a homework
     * @param view View
     */
    public void add(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.add_homework));

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        DatePicker datePicker = new DatePicker(this);

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
                String date = datePicker.getDayOfMonth() + "/" + (datePicker.getMonth()+1) + "/" + datePicker.getYear();
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
    public void reload(){
        Database database = new Database(this);
        RecyclerView list = findViewById(R.id.homeworks_list);
        ArrayList<ArrayList<String>> data = database.toTabHomeworks();
        database.close();

        recyclerAdapter recyclerAdapter = new recyclerAdapter(data);
        list.setLayoutManager(new LinearLayoutManager(Homeworks.this));
        list.setAdapter(recyclerAdapter);
    }

    /**
     * RecyclerView Adapter
     */
    private class recyclerAdapter extends RecyclerView.Adapter<recyclerAdapter.ViewHolder>{
        /**
         * Data
         */
        private ArrayList<ArrayList<String>> data;

        /**
         * Constructor
         * @param data Data
         */
        public recyclerAdapter(ArrayList<ArrayList<String>> data) {
            this.data = data;
        }

        /**
         * On create ViewHolder
         * @param parent ViewGroup
         * @param viewType Type of view
         * @return ViewHolder
         */
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View listItem= layoutInflater.inflate(R.layout.list_item_homeworks, parent, false);
            recyclerAdapter.ViewHolder viewHolder = new recyclerAdapter.ViewHolder(listItem);
            return viewHolder;
        }

        /**
         * On bind ViewHolder
         * @param holder ViewHolder
         * @param p Position
         */
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int p) {
            int position = p;
            if(!data.get(position).isEmpty()) {
                String status;
                holder.title.setText(data.get(position).get(0));
                holder.date.setText(data.get(position).get(1));
                holder.description.setText(data.get(position).get(2));
                status = data.get(position).get(3);
                if (status.equals("f")) { //red if not done
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        holder.itemView.setBackgroundColor(getColor(R.color.homework_red));
                    }
                    else{
                        holder.itemView.setBackgroundColor(Color.RED);
                    }
                }
                else { //green if done
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                        builder.setTitle(v.getContext().getString(R.string.action));

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
                                }
                                else {
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
