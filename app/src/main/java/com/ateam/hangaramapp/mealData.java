package com.ateam.hangaramapp;

/**
 * Created by Suhyun on 2016-01-06.
 */

public class  mealData{

    public mealData(int date, String lunch, String dinner, boolean check){
        this.date = date;
        this.lunch = lunch;
        this.dinner = dinner;
        this.check = check;
    }

    public mealData(int date, String lunch, String dinner){
        this.date = date;
        this.lunch = lunch;
        this.dinner = dinner;
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
    public String getLunch(){ return removeAllergie(lunch); }
    public String getMealData(){ return removeAllergie("[중식]\n"+getLunch()+"\n[석식]\n"+getDinner());}
    public String getDinner(){ return removeAllergie(dinner); }
    public boolean getCheck(){ return check;}
    public void setCheck(){check = true;}
    public int getDate(){ return date;}

    public String removeAllergie(String a){
        String allergiesym[] = {"①","②","③", "④", "⑤", "⑥", "⑦","⑧","⑨","⑩","⑪","⑫","⑬"};
        String _str = a;
        for(int i=0;i<allergiesym.length;i++){
            _str = _str.replaceAll(allergiesym[i],"");
        }
        return _str;
    }


}
