package com.xptschool.teacher.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.viewpagerindicator.CirclePageIndicator;
import com.xptschool.teacher.R;
import com.xptschool.teacher.adapter.MyTopPagerAdapter;
import com.xptschool.teacher.model.BeanBanner;
import com.xptschool.teacher.push.BannerHelper;
import com.xptschool.teacher.ui.alarm.AlarmActivity;
import com.xptschool.teacher.ui.checkin.CheckinActivity;
import com.xptschool.teacher.ui.homework.HomeWorkActivity;
import com.xptschool.teacher.ui.leave.LeaveActivity;
import com.xptschool.teacher.ui.main.MainActivity;
import com.xptschool.teacher.ui.notice.NoticeActivity;
import com.xptschool.teacher.ui.question.QuestionActivity;
import com.xptschool.teacher.ui.score.ScoreActivity;
import com.xptschool.teacher.view.AutoScrollViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class HomeFragment extends BaseFragment {

    @BindView(R.id.viewPagerTop)
    AutoScrollViewPager viewPagerTop;
    @BindView(R.id.indicator)
    CirclePageIndicator indicator;
    @BindView(R.id.tipTitle)
    TextView tipTitle;

    private Unbinder unbinder;
    private MyTopPagerAdapter topAdapter;
    private List<BeanBanner> topBanners = new ArrayList<>();

    public HomeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("reback", "HomeFragment inflateView: ");
        mRootView = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, mRootView);
        initView();
        return mRootView;
    }

    private void initView() {
        Log.i(TAG, "HomeFragment initView: ");
        viewPagerTop.setCycle(true);
        topAdapter = new MyTopPagerAdapter(this.getContext());
        viewPagerTop.setAdapter(topAdapter);
        indicator.setViewPager(viewPagerTop);
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                Log.i(TAG, "onPageScrolled: " + position);
            }

            @Override
            public void onPageSelected(int position) {
                Log.i(TAG, "onPageSelected: banners " + topBanners.size() + "  position: " + position);
                if (topBanners.size() > position) {
                    BeanBanner banner = topBanners.get(position);
                    Log.i(TAG, "onPageSelected: banner type " + banner.getType());
                    if (banner != null) {
                        tipTitle.setText(banner.getTitle());
                        BannerHelper.postShowBanner(banner, "1");
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
//                Log.i(TAG, "onPageScrollStateChanged: ");
            }
        });
        indicator.setCurrentItem(0);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (viewPagerTop != null) {
            viewPagerTop.startAutoScroll();
        }
        Log.i(TAG, "onResume: ");
    }

    @OnClick({R.id.home_homework, R.id.home_alarm, R.id.home_checkin, R.id.home_score,
            R.id.home_leave, R.id.home_location, R.id.home_notice, R.id.home_question})
    void buttonOnClick(View view) {
        switch (view.getId()) {
            case R.id.home_homework:
                startActivity(new Intent(getContext(), HomeWorkActivity.class));
                break;
            case R.id.home_notice:
                startActivity(new Intent(getContext(), NoticeActivity.class));
                break;
            case R.id.home_question:
                startActivity(new Intent(getContext(), QuestionActivity.class));
                break;
            case R.id.home_location:
                ((MainActivity) this.getActivity()).resetNavBar();
                ((MainActivity) this.getActivity()).showMap();
                break;
            case R.id.home_score:
                startActivity(new Intent(getContext(), ScoreActivity.class));
                break;
            case R.id.home_checkin:
                startActivity(new Intent(getContext(), CheckinActivity.class));
                break;
            case R.id.home_leave:
                startActivity(new Intent(getContext(), LeaveActivity.class));
                break;
            case R.id.home_alarm:
                startActivity(new Intent(getContext(), AlarmActivity.class));
                break;
        }
    }

    @Override
    protected void initData() {
        Log.i("reback", "HomeFragment initData: ");
    }

    public void reloadTopFragment(List<BeanBanner> banners) {
        Log.i(TAG, "reloadTopFragment: " + banners.size());
        if (topAdapter != null) {
            topBanners = banners;
            BeanBanner banner = banners.get(0);
            if (banner != null) {
                tipTitle.setText(banner.getTitle());
            }
            topAdapter.reloadData(banners);
        }
        if (viewPagerTop != null) {
            viewPagerTop.startAutoScroll();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        viewPagerTop.stopAutoScroll();
        Log.i(TAG, "onPause: ");
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

}
