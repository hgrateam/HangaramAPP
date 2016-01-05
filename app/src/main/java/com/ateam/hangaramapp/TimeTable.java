package com.ateam.hangaramapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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

    private int day, column;
    private TimeTableAdapter ttadapter;
    private ArrayList<cellInfo> cellinfos;
    private TimeTableGridView gv;
    private DBHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_table);

        cellinfos = new ArrayList<cellInfo>();
        ttadapter = new TimeTableAdapter(TimeTable.this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_time_table);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();

        if(ab!=null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        gv = (TimeTableGridView) findViewById(R.id.ttgrid);
        gv.setAdapter(ttadapter);
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("info", "pos = " + position + " id = " + id);

                if (position % 6 != 0 && position >= 7) {
                    day = position % 6; // 1부터 시작
                    column = position / 6; // 1부터 시작
                    showTimeTableDialog(day, column);
                }
            }
        });

        // 이미 저장되어있는 DB가 존재하면 표에 추가해준다.
        helper = new DBHelper(TimeTable.this, DBHelper.DB_FILE_NAME, null, 1, DBHelper.TIMETABLE_TABLE);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + DBHelper.TIMETABLE_TABLE_NAME, null);
        while (cursor.moveToNext()) {
            addClass(cursor.getString(1), cursor.getInt(2) % 6, cursor.getInt(2) / 6, cursor.getString(3));
        }
        helper.close();
    }
    public void showTimeTableDialog(int day, int column){
        TimeTableDialogFragment fragment = new TimeTableDialogFragment();
        // 이때까지 저장되어있는 과목명단을 넘겨준다.
        Log.i("info", "day : " + day + " column : " + column + "번째 클릭-> Dialog 표시");

        int array_pos = -1;
        for(int i=0;i<cellinfos.size();i++){
            if(cellinfos.get(i).getPosition() == day+column*6){
                array_pos = i;
                break;
            }
        }
        // 처음 칸에 입력할때
        if(array_pos == -1){
            addClass("", day, column, "");
            fragment.setCellInfo(cellinfos, cellinfos.size()-1);
            fragment.show(getSupportFragmentManager(), "TimeTable");
        }
        else{
            fragment.setCellInfo(cellinfos,array_pos);
            fragment.show(getSupportFragmentManager(), "TimeTable");
        }
        Log.i("info","array_pos = "+array_pos);
        // ArrayList<String> Subject에 중복없이 이때까지 기록된 과목명들이 저장된다.
    }

    @Override
    public void onDialogPositiveClick(ArrayList<cellInfo> cellinfos, int array_pos, boolean isUpdate) {
        String value = cellinfos.get(array_pos).getName();
        String memo = cellinfos.get(array_pos).getMemo();
        int position = cellinfos.get(array_pos).getPosition();

        Log.i("info", "onDialog , isUpdate = "+isUpdate+ " 내용 : "+value+" | "+memo);
        // Dialog 에서 정보가 입력되었을때 DB를 기록한다.
        if(isUpdate == false) {
            helper.insert("insert into " + DBHelper.TIMETABLE_TABLE_NAME + " (name, pos, memo) values ('" + value + "',  + " + (position) + ", '" + memo + "');");
        }
        //update creature_template set creature_template.name = 'Test' where creature_template.entry = 3;
        else {
            // 이미 있는 경우에는 DB가 중복되니까..
            helper.insert("delete from " + DBHelper.TIMETABLE_TABLE_NAME + " where " + DBHelper.TIMETABLE_TABLE_NAME + ".pos = " + (position));
            helper.insert("insert into " + DBHelper.TIMETABLE_TABLE_NAME + " (name, pos, memo) values ('" + value + "',  + " + (position) + ", '" + memo + "');");
        }

        ttadapter.notifyDataSetChanged();

        gv.invalidateViews();
        gv.setAdapter(ttadapter);
        // Todo: 빈칸이면 DB삭제! 아니면 DB삭제할 방법 생각해두기.
    }
    public void addClass(String value, int day, int column, String memo){
        cellinfos.add(new cellInfo(value, day, column, memo));
        ttadapter.setCellInfos(cellinfos);
        ttadapter.notifyDataSetChanged();
    }
}
class cellInfo{
    public cellInfo(String name, int day, int column, String memo){
        this.name = name;
        this.day = day;
        this.column = column;
        this.memo = memo;
    }
    public int getPosition(){
        return day+column*6;
    }
    public String getName(){
        return name;
    }
    public void setName(String name){this.name = name;}
    public void setMemo(String memo){this.memo = memo;}
    public String getMemo()  {return memo;}
    public int getDay(){ return day;}
    public int getColumn(){ return column;}
    private String name;
    private int day;
    private int column;
    private String memo;
}
class TimeTableAdapter extends BaseAdapter{
    String dayName[]={"","월","화","수","목","금"};
    String timeIndi[] ={"8:00~\n9:15", "9:25~\n10:40", "10:50~\n12:50", "13:10~\n14:25", "14:35~\n15:50","16:00~\n17:15"};
    Context context;
    ArrayList<cellInfo> cellinfos;
    LayoutInflater inf;

    public TimeTableAdapter(Context context) {
        this.context = context;
        inf = (LayoutInflater) context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
    }
    public void setCellInfos(ArrayList<cellInfo> cellinfos){
        this.cellinfos = cellinfos;
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
            convertView = inf.inflate(R.layout.timetable_dayindi, null);
            TextView tv = (TextView) convertView.findViewById(R.id.ttdayindi);
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
            if(cellinfos != null) {
                for (int i = 0; i < cellinfos.size(); i++) {
                    if (cellinfos.get(i).getPosition() == position) {
                        tv2.setText(cellinfos.get(i).getName());
                        break;
                    }
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