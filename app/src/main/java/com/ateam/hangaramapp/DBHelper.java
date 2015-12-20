package com.ateam.hangaramapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by hyun ji on 2015-12-20.
 */
public class DBHelper extends SQLiteOpenHelper{

    public static String DB_FILE_NAME = "hgrdb.db";

    public static String TODAYMEAL_TABLE_NAME = "todaymeal";

    Context mContext;

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+TODAYMEAL_TABLE_NAME+"( _id INTEGER PRIMARY KEY AUTOINCREMENT, date INTEGER, lunch TEXT, dinner TEXT);");
    }
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

    /*
        public String PrintData() {
        SQLiteDatabase db = getReadableDatabase();
        String str = "";

        Cursor cursor = db.rawQuery("select * from DATA", null);
        while(cursor.moveToNext()) {
            str += cursor.getInt(0)
                    + " : reason "
                    + cursor.getString(1)
                    + ", cost = "
                    + cursor.getInt(2)
                    + ", date = "
                    + cursor.getInt(3)
                    + "\n";
        }
        return str;
    }

    * */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
