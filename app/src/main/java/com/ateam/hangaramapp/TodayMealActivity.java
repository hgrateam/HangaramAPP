package com.ateam.hangaramapp;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import java.util.Calendar;

import com.squareup.timessquare.CalendarPickerView;
import java.util.Date;
import java.util.GregorianCalendar;

import android.widget.Toast;

public class TodayMealActivity extends AppCompatActivity {

    private ProgressDialog progDialog;
    private boolean parseStat;
    private CalendarPickerView calendar;
    int startDate = 0, endDate = 0; //날짜 초기화
    private int t_year,t_month,t_day;

    private static String MSG_PARSE_ERROR = "무엇인가 잘못되었다. 콜미";
    private static String MSG_NETWORK_SUCKS = "네트워크 상태가 올바르지 않습니다.";
    private static String MSG_FIRST_NETWORK_SUCKS = "네트워크 상태가 올바르지 않습니다. 처음 접근은 네트워크가 필요합니다.";
    private static String MSG_ALREADY_EXISTS = "이미 최신 정보입니다.";
    private static String MSG_UPDATE="최신 정보를 갱신하였습니다.";

    ParseCallBack callbackEvent = new ParseCallBack() {

        @Override
        public void callbackMethod(ParseSen a) {

            DBHelper helper = new DBHelper(TodayMealActivity.this, DBHelper.DB_FILE_NAME, null, 1);
            SQLiteDatabase db = helper.getReadableDatabase();
            String str = "";
            int dbSize = 0, dbLastday = -1;
            boolean dbCheck[] = new boolean[33];
            boolean noupdateFlag=false;

            for (int i = 0; i < 32; i++) {
                dbCheck[i] = false;
            }
            int parseLastday;

            Log.i("info", "제일 최근 정보 : " + a.getLastday() + "일자 정보");

            Cursor cursor = db.rawQuery("select * from " + DBHelper.TODAYMEAL_TABLE_NAME, null);
            int date;
            while (cursor.moveToNext()) {
                date = cursor.getInt(1);
                str += cursor.getInt(0) //id값; 신경 안써도 됨
                        + " : date "
                        + cursor.getInt(1) //날짜
                        + ", lunch = "
                        + cursor.getString(2) //점심
                        + ", dinner = "
                        + cursor.getString(3)  //저녁
                        + "\n";
                if (cursor.getInt(1) > dbLastday) {
                    dbLastday = date;
                }
                dbSize++;
                if (date / 10000 == a.getYear() && (date % 10000) / 100 == a.getMonth()) {
                    dbCheck[(cursor.getInt(1)) % 100] = true;
                    noupdateFlag = true;
                }
            }

            if(a.getIsFirst()) {
                if (noupdateFlag && a.getLastday() != -1) {
                    Toast toast = Toast.makeText(TodayMealActivity.this, MSG_PARSE_ERROR, Toast.LENGTH_LONG);
                    toast.show();
                    // 이게 호출되면 나한테 무언가가 잘못된거임..
                    // 나한테 말해줘
                }
            }
            if (noupdateFlag || a.getErrorCode()==ParseSen.ERR_NET_ERROR) {
                // 인터넷 연결이 영 이상하단 말이야 or 자료가 없어?
                Log.i("info", "인터넷 확인좀...?");

                progDialog.dismiss();

                if(a.getIsFirst()){
                    Toast toast = Toast.makeText(TodayMealActivity.this, MSG_FIRST_NETWORK_SUCKS, Toast.LENGTH_LONG);
                    toast.show();
                    finish();
                }
                Toast toast = Toast.makeText(TodayMealActivity.this, MSG_NETWORK_SUCKS, Toast.LENGTH_LONG);
                toast.show();

                return;
            }

            parseLastday = a.getYear() * 10000 + a.getMonth() * 100 + a.getLastday();

            // 새로운 정보가 또 있네?
            Log.i("info", "dbLastday = " + dbLastday + " // parseLastday = " + parseLastday);

            if (dbLastday != parseLastday) {
                for (int i = 1; i <= 31; i++) {
                    if (a.isMenuExist(i)) { // 파싱했는데 해당
                        if (!dbCheck[i]) { // 이미 있엉

                            date = a.getYear() * 10000 + a.getMonth() * 100 + i;
                            Log.i("info", "insert into " + DBHelper.TODAYMEAL_TABLE_NAME + " (date, lunch, dinner) values (" + date + ", '" + a.getLunch(i) + "', '" + a.getDinner(i) + "');");
                            helper.insert("insert into " + DBHelper.TODAYMEAL_TABLE_NAME + " (date, lunch, dinner) values (" + date + ", '" + a.getLunch(i) + "', '" + a.getDinner(i) + "');");
                        }
                    }
                }

                Toast toast = Toast.makeText(TodayMealActivity.this, MSG_UPDATE, Toast.LENGTH_LONG);
                toast.show();

            } else {
                Log.i("info", "이미 정보가 있다!");
                Toast toast = Toast.makeText(TodayMealActivity.this, MSG_ALREADY_EXISTS, Toast.LENGTH_LONG);
                toast.show();
                Log.i("info", "TODAYMEAL_DBLOG: " + str);
            }

            if(a.getIsFirst()){
                drawCalendar();
            }
            progDialog.dismiss();
        }

        @Override
        public void callbackMethod_Cal(ParseCal a) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today_meal);

        Button button1 = (Button) findViewById(R.id.today_meal_button1);
        calendar = (CalendarPickerView) findViewById(R.id.calendar_view);


        progDialog = new ProgressDialog(this);

        // Get a support ActionBar corresponding to this toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_today_meal);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        drawCalendar();


        if(startDate == 0) // 기존 정보가 하나도 없어! -> 정보를 못받아올때는 그릴게 없으니까 그냥 엑티비티를 나갈거임!
            gotoParse(t_year, t_month, true);

        else if(endDate < t_year*10000+t_month*100+t_day){ // 오늘자 정보가 없어! -> 파싱
            gotoParse(t_year, t_month, false);
        }

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoParse(t_year, t_month, false);
                // ps.parse가 처리하는데 시간이 걸리기 때문에 그냥 getMenu()를 하면 에러가 떠버린다.
            }
        });
    }
    public void gotoParse(int year, int month, boolean isFirst){
        ParseSen ps;

        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setMessage("정보를 받아오는 중입니다..");
        progDialog.show();

        ps = new ParseSen(callbackEvent);

        ps.setMM(month);
        ps.setAY(year);
        ps.setIsFirst(isFirst);
        ps.parse();
    }

    public int[] getDateRange() {

        DBHelper helper = new DBHelper(TodayMealActivity.this, DBHelper.DB_FILE_NAME, null, 1);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + DBHelper.TODAYMEAL_TABLE_NAME, null);

        int focusedDate;
        while (cursor.moveToNext()) {
            focusedDate = cursor.getInt(1);
            if (startDate == 0) {
                startDate = focusedDate; //시작날짜에 포커스값 대입
            } else if (endDate == 0) {
                endDate = focusedDate; //끝날짜에 포커스값 대입
            } else {
                if (focusedDate < startDate) {
                    startDate = focusedDate; //시작날짜를 더 이전날짜가 존재하면 변경
                }
                if (focusedDate > endDate) {
                    endDate = focusedDate; //끝날짜를 더 나중날짜가 존재하면 변경
                }
            }
        }
        Log.i("info", "startDate = " + startDate + " 입니다. \n endDate = " + endDate + " 입니다.");

        int[] dateBundle = {startDate, endDate};
        return (dateBundle);
    }
    private void drawCalendar(){
        int[] dateBundle = getDateRange();

        //
        int startYear = dateBundle[0] / 10000;
        int startMonth = ((dateBundle[0] % 10000) / 100);
        int startDay = dateBundle[0] % 100;

        GregorianCalendar gcalendar = new GregorianCalendar();

        Calendar calStart = Calendar.getInstance();
        calStart.set(startYear, --startMonth, startDay);


        t_year=gcalendar.get(Calendar.YEAR);
        t_month=gcalendar.get(Calendar.MONTH)+1;
        t_day=gcalendar.get(Calendar. DAY_OF_MONTH);

        Log.i("info", "오늘의 날짜 : "+t_year +"/"+ t_month +"/"+ t_day);
        Log.i("info", "startYear = " + startYear + " 입니다.");

        int endYear = dateBundle[1] / 10000;
        int endMonth = ((dateBundle[1] % 10000) / 100);
        int endDay = dateBundle[1] % 100;

        Calendar calEnd = Calendar.getInstance();
        calEnd.set(endYear, --endMonth, ++endDay);

        //급식 선택 달력에 표시되는 날짜의 범위를 설정한다.
        Date today = new Date();
        calendar.init(calStart.getTime(), calEnd.getTime());

        try {
            calendar.selectDate(today);
        }catch (IllegalArgumentException exception){
        }
    }

}
