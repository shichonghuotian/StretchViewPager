package com.example.stretch.demo.frag;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.example.stretch.demo.R;

/**
 * @author uis 2018/7/21
 */
public class Frag extends Fragment{


    RecyclerView recycler_view,recyclerView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frg_icon,container,false);

//        ViewGroup.LayoutParams layoutParams = v.findViewById(R.id.fragmentIv).getLayoutParams();
//        layoutParams.width = getScreenWidth(getActivity()) ;
//        v.findViewById(R.id.fragmentIv).setLayoutParams(layoutParams);
        //        recycler_view = v.findViewById(R.id.recycler_view);
//        recyclerView = v.findViewById(R.id.recyclerView);
//
//        int id = getArguments().getInt("id",0);
//        recycler_view.setLayoutManager(new LinearLayoutManager(container.getContext(),LinearLayoutManager.HORIZONTAL,false));
//        recycler_view.setAdapter(new RecyclerAdapter(id,true));
//        recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext(),LinearLayoutManager.VERTICAL,false));
//        recyclerView.setAdapter(new RecyclerAdapter(id,false));





        return v;
    }

    /**
     * 获取手机宽度
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        assert manager != null;
        Display display = manager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        if (size.x > size.y) return size.y;
        return size.x;
    }
}
