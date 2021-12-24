package com.ezstudies.app;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;

public class AgendaFragment extends Fragment {
    private int page;
    private View view;
    private RecyclerView list;

    public AgendaFragment(){};

    public AgendaFragment (int page){
        this.page = page;

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.agenda_page, container, false);
        TextView day = view.findViewById(R.id.day);
        switch (page){
            case 1:
                day.setText("Lundi");
                break;
            case 2:
                day.setText("Mardi");
                break;
            case 3:
                day.setText("Mercredi");
                break;
            case 4:
                day.setText("Jeudi");
                break;
            case 5:
                day.setText("Vendredi");
                break;
            case 6:
                day.setText("Samedi");
                break;
            default:
                break;
        }
        list = view.findViewById(R.id.list);
        Database database = new Database(this.getContext());
        ArrayList<ArrayList<String>> data = database.toTab();
        ArrayList<ArrayList<String>> coursList = new ArrayList<>();
        for(ArrayList<String> row : data){
            String[] dateTab = row.get(0).split("/");
            Calendar cal = Calendar.getInstance();
            cal.setFirstDayOfWeek(Calendar.MONDAY);
            cal.set(Integer.parseInt(dateTab[2]), Integer.parseInt(dateTab[1])-1, Integer.parseInt(dateTab[0]));
            int weekDay = cal.get(Calendar.DAY_OF_WEEK)-1;
            Log.d("day", Integer.parseInt(dateTab[2]) + "/" + Integer.parseInt(dateTab[1]) + "/" + Integer.parseInt(dateTab[0]) + " = " + weekDay);
            if (weekDay == page){
                ArrayList<String> coursData = new ArrayList<String>();
                coursData.add(row.get(1));
                coursData.add(row.get(2) + " - " + row.get(3));
                if (row.get(4).contains(" / ")){
                    coursData.add(row.get(4).split(" / ")[0]);
                    coursData.add(row.get(4).split(" / ")[1]);
                }
                else{
                    coursData.add(row.get(4));
                }
                coursList.add(coursData);
            }
        }
        recyclerAdapter recyclerAdapter = new recyclerAdapter(this.getContext(), coursList);
        list.setLayoutManager(new LinearLayoutManager(this.getContext()));
        list.setAdapter(recyclerAdapter);
        return view;
    }

    public class recyclerAdapter extends RecyclerView.Adapter<recyclerAdapter.ViewHolder>{
        ArrayList<ArrayList<String>> data;

        public recyclerAdapter(Context context, ArrayList<ArrayList<String>> data){
            Database database = new Database(context);
            this.data = data;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View listItem= layoutInflater.inflate(R.layout.list_item_cours, parent, false);
            ViewHolder viewHolder = new ViewHolder(listItem);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            if(!data.get(position).isEmpty()){
                holder.cours.setText(data.get(position).get(0));
                holder.hour.setText(data.get(position).get(1));
                holder.salle.setText(data.get(position).get(2));
                holder.info.setText(data.get(position).get(3));
            }

        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView cours;
            public TextView hour;
            public TextView info;
            public TextView salle;
            public ViewHolder(View itemView) {
                super(itemView);
                cours = itemView.findViewById(R.id.TextViewCours);
                hour = itemView.findViewById(R.id.textViewHour);
                info = itemView.findViewById(R.id.textViewInfo);
                salle = itemView.findViewById(R.id.textViewSalle);
            }
        }
    }

}
