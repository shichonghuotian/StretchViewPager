/*
 * Copyright by uis 2018.
 */

package com.uis.stretch;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

/**
 * @author uis 2019/7/20
 */
public class StretchPager extends ViewPager implements ValueAnimator.AnimatorUpdateListener {
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
    private final ValueAnimator anim = ValueAnimator.ofInt(0, 1);
    private int activePointerId;
    /**
     * first touch down,current scrollx vaule
     */
    private int firstScrollX = 0;
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

    public StretchPager(@NonNull Context context) {
        this(context, null);
    }

    public StretchPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.setDuration(300);
    }

    public void setRefreshView(View leftView, View rightView) {
        this.leftView = leftView;
        this.rightView = rightView;
        if (leftView != null) {
            refreshModel |= STRETCH_LEFT;
        }
        if (rightView != null) {
            refreshModel |= STRETCH_RIGHT;
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

    public void setAnimInterpolator(Interpolator interpolator) {
        this.anim.setInterpolator(interpolator);
    }

    public void setAnimDuration(int duration) {
        this.anim.setDuration(duration);
    }

    private int offsetX = -200;

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        final int count = getChildCount();//重新放置位置
        View child = getChildAt(count - 1);
        if (child != null && (leftView == child || rightView == child)) {
            int width = getMeasuredWidth();
            int left = expectDistance + (child == leftView ? -width : width);
            int right = left + width;
            child.layout(left + offsetX, 0, right + offsetX, getMeasuredHeight());
        }
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
        } else if ((stretchRight || refreshRight) && getAdapter().getCount() == getCurrentItem() + 1 && distanceX < 0) {
            directionModel = STRETCH_RIGHT;//right edge and distanceX LT 0
        } else {
            directionModel = STRETCH_NONE;
            enable = false;
        }
        return enable;
    }

    private void scrollByMove(int x) {
        addLeftRightEdge();
        int total = 8 * getWidth() / 10;
        int scroll = Math.abs(getScrollX() - firstScrollX);
        double dx = Math.signum(-x) * (scroll > 0.9 * total ? (scroll > total ? 0 : 1) : 0.75 * Math.abs(x));
        scrollBy((int) dx, 0);
        if (null != listener) {
            listener.onScrolled(directionModel, getScrollDistance());
        }
    }

    private void addLeftRightEdge() {
        if (directionModel == STRETCH_LEFT && leftView != null && leftView.getParent() == null) {
            addEdgeView(leftView);
        } else if (directionModel == STRETCH_RIGHT && rightView != null && rightView.getParent() == null) {
            addEdgeView(rightView);
        }
    }

    private void addEdgeView(View view) {
        LayoutParams p = new LayoutParams();
        p.isDecor = true;
        addView(view, p);
    }

    private void scrollEndMove() {
        final int scrollDistance = getScrollDistance();
        if (null != listener) {
            listener.onRefresh(directionModel, Math.abs(scrollDistance));
        }
        refreshDoneAnim();
    }

    private void refreshDoneAnim() {
        isAnimalRunning = true;
        anim.addUpdateListener(this);
        anim.start();
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        float percent = animation.getAnimatedFraction();
        int distance = getScrollDistance();
        int firstTotalDistance = distance + lastTotalDistance;
        int dx = (int) ((percent > 1.0f ? 1.0 : percent) * firstTotalDistance) - lastTotalDistance;
        lastTotalDistance += dx;
        scrollBy(dx, 0);
        if (1.0f <= percent || distance == 0) {
            anim.removeAllUpdateListeners();
            if (null != listener) listener.onRelease(directionModel);
            removeView(leftView);
            removeView(rightView);
            lastTotalDistance = 0;
            isAnimalRunning = false;
            stretchStatus = false;
        }
    }

    private int getScrollDistance() {
        return expectDistance - getScrollX();
    }
}
