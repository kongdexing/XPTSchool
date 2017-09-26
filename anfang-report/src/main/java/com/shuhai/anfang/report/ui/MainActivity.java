package com.shuhai.anfang.report.ui;

import android.os.Bundle;

import com.shuhai.anfang.report.R;
import com.shuhai.anfang.report.custom.AutoScrollViewPager;
import com.shuhai.anfang.report.custom.MyPagerAdapter;

public class MainActivity extends BaseActivity {

    private AutoScrollViewPager autoScrollViewPager;
    MyPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        autoScrollViewPager = (AutoScrollViewPager) findViewById(R.id.autoViewPager);
//        autoScrollViewPager.setCycle(true);
        adapter = new MyPagerAdapter(this);
        autoScrollViewPager.setAdapter(adapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.loadData();
//        autoScrollViewPager.startAutoScroll();
    }
}
