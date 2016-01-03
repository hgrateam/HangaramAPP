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
    private static String MSG_NO_SHOW="보여줄 급식정보가 없습니다.";

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today_meal);

        Button button1 = (Button) findViewById(R.id.today_meal_button1);
        // 잠시 버튼은 안보이게...
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

        GregorianCalendar gcalendar = new GregorianCalendar();
        t_year=gcalendar.get(Calendar.YEAR); // index 1
        t_month=gcalendar.get(Calendar.MONTH)+1; // index 1
        t_day=gcalendar.get(Calendar. DAY_OF_MONTH); // index 1

        Log.i("info", "오늘의 날짜 : " + t_year + "/" + t_month + "/" + t_day);

        drawCalendar();

        if(startDate == 0) { // 기존 정보가 하나도 없어! -> 정보를 못받아올때는 그릴게 없으니까 그냥 엑티비티를 나갈거임!
            Log.i("info", "기존 정보가 하나도 없군");
            gotoParse(t_year, t_month, 0, true);
            gotoParse(t_year, t_month, 1, true);
            gotoParse(t_year, t_month, -1, true);
        }

        else if(endDate < ymdToInt(t_year, t_month, t_day)){ // 오늘자 정보가 없어! -> 파싱
            Log.i("info", "오늘자 정보가 없군!");
            gotoParse(t_year, t_month, 0, false);
            gotoParse(t_year, t_month, 1, false);
            gotoParse(t_year, t_month, -1, false);
        }
        else getMealInfo();
    }
    public void gotoParse(int y, int m, int calc,  boolean isFirst){

        // boolean isFirst 의 역활 :
        ParseSen ps;
        int year, month;
        year = y;
        month = m;

        if(calc != 0 ){
            if(calc>0){
                if(month + calc >=12){
                    year++;
                    month+=calc;
                    month-=12;
                    Log.i("info", " 더한다!");
                }
                else{
                    month+=calc;
                }
            }
            else{
                if(month + calc <= 0){
                    year--;
                    month+=calc;
                    month+=12;
                    Log.i("info", " 뺀다!");
                }
                else{
                    month+=calc;
                }
            }

        }
        Log.i("info", "Parse Target : "+ year + month+ "  calc : "+calc);

        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setMessage("정보를 받아오는 중입니다..");
        progDialog.show();

        ps = new ParseSen();
        ps.setCallBackListener(new ParseSen.ParseCallBack() {
            @Override
            public void OnFinish(ParseSen a) {

                // 파싱이 다 되고 난후에 여기가 실행된다.

                DBHelper helper = new DBHelper(TodayMealActivity.this, DBHelper.DB_FILE_NAME, null, 1, DBHelper.TIMETABLE_TABLE);
                SQLiteDatabase db = helper.getReadableDatabase();
                int dbLastday = -1;
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
                    if (cursor.getInt(1) > dbLastday) {
                        dbLastday = date;
                    }
                    if (intToYear(date) == a.getYear() && intToMonth(date) == a.getMonth()) {
                        dbCheck[intToDay(date)] = true;
                        noupdateFlag = true;
                    }
                }
                helper.close();
                /*
                (ParseSen 에서 파싱 해온거는 아직 DB에 저장은 안 한 상태임)
                *   a.getLastDay() == -1 : 파싱을 하긴했는데 말이야 아무것도 없더라구
                *   noupdateFlag==true : 기존에 있던 DB를 다 뒤져봤는데 새로 업데이트 할게 없더라구. 기존에 DB가 있었는지 없었는지는 몰라.
                *   a.getisFirst() == true : 기존에 DB가 없어! 이미 아무것도 없던데?!
                *   dbLastday == -1 : db에 아무것도 없음 ㅋ
                *
                * */
                if (a.getLastday()==-1){ // 파싱해올거도 없고...
                    if(dbLastday == -1){ // DB도 없고.. 표시해줄게 없는데?

                        startDate = ymdToInt(t_year, t_month, 1);
                        endDate = ymdToInt(t_year, t_month, 28);

                        Toast toast = Toast.makeText(TodayMealActivity.this, MSG_NO_SHOW, Toast.LENGTH_LONG);
                        toast.show();
                        progDialog.dismiss();

                        drawNullCalendar();

                        return;
                    }
                    else{
                        //  파싱해 올건 없는데 기존 데이터는 있네?
                        Log.i("info", "이미 정보가 있다! 파싱해올 정보가 없다.");
                        Toast toast = Toast.makeText(TodayMealActivity.this, MSG_ALREADY_EXISTS, Toast.LENGTH_LONG);
                        progDialog.dismiss();
                        toast.show();
                        return;
                    }
                }
                if (a.getErrorCode()==ParseSen.ERR_NET_ERROR) {
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

                if(a.getLastday() != - 1) {
                    for (int i = 1; i <= 31; i++) {
                        if (a.isMenuExist(i)) { // 파싱했는데 해당
                            if (!dbCheck[i]) { // 이미 있는경우는 제외 없으면 DB추가

                                date = ymdToInt(a.getYear(), a.getMonth(), i);
                                Log.i("info", "insert into " + DBHelper.TODAYMEAL_TABLE_NAME + " (date, lunch, dinner) values (" + date + ", '" + a.getLunch(i) + "', '" + a.getDinner(i) + "');");
                                helper.insert("insert into " + DBHelper.TODAYMEAL_TABLE_NAME + " (date, lunch, dinner) values (" + date + ", '" + a.getLunch(i) + "', '" + a.getDinner(i) + "');");
                            }
                        }
                    }
                }

                parseLastday = ymdToInt(a.getYear(), a.getMonth(), a.getLastday());

                // 새로운 정보가 또 있네?
                Log.i("info", "dbLastday = " + dbLastday + " // parseLastday = " + parseLastday);


                Toast toast = Toast.makeText(TodayMealActivity.this, MSG_UPDATE, Toast.LENGTH_LONG);
                toast.show();

                getMealInfo();
                drawCalendar();
                progDialog.dismiss();
            }
        });

        ps.setMM(month);
        ps.setAY(year);
        ps.setIsFirst(isFirst);
        ps.parse();
    }

    public int[] getDateRange() {

        DBHelper helper = new DBHelper(TodayMealActivity.this, DBHelper.DB_FILE_NAME, null, 1, DBHelper.TODAYMEAL_TABLE);
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
        DBHelper helper = new DBHelper(TodayMealActivity.this, DBHelper.DB_FILE_NAME, null, 1, DBHelper.TODAYMEAL_TABLE);
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
        setCellInfo(today);
        helper.close();

    }

    private void setCellInfo(Date date){
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
    private void drawNullCalendar(){
        // 달력에 그릴 범위를 계산해온다.
        //급식 캘린더의 시작 날짜를 초기화한다.

        Calendar calStart = Calendar.getInstance();
        calStart.set(t_year, t_month-1, 1);

        Calendar calEnd = Calendar.getInstance();
        calEnd.set(t_year, t_month-1, 27);

        //급식 선택 달력에 표시되는 날짜의 범위를 설정한다.
        calendar.init(calStart.getTime(), calEnd.getTime());

        calendar.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date date) {
                // public void setText(date)참조;
                setCellInfo(date);
            }
            @Override
            public void onDateUnselected(Date date) {

            }
        });
    }
    private void drawCalendar(){
        // 달력에 그릴 범위를 계산해온다.
        int[] dateBundle = getDateRange();

        //급식 캘린더의 시작 날짜를 초기화한다.
        int startYear = intToYear(dateBundle[0]);
        int startMonth = intToMonth(dateBundle[0]);
        int startDay = intToDay(dateBundle[0]);


        Calendar calStart = Calendar.getInstance();
        calStart.set(startYear, --startMonth, startDay);



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
                setCellInfo(date);
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
