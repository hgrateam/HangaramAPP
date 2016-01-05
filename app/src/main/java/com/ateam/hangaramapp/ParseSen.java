package com.ateam.hangaramapp;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

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

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mcallback.OnFinish(ParseSen.this);
        }
    };

    public void setIsFirst(boolean a){  isFirst = a;    };
    public boolean getIsFirst(){ return isFirst;}
    public int getErrorCode(){ return error_code;}
    public boolean isMenuExist(int d){
        return check[d];
    }
    private void setCheck(int d){
        check[d]=true;

        if(d>lastday){
            lastday = d;
        }
    }
    public int getLastday(){
        return lastday;
    }
    public String getLunch(int d){
        if(isMenuExist(d)) {
            // 원하는 꼴로 수정하셈 (menu_l[날짜] = 점심 메뉴 menu_d[날짜] = 저녁메뉴
//            return "점심 : " + menu_l[d] + "저녁 : " + menu_d[d];
            return menu_l[d];
        }
        else return PARSE_ERROR;
    }
    public String getDinner(int d){
        if(isMenuExist(d)) {
            // 원하는 꼴로 수정하셈 (menu_l[날짜] = 점심 메뉴 menu_d[날짜] = 저녁메뉴
            return menu_d[d];
        }
        else return PARSE_ERROR;
    }
    public void parse(){
        Thread myThread = new Thread(new Runnable() {
            public void run() {
                String urlToRead="http://hes.sen.go.kr/spr_sci_md00_001.do?";
                urlToRead+="mm="+mm;
                urlToRead+="&ay="+ay;
                error_code = ERR_NO_ERROR;
                int time = 0;
                urlToRead+="&schulCode=B100000549&schulCrseScCode=4";

                Log.i("info", "PARSE TARGET : " + urlToRead);

                URL url; // The URL to read
                HttpURLConnection conn; // The actual connection to the web page
                BufferedReader rd; // Used to read results from the web page
                String line; // An individual line of the web page HTML

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
                                Log.i("info",ay+""+mm+""+d+"심봤다!");
                                if(line.contains("[석식]")) { // 점심 + 저녁
                                    menu_l[d] = line.substring(line.indexOf("[중식]") + "[중식]".length(), line.indexOf("[석식]"));
                                    menu_d[d] = line.substring(line.indexOf("[석식]") + "[석식]".length(), line.length());

                                    setCheck(d);
                                }
                                else{ // only 점심
                                    menu_l[d] = line.substring(line.indexOf("[중식]") + "[중식]".length(), line.length());
                                    setCheck(d);
                                }
                            }
                            else if(line.contains("[석식]")){ // only 저녁
                                menu_d[d] = line.substring(line.indexOf("[석식]") + "[석식]".length(), line.length());
                                setCheck(d);
                            }
                        }
                    }
                    rd.close();
                    Log.i("info", "핸들 발싸!" + ay + "" + mm);
//
//                    handler.sendMessage(handler.obtainMessage());

                    mcallback.OnFinish(ParseSen.this);

                }catch (UnknownHostException e) {
                    Log.i("info", "네트워크 에러! in ParseSen");
                    lastday = -1;
                    error_code = ERR_NET_ERROR;
                    System.out.println("Check Internet Connection!!!");
//                    handler.sendMessage(handler.obtainMessage());
                    mcallback.OnFinish(ParseSen.this);

                } catch (Exception ex) {
                    Log.i("info", "팅김! in ParseSen");
                    error_code = ERR_NET_ERROR;
                    lastday = -1;
                    ex.printStackTrace();
//                    handler.sendMessage(handler.obtainMessage());
                    mcallback.OnFinish(ParseSen.this);

                }
            }
        });
        myThread.start();
    }
    void setMM(int m){
        mm = m;
    }
    void setAY(int y){
        ay =y;
    }

    int getMonth(){
        return mm;
    }
    int getYear(){
        return ay;
    }
}
