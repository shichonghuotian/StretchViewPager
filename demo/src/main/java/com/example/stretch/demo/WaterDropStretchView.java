package com.example.stretch.demo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * author: jiangtao.liang
 * date:   On 2019/7/24 19:32
 */
public class WaterDropStretchView extends View {

    private Paint mLeftCirclePaint;

    private RectF mLeftCircleRect;

    private RectF mLineRect;

    private RectF mRightCircleRect;

    private static final int RADIUS = 200;

    private static final int LINK_LINE_HEIGHT = 50;

    public void setLineLength(int lineLength) {
        this.lineLength = lineLength;
    }

    private int lineLength = 200;

    public WaterDropStretchView(Context context) {
        super(context);
        initResource();
    }

    public WaterDropStretchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initResource();
    }

    public WaterDropStretchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initResource();
    }


    private void initResource() {
        mLeftCirclePaint = new Paint();
        mLeftCirclePaint.setStyle(Paint.Style.FILL);
        mLeftCirclePaint.setColor(Color.RED);
        mLeftCirclePaint.setAntiAlias(true);

        mLeftCircleRect = new RectF(-RADIUS, 0, RADIUS, RADIUS * 2);

        // TODO: 2019/7/24 test
//        mLeftCircleRect = new RectF( 0, 0, 2*RADIUS, RADIUS * 2);

        mLineRect = new RectF(RADIUS - 10, RADIUS - LINK_LINE_HEIGHT / 2 - 5, RADIUS + lineLength, RADIUS + LINK_LINE_HEIGHT - 5);

        mRightCircleRect = new RectF(RADIUS + lineLength - 5, 0, 3 * RADIUS + lineLength - 5, RADIUS * 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawArc(mLeftCircleRect, -90, 180, true, mLeftCirclePaint);

        canvas.drawRect(mLineRect, mLeftCirclePaint);

        canvas.drawArc(mRightCircleRect, 90, 180, true, mLeftCirclePaint);


    }
}
