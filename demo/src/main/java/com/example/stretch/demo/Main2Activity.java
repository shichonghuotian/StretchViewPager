package com.example.stretch.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Main2Activity extends AppCompatActivity {

    StretchBesselView stretchBesselView;

    private int l  = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        stretchBesselView = findViewById(R.id.view2);

        findViewById(R.id.controlBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                l += 10;
                stretchBesselView.setLINK_LINE_WIDTH(l);

            }
        });

        findViewById(R.id.decontrolBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                l-=10;
                stretchBesselView.setLINK_LINE_WIDTH(l);
            }
        });
    }
}
