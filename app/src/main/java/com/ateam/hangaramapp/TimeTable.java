package com.ateam.hangaramapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class TimeTable extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_table);

        if(!isSubjectExist()){
            Intent intent = new Intent(TimeTable.this, AddSubject.class);
            Log.i("info", "Goto AddSubject.class");
            startActivity(intent);
        }
        else{

        }
    }
    private boolean isSubjectExist(){
        DBHelper helper = new DBHelper(TimeTable.this, DBHelper.DB_FILE_NAME, null, 1, DBHelper.SUBJECTS_TABLE);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + DBHelper.SUBJECTS_TABLE_NAME, null);

        int cnt=0;

        while (cursor.moveToNext()) {
            cnt++;
        }

        helper.close();
        if(cnt == 0 ) {
            return false;
        }
        return true;
    }
}
