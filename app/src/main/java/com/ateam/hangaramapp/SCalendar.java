package com.ateam.hangaramapp;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Calendar;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.view.Gravity;

public class SCalendar extends AppCompatActivity{

    RecyclerView rv;
    RecyclerView.Adapter mAdapter;
    ProgressDialog progDialog;

    ArrayList<Calendar_cellInfo> calendarlist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        progDialog = new ProgressDialog(this);

        ParseCal parsecal = new ParseCal();
        parsecal.setCallBackListener(new ParseCal.ParseCallBack() {
            @Override
            public void OnFinish(ParseCal a) {
                progDialog.dismiss();
                Log.i("info", "총 몇개 파싱함 ㅎㅎ"+calendarlist.size());
                rv.setAdapter(new CalendarAdapter(calendarlist));
                // 처리는 여기에
            }
        });

        rv = (RecyclerView) findViewById(R.id.my_recycler_view);

        rv.setLayoutManager(new LinearLayoutManager(this));

        calendarlist = new ArrayList<>();
        mAdapter = new CalendarAdapter(calendarlist);
        rv.setAdapter(mAdapter);

        int year, month;
        year = 2015;
        month = 12;

        parsecal.setYear(year);
        parsecal.setMonth(month);
        parsecal.parse(calendarlist);

        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setMessage("정보를 받아오는 중입니다..");
        progDialog.show();


        month --;
    }
}
