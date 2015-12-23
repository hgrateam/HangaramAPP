package com.ateam.hangaramapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.util.Calendar;

import android.util.Log;
import android.util.TypedValue;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.view.Gravity;

public class SCalendar extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);


        ParseCal parsecal = new ParseCal();
        parsecal.setCallBackListener(new ParseCal.ParseCallBack() {
            @Override
            public void OnFinish(ParseCal a) {
                // 처리는 여기에
            }
        });

        TextView title = (TextView) findViewById(R.id.calendar_title);
        Calendar cal = Calendar.getInstance();

        int month, year;
        float mScale = getResources().getDisplayMetrics().density;

        year = 2015;
        month = 12;

        parsecal.setYear(year);
        parsecal.setMonth(month);
        parsecal.parse();

        month --;
    }
}
