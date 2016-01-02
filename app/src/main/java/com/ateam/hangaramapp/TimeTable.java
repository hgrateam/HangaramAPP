package com.ateam.hangaramapp;

import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;

public class TimeTable extends AppCompatActivity implements TimeTableDialogFragment.TimeTableDialogListener{

    int day, column;
    TimeTableAdapter ttadapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_table);

        ttadapter = new TimeTableAdapter(TimeTable.this);
        GridView gv = (GridView) findViewById(R.id.ttgrid);
        gv.setAdapter(ttadapter);
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("info", "pos = " + position + " id = " + id);
                // 7 8 9 10 11
                // 13 14 15 16 17
                // ...
                if(position%6!=0 && position>=7){
                    day = position%6; // 1부터 시작
                    column = position/6; // 1부터 시작
                    showTimeTableDialog(day, column);
                }
            }
        });
    }
    public void showTimeTableDialog(int day, int column){
        TimeTableDialogFragment fragment = new TimeTableDialogFragment();
        fragment.setDate(day, column);
        fragment.show(getSupportFragmentManager(),"TimeTable");

    }

    @Override
    public void onDialogPositiveClick(String value) {
        Log.i("info", "name is "+value);
//        ttadapter.addClass(value,day,column);
        ttadapter.addClass(new cellInfo(value,day,column));

        ttadapter.notifyDataSetChanged();
    }
}
class cellInfo{
    public cellInfo(String name, int day, int column){
        this.name = name;
        this.day = day;
        this.column = column;
    }
    public int getPosition(){
        return day+column*6;
    }
    public String getName(){
        return name;
    }
    private String name;
    private int day;
    private int column;
}
class TimeTableAdapter extends BaseAdapter{
    String dayName[]={"","월","화","수","목","금"};
    String timeIndi[] ={"8:00~\n9:15", "9:25~\n10:40", "10:50~\n12:5", "13:10~\n14:25", "14:35~\n15:50","16:00~\n17:15"};
    Context context;
    ArrayList<cellInfo> cellinfos;
    LayoutInflater inf;

    public TimeTableAdapter(Context context) {
        this.context = context;
        inf = (LayoutInflater) context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        cellinfos = new ArrayList<cellInfo>();
    }

    public void addClass(cellInfo a){
        cellinfos.add(a);
    }
    @Override
    public int getCount() {
        return 42;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        if (convertView==null)
        if(position<6){
            convertView = inf.inflate(R.layout.timetable_indicator, null);
            TextView tv = (TextView) convertView.findViewById(R.id.ttindi);
            tv.setText(dayName[position]);
        }
        else if(position%6==0) {
            convertView = inf.inflate(R.layout.timetable_indicator, null);
            TextView tv = (TextView) convertView.findViewById(R.id.ttindi);
            tv.setText(timeIndi[(position/6)-1]);
        }
        else{
            convertView = inf.inflate(R.layout.timetable_row, null);
            TextView tv2 = (TextView) convertView.findViewById(R.id.ttrow);
            for(int i=0;i<cellinfos.size();i++){
                if(cellinfos.get(i).getPosition() == position) {
                    tv2.setText(cellinfos.get(i).getName());
                }

            }
        }
        return convertView;
    }
}



/*
* 1교시 8시~9시15분
2교시 9시25분~10시40분
3교시 10시 50분~ 12시 5분
점심
4교시 1시 10분~2시25분
5교시 2시35분~3시50분
6교시 4시~5시15분(6교시없을땐 이게 야자1교시입니다!)
2교시야자 6시~8시
3교시야자 8시10분~10시
11시야자는 11시까지 이렇게
*/