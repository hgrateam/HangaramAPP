package com.ateam.hangaramapp;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
/**
 * Created by hyun ji on 2015-12-23.
 */
public class SubjectContainer extends View{

    private static int SIZE = 100;
    int size;
    private static int block_height = 150;
    private static int divideN = 3;

    private int heightSize = 0;
    private int widthSize = 0;

    private boolean highlightedCell[];
    public interface OnSubjectListener{
        void onSubjectSelected(int sn);
    }
    public void setSubjectListener(OnSubjectListener listener){
        subjectListener = listener;
    }
    private OnSubjectListener subjectListener;


    private int cnt=0;
    int column;
    private String className[];
    private int textColor[], foreColor[];

    private float startx[], endx[],starty[],endy[];


    private void resetVar(){
        textColor = new int[SIZE];
        foreColor = new int[SIZE];
        className = new String[SIZE];


        startx = new float[SIZE];
        endx = new float[SIZE];
        starty = new float[SIZE];
        endy = new float[SIZE];

        highlightedCell = new boolean [SIZE];
        resetHighlights();
        cnt=0;
    }


    public void resetHighlights(){
        for(int i=0;i<SIZE;i++)
            highlightedCell[i]=false;
    }

    public void setHighlight(int sn){
        highlightedCell[sn]= true;
    }
    public void removeHighlight(int sn){
        highlightedCell[sn]= false;
    }
    public SubjectContainer(Context context) {
        super(context);
        resetVar();
    }

    public SubjectContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        resetVar();
    }

    public SubjectContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        resetVar();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SubjectContainer(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        resetVar();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float px = event.getX();
        float py = event.getY();
        for(int i=0;i<cnt;i++){
            if(startx[i] <= px && px < endx[i]){
                if(starty[i] <= py && py < endy[i]){
                    subjectListener.onSubjectSelected(i);
                    break;
                }
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);

        int blockCnt;
        if(cnt==0){
            super.onDraw(canvas);
            return;
        }
        int block_width = widthSize/divideN;
        Paint p = new Paint();

        Log.i("info", "SC - onDraw() cnt = "+cnt);
        p.setAntiAlias(true);
        p.setTextAlign(Paint.Align.CENTER);

        for(int i=0;i<(cnt%divideN ==0 ? cnt/divideN:(cnt/divideN)+1);i++) {
            for(int j=0;j<divideN;j++) {
                blockCnt = i*divideN+j;

                if(blockCnt >= cnt){
                    break;
                }
                p.setTextSize(35);

                if(highlightedCell[blockCnt]) {
                    p.setColor(Color.parseColor("#90CAF9"));
                }
                else p.setColor(foreColor[blockCnt]);
                p.setStyle(Paint.Style.FILL);

                startx[blockCnt] = j*block_width;
                starty[blockCnt] = i*block_height;
                endx[blockCnt] = (j+1)*block_width;
                endy[blockCnt] = (i+1)*block_height;

                canvas.drawRect(startx[blockCnt], starty[blockCnt], endx[blockCnt], endy[blockCnt],p);

                p.setColor(Color.BLACK);
                p.setStyle(Paint.Style.STROKE);

                canvas.drawRect(startx[blockCnt], starty[blockCnt], endx[blockCnt], endy[blockCnt],p);

                p.setColor(textColor[blockCnt]);

                canvas.drawText(className[blockCnt],(j+0.5f)*block_width,(i + 0.5f) * block_height,p);
            }
        }
    }

    public void add(String name, int textColor, int foregroundColor){
        className[cnt] = name;
        this.textColor[cnt] = textColor;
        foreColor[cnt]=foregroundColor;
        cnt++;
    }

    public void redraw()
    {
        invalidate();
    }
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        switch(heightMode) {
            case MeasureSpec.UNSPECIFIED:    // mode 가 셋팅되지 않은 크기가 넘어올때
                heightSize = heightMeasureSpec;
                break;
            case MeasureSpec.AT_MOST:        // wrap_content (뷰 내부의 크기에 따라 크기가 달라짐)
                heightSize = (int)(block_height*(8));
                break;
            case MeasureSpec.EXACTLY:        // fill_parent, match_parent (외부에서 이미 크기가 지정되었음)
                heightSize = MeasureSpec.getSize(heightMeasureSpec);
                break;
        }

        // width 진짜 크기 구하기
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        switch(widthMode) {
            case MeasureSpec.UNSPECIFIED:    // mode 가 셋팅되지 않은 크기가 넘어올때
                widthSize = widthMeasureSpec;
                break;
            case MeasureSpec.AT_MOST:        // wrap_content (뷰 내부의 크기에 따라 크기가 달라짐)
                widthSize = 100;
                break;
            case MeasureSpec.EXACTLY:        // fill_parent, match_parent (외부에서 이미 크기가 지정되었음)
                widthSize = MeasureSpec.getSize(widthMeasureSpec);
                break;
        }
        setMeasuredDimension(widthSize, heightSize);
    }

    //reference

    //안드로이드-커스텀뷰-이해하기
    //http://blog.burt.pe.kr/%EC%95%88%EB%93%9C%EB%A1%9C%EC%9D%B4%EB%93%9C-%EC%BB%A4%EC%8A%A4%ED%85%80%EB%B7%B0-%EC%9D%B4%ED%95%B4%ED%95%98%EA%B8%B0/
}
