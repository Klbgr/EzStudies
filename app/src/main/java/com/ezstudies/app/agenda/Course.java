package com.ezstudies.app.agenda;

import java.util.ArrayList;

public class Course {
    private Hours hours;
    private String type;
    private String title;
    private String place;
    private ArrayList<String> teachers;

    public Course(Hours hours, String type, String title, String place, ArrayList<String> teachers) {
        this.hours = hours;
        this.type = type;
        this.title = title;
        this.place = place;
        this.teachers = teachers;
    }

    public Hours getHours() {
        return hours;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getPlace() {
        return place;
    }

    public ArrayList<String> getTeachers() {
        return teachers;
    }

    public void setTeachers(ArrayList<String> teachers) {
        this.teachers = teachers;
    }

    @Override
    public String toString() {
        return "Course{" +
                "hours=" + hours.toString() +
                ", type='" + type + '\'' +
                ", title='" + title + '\'' +
                ", place='" + place + '\'' +
                ", teachers=" + teachers +
                '}';
    }
}
