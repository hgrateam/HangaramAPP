package com.ateam.hangaramapp;

import android.util.Log;

/**
 * Created by Suhyun on 2015-12-21.
 */
public class MealInfo {
    private static int SIZE=1000;
    private int cnt;

    private String mealInfo[];
    private int mealDate[];

    MealInfo(){
        mealInfo = new String[SIZE];
        mealDate = new int[SIZE];
    }

    public void resetMeal(){
        cnt = 0;
        for(int i=0;i<SIZE;i++){
            mealDate[i] = 0;
            mealInfo[i]="";
        }
    }
    public boolean isMealExist(int d){
        for(int i=0;i<cnt;i++){
            if(mealDate[i] == d){
                return true;
            }
        }
        return false;
    }
    public void add(int d, String str){
        mealInfo[cnt] = str;
        mealDate[cnt++] = d;
        Log.i("info", "mealInfo add:"+d+" " +"|"+str);
    }

    public String getData(int d){
        for(int i=0;i<getCnt();i++){
            if(mealDate[i] == d){
                return mealInfo[i];
            }
        }
        return "null";
    }
    public int getCnt(){ return cnt; }

}
