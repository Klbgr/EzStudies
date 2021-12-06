package com.ezstudies.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class Database extends SQLiteOpenHelper {
    private final String table = "agenda";
    private final String col0 = "date";
    private final String col1 = "title";
    private final String col2 = "startingAt";
    private final String col3 = "endingAt";
    private final String col4 = "description";

    public Database(@Nullable Context context) {
        super(context, "Database", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String request = "CREATE TABLE "+table+"("+col0+" TEXT, "+col1+" TEXT, "+col2+" TEXT, "+col3+" TEXT, "+col4 +" TEXT);";
        sqLiteDatabase.execSQL(request);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + table);
        onCreate(sqLiteDatabase);
    }

    public void add(String date, String title, String startingAt, String endingAt, String description){
        ContentValues contentValues;
        SQLiteDatabase db =this.getWritableDatabase();
        contentValues = new ContentValues();
        contentValues.put(col0, date);
        contentValues.put(col1, title);
        contentValues.put(col2, startingAt);
        contentValues.put(col3, endingAt);
        contentValues.put(col4, description);
        db.insert(table, null, contentValues);
        db.close();
    }

    public void clear() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + table);
        db.close();
    }

    public String toString(){
        String selectQuery = "SELECT * FROM " + table;
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

}
