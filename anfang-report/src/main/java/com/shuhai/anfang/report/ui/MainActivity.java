package com.shuhai.anfang.report.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

import com.shuhai.anfang.report.R;
import com.shuhai.anfang.report.custom.AutoScrollViewPager;
import com.shuhai.anfang.report.custom.MyPagerAdapter;
import com.shuhai.anfang.report.http.HttpAction;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends BaseActivity {

    private AutoScrollViewPager autoScrollViewPager;
    private CirclePageIndicator indicator;
    MyPagerAdapter adapter;
    private Timer mTimer;

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

        indicator = (CirclePageIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(autoScrollViewPager);
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                Log.i(TAG, "onPageScrolled: " + position);
            }

            @Override
            public void onPageSelected(int position) {
                Log.i(TAG, "animationReportXY onPageSelected: " + position);
                try {
                    switch (position) {
                        case 0:
                            ((Report1View) adapter.getReportViews().get(position)).loadData();
                            break;
                        case 1:
                            ((Report2View) adapter.getReportViews().get(position)).loadData();
                            break;
                        case 2:
                            ((Report3View) adapter.getReportViews().get(position)).loadData();
                            break;
                    }
                } catch (Exception ex) {

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
//                Log.i(TAG, "onPageScrollStateChanged: ");
            }
        });
        indicator.setCurrentItem(0);
        adapter.loadData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        autoScrollViewPager.startAutoScroll();
        startService();
    }

    public void startService() {
        mTimer = new Timer();

        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
//                Intent intent = new Intent(HttpAction.TIMER_RELOAD);
//                sendBroadcast(intent);
            }
        }, 200, 10 * 60 * 1000);
    }

}
