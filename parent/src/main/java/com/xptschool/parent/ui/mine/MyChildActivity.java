package com.xptschool.parent.ui.mine;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.viewpagerindicator.CirclePageIndicator;
import com.xptschool.parent.R;
import com.xptschool.parent.model.BeanStudent;
import com.xptschool.parent.model.GreenDaoHelper;
import com.xptschool.parent.ui.cardset.ChildFragment;
import com.xptschool.parent.ui.main.BaseListActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 我的孩子
 */
public class MyChildActivity extends BaseListActivity {

    @BindView(R.id.indicator)
    CirclePageIndicator indicator;

    @BindView(R.id.pager)
    ViewPager viewPager;

    private List<ChildFragment> listFrags = new ArrayList<>();
    private List<BeanStudent> beanStudents = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child);
        setTitle(R.string.mine_child);
        initData();
    }

    private void initData() {
        beanStudents = GreenDaoHelper.getInstance().getStudents();
        FragmentPagerAdapter adapter = new ChildFragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        indicator.setViewPager(viewPager);
        indicator.setCurrentItem(0);

        for (int i = 0; i < beanStudents.size(); i++) {
            ChildFragment fragment = new ChildFragment();
            listFrags.add(fragment);
        }
    }

    class ChildFragmentAdapter extends FragmentPagerAdapter {
        public ChildFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            ChildFragment fragment = listFrags.get(position);
            fragment.setStudent(beanStudents.get(position));
            return fragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            BeanStudent student = beanStudents.get(position);
            return student.getStu_name();
        }

        @Override
        public int getCount() {
            return beanStudents.size();
        }
    }
}
