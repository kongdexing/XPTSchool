package com.xptschool.parent.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

import com.umeng.analytics.MobclickAgent;

/**
 * Created by Administrator on 2016/10/18 0018.
 */
public abstract class BaseFragment extends Fragment {

    protected Context mContext;
    public static String TAG;
    public View mRootView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        Log.i(TAG, "onCreate: ");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        TAG = getClass().getSimpleName();
        initData();
    }

    //初始化数据
    protected abstract void initData();

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG);
    }

}
