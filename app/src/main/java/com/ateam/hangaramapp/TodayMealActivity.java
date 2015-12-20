package com.ateam.hangaramapp;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.Toast;

public class TodayMealActivity extends AppCompatActivity {

    Button button1;
    ProgressDialog progDialog;
    ParseCallBack callbackEvent = new ParseCallBack() {

        @Override
        public void callbackMethod(ParseSen a) {

            DBHelper helper = new DBHelper(TodayMealActivity.this, DBHelper.DB_FILE_NAME, null,1);
            SQLiteDatabase db = helper.getReadableDatabase();
            String str = "";
            int dbSize=0, dbLastday=-1;
            boolean dbCheck[] = new boolean[33];
            for(int i=0;i<32;i++){
                dbCheck[i] = false;
            }
            int parseLastday;

            Log.i("info", "제일 최근 정보 : "+a.getLastday()+"일자 정보");

            Cursor cursor = db.rawQuery("select * from "+DBHelper.TODAYMEAL_TABLE_NAME, null);

            int date;
            while(cursor.moveToNext()) {
                date = cursor.getInt(1);
                str += cursor.getInt(0)
                        + " : date "
                        + cursor.getInt(1)
                        + ", lunch = "
                        + cursor.getString(2)
                        + ", dinner = "
                        + cursor.getString(3)
                        + "\n";
                if(cursor.getInt(1) > dbLastday) {
                    dbLastday = date;
                }
                dbSize++;
                if(date/10000==a.getYear() && (date%10000)/100==a.getMonth())
                    dbCheck[(cursor.getInt(1))%100]=true;
            }

            if(a.getLastday() == -1){
                // 인터넷 연결이 영 이상하단 말이야
                Log.i("info", "인터넷 확인좀...?");
                progDialog.dismiss();
                return;
            }

            parseLastday = a.getYear()*10000+a.getMonth()*100+a.getLastday();

            // 새로운 정보가 또 있네?
            Log.i("info", "dbLastday = " + dbLastday + " // parseLastday = " + parseLastday);


            if(dbLastday != parseLastday) {
                for (int i = 1; i <= 31; i++) {
                    if (a.isMenuExist(i)) { // 파싱했는데 해당
                        if (!dbCheck[i]) { // 이미 있엉

                            date = a.getYear()*10000 + a.getMonth() *100 + i;
                            Log.i("info","insert into " + DBHelper.TODAYMEAL_TABLE_NAME + " (date, lunch, dinner) values (" + date + ", '" + a.getLunch(i) + "', '" + a.getDinner(i) + "');");
                            helper.insert("insert into " + DBHelper.TODAYMEAL_TABLE_NAME + " (date, lunch, dinner) values (" + date + ", '" + a.getLunch(i) + "', '" + a.getDinner(i) + "');");
                        }
                    }
                }
            }
            else {
                Log.i("info", "이미 정보가 있다!");
                Log.i("info", "TODAYMEAL_DBLOG: " + str);
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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_today_meal);
        Button button1 = (Button) findViewById(R.id.today_meal_button1);

        progDialog = new ProgressDialog(this);

        setSupportActionBar(toolbar);
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);


        Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 1);

        CalendarPickerView calendar = (CalendarPickerView) findViewById(R.id.calendar_view);
        Date today = new Date();
        calendar.init(today, nextYear.getTime())
                .withSelectedDate(today);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseSen ps;

                progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progDialog.setMessage("정보를 가져오는 중입니다..");
                progDialog.show();

                ps = new ParseSen(callbackEvent);
                ps.setMM(12);
                ps.setAY(2015);
                ps.parse();
                // ps.parse가 처리하는데 시간이 걸리기 때문에 그냥 getMenu()를 하면 에러가 떠버린다.
            }
        });
    }
}
