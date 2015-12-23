package com.ateam.hangaramapp;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.Random;

public class TimeTable extends AppCompatActivity{

    private String subjects_name[]={"소환사의 협곡","수학","물리","잘 찍기","영어","하핫","룰루 랄라", "뻐킹 생명과학", "으쓱 으쓱", "하스스톤", "워크래프트의 영웅들", "자바2", "피곤행", "당첨!", "읎다"};
    private int text_color[];
    private int foreground_color[];

    private void collectColor(){



        int palette_fore[];
        String palette_color[]={"#EF9A9A", "#F48FB1","#CE93D8","#9FA8DA","#90CAF9","#FFE082","#FFCC80","#D7CCC8","#FFF59D", "#A5D6A7", "#81D4FA"};
        int palette_text[]={Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE};
//        int palette_text[]={Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK};

        int leng = subjects_name.length;
        text_color = new int [leng];
        foreground_color = new int [leng];

        palette_fore = new int[palette_color.length];
        for(int i=0;i<palette_color.length;i++){
            palette_fore[i] = Color.parseColor(palette_color[i]);
        }



        int r;
        for(int i=0;i<leng;i++){

            r = (int)(Math.random()*(palette_color.length));
            Log.i("info","Math.random = "+Math.random()+ "r = "+r);

//            text_color[i] = palette_text[r];
            text_color[i] = Color.BLACK;
            foreground_color[i] = palette_fore[r];
        }
    }
//    private int subjects_color[]={Color.BLACK,Color.BLACK,Color.BLACK,Color.BLACK,Color.BLACK,Color.BLACK,Color.BLACK,Color.BLACK,Color.BLACK};
//    private int foreground_color[]={Color.WHITE,Color.WHITE,Color.WHITE,Color.WHITE,Color.WHITE,Color.WHITE,Color.WHITE,Color.WHITE,Color.WHITE};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_table);

        collectColor();
        SubjectContainer subcon = (SubjectContainer) findViewById(R.id.subject_container);
        for(int i=0;i<subjects_name.length;i++) {
            subcon.add(subjects_name[i], text_color[i],foreground_color[i]);
        }
        subcon.redraw();
    }
}
