package com.xptschool.teacher.ui.course;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.viewpagerindicator.TitlePageIndicator;
import com.xptschool.teacher.R;
import com.xptschool.teacher.model.BeanClass;
import com.xptschool.teacher.model.GreenDaoHelper;
import com.xptschool.teacher.ui.main.BaseListActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 课程
 */
public class CourseActivity extends BaseListActivity {

    @BindView(R.id.indicator)
    TitlePageIndicator indicator;

    @BindView(R.id.pager)
    ViewPager viewPager;

    private List<CourseFragment> listFrags = new ArrayList<>();
    private List<BeanClass> myClass = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
        setTitle(R.string.mine_course);
        initData();
    }

    private void initData() {
        myClass = GreenDaoHelper.getInstance().getAllClass();
        FragmentPagerAdapter adapter = new CourseFragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        viewPager.setOffscreenPageLimit(myClass.size());

        indicator.setViewPager(viewPager);
        indicator.setCurrentItem(0);

    }

    class CourseFragmentAdapter extends FragmentPagerAdapter {
        public CourseFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Log.i(TAG, "getItem: " + position);
            CourseFragment fragment = null;
            if (position >= listFrags.size()) {
                fragment = new CourseFragment();
                fragment.setClassId(myClass.get(position).getC_id());
                listFrags.add(position, fragment);
            } else {
                fragment = listFrags.get(position);
            }
            return fragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return myClass.get(position).getName().toUpperCase();
        }

        @Override
        public int getCount() {
            Log.i(TAG, "getCount: " + myClass.size());
            return myClass.size();
        }
    }

}
