package com.ateam.hangaramapp;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public int dateToInt(Date date){
        return ymdToInt(date.getYear()+1900,date.getMonth()+1, date.getDate());
    }
    public int ymdToInt(int y, int m, int d){
        return y*10000+m*100+d;
    }

    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);

        setSupportActionBar(toolbar);

        mContext = getApplicationContext();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        CardView cardView = (CardView) findViewById(R.id.card_today_meal);


        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, TodayMealActivity.class);

                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        // the context of the activity
                        MainActivity.this,
                        // For each shared element, add to this method a new Pair item,
                        // which contains the reference of the view we are transitioning *from*,
                        // and the value of the transitionName attribute
                        new Pair<View, String>(v.findViewById(R.id.card_today_meal),
                                getString(R.string.transition_name_today_meal_card)),
                        new Pair<View, String>(v.findViewById(R.id.text_today_meal),
                                getString(R.string.transition_name_today_meal_text))
                );
                //오늘의 급식으로 진입한다.
                ActivityCompat.startActivity(MainActivity.this, intent, options.toBundle());
            }
        });

        CardView cardView2 = (CardView) findViewById(R.id.card_schedule);
        cardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SCalendar.class);


                //학사 일정으로 진입한다.
                startActivity(intent);
            }
        });

        CardView cardView3 = (CardView) findViewById(R.id.card_time_table);
        cardView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, TimeTable.class);
                //오늘의 급식으로 진입한다.
                startActivity(intent);
            }
        });
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        drawTodayMealCard();

    }
    // 그래서 다음 소스대로 하면 된다.

    public void drawTodayMealCard(){

        TextView today_meal = (TextView) findViewById(R.id.text_today_meal);

        Date today = new Date();


        ArrayList<mealData> mealDatas = new ArrayList<>();

        DBHelper helper = new DBHelper(MainActivity.this, DBHelper.DB_FILE_NAME, null, 1, DBHelper.TODAYMEAL_TABLE);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + DBHelper.TODAYMEAL_TABLE_NAME, null);

        while (cursor.moveToNext()) {
            int date = cursor.getInt(1);
            mealDatas.add(new mealData(date, cursor.getString(2), cursor.getString(3)));
        }
        db.close();




        GregorianCalendar gcalendar = new GregorianCalendar();
        int t_year=gcalendar.get(Calendar.YEAR); // index 1
        int t_month=gcalendar.get(Calendar.MONTH)+1; // index 1
        int t_day=gcalendar.get(Calendar. DAY_OF_MONTH); // index 1

        for(int i=0;i<mealDatas.size();i++){
            if(mealDatas.get(i).getDate() == t_year*10000+t_month*100+t_day){
                today_meal.setTextSize(15);
                today_meal.setText("<"+t_year+"년 "+t_month+"월 "+t_day+"일>\n"+mealDatas.get(i).getMealData());
                return;
            }
        }

        today_meal.setTextSize(18);
        today_meal.setText("오늘의 급식");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_help) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_settings) {
            item.setCheckable(false);
            Intent intent = new Intent(mContext, SettingsActivity.class);
            //설정으로 진입한다.
            startActivity(intent);

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

//git 공부중 2
