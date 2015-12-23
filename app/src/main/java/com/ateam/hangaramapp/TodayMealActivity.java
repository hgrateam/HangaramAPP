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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Calendar;

import com.squareup.timessquare.CalendarPickerView;

import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;

import android.widget.TextView;
import android.widget.Toast;

public class TodayMealActivity extends AppCompatActivity {

    private ProgressDialog progDialog;
    private CalendarPickerView calendar;
    private TextView schedule;
    private MealInfo mealinfo;


    private boolean dbCheck[];
    int startDate = 0, endDate = 0; //날짜 초기화
    private int t_year,t_month,t_day;


    private static String MSG_PARSE_ERROR = "무엇인가 잘못되었다. 콜미";
    private static String MSG_NETWORK_SUCKS = "네트워크 상태가 올바르지 않습니다.";
    private static String MSG_FIRST_NETWORK_SUCKS = "네트워크 상태가 올바르지 않습니다. 처음 접근은 네트워크가 필요합니다.";
    private static String MSG_ALREADY_EXISTS = "이미 최신 정보입니다.";
    private static String MSG_UPDATE="최신 정보를 갱신하였습니다.";
    private static String MSG_NO_MEAL="해당 날짜의 급식 정보가 없습니다.";




    public int intToYear(int d){
        return d/10000;
    }
    public int intToMonth(int d){
        return (d%10000)/100;
    }
    public int intToDay(int d){
        return d%100;
    }
    public int ymdToInt(int y, int m, int d){
        return y*10000+m*100+d;
    }
    public int dateToInt(Date date){
        return ymdToInt(date.getYear()+1900,date.getMonth()+1, date.getDate());
    }
    ParseCallBack callbackEvent = new ParseCallBack() {

        @Override
        public void callbackMethod(ParseSen a) {

            DBHelper helper = new DBHelper(TodayMealActivity.this, DBHelper.DB_FILE_NAME, null, 1);
            SQLiteDatabase db = helper.getReadableDatabase();
            String str = "";
            int dbSize = 0, dbLastday = -1;
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
                if (intToYear(date) == a.getYear() && intToMonth(date) == a.getMonth()) {
                    dbCheck[intToDay(date)] = true;
                    noupdateFlag = true;
                }
            }
            helper.close();

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

            parseLastday = ymdToInt(a.getYear(), a.getMonth(), a.getLastday());

            // 새로운 정보가 또 있네?
            Log.i("info", "dbLastday = " + dbLastday + " // parseLastday = " + parseLastday);

            if (dbLastday != parseLastday) {
                for (int i = 1; i <= 31; i++) {
                    if (a.isMenuExist(i)) { // 파싱했는데 해당
                        if (!dbCheck[i]) { // 이미 있엉

                            date = ymdToInt(a.getYear(), a.getMonth(), i);
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

            getMealInfo();

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
        // 잠시 버튼은 안보이게...

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            gotoParse(t_year, t_month, false);
                // ps.parse가 처리하는데 시간이 걸리기 때문에 그냥 getMenu()를 하면 에러가 떠버린다.
            }
        });
        // Todo: 버튼 삭제하고 메뉴로 올려버려야함.
        button1.setVisibility(View.GONE);

        schedule = (TextView) findViewById(R.id.schedule_today_meal);
        calendar = (CalendarPickerView) findViewById(R.id.calendar_view);
        mealinfo = new MealInfo(this);

        dbCheck = new boolean[33];

        progDialog = new ProgressDialog(this);

        // Get a support ActionBar corresponding to this toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_today_meal);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        drawCalendar();


        if(startDate == 0) { // 기존 정보가 하나도 없어! -> 정보를 못받아올때는 그릴게 없으니까 그냥 엑티비티를 나갈거임!
            gotoParse(t_year, t_month, true);
        }

        else if(endDate < ymdToInt(t_year, t_month, t_day)){ // 오늘자 정보가 없어! -> 파싱
            gotoParse(t_year, t_month, false);
        }
        else getMealInfo();

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
        helper.close();

        Log.i("info", "startDate = " + startDate + " 입니다. \n endDate = " + endDate + " 입니다.");

        int[] dateBundle = {startDate, endDate};
        return (dateBundle);
    }
    private void getMealInfo(){
        DBHelper helper = new DBHelper(TodayMealActivity.this, DBHelper.DB_FILE_NAME, null, 1);
        Date today = new Date();
        Collection<Date> highdates = new ArrayList<Date>(); // 하이라이트 할 날짜 목록
        int date;

        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.rawQuery("select * from " + DBHelper.TODAYMEAL_TABLE_NAME, null);

        mealinfo.resetMeal();
        calendar.clearHighlightedDates();
        while (cursor.moveToNext()) {
            date = cursor.getInt(1);
            if(startDate <= date && date <= endDate) {
                mealinfo.setTemplate(intToYear(date)+"년 "+intToMonth(date)+"월 "+ intToDay(date)+"일\n [중식]\n!lunch!\n\n[저녁]\n!dinner!");
                mealinfo.add(cursor.getInt(1), cursor.getString(2), cursor.getString(3));
//                mealinfo.add(cursor.getInt(1), "[중식]\n" + cursor.getString(2) + "\n\n" + "[석식]\n" + cursor.getString(3));

                Date date2 = new Date();
                date2.setYear(intToYear(date) - 1900);
                date2.setMonth(intToMonth(date) - 1);
                date2.setDate(intToDay(date));
                Log.i("info", "highlight : " + dateToInt(date2));

                if(dateToInt(today)!=date) {
                    highdates.add(date2); // 오늘은 하이라이트 노노해
                }
            }
        }
        calendar.highlightDates(highdates);
        // 오늘 날짜를 선택한다.
        try {
            calendar.selectDate(today);
        }catch (IllegalArgumentException exception){
        }
        calendar.scrollToDate(today);
        setText(today);
        helper.close();

    }

    private void setText(Date date){
        String str = mealinfo.getData(dateToInt(date));
        Log.i("info", dateToInt(date) + " 날짜가 선택되었당.");
        if(mealinfo.isMealExist(dateToInt(date))){
            Log.i("info", "선택된 날짜의 정보 : "+str);

            // 알레르기 정보를 표기 할것인가?
            // 이건 프리퍼런스에서 변수를 설정한다음에 그 후에 처리하기
            if(false){
                schedule.setText(str);

            }
            else {
                //①난류 ②우유 ③메밀 ④땅콩 ⑤대두 ⑥밀 ⑦고등어 ⑧게 ⑨새우 ⑩돼지고기 ⑪복숭아 ⑫토마토 ⑬아황산염
                schedule.setText(mealinfo.removeAllergie(str));
            }
        }
        else{
            schedule.setText(MSG_NO_MEAL);
        }

    }
    private void drawCalendar(){
        int[] dateBundle = getDateRange();

        //급식 캘린더의 시작 날짜를 초기화한다.
        int startYear = intToYear(dateBundle[0]);
        int startMonth = intToMonth(dateBundle[0]);
        int startDay = intToDay(dateBundle[0]);

        GregorianCalendar gcalendar = new GregorianCalendar();

        Calendar calStart = Calendar.getInstance();
        calStart.set(startYear, --startMonth, startDay);


        t_year=gcalendar.get(Calendar.YEAR);
        t_month=gcalendar.get(Calendar.MONTH)+1;
        t_day=gcalendar.get(Calendar. DAY_OF_MONTH);

        Log.i("info", "오늘의 날짜 : "+t_year +"/"+ t_month +"/"+ t_day);
        Log.i("info", "startYear = " + startYear + " 입니다.");

        //급식 캘린더의 끝 날짜를 초기화한다.
        int endYear = intToYear(dateBundle[1]);
        int endMonth = intToMonth(dateBundle[1]);
        int endDay = intToDay(dateBundle[1]);

        Calendar calEnd = Calendar.getInstance();
        calEnd.set(endYear, --endMonth, ++endDay);

        //급식 선택 달력에 표시되는 날짜의 범위를 설정한다.
        calendar.init(calStart.getTime(), calEnd.getTime());

        calendar.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date date) {
                // public void setText(date)참조;
                setText(date);
            }
            @Override
            public void onDateUnselected(Date date) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
