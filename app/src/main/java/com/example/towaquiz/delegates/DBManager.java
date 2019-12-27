package com.example.towaquiz.delegates;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DBManager {

    private DBHelper dbHelper;
    private Context context;
    private SQLiteDatabase database;


    public DBManager(Context context){
        this.context = context;
    }

    public DBManager open() {
        this.dbHelper = new DBHelper(this.context);
        this.database = this.dbHelper.getWritableDatabase();

        return this;
    }

    public void close(){
        this.dbHelper.close();
    }

    public void insert(String url, String params){
        Log.i("inserting", url);
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.URL, url);
        contentValues.put(DBHelper.PARAMS, params);
        if( -1 == this.database.insert(DBHelper.TABLE_NAME, null, contentValues)){
            replace(url, params);
        }
    }

    public Cursor fetch(){
        String[] columns = new String[] {DBHelper.ID, DBHelper.URL, DBHelper.PARAMS};
        Cursor cursor = this.database.query(DBHelper.TABLE_NAME, columns, null, null, null, null, null);
        if(cursor != null){
            cursor.moveToFirst();
        }
        return cursor;
    }

    public int update(long id, String url, String params){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.URL, url);
        contentValues.put(DBHelper.PARAMS, params);

        return this.database.update(DBHelper.TABLE_NAME, contentValues, DBHelper.ID + " = " + id, null);
    }

    public long replace(String url, String params){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.URL, url);
        contentValues.put(DBHelper.PARAMS, params);

        return this.database.replace(DBHelper.TABLE_NAME, null, contentValues);
    }

    public void delete(long id){
        this.database.delete(DBHelper.TABLE_NAME, DBHelper.ID + " = " + id, null);
    }

    public Cursor suggestItemCompletions(String partialItemName) {
        String[] columns = new String[] {DBHelper.ID, DBHelper.URL, DBHelper.PARAMS};
        Cursor cursor = this.database.query(DBHelper.TABLE_NAME, columns,
                DBHelper.URL + " LIKE ?",
                new String[] { "%" + partialItemName + "%" },
                null, null, null, null);
        if(cursor != null){
            cursor.moveToFirst();
        }
        return cursor;
    }
}
