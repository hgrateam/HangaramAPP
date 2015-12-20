package com.ateam.hangaramapp;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Suhyun on 2015-12-11.
 */
public class ParseCal {

    private ParseCallBack callbackEvent;
    String returnSpace(int a){
        String b ="";
        for(int i=0;i<a;i++) b+="\t";
        return b;
    }

    boolean check[];

    static String PARSE_ERROR = "정보가 존재하지 않습니다.";
    static int HEAD_OPEN = 1;
    static int HEAD_CLOSE = 2;

    private int year, month;

    ParseCal(ParseCallBack event) {

        callbackEvent = event;

        check = new boolean[31];
        for(int i=0;i<31;i++) {
            check[i] = false;
        }

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            callbackEvent.callbackMethod_Cal(ParseCal.this);
        }
    };

    public int intToYear(int a){
        return a/10000;
    }
    public int intToMonth(int a){
        return (a%10000)/100;
    }
    public int intToDay(int a){
        return a%100;
    }
    public boolean isMenuExist(int d){
        return check[d];
    }
    public String getItem(int d){
        if(isMenuExist(d)) {
            // 원하는 꼴로 수정하셈 (menu_l[날짜] = 점심 메뉴 menu_d[날짜] = 저녁메뉴
            return "hello";
        }
        else return PARSE_ERROR;
    }
    public void parse(){
        Thread myThread = new Thread(new Runnable() {
            public void run() {
/*                String urlToRead="https://calendar.google.com/calendar/htmlembed?src=hangaramhs@gmail.com&dates=";
                //
                                // + 20151101/20151201
                urlToRead+=""+ay+((mm<10) ? "0"+mm:mm)+"01/";
                urlToRead+=(mm==12 ? ""+(ay+1)+"0101":""+ay+((mm<10) ? "0"+mm:mm)+"01");
                */
                String urlToRead="https://calendar.google.com/calendar/ical/hangaramhs@gmail.com/public/basic.ics";

                int time = 0;

                Log.i("info", "PARSE TARGET : " + urlToRead);

                URL url; // The URL to read
                HttpURLConnection conn; // The actual connection to the web page
                BufferedReader rd; // Used to read results from the web page
                String line; // An individual line of the web page HTML
                String strdStart, strdEnd;
                String strItem=PARSE_ERROR;
                int intdstart=-1, intdend=-1;
                int depth = 0;

                try {
                    url = new URL(urlToRead);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line = rd.readLine()) != null) {
                        if(depth==1){
                            if(line.contains("DTSTART;VALUE=DATE:")){
                                strdStart = line.substring(line.indexOf(":")+1,line.length());
                                intdstart = Integer.parseInt(strdStart);
                            }
                            if(line.contains("DTEND;VALUE=DATE:")){
                                strdEnd = line.substring(line.indexOf(":")+1,line.length());
                                intdend = Integer.parseInt(strdEnd);
                            }

                            if(line.contains("SUMMARY:")){
                                strItem = line.substring(line.indexOf(":")+1, line.length());
                            }
                        }
                        if(line.contains("BEGIN:VEVENT")){
                            intdstart = intdend = -1;
                            depth++;
                        }
                        else if(line.contains("END:VEVENT")) {
                            if(intToYear(intdstart)==year || intToYear(intdstart)==year-1 || intToYear(intdstart)==year+1) {
                                strItem.replace("\\","");
                                Log.i("info", "dstart : " + intdstart + "|dend = " + intdend + " |item = " + strItem);

                            }   depth--;
                        }
                    }
                    rd.close();

                    handler.sendMessage(handler.obtainMessage());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        myThread.start();
    }
    void setMonth(int m){
        month = m;
    }
    void setYear(int y){
        year =y;
    }
}
