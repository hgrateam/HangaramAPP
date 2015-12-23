package com.ateam.hangaramapp;

        import android.graphics.Color;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.util.Log;
        import android.widget.TextView;

public class AddSubject extends AppCompatActivity{

    private String subjects_name[]={"소환사의 협곡","수학","물리","잘 찍기","영어","하핫","룰루 랄라", "뻐킹 생명과학", "으쓱 으쓱", "하스스톤", "워크래프트의 영웅들", "자바2", "피곤행", "당첨!", "읎다", "어맛!"};
    private SubjectContainer subcon;
    private boolean isSelected[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_subject);


        subcon = (SubjectContainer) findViewById(R.id.subject_container);
        isSelected = new boolean [subjects_name.length];
        for(int i=0;i<subjects_name.length;i++) {
            subcon.add(subjects_name[i], Color.BLACK, Color.WHITE);
            isSelected[i] = false; // initialize;
        }

        subcon.setSubjectListener(new SubjectContainer.OnSubjectListener() {
            @Override
            public void onSubjectSelected(int sn) {
                if (!isSelected[sn]){
                    setSelected(sn);
                    subcon.setHighlight(sn);
                }
                else if (isSelected[sn]){
                    setUnSelected(sn);
                    subcon.removeHighlight(sn);
                }
                subcon.redraw();

            }
        });
        subcon.redraw();

    }
    private void setSelected(int sn){
        isSelected[sn] = true;
    }
    private void setUnSelected(int sn){
        isSelected[sn] = false;

    }

}
