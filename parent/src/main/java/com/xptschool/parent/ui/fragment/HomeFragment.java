package com.xptschool.parent.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.viewpagerindicator.CirclePageIndicator;
import com.xptschool.parent.R;
import com.xptschool.parent.adapter.MyTopPagerAdapter;
import com.xptschool.parent.model.BeanBanner;
import com.xptschool.parent.ui.main.MainActivity;
import com.xptschool.parent.ui.alarm.AlarmActivity;
import com.xptschool.parent.ui.checkin.CheckinActivity;
import com.xptschool.parent.ui.homework.HomeWorkActivity;
import com.xptschool.parent.ui.leave.LeaveActivity;
import com.xptschool.parent.ui.notice.NoticeActivity;
import com.xptschool.parent.ui.question.QuestionActivity;
import com.xptschool.parent.ui.score.ScoreActivity;
import com.xptschool.parent.view.AutoScrollViewPager;

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

    private Unbinder unbinder;
    private MyTopPagerAdapter topAdapter;

    public HomeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "HomeFragment inflateView: ");
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
        indicator.setCurrentItem(0);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (viewPagerTop != null) {
            viewPagerTop.startAutoScroll();
        }
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
        Log.i(TAG, "HomeFragment initData: ");
    }

    public void reloadTopFragment(List<BeanBanner> banners) {
        Log.i(TAG, "reloadTopFragment: " + banners.size());
        if (topAdapter != null) {
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
