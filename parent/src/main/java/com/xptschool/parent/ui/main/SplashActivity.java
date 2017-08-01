package com.xptschool.parent.ui.main;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;

import com.viewpagerindicator.CirclePageIndicator;
import com.xptschool.parent.R;
import com.xptschool.parent.adapter.SplashAdapter;
import com.xptschool.parent.common.SharedPreferencesUtil;

import butterknife.BindView;
import butterknife.OnClick;

public class SplashActivity extends BaseActivity {

    @BindView(R.id.pager)
    ViewPager pager;

    @BindView(R.id.indicator)
    CirclePageIndicator indicator;

    @BindView(R.id.btnEnter)
    LinearLayout btnEnter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        showActionBar(false);

        SharedPreferencesUtil.saveData(this, SharedPreferencesUtil.KEY_SPLASH_INIT, "1");

        SplashAdapter adapter = new SplashAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
//        indicator.setViewPager(pager);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == 2) {
                    btnEnter.setVisibility(View.VISIBLE);
                    indicator.setVisibility(View.GONE);
                } else {
                    btnEnter.setVisibility(View.GONE);
                    indicator.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @OnClick({R.id.btnEnter})
    void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.btnEnter:
                finish();
                break;
        }
    }

}
