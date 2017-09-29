package com.shuhai.anfang.report.ui;

import android.os.Bundle;
import android.view.View;

import com.shuhai.anfang.report.R;
import com.shuhai.anfang.report.custom.AutoScrollViewPager;
import com.shuhai.anfang.report.custom.MyPagerAdapter;

public class MainActivity extends BaseActivity {

    private AutoScrollViewPager autoScrollViewPager;
    MyPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;

        decorView.setSystemUiVisibility(uiOptions);

        setContentView(R.layout.activity_main);
        autoScrollViewPager = (AutoScrollViewPager) findViewById(R.id.autoViewPager);
        autoScrollViewPager.setCycle(true);
        autoScrollViewPager.setOffscreenPageLimit(2);

        adapter = new MyPagerAdapter(this);
        autoScrollViewPager.setAdapter(adapter);
        adapter.loadData();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        autoScrollViewPager.startAutoScroll();
    }
}
