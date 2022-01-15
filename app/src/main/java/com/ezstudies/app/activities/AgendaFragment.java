package com.ezstudies.app.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ezstudies.app.Database;
import com.ezstudies.app.R;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Fragments of Agenda
 */
public class AgendaFragment extends Fragment {
    /**
     * Page of ViewPager
     */
    private int page;
    /**
     * View to be displayed
     */
    private View view;
    /**
     * List of courses
     */
    private RecyclerView list;
    /**
     * Agenda
     */
    private Agenda agenda;
    /**
     * Broadcast receiver for CourseEditor
     */
    private EditorReceiver editorReceiver;

    /**
     * Constructor
     */
    public AgendaFragment(){}

    /**
     * Constructor
     * @param page Requested page
     */
    public AgendaFragment (int page, Agenda agenda){
        this.page = page;
        this.agenda = agenda;
    }

    /**
     * On create View
     * @param inflater LayoutInflater
     * @param container ViewGroup
     * @param savedInstanceState Bundle
     * @return View
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.agenda_page, container, false);
        TextView day = view.findViewById(R.id.agenda_day);
        switch (page){
            case 1: //monday
                day.setText(getString(R.string.monday));
                break;
            case 2: //tuesday
                day.setText(getString(R.string.tuesday));
                break;
            case 3: //wednesday
                day.setText(getString(R.string.wednesday));
                break;
            case 4: //thursday
                day.setText(getString(R.string.thursday));
                break;
            case 5: //friday
                day.setText(getString(R.string.friday));
                break;
            case 6: //saturday
                day.setText(getString(R.string.saturday));
                break;
            default:
                break;
        }
        list = view.findViewById(R.id.agenda_list);
        Database database = new Database(this.getContext());
        ArrayList<ArrayList<String>> data = database.toTabAgenda();
        database.close();
        ArrayList<ArrayList<String>> coursList = new ArrayList<>();
        for(ArrayList<String> row : data){ //for each courses of the week
            String[] dateTab = row.get(0).split("/");
            Calendar cal = Calendar.getInstance();
            cal.setFirstDayOfWeek(Calendar.MONDAY);
            cal.set(Integer.parseInt(dateTab[2]), Integer.parseInt(dateTab[1])-1, Integer.parseInt(dateTab[0]));
            int weekDay = cal.get(Calendar.DAY_OF_WEEK)-1;
            if (weekDay == page){ //correct day
                ArrayList<String> courseData = new ArrayList<String>();
                courseData.add(row.get(1));
                courseData.add(row.get(0) + " : " + row.get(2) + " - " + row.get(3));
                if(row.get(4) == null){
                    courseData.add("");
                }
                else if (row.get(4).contains(" / ")){ //if multiple infos
                    courseData.add(row.get(4).split(" / ")[0]);
                    courseData.add(row.get(4).split(" / ")[1]);
                }
                else{
                    courseData.add(row.get(4));
                }
                coursList.add(courseData);
            }
        }
        RecyclerAdapterAgenda recyclerAdapter = new RecyclerAdapterAgenda(coursList);
        list.setLayoutManager(new LinearLayoutManager(this.getContext()));
        list.setAdapter(recyclerAdapter);

        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.agenda_swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            /**
             * On refresh
             */
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                agenda.refresh(null);
            }
        });
        return view;
    }

    /**
     * RecyclerView Adapter
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
         * @return viewHolder
         */
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View listItem= layoutInflater.inflate(R.layout.list_item_course, parent, false);
            ViewHolder viewHolder = new ViewHolder(listItem);
            return viewHolder;
        }

        /**
         * On bind ViewHolder
         * @param holder ViewHolder
         * @param position Position
         */
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            if(!data.get(position).isEmpty()){ //not null
                holder.card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        editorReceiver = new EditorReceiver();
                        agenda.registerReceiver(editorReceiver, new IntentFilter("AgendaEdited"));
                        Intent intent = new Intent(getContext(), CourseEditor.class);
                        intent.putExtra("course", holder.course.getText());
                        intent.putExtra("hour", holder.hour.getText());
                        intent.putExtra("place", holder.place.getText());
                        intent.putExtra("info", holder.info.getText());
                        startActivity(intent);
                    }
                });
                try{
                    holder.course.setText(data.get(position).get(0));
                    holder.hour.setText(data.get(position).get(1));
                    holder.place.setText(data.get(position).get(2));
                    holder.info.setText(data.get(position).get(3));
                } catch (IndexOutOfBoundsException e){
                    e.printStackTrace();
                }
            }
        }

        /**
         * Get number of items
         * @return NUmber of items
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
             * Main layout
             */
            public ConstraintLayout card;

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
                card = itemView.findViewById(R.id.card);
            }
        }
    }

    /**
     * Broadcast receiver for CourseEditor
     */
    private class EditorReceiver extends BroadcastReceiver{
        /**
         * On receive
         * @param context Context
         * @param intent Intent
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            agenda.unregisterReceiver(editorReceiver);
            if (intent.getBooleanExtra("edited", false)){
                agenda.restart();
            }
        }
    }
}
