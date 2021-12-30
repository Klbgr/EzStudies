package com.ezstudies.app.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
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

public class Homeworks extends AppCompatActivity {
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
                Log.d("db", database.toStringHomeworks());
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

    public void reload(){
        Database database = new Database(this);
        RecyclerView list = findViewById(R.id.homeworks_list);
        ArrayList<ArrayList<String>> data = database.toTabHomeworks();
        database.close();

        recyclerAdapter recyclerAdapter = new recyclerAdapter(data);
        list.setLayoutManager(new LinearLayoutManager(Homeworks.this));
        list.setAdapter(recyclerAdapter);
    }

    private class recyclerAdapter extends RecyclerView.Adapter<recyclerAdapter.ViewHolder>{
        private ArrayList<ArrayList<String>> data;

        public recyclerAdapter(ArrayList<ArrayList<String>> data) {
            this.data = data;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View listItem= layoutInflater.inflate(R.layout.list_item_homeworks, parent, false);
            recyclerAdapter.ViewHolder viewHolder = new recyclerAdapter.ViewHolder(listItem);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int p) {
            int position = p;
            if(!data.get(position).isEmpty()) {
                String status;
                holder.title.setText(data.get(position).get(0));
                holder.date.setText(data.get(position).get(1));
                holder.description.setText(data.get(position).get(2));
                status = data.get(position).get(3);
                if (status.equals("f")) {
                    holder.itemView.setBackgroundColor(Color.RED);
                } else {
                    holder.itemView.setBackgroundColor(Color.GREEN);
                }

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                        builder.setTitle(v.getContext().getString(R.string.action));

                        builder.setNegativeButton(getString(R.string.remove), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Database database = new Database(Homeworks.this);
                                database.removeHomework(data.get(position).get(0), data.get(position).get(1), data.get(position).get(2));
                                Log.d("db", database.toStringHomeworks());
                                database.close();

                                reload();
                            }
                        });

                        builder.setNeutralButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        builder.setPositiveButton(getString(R.string.toggle), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Database database = new Database(Homeworks.this);
                                if (status.equals("f")) {
                                    database.setHomeworkDone(data.get(position).get(0), data.get(position).get(1), data.get(position).get(2), "t");
                                } else {
                                    database.setHomeworkDone(data.get(position).get(0), data.get(position).get(1), data.get(position).get(2), "f");
                                }
                                Log.d("db", database.toStringHomeworks());
                                database.close();

                                reload();
                            }
                        });

                        builder.show();
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView title;
            public TextView date;
            public TextView description;
            public ViewHolder(View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.homeworks_title);
                date = itemView.findViewById(R.id.homeworks_date);
                description = itemView.findViewById(R.id.homeworks_description);
            }
        }
    }
}
