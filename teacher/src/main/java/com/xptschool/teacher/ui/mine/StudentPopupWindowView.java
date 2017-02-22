package com.xptschool.teacher.ui.mine;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.viewpagerindicator.CirclePageIndicator;
import com.xptschool.teacher.R;
import com.xptschool.teacher.model.BeanClass;
import com.xptschool.teacher.model.GreenDaoHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/10.
 */

public class StudentPopupWindowView extends LinearLayout {

    View view;
    LinearLayout llStudent;

    CirclePageIndicator indicator;
    ViewPager viewPager;
    private ArrayList<View> mViews;

    private List<BeanClass> myClass = new ArrayList<>();

    StudentAdapter adapter;
    private Context mContext;

    float popupWindowMaxHeight;

    public StudentPopupWindowView(Context context) {
        this(context, null);
    }

    public StudentPopupWindowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        view = LayoutInflater.from(mContext).inflate(R.layout.view_student_pop, this, true);
        llStudent = (LinearLayout) view.findViewById(R.id.llStudent);
        indicator = (CirclePageIndicator) view.findViewById(R.id.indicator);
        viewPager = (ViewPager) view.findViewById(R.id.studentPager);
        initData();
    }

    public void initData() {
        myClass = GreenDaoHelper.getInstance().getAllClass();
        mViews = new ArrayList<>();
        for (int i = 0; i < myClass.size(); i++) {
            AllStudentsView view = new AllStudentsView(mContext);
            view.getStudentByClassId(myClass.get(i));
            mViews.add(view);
        }
        viewPager.setAdapter(new StudentFragmentAdapter());
        indicator.setViewPager(viewPager);
        indicator.setCurrentItem(0);
    }

    public void setMyGridViewClickListener(StudentAdapter.MyGridViewClickListener listener){
        for (int i = 0; i < mViews.size(); i++) {
            ((AllStudentsView)mViews.get(i)).setGrdvStudentOnClickListener(listener);
        }
    }

    class StudentFragmentAdapter extends PagerAdapter {
        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView(mViews.get(arg1));
        }

        @Override
        public void finishUpdate(View arg0) {

        }

        @Override
        public int getCount() {
            return mViews.size();
        }

        @Override
        public Object instantiateItem(View arg0, int arg1) {
            ((ViewPager) arg0).addView(mViews.get(arg1));
            return mViews.get(arg1);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View arg0) {

        }

    }

}
