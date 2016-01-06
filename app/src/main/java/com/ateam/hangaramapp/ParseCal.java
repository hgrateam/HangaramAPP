package com.ateam.hangaramapp;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Suhyun on 2015-12-11.
 */
public class ParseCal {


    public interface ParseCallBack{
        public void OnFinish(ParseCal a);
    }
    ParseCallBack mcallback;
    public void setCallBackListener(ParseCallBack callback){
        mcallback = callback;
    }

    final String PARSE_ERROR = "정보가 존재하지 않습니다.";
    final int HEAD_OPEN = 1;
    final int HEAD_CLOSE = 2;

    private int year, month;


    ParseCal() {
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mcallback.OnFinish(ParseCal.this);
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
    public void parse(final ArrayList<Calendar_cellInfo> callist){
        Thread myThread = new Thread(new Runnable() {
            public void run() {
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
                int lastheader=0;
                int lastdate = 0;
                int cnt=0;
                try {
                    url = new URL(urlToRead);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    while ((line = rd.readLine()) != null) {
                        Log.i("parseCal",line);

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
                                strItem = strItem.replace("\\", "");
//                                Log.i("info", "dstart : " + intdstart + "|dend = " + intdend + " |item = " + strItem+ "| "+callist.size()+"번쨰");

                                if(intdstart/100 != lastdate/100){
                                    // mheader 추가
                                    if(callist.size()!=0) {
                                        callist.set(lastheader, new Calendar_cellInfo(lastdate / 10000, lastdate % 10000 / 100, cnt));
                                    }
                                    lastheader = callist.size();
                                    callist.add(new Calendar_cellInfo(intdstart / 10000, intdstart % 10000 / 100, 0));
                                    cnt=0;
                                }
                                callist.add(new Calendar_cellInfo(strItem,intdstart));
                                lastdate = intdstart;
                                cnt++;

                            }   depth--;
                        }
                    }
                    rd.close();

                    if(callist.size()!=0) {
                        callist.set(lastheader, new Calendar_cellInfo(lastdate / 10000, lastdate % 10000 / 100, cnt));
                    }
                    if(cnt!=0) {
                        if(intToYear(intdstart)==year || intToYear(intdstart)==year-1 || intToYear(intdstart)==year+1) {
                            callist.add(new Calendar_cellInfo(intdstart / 10000, intdstart % 10000 / 100, 0));
                        }
                    }

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
