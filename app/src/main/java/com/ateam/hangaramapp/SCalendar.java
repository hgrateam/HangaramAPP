package com.ateam.hangaramapp;

import android.app.ProgressDialog;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.view.Gravity;
/**
 * 이름 내림차순
 * @author falbb
 *
 */
class DateDescCompare implements Comparator<Calendar_cellInfo> {

    /**
     * 내림차순(DESC)
     */
    @Override
    public int compare(Calendar_cellInfo arg0, Calendar_cellInfo arg1) {
        // TODO Auto-generated method stub
        return arg1.getDate()+"".compareTo(arg0.getDate() + "");
    }

}

public class SCalendar extends AppCompatActivity{

    RecyclerView rv;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    ProgressDialog progDialog;
    int t_year, t_month, t_day, t_date;

    ArrayList<Calendar_cellInfo> calendarlist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        progDialog = new ProgressDialog(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_calendar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();

        if(ab!=null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }


        ParseCal parsecal = new ParseCal();
        parsecal.setCallBackListener(new ParseCal.ParseCallBack() {
            @Override
            public void OnFinish(ParseCal a) {
                progDialog.dismiss();
                Log.i("info", "총 몇개 파싱함 ㅎㅎ" + calendarlist.size());


                // 정렬은 하고
                final Comparator<Calendar_cellInfo> comparator  = new Comparator<Calendar_cellInfo>() {
                    @Override
                    public int compare(Calendar_cellInfo lhs, Calendar_cellInfo rhs) {
                        int by1 = lhs.getDate();
                        int by2 = rhs.getDate();
                        return by1 > by2 ? -1 : (by1 == by2 ? 0 : 1);
                    }
                };

                Collections.sort(calendarlist, comparator);

                int pos=0;
                int date;
                for(int i=0;i<calendarlist.size()-1;i++){
                    if(calendarlist.get(i).getDate() == calendarlist.get(i+1).getDate() && calendarlist.get(i).getType() == 1){
                        date = calendarlist.get(i).getDate();
                        calendarlist.set(i, new Calendar_cellInfo(date/10000, date%10000/100, calendarlist.get(i).getCnt() + calendarlist.get(i+1).getCnt()));
                        calendarlist.remove(i + 1);
                        i--;
                    }

                }
                for(int i=0;i<calendarlist.size();i++){
                    if(calendarlist.get(i).getDate()/100 == t_date/100){
                        pos = i;
                        break;
                    }

                }

                rv.setAdapter(new CalendarAdapter(calendarlist));

                mLayoutManager.scrollToPosition(pos);
                // 처리는 여기에
            }
        });

        rv = (RecyclerView) findViewById(R.id.my_recycler_view);

        mLayoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(mLayoutManager);

        calendarlist = new ArrayList<>();
        mAdapter = new CalendarAdapter(calendarlist);
        rv.setAdapter(mAdapter);

        int year, month;

        GregorianCalendar gcalendar = new GregorianCalendar();
        t_year=gcalendar.get(Calendar.YEAR); // index 1
        t_month=gcalendar.get(Calendar.MONTH)+1; // index 1
        t_day=gcalendar.get(Calendar. DAY_OF_MONTH); // index 1
        t_date = t_year*10000+t_month*100+t_day;

        parsecal.setYear(t_year);
        parsecal.setMonth(t_month);
        parsecal.parse(calendarlist);

        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setMessage("정보를 받아오는 중입니다..");
        progDialog.show();

    }
}
