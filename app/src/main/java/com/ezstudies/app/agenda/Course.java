package com.ezstudies.app.agenda;

public class Course {
    private Hours hours;
    private String type;
    private String title;
    private String description;

    public Course(Hours hours, String type, String title, String description) {
        this.hours = hours;
        this.type = type;
        this.title = title;
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "Course{" +
                "hours=" + hours +
                ", type='" + type + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
