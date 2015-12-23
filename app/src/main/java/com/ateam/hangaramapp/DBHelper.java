package com.ateam.hangaramapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by hyun ji on 2015-12-20.
 */
public class DBHelper extends SQLiteOpenHelper{

    public final static String DB_FILE_NAME = "hgrdb.db";

    public final static String TODAYMEAL_TABLE_NAME = "todaymeal";
    public final static String SUBJECTS_TABLE_NAME = "subjects";
    public final static String TIMETABLE_TABLE_NAME = "timetable";

    public final static int TODAYMEAL_TABLE = 1;
    public final static int SUBJECTS_TABLE = 2;
    public final static int TIMETABLE_TABLE = 3;
    int table_code;
    private SQLiteDatabase db;
    Context mContext;

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, int tableFlag) {
        super(context, name, factory, version);
        table_code = tableFlag;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("info"," DBHelper.onCreate : table+code = "+table_code);
  /*      switch(table_code) {
            case TODAYMEAL_TABLE:
                Log.i("info"," 오늘의급식DB 생성!");
                db.execSQL("CREATE TABLE " + TODAYMEAL_TABLE_NAME + "( _id INTEGER PRIMARY KEY AUTOINCREMENT, date INTEGER, lunch TEXT, dinner TEXT);");
                break;
            case SUBJECTS_TABLE:
                Log.i("info"," 과목 생성!");
                db.execSQL("CREATE TABLE " + SUBJECTS_TABLE_NAME + "( _id INTEGER PRIMARY KEY AUTOINCREMENT, name INTEGER, colorcode INTENGER memo TEXT);");
                break;
        }
*/    }

    public void insert(String _query) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(_query);
        db.close();
    }

    public void update(String _query) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(_query);
        db.close();
    }

    public void delete(String _query) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(_query);
        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        switch(table_code) {
            case TODAYMEAL_TABLE:
                Log.i("info"," 오늘의급식DB 생성!");
                db.execSQL("CREATE TABLE IF NOT EXISTS " + TODAYMEAL_TABLE_NAME + "( _id INTEGER PRIMARY KEY AUTOINCREMENT, date INTEGER, lunch TEXT, dinner TEXT);");
                break;
            case SUBJECTS_TABLE:
                Log.i("info"," 과목 생성!");
                db.execSQL("CREATE TABLE IF NOT EXISTS " + SUBJECTS_TABLE_NAME + "( _id INTEGER PRIMARY KEY AUTOINCREMENT, name INTEGER, colorcode INTENGER memo TEXT);");
                break;
        }
    }
}
