package com.ezstudies.app.agenda;

public class Date {
    private int day;
    private int month;
    private int year;

    public Date(int day, int month, int year) {
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public String getDay() {
        return String.format("%02d", day);
    }

    public String getMonth() {
        return String.format("%02d", month);
    }

    public String getYear() {
        return String.valueOf(year);
    }
}
