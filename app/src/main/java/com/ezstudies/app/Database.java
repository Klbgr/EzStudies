package com.ezstudies.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.ezstudies.app.activities.Agenda;

import java.util.ArrayList;

/**
 * Database
 */
public class Database extends SQLiteOpenHelper {
    /**
     * Name of table 0
     */
    private final String table0 = "agenda";
    /**
     * Name of table 1
     */
    private final String table1 = "homeworks";
    /**
     * Name of column 0 of table 0
     */
    private final String table0_col0 = "date";
    /**
     * Name of column 1 of table 0
     */
    private final String table0_col1 = "title";
    /**
     * Name of column 2 of table 0
     */
    private final String table0_col2 = "startingAt";
    /**
     * Name of column 3 of table 0
     */
    private final String table0_col3 = "endingAt";
    /**
     * Name of column 4 of table 0
     */
    private final String table0_col4 = "description";
    /**
     * Name of column 0 of table 1
     */
    private final String table1_col0 = "title";
    /**
     * Name of column 1 of table 1
     */
    private final String table1_col1 = "date";
    /**
     * Name of column 2 of table 1
     */
    private final String table1_col2 = "description";
    /**
     * Name of column 3 of table 1
     */
    private final String table1_col3 = "done";

    /**
     * Constructor
     * @param context Context
     */
    public Database(@Nullable Context context) {
        super(context, "Database", null, 1);
    }

    /**
     * On create, creates agenda and homeworks tables
     * @param sqLiteDatabase SQLiteDatabase
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE "+table0+"("+table0_col0+" TEXT, "+table0_col1+" TEXT, "+table0_col2+" TEXT, "+table0_col3+" TEXT, "+table0_col4 +" TEXT);");
        sqLiteDatabase.execSQL("CREATE TABLE "+table1+"("+table1_col0+" TEXT, "+table1_col1+" TEXT, "+table1_col2+" TEXT, "+table1_col3+" TEXT);");
    }

    /**
     * On upgrade, drop all tables
     * @param sqLiteDatabase SQLiteDatabase
     * @param i i
     * @param i1 i1
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + table0);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + table1);
        onCreate(sqLiteDatabase);
    }

    /**
     * Add row to agenda table
     * @param date Date
     * @param title Title
     * @param startingAt Start
     * @param endingAt End
     * @param description Description
     */
    public void addAgenda(String date, String title, String startingAt, String endingAt, String description){
        ContentValues contentValues;
        SQLiteDatabase db = this.getWritableDatabase();
        contentValues = new ContentValues();
        contentValues.put(table0_col0, date);
        contentValues.put(table0_col1, title);
        contentValues.put(table0_col2, startingAt);
        contentValues.put(table0_col3, endingAt);
        contentValues.put(table0_col4, description);
        db.insert(table0, null, contentValues);
        db.close();
    }

    public void editAgenda(String date, String title, String startingAt, String endingAt, String newDate, String newTitle, String newStartingAt, String newEndingAt, String newDescription, Context context){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(table0_col0, newDate);
        cv.put(table0_col1, newTitle);
        cv.put(table0_col2, newStartingAt);
        cv.put(table0_col3, newEndingAt);
        cv.put(table0_col4, newDescription);
        db.update(table0, cv, table0_col0 + " = ? AND " + table0_col1 + " = ? AND " + table0_col2 + " = ? AND " + table0_col3 + " = ?", new String[]{date, title, startingAt, endingAt});
        db.close();
        Agenda.cancelNotificationsAgenda(context);
        Agenda.setNotificationsAgenda(context);
    }

    public void deleteAgenda(String date, String title, String startingAt, String endingAt, Context context){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(table0, table0_col0 + " = ? AND " + table0_col1 + " = ? AND " + table0_col2 + " = ? AND " + table0_col3 + " = ?", new String[]{date, title, startingAt, endingAt});
        db.close();
        Agenda.cancelNotificationsAgenda(context);
        Agenda.setNotificationsAgenda(context);
    }

    /**
     * Add row to homeworks table
     * @param title Title
     * @param date Date
     * @param description Description
     */
    public void addHomework(String title, String date, String description){
        ContentValues contentValues;
        SQLiteDatabase db = this.getWritableDatabase();
        contentValues = new ContentValues();
        contentValues.put(table1_col0, title);
        contentValues.put(table1_col1, date);
        contentValues.put(table1_col2, description);
        contentValues.put(table1_col3, "f");
        db.insert(table1, null, contentValues);
        db.close();
    }

    /**
     * Clear agenda table
     */
    public void clearAgenda() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + table0);
        db.close();
    }

    /**
     * Clear homeworks table
     */
    public void clearHomeworks(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + table1);
        db.close();
    }

    /**
     * Agenda table to String
     * @return String
     */
    public String toStringAgenda(){
        String selectQuery = "SELECT * FROM " + table0;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        String out = "";
        while (cursor.moveToNext()){
            out += cursor.getString(0) + "\t" + cursor.getString(1) + "\t" + cursor.getString(2) + "\t" + cursor.getString(3) + "\t" + cursor.getString(4) + "\n";
        }
        cursor.close();
        db.close();
        return out;
    }

    /**
     * Homeworks table to String
     * @return String
     */
    public String toStringHomeworks(){
        String selectQuery = "SELECT * FROM " + table1;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        String out = "";
        while (cursor.moveToNext()){
            out += cursor.getString(0) + "\t" + cursor.getString(1) + "\t" + cursor.getString(2) + "\t" + cursor.getString(3) + "\n";
        }
        cursor.close();
        db.close();
        return out;
    }

    /**
     * Agenda table to tab
     * @return Tab
     */
    public ArrayList<ArrayList<String>> toTabAgenda(){
        ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>();

        String selectQuery = "SELECT * FROM " + table0;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        while(cursor.moveToNext()){
            ArrayList<String> row = new ArrayList<String>();
            for(int i = 0; i <=4; i++){
                row.add(cursor.getString(i));
            }
            list.add(row);
        }
        cursor.close();
        db.close();
        return list;
    }

    /**
     * Homeworks table to tab
     * @return Tab
     */
    public ArrayList<ArrayList<String>> toTabHomeworks(){
        ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>();

        String selectQuery = "SELECT * FROM " + table1 + " ORDER BY " + table1_col3 + " ASC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        while(cursor.moveToNext()){
            ArrayList<String> row = new ArrayList<String>();
            for(int i = 0; i <=3; i++){
                row.add(cursor.getString(i));
            }
            list.add(row);
        }
        cursor.close();
        db.close();
        return list;
    }

    /**
     * Agenda table to ICS
     * @return ICS
     */
    public String toICS(){
        String selectQuery = "SELECT * FROM " + table0;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        String ics = "BEGIN:VCALENDAR\n" +
                "VERSION:2.0\n" +
                "CALSCALE:GREGORIAN\n";
        while (cursor.moveToNext()){
            String date[] = cursor.getString(0).split("/");
            String start[] = cursor.getString(2).split(":");
            String end[] = cursor.getString(3).split(":");
            ics += "BEGIN:VEVENT\n" +
                    "DTSTART:" + date[2] + format(date[1], 2) + format(date[0], 2) + "T" + format(start[0], 2) + format(start[1], 2) + "00Z\n" +
                    "DTEND:" + date[2] + format(date[1], 2) + format(date[0], 2) + "T" + format(end[0], 2) + format(end[1], 2) + "00Z\n" +
                    "DESCRIPTION:" + cursor.getString(4) + "\n" +
                    "SUMMARY:" + cursor.getString(1) + "\n" +
                    "END:VEVENT\n";
        }
        ics += "END:VCALENDAR";
        cursor.close();
        db.close();
        return ics;
    }

    /**
     * Concatenate a number of 0 before String
     * @param s String
     * @param n Number of 0
     * @return Formatted string
     */
    public String format(String s, int n){
        for(int i = s.length() ; i<n ; i++){
            s = "0" + s;
        }
        return s;
    }

    /**
     * Get first course of every days
     * @return First course of every day
     */
    public ArrayList<String[]> getFirsts(){
        String selectQuery = "SELECT * FROM " + table0 + " GROUP BY " + table0_col0 + " ORDER BY " + table0_col0 + " ASC, " + table0_col2 + " ASC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<String[]> list = new ArrayList<>();
        while(cursor.moveToNext()){
            String row[] = {cursor.getString(0), cursor.getString(2)};
            list.add(row);
        }
        cursor.close();
        db.close();
        return list;
    }

    /**
     * Delete a row from homeworks table
     * @param title Title
     * @param date Date
     * @param description Description
     */
    public void removeHomework(String title, String date, String description){
        String deleteQuery = "DELETE FROM " + table1 + " WHERE " + table1_col0 + "='" + title + "' AND " + table1_col1 + "='" + date + "' AND " + table1_col2 + "='" + description + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(deleteQuery);
        db.close();
    }

    /**
     * Update done column of homeworks table
     * @param title Title
     * @param date Date
     * @param description Description
     * @param done Done
     */
    public void setHomeworkDone(String title, String date, String description, String done){
        String updateQuery = "UPDATE " + table1 + " SET " + table1_col3 + " = '" + done + "' WHERE " + table1_col0 + "='" + title + "' AND " + table1_col1 + "='" + date + "' AND " + table1_col2 + "='" + description + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(updateQuery);
        db.close();
    }

    /**
     * A day of agenda table to tab
     * @param day Day
     * @param month Month
     * @param year Year
     * @return Tab
     */
    public ArrayList<ArrayList<String>> toTabAgendaDay(int day, int month, int year){
        ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
        String date = format(String.valueOf(day), 2) + "/" + format(String.valueOf(month), 2) + "/" + format(String.valueOf(year), 4);
        String selectQuery = "SELECT * FROM " + table0 + " WHERE " + table0_col0 + "='" + date + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        while(cursor.moveToNext()){
            ArrayList<String> row = new ArrayList<String>();
            for(int i = 0; i <=4; i++){
                row.add(cursor.getString(i));
            }
            list.add(row);
        }
        cursor.close();
        db.close();
        return list;
    }
}
