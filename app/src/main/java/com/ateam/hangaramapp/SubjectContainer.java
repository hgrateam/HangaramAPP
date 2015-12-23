package com.ateam.hangaramapp;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
/**
 * Created by hyun ji on 2015-12-23.
 */
public class SubjectContainer extends View{

    private int num;

    public SubjectContainer(Context context) {
        super(context);
    }

    public SubjectContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SubjectContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SubjectContainer(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setOption(int n){

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint p = new Paint();
        p.setColor(Color.WHITE);
        p.setStyle(Paint.Style.FILL);
//        canvas.drawRect();
        p.setColor(Color.BLACK);
        p.setStyle(Paint.Style.STROKE);
  //      canvas.drawRect();

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    //reference
    
    //안드로이드-커스텀뷰-이해하기
    //http://blog.burt.pe.kr/%EC%95%88%EB%93%9C%EB%A1%9C%EC%9D%B4%EB%93%9C-%EC%BB%A4%EC%8A%A4%ED%85%80%EB%B7%B0-%EC%9D%B4%ED%95%B4%ED%95%98%EA%B8%B0/
}
