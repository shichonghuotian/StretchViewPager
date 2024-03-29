/*
 * Copyright by uis 2018.
 */

package com.uis.stretch;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.Scroller;

/**
 * @author uis 2019/7/20
 */
public class StretchPager extends ViewPager  {
    public static final int STRETCH_NONE = 0x00;
    /**
     * left stretch
     */
    public static final int STRETCH_LEFT = 0x01;
    /**
     * right stretch
     */
    public static final int STRETCH_RIGHT = 0x10;
    /**
     * both stretch
     */
    public static final int STRETCH_BOTH = 0x11;
    /**
     * refresh priority GT stretch
     */
    private int refreshModel = STRETCH_NONE;
    private int stretchModel = STRETCH_BOTH;
    private int directionModel = STRETCH_NONE;
    private int lastX = 0;
    private int lastY = 0;
    private int distanceX = 0;
    private int expectDistance;
    private boolean stretchStatus = false;
    private OnStretchListener listener;

    //可以使用scroller来处理滑动问题，只能新建一个，私有的无法使用

    private Scroller scroller;

    private boolean isScrollerRunning;

    private int activePointerId;
    /**
     * first touch down,current scrollx vaule
     */
    private int firstScrollX = 0;
    private int endScrollX = 0;

    private int lastTotalDistance = 0;
    /**
     * 回弹动画正在播放
     */
    private boolean isAnimalRunning = false;
    /**
     * 触摸在边界位置合法
     */
    private boolean validTouch = false;
    private View leftView, rightView;
    private boolean isFirstMove, isMoveX;


    private static final Interpolator sInterpolator = new Interpolator() {
        public float getInterpolation(float t) {
            --t;
            return t * t * t * t * t + 1.0F;
        }
    };


    public StretchPager(@NonNull Context context) {
        this(context, null);
    }

    public StretchPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        scroller = new Scroller(context,sInterpolator);

    }

    public void setRefreshView(View leftView, View rightView) {
        this.leftView = leftView;
        this.rightView = rightView;
        if (leftView != null) {
            refreshModel |= STRETCH_LEFT;
        }
        if (rightView != null) {
            refreshModel |= STRETCH_RIGHT;

//            addEdgeView(rightView);
        }
    }



    public int getRefreshModel() {
        return refreshModel;
    }

    /**
     * set Stretch model，default is: opened
     *
     * @param model one of {@link #STRETCH_BOTH},{@link #STRETCH_LEFT},{@link #STRETCH_RIGHT},{@link #STRETCH_NONE}
     */
    public void setStretchModel(int model) {
        this.stretchModel = model;
    }

    public int getStretchModel() {
        return stretchModel;
    }

    public void setOnStretchListener(OnStretchListener l) {
        listener = l;
    }


    private int offsetX = -200;

    private static final int Bessel_Width = 200;


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
//        final int count = getChildCount();//重新放置位置

        if(rightView != null) {
            removeView(rightView);

            addEdgeView(rightView);
            int w = getWidth();

            int contentWidth = getAdapter().getCount() *( getWidth() ) - getPaddingRight() - getPaddingLeft();

            int left = contentWidth - Bessel_Width;

            int right = left + Bessel_Width;


            rightView.layout(left,0, right,b);

        }

    }

    @Override
    public void removeView(View view) {
        super.removeView(view);

    }

    @Override
    public void removeViewAt(int index) {
        super.removeViewAt(index);


    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int actionId = ev.getAction() & MotionEvent.ACTION_MASK;
        switch (actionId) {
            case MotionEvent.ACTION_DOWN://0
                validTouch = !isAnimalRunning;
                if (validTouch) {
                    firstScrollX = getScrollX();
                    int width = getWidth();
                    int round = (int) Math.round(1.0 * firstScrollX / width);//fixed scrollx distance
                    expectDistance = round * width;
                }
                isFirstMove = true;
                isMoveX = false;
                lastX = (int) ev.getX();
                lastY = (int) ev.getY();
                activePointerId = ev.getPointerId(0);
                break;
            case MotionEvent.ACTION_MOVE://2
                final int pointerIndex = ev.findPointerIndex(activePointerId);
                if (null == getAdapter() || -1 == pointerIndex) {
                    break;
                }
                int currentX = (int) ev.getX(pointerIndex);
                distanceX = currentX - lastX;
                /** swip by horizontal*/
                if (isFirstMove) {
                    int distanceY = (int) ev.getY(pointerIndex) - lastY;
                    if (distanceX != 0 && distanceX != distanceY) {
                        isFirstMove = false;
                        isMoveX = Math.abs(distanceX) > Math.abs(distanceY);
                    }
                }
                if (isMoveX) {
                    lastX = currentX;
                    if (!stretchStatus) {
                        stretchStatus = validTouch && getStretchEnable(distanceX);
                    }
                }
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (stretchStatus) {
            int actionId = ev.getAction() & MotionEvent.ACTION_MASK;
            switch (actionId) {
                case MotionEvent.ACTION_MOVE://2
                    if (null != getAdapter() && -1 != ev.findPointerIndex(activePointerId)) {
                        scrollByMove(distanceX);
                    }
                    return true;
                case MotionEvent.ACTION_UP://1
                case MotionEvent.ACTION_CANCEL://3
                    if (validTouch) {
                        validTouch = false;
                        scrollEndMove();
                        return true;
                    }
                    break;
                case MotionEvent.ACTION_POINTER_DOWN://5
                    final int index = ev.getActionIndex();
                    lastX = (int) ev.getX(index);//multi-touch
                    activePointerId = ev.getPointerId(index);
                    return true;
            }
        }
        return super.onTouchEvent(ev);
    }

    private boolean getStretchEnable(int distanceX) {
        boolean enable = true;
        boolean refreshLeft = (STRETCH_LEFT & refreshModel) > 0;
        boolean refreshRight = (STRETCH_RIGHT & refreshModel) > 0;
        boolean stretchLeft = (STRETCH_LEFT & stretchModel) > 0;
        boolean stretchRight = (STRETCH_RIGHT & stretchModel) > 0;
        if ((stretchLeft || refreshLeft) && 0 == getCurrentItem() && distanceX > 0) {
            directionModel = STRETCH_LEFT;//left edge and distanceX GT 0
            enable = false;
        } else if ((stretchRight || refreshRight) && getAdapter().getCount() == getCurrentItem() + 1 && distanceX < 0) {
            directionModel = STRETCH_RIGHT;//right edge and distanceX LT 0
        } else {
            directionModel = STRETCH_NONE;
            enable = false;
        }
        return enable;
    }


    ///1。有一点点延迟 ---- 这个是因为修改了canvas 的 clip 导致的，去掉既可以
    //2。先向左拖动，再向右拖，会有一点点问题，没有滑动到正常的页码---处理成都滑动到最后一页即可
    protected void scrollRightView(int dx) {

        if(rightView != null) {

            int contentWidth = getAdapter().getCount() *( getWidth() ) - getPaddingRight() - getPaddingLeft();

            contentWidth += dx;

            //多加几个像素。。。
            rightView.setRight(contentWidth );
        }


    }

    private void scrollByMove(int x) {


        int total = 8 * getWidth() / 10;
        int scroll = Math.abs(getScrollX() - firstScrollX);
        double dx = Math.signum(-x) * (scroll > 0.9 * total ? (scroll > total ? 0 : 1) : 0.75 * Math.abs(x));


        scrollBy((int) dx, 0);
        scrollRightView(scroll);


        //可以在这里添加事件，如果滑动到一定的距离

//        if(distance > xxxx) {
//
//            listener.xxxxx(true);
//        }


        if (null != listener) {
//            listener.onScrolled(directionModel, getScrollDistance());
            listener.onScrolled(directionModel, scroll);


        }

    }

    private void scrollEndMove() {

        refreshDoneAnim();
    }

    private void refreshDoneAnim() {


        int w = (getAdapter().getCount() -1) *( this.getWidth() - this.getPaddingLeft());

        lastTotalDistance = w - getScrollX();

        //两个scroller 可能冲突

          scroller.startScroll(getScrollX(),0,lastTotalDistance,0);

          isScrollerRunning = true;

        invalidate();
    }

    @Override
    public void computeScroll() {

        if(stretchStatus && isScrollerRunning) {
            if (!scroller.isFinished()&&scroller.computeScrollOffset()) {
                scrollTo(scroller.getCurrX(), scroller.getCurrY());
                invalidate();
            }else {
                if(scroller.isFinished()) {

                    Log.e("wy","wy>> " + "scrollEndMove" + " end");

                    lastTotalDistance = 0;
                    isAnimalRunning = false;
                    stretchStatus = false;
                    isScrollerRunning = false;
                }

            }
        }else {
            super.computeScroll();

        }

    }

    private void addEdgeView(View view) {
        LayoutParams p = new LayoutParams();
        p.isDecor = true;
        addView(view, p);
    }

}
