package com.example.towaquiz.delegates;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 4;
    public static final String DATABASE_NAME = "urlRequests.db";

    public static final String TABLE_NAME = "urls";
    //columns
    public static final String ID = "_id";
    public static final String URL = "url";
    public static final String PARAMS = "params";

    // Creating table query
    private static final String CREATE_TABLE = "create table " + TABLE_NAME + "(" + ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + URL + " TEXT UNIQUE, " + PARAMS + " TEXT NOT NULL" + ")";

    public DBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
