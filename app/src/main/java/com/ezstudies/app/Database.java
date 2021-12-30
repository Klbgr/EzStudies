package com.ezstudies.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class Database extends SQLiteOpenHelper {
    private final String table0 = "agenda";
    private final String table1 = "homeworks";
    private final String table0_col0 = "date";
    private final String table0_col1 = "title";
    private final String table0_col2 = "startingAt";
    private final String table0_col3 = "endingAt";
    private final String table0_col4 = "description";
    private final String table1_col0 = "title";
    private final String table1_col1 = "date";
    private final String table1_col2 = "description";
    private final String table1_col3 = "done";

    public Database(@Nullable Context context) {
        super(context, "Database", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE "+table0+"("+table0_col0+" TEXT, "+table0_col1+" TEXT, "+table0_col2+" TEXT, "+table0_col3+" TEXT, "+table0_col4 +" TEXT);");
        sqLiteDatabase.execSQL("CREATE TABLE "+table1+"("+table1_col0+" TEXT, "+table1_col1+" TEXT, "+table1_col2+" TEXT, "+table1_col3+" TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + table0);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + table1);
        onCreate(sqLiteDatabase);
    }

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

    public void clearAgenda() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + table0);
        db.close();
    }

    public void clearHomeworks(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + table1);
        db.close();
    }

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

    public String format(String s, int n){
        for(int i = s.length() ; i<n ; i++){
            s = "0" + s;
        }
        return s;
    }

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

    public void removeHomework(String title, String date, String description){
        String deleteQuery = "DELETE FROM " + table1 + " WHERE " + table1_col0 + "='" + title + "' AND " + table1_col1 + "='" + date + "' AND " + table1_col2 + "='" + description + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(deleteQuery);
        db.close();
    }

    public void setHomeworkDone(String title, String date, String description, String done){
        String updateQuery = "UPDATE " + table1 + " SET " + table1_col3 + " = '" + done + "' WHERE " + table1_col0 + "='" + title + "' AND " + table1_col1 + "='" + date + "' AND " + table1_col2 + "='" + description + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(updateQuery);
        db.close();
    }
}
