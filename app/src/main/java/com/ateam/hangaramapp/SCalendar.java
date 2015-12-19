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

public class SCalendar extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        TextView title = (TextView) findViewById(R.id.calendar_title);
        TableLayout ty = (TableLayout) findViewById(R.id.calendar_tablelayout);
        Calendar cal = Calendar.getInstance();

        int month, year;
        float mScale = getResources().getDisplayMetrics().density;

        year = 2015;
        month = 12;
        month --;

        Log.i("info", "MONTH = " + month + ", YEAR = " + year);

        cal.set(year, month, 1);
        int day_of_week = cal.get(Calendar.DAY_OF_WEEK);
        title.setText(year+"년 "+month+"월");


        System.out.println(day_of_week);

        int dayCnt=-1;
        int doM[]={0,31,28,31,30,31,30,31,31,30,31,30,31};
        String noD[]={"일","월","화","수","목","금","토"};

        boolean flag = false;

        for(int i=0;dayCnt<doM[month];i++){
            TableRow tr = new TableRow(this);
            tr.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

            for(int j=0;j<7;j++) {
                TextView a = new TextView(this);
                if(dayCnt==-1) {
                    a.setText(""+noD[j]);
                    if(j==6)
                        dayCnt=0;
                }
                else {
                    if (dayCnt < 7 && !flag) {
                        if (day_of_week-1 == j)
                            flag = true;
                    }
                    if (flag && dayCnt<=doM[month])
                        a.setText("" + ++dayCnt);
                    else
                        a.setText("");
                }

                a.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT, 1f));
                a.setGravity(Gravity.CENTER);
                tr.addView(a);
            }
            ty.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,TableLayout.LayoutParams.WRAP_CONTENT));
        }
    }
}
