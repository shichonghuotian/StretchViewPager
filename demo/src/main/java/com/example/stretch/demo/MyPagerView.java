package com.example.stretch.demo;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

import com.uis.stretch.OnStretchListener;
import com.uis.stretch.StretchPager;

import static com.uis.stretch.StretchPager.STRETCH_RIGHT;

/**
 * Created by Arthur on 2019/7/26.
 */
public class MyPagerView extends FrameLayout implements OnStretchListener {

    private static final int Bessel_Width = 200;
    private ViewPager pagerView;


    private StretchBesselView besselView;


    public MyPagerView(@NonNull Context context) {
        super(context);

        init();
    }

    public MyPagerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public void init() {

        StretchPager pagerView = new StretchPager(getContext());

//        pagerView.setPageMargin(60);
        pagerView.setId(1232333);

//        LayoutParams pagerParams = new ViewGroup.LayoutParams(2112,1440);
        pagerView.setPageMargin(200);

        pagerView.setPadding(200,0,200,0);
        pagerView.setClipChildren(false);

        pagerView.setClipToPadding(false);
        addView(pagerView);
        this.pagerView = pagerView;
        besselView = new StretchBesselView(getContext());


//        LayoutParams basseParams = new ViewGroup.LayoutParams(500,500);

        addView(besselView);

//        addView(besselView,basseParams);


        pagerView.setOnStretchListener(this);
        Log.e("wy","wy -> " + "init");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);


    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {


        pagerView.setBackgroundColor(Color.BLUE);

        besselView.setBackgroundColor(Color.YELLOW);

        if(pagerView != null) {

            pagerView.layout(l ,t,r ,b);
        }

        if(besselView != null) {

            int bw = Bessel_Width;
            int left = getWidth()  ;
            int right = left + bw;

            besselView.layout(left,50,right,50 + 360);

        }


    }


    public void setOffscreenPageLimit(int limit) {

        pagerView.setOffscreenPageLimit(limit);
    }

    public void addOnPageChangeListener(@NonNull ViewPager.OnPageChangeListener listener) {

//        pagerView.addOnPageChangeListener(listener);
        pagerView.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


                //判断一下位置

                //如果
                int count = pagerView.getChildCount() ;

                int contentWidth = count * pagerView.getWidth();


                int st = (count -1) *( pagerView.getWidth() - pagerView.getPaddingLeft());

                int scrollX = pagerView.getScrollX();

                int bw = Bessel_Width;

                int left = getWidth();
                if(scrollX >= st - bw) {

                     left = scrollX - (st - bw);

                    left = getWidth() - left;
                    besselView.setLeft(left);

                    besselView.setRight(left + bw);


                }else {
                    besselView.setLeft(left);

                    besselView.setRight(left + bw);


                }


            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }




    public void setAdapter(@Nullable PagerAdapter adapter) {

        pagerView.setAdapter(adapter);



    }

    //暂时先用这种，有时间加上别的

    ///先向左拖动，然后向右，缓慢拖动会有一个bug

    @Override
    public void onScrolled(int direction, int distance) {


        //还要处理一下业务逻辑，xian
        if(direction == STRETCH_RIGHT) {
            int left = getWidth()  - Bessel_Width - (distance );



            besselView.setLeft(left);

            besselView.setRight(getWidth());
        }



//        besselView.setLINK_LINE_WIDTH(besselView.getWidth());
    }

    @Override
    public void onRefresh(int direction, int distance) {

    }

    @Override
    public void onRelease(int direction) {

    }
}
