package com.ateam.hangaramapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
    ArrayList<cellInfo> cellinfos;
    DBHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_table);

        cellinfos = new ArrayList<cellInfo>();
        ttadapter = new TimeTableAdapter(TimeTable.this);

        TimeTableGridView gv = (TimeTableGridView) findViewById(R.id.ttgrid);
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
        ArrayList<String> subjects = new ArrayList<String>();

        String name="", memo="";
        for(int i=0;i<cellinfos.size();i++){
            boolean flag = false;

            if(cellinfos.get(i).getPosition() == day+column*6){
                name = cellinfos.get(i).getName();
                memo = cellinfos.get(i).getMemo();
            }
            for(int j=0;j<subjects.size();j++){
                if(cellinfos.get(i).getName().equals(subjects.get(j))){
                    flag = true;
                }
            }

            if(flag == false){
                subjects.add(cellinfos.get(i).getName());
            }
        }
        // ArrayList<String> Subject에 중복없이 이때까지 기록된 과목명들이 저장된다.
        fragment.setDate(day, column);
        fragment.setSubjectList(subjects);
        fragment.setCellInfo(name,memo);
        fragment.show(getSupportFragmentManager(), "TimeTable");
    }

    @Override
    public void onDialogPositiveClick(String value, String memo) {
        addClass(value, day, column, memo);
        // Dialog 에서 정보가 입력되었을때 DB를 기록한다.
        helper.insert("insert into " + DBHelper.TIMETABLE_TABLE_NAME + " (name, pos, memo) values ('" + value + "',  + "+(day+column*6) + ", '"+memo+"');");
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
    public String getMemo()  {return memo;}
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