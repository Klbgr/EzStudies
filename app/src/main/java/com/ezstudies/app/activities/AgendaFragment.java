package com.ezstudies.app.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
     * Constructor
     */
    public AgendaFragment(){}

    /**
     * Constructor
     * @param page Requested page
     */
    public AgendaFragment (int page){
        this.page = page;
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
                courseData.add(row.get(2) + " - " + row.get(3));
                if (row.get(4).contains(" / ")){ //if multiple infos
                    courseData.add(row.get(4).split(" / ")[0]);
                    courseData.add(row.get(4).split(" / ")[1]);
                }
                else{
                    courseData.add(row.get(4));
                }
                coursList.add(courseData);
            }
        }
        recyclerAdapter recyclerAdapter = new recyclerAdapter(this.getContext(), coursList);
        list.setLayoutManager(new LinearLayoutManager(this.getContext()));
        list.setAdapter(recyclerAdapter);
        return view;
    }

    /**
     * RecyclerView Adapter
     */
    public class recyclerAdapter extends RecyclerView.Adapter<recyclerAdapter.ViewHolder>{
        /**
         * Data
         */
        private ArrayList<ArrayList<String>> data;

        /**
         * Constructor
         * @param context Context
         * @param data Data
         */
        public recyclerAdapter(Context context, ArrayList<ArrayList<String>> data){
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
                holder.course.setText(data.get(position).get(0));
                holder.hour.setText(data.get(position).get(1));
                holder.place.setText(data.get(position).get(2));
                holder.info.setText(data.get(position).get(3));
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
}
