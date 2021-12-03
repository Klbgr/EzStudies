package com.ezstudies.app.agenda;

public class Course {
    private Hours hours;
    private String title;
    private String description;

    public Course(Hours hours, String title, String description) {
        this.hours = hours;
        this.title = title;
        this.description = description;
    }

    public Hours getHours() {
        return hours;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
