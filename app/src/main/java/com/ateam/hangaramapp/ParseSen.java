package com.ateam.hangaramapp;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * Created by Suhyun on 2015-12-11.
 */
public class ParseSen {

//    private ParseCallBack callbackEvent;

    public interface ParseCallBack{
        public void OnFinish(ParseSen a);
    }
    ParseCallBack mcallback;
    public void setCallBackListener(ParseCallBack callback){
        mcallback = callback;
    }

    String menu_l[];
    String menu_d[];
    boolean check[];

    static final String PARSE_ERROR = "정보가 존재하지 않습니다.";
    static final int TIME_LUNCH = 1;
    static final int TIME_DINNER = 2;

    public final static int ERR_NO_ERROR = 3;
    public final static int ERR_NET_ERROR = 4;

    private boolean isFirst;
    int error_code;
    int mm, ay;
    int lastday;

    ParseSen() {
        check = new boolean[33];
        menu_d = new String[1000];
        menu_l = new String[1000];
        isFirst = false;
        for(int i=0;i<=31;i++) {
           check[i] = false;
            menu_d[i]=menu_l[i]="";
        }
        lastday = -1;
    }

    public void setIsFirst(boolean a){  isFirst = a;    };
    public boolean getIsFirst(){ return isFirst;}
    public int getErrorCode(){ return error_code;}
    public boolean isMenuExist(int d){
        return check[d];
    }
    public int getLastday(){
        return lastday;
    }
    public void parse_part(int m, int y, int calc, ArrayList<mealData> mealDatas){
        Log.i("info", "parse_part"+m+""+y);
        if(calc == -1){
            if(m==1){
                m=12;
                y--;
            }
            else
                m--;

        }
        else if(calc == 1){
            if(m==12){
                m=1;
                y++;
            }
            else
                m++;
        }
        String urlToRead="http://hes.sen.go.kr/spr_sci_md00_001.do?";
        urlToRead+="mm="+m;
        urlToRead+="&ay="+y;
        error_code = ERR_NO_ERROR;
        urlToRead+="&schulCode=B100000549&schulCrseScCode=4";
        Log.i("info", "PARSE TARGET : " + urlToRead);

        URL url; // The URL to read
        HttpURLConnection conn; // The actual connection to the web page
        BufferedReader rd; // Used to read results from the web page
        String line=""; // An individual line of the web page HTML

        try {
            url = new URL(urlToRead);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            while ((line = rd.readLine()) != null) {

                if(line.contains("<td>")){
                    // 이상한건 삭제한다.
                    line = line.replace("\t","");
                    line = line.replace("</td>","");
                    line = line.replace("<br />"," ");

                    int d=0; // 일자

                    if(line.contains("[")) {
                        if (line.substring(line.indexOf("<td>") + 1, line.indexOf("[")) != null) {
                            d = Integer.parseInt(line.substring(line.indexOf("<td>") + "<td>".length(), line.indexOf("[")).replace(" ",""));
                        }
                    }

                    line = line.replace("<td>","");

                    if(d==0) continue;

                    if (line.contains("[중식]")) {
                        if(line.contains("[석식]")) { // 점심 + 저녁
                            String lunch = line.substring(line.indexOf("[중식]") + "[중식]".length(), line.indexOf("[석식]"));
                            String dinner = line.substring(line.indexOf("[석식]") + "[석식]".length(), line.length());
                            mealDatas.add(new mealData(y * 10000 + m * 100 + d, lunch, dinner, false));
                            Log.i("info", y+""+m+""+d+" | "+lunch+ "|"+ dinner + "on ParseSen");

                        }
                        else{ // only 점심
                            String lunch = line.substring(line.indexOf("[중식]") + "[중식]".length(), line.length());
                            mealDatas.add(new mealData(y * 10000 + m * 100 + d, lunch, "", false));
                            Log.i("info", y + "" + m + "" + d + " | " + lunch + "|" +  "on ParseSen");
                        }
                    }
                    else if(line.contains("[석식]")){ // only 저녁
                        String dinner = line.substring(line.indexOf("[석식]") + "[석식]".length(), line.length());
                        mealDatas.add(new mealData(y * 10000 + m * 100 + d, "", dinner, false));
                        Log.i("info", y + "" + m + "" + d + " | " +  "|" + dinner + "on ParseSen");
                    }
                }
            }
            rd.close();
            //return;

        }catch (UnknownHostException e) {
            Log.i("info", "네트워크 에러! in ParseSen");
            error_code = ERR_NET_ERROR;
            System.out.println("Check Internet Connection!!!");
            return;

        } catch (Exception ex) {
            Log.i("info", "팅김! in ParseSen"+line);
//                    error_code = ERR_NET_ERROR;
            ex.printStackTrace();
        }

    }
    public void parse(final ArrayList<mealData> mealDatas){
        Thread myThread = new Thread(new Runnable() {
            public void run() {
                parse_part(mm, ay, -1, mealDatas);
                parse_part(mm, ay, 0, mealDatas);
                parse_part(mm, ay, 1, mealDatas);
                mcallback.OnFinish(ParseSen.this);
            }
        });
        myThread.start();
    }
    void setDate(int m, int y){ mm=m; ay=y;}
    int getMonth(){
        return mm;
    }
    int getYear(){
        return ay;
    }
}
