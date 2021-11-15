package com.ezstudies.app.agenda;

public class Hours {
    private int startHour;
    private int startMinute;
    private int endHour;
    private int endMinute;

    public Hours(int startHour, int startMinute, int endHour, int endMinute) {
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.endHour = endHour;
        this.endMinute = endMinute;
    }

    public String getStartHour() {
        return String.format("%02d", startHour);
    }

    public String getStartMinute() {
        return String.format("%02d", startMinute);
    }

    public String getEndHour() {
        return String.format("%02d", endHour);
    }

    public String getEndMinute() {
        return String.format("%02d", endMinute);
    }

    @Override
    public String toString(){
        return startHour + ":" + startMinute + "-" + endHour + ":" + endMinute;
    }
}
