package com.example.stretch.demo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * author: jiangtao.liang
 * date:   On 2019/7/25 10:39
 *
 *
 *可以修改成根据宽度，绘制贝塞尔曲线
 */
public class StretchBesselView extends View {

    //圆半径
    private static final int RADIUS = 180;

    public void setLinkLineWidth(int linkLineWidth) {
        Log.i("moon" , "set >>>>>>>>"+ linkLineWidth +"");
        this.linkLineWidth = linkLineWidth;
//        init();
        invalidate();
    }

    public int getLinkLineWidth() {
        return getWidth();
    }

    //连接线长度
    private int linkLineWidth = -280;

    // 两个半圆两侧矩形的宽度
    private static final int EXP_WIDTH = 40;

    private RectF circleRectF, mRigtCircleRectF;

    Paint mPaint = new Paint();

    Path mPath = new Path();

    float p1x, p1y, p2x, p2y, p3x, p3y, p4x, p4y;

    float anchor1x, anchor1y, anchor2x, anchor2y; // 辅助点

    private static final String drawText = "查看更多";

    private int textLength;

    private static final int TEXT_OFFSET_LEFT = 60;
    private static final int TEXT_OFFSET_TOP = 140;

    private Bitmap backIconBitmap;

    public StretchBesselView(Context context) {
        super(context);
        initResource();
    }

    public StretchBesselView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initResource();
    }

    public StretchBesselView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initResource();
    }

    private void initResource() {
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(3f);


        circleRectF = new RectF(-RADIUS + EXP_WIDTH, 0, EXP_WIDTH + RADIUS, 2 * RADIUS);

        mRigtCircleRectF = new RectF(getLinkLineWidth() + EXP_WIDTH + RADIUS, 0, EXP_WIDTH + 3 * RADIUS +
                getLinkLineWidth(), 2 * RADIUS);

        textLength = drawText.length();

        backIconBitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.back);

        update();
    }


    ///不会算。。。。
    public void update() {

        //这里

        //宽度 - width

        int circleWidth = 2 * RADIUS;

        float left = getWidth() - circleWidth;


        mRigtCircleRectF.set(  left, 0,  circleWidth + left , 2 *
                RADIUS);

        p1x = EXP_WIDTH;
        p1y = 0;

        p2x = EXP_WIDTH + circleWidth + getLinkLineWidth();
        p2y = 0;

        p3x = EXP_WIDTH;
        p3y = 2 * RADIUS;

        p4x = EXP_WIDTH + circleWidth + getLinkLineWidth();
        p4y = 2 * RADIUS;

        anchor1x = (p1x + p4x) / 2;
        anchor1y = (p1y + p4y) / 2 + 110;

        anchor2x = (p2x + p3x) / 2;
        anchor2y = (p2y + p3y) / 2 - 110;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        //绘制左边的半圆+ 矩形
        update();

//        canvas.clipRect(new Rect(0,0,getWidth(),getHeight()));
        mPath.lineTo(EXP_WIDTH, 0);

        mPath.moveTo(EXP_WIDTH, 0);

        mPath.arcTo(circleRectF, -90, 180, false);

        mPath.lineTo(0, 2 * RADIUS);

        mPath.lineTo(0, 0);

        mPaint.setStyle(Paint.Style.FILL);

        mPaint.setColor(Color.RED);
        canvas.drawPath(mPath, mPaint);

        //绘制右边半圆 + 矩形
        mPath.moveTo(getLinkLineWidth() + 2 * RADIUS + EXP_WIDTH, 0);

        mPath.lineTo(getLinkLineWidth() + 2 * RADIUS + 2 * EXP_WIDTH, 0);
        mPath.lineTo(getLinkLineWidth() + 2 * RADIUS + 2 * EXP_WIDTH, 2 * RADIUS);
        mPath.lineTo(getLinkLineWidth() + 2 * RADIUS + EXP_WIDTH, 2 * RADIUS);
        mPath.arcTo(mRigtCircleRectF, 90, 180, false);

        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.RED);

        canvas.drawPath(mPath, mPaint);

        if (getLinkLineWidth() > -220){
            // 绘制粘滞图形
            mPath.reset();
            mPath.moveTo(p1x, p1y);
            mPath.quadTo(anchor1x, anchor1y, p2x, p2y);
            mPath.lineTo(p4x, p4y);
            mPath.quadTo(anchor2x, anchor2y, p3x, p3y);
            mPath.lineTo(p1x, p1y);

            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawPath(mPath, mPaint);
        }

//        // 绘制文字和图标
        mPaint.setColor(Color.WHITE);
        mPaint.setTextSize(42);

        for (int i = 0; i < textLength; i++) {
            canvas.drawText(String.valueOf(drawText.charAt(i)), TEXT_OFFSET_LEFT, TEXT_OFFSET_TOP + i * 40, mPaint);
        }

//        canvas.drawBitmap(backIconBitmap, 20 ,170,mPaint);
    }

    public void destroy(){
        if (backIconBitmap != null && !backIconBitmap.isRecycled()){
            backIconBitmap.recycle();
            backIconBitmap = null;
        }
    }
}
