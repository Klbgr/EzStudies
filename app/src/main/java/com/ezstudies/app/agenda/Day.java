package com.ezstudies.app.agenda;

import java.util.ArrayList;

public class Day {
    private Date date;
    private ArrayList<Course> courses;

    public Day(Date date) {
        this.date = date;
        courses = new ArrayList<>();
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public ArrayList<Course> getCourses() {
        return courses;
    }

    public void setCourses(ArrayList<Course> courses) {
        this.courses = courses;
    }

    public void addCourse(Course c){
        courses.add(c);
    }
}
