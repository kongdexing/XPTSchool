package com.xptschool.parent.ui.course;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.viewpagerindicator.TitlePageIndicator;
import com.xptschool.parent.R;
import com.xptschool.parent.model.BeanStudent;
import com.xptschool.parent.model.GreenDaoHelper;
import com.xptschool.parent.ui.main.BaseListActivity;

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
    private List<BeanStudent> beanStudents = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
        setTitle(R.string.mine_course);
        initData();
    }

    private void initData() {
        beanStudents = GreenDaoHelper.getInstance().getStudents();
        FragmentPagerAdapter adapter = new CourseFragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

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
                fragment.setStudent(beanStudents.get(position));
                listFrags.add(position, fragment);
            } else {
                fragment = listFrags.get(position);
            }
            return fragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            BeanStudent student = beanStudents.get(position);
            return student.getName();
        }

        @Override
        public int getCount() {
            Log.i(TAG, "getCount: " + beanStudents.size());
            return beanStudents.size();
        }
    }

}
