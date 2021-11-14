package com.ezstudies.app.agenda;

import java.util.ArrayList;

public class Week {
    private ArrayList<Day> days;

    public Week() {
        days = new ArrayList<>();
    }

    public ArrayList<Day> getDays() {
        return days;
    }

    public void setDays(ArrayList<Day> days) {
        this.days = days;
    }

    public void addDay(Day d){
        days.add(d);
    }
}
