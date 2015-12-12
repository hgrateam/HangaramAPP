package com.ateam.hangaramapp;

import android.app.ProgressDialog;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class TodayMealActivity extends AppCompatActivity {

    Button button1;
    ProgressDialog progDialog;
    ParseCallBack callbackEvent = new ParseCallBack() {

        @Override
        public void callbackMethod(ParseSen a) {
            // TODO Auto-generated method stub

            // ParseSen의 파싱이 끝나면 여기가 호출된다. 이제부터는 getMenu()를 사용할 수 있다.
            // 여기서부터 처리하고 싶은거 처리하면 됨
            // 12월 2015년 으로 설정되어있따.
            Log.i("info", "23일 메뉴 : " + a.getMenu(23));
            Log.i("info", "12일 메뉴 : " + a.getMenu(12));
            Log.i("info", "14일 메뉴 : " + a.getMenu(14));
            progDialog.dismiss();
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
            }
        });




    }
}
