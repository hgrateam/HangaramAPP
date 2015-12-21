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
        ParseCallBack callbackEvent = new ParseCallBack() {

            @Override
            public void callbackMethod(ParseSen a) {

            }

            @Override
            public void callbackMethod_Cal(ParseCal a) {
                // ParseSen의 파싱이 끝나면 여기가 호출된다. 이제부터는 getMenu()를 사용할 수 있다.
                // 여기서부터 처리하고 싶은거 처리하면 됨
                // 12월 2015년 으로 설정되어있따.
            }
        };

        ParseCal parsecal = new ParseCal(callbackEvent);

        TextView title = (TextView) findViewById(R.id.calendar_title);
        TableLayout ty = (TableLayout) findViewById(R.id.calendar_tablelayout);
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
