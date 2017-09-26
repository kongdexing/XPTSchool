package com.shuhai.anfang.report.custom;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.shuhai.anfang.report.ui.Report1View;
import com.shuhai.anfang.report.ui.Report2View;
import com.shuhai.anfang.report.ui.Report3View;

/**
 * Created by dexing on 2017/1/13.
 * No1
 */

public class MyPagerAdapter extends PagerAdapter {

    private String TAG = MyPagerAdapter.class.getSimpleName();
    private Context mContext;

    public MyPagerAdapter(Context context) {
        super();
        mContext = context;
    }

    public void loadData() {
        Log.i(TAG, "loadData: ");
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == o;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Log.i(TAG, "instantiateItem: "+position);
        View reportView = null;
        if (position == 0) {
            reportView = new Report1View(mContext);
            container.addView(reportView);
        } else if (position == 1) {
            reportView = new Report2View(mContext);
            container.addView(reportView);
        } else if (position == 2) {
            reportView = new Report3View(mContext);
            container.addView(reportView);
        }
        return reportView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}