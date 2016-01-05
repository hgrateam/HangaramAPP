package com.ateam.hangaramapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.Date;

/**
 * Created by Suhyun on 2015-12-21.
 */
public class MealInfo {
    private final int SIZE=1000;
    private int cnt;
    private String template;
    public final String NULL = "";
    Context mContext;
    private String mealInfo[];
    private int mealDate[];

    MealInfo(Context mcontext){
        mealInfo = new String[SIZE];
        mealDate = new int[SIZE];
        mContext = mcontext;
        resetMeal();
    }

    public void resetMeal(){
        cnt = 0;
        for(int i=0;i<SIZE;i++){
            mealDate[i] = 0;
            mealInfo[i]="";
        }
        resetTemplate();
    }
    public void resetTemplate(){
        template = "[중식]\n!lunch! \n\n[석식]\n!dinner!";
    }
    public void setTemplate(String temp){
        template = temp;
    }
    public void acesssDB(){
        DBHelper helper = new DBHelper(mContext, DBHelper.DB_FILE_NAME, null, 1, DBHelper.TODAYMEAL_TABLE);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + DBHelper.TODAYMEAL_TABLE_NAME, null);
        int date;


        while (cursor.moveToNext()) {
            date = cursor.getInt(1);
            add(date, cursor.getString(2), cursor.getString(3));
        }

        helper.close();
    }
    public boolean isMealExist(int d){
        for(int i=0;i<cnt;i++){
            if(mealDate[i] == d){
                return true;
            }
        }
        return false;
    }
    public void add(int d, String lunch, String dinner){
        String _template = template;
        _template = _template.replace("!lunch!",lunch);
        _template = _template.replace("!dinner!",dinner);
        mealInfo[cnt] = _template;
        mealDate[cnt++] = d;
    }

    public String getData(int d){
        for(int i=0;i<getCnt();i++){
            if(mealDate[i] == d){
                return mealInfo[i];
            }
        }
        return NULL;
    }
    public String removeAllergie(String a){
        String allergiesym[] = {"①","②","③", "④", "⑤", "⑥", "⑦","⑧","⑨","⑩","⑪","⑫","⑬"};
        String _str = a;
        for(int i=0;i<allergiesym.length;i++){
            _str = _str.replaceAll(allergiesym[i],"");
        }
        return _str;
    }
    public int getCnt(){ return cnt; }
}

class  mealData{
    public mealData(int date, String lunch, String dinner, boolean check){
        this.date = date;
        this.lunch = lunch;
        this.dinner = dinner;
        this.check = check;
    }
    public mealData(mealData mealdata, boolean check){
        this.date = mealdata.getDate();
        this.lunch = mealdata.getLunch();
        this.dinner = mealdata.getDinner();
        this.check = check;
    }
    private String lunch;
    private String dinner;
    private int date;
    private boolean check;
    public String getLunch(){ return lunch; }
    public String getDinner(){ return dinner; }
    public boolean getCheck(){ return check;}
    public void setCheck(){check = true;}
    public int getDate(){ return date;}

}
