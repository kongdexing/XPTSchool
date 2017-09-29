package com.shuhai.anfang.report.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.github.mikephil.charting.charts.Chart;
import com.shuhai.anfang.report.http.HttpAction;

/**
 * Created by dexing on 2017/9/27 0027.
 * No1
 */

public class BaseReportView extends LinearLayout {

    public Typeface mTfRegular;
    public Typeface mTfLight;
    public String TAG = "";

    public BaseReportView(Context context) {
        this(context, null);
    }

    public BaseReportView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TAG = this.getClass().getSimpleName();
        mTfRegular = Typeface.createFromAsset(this.getContext().getAssets(), "OpenSans-Regular.ttf");
        mTfLight = Typeface.createFromAsset(this.getContext().getAssets(), "OpenSans-Light.ttf");

        context.registerReceiver(mReceiver, new IntentFilter(HttpAction.TIMER_RELOAD));
    }

    public void loadData() {

    }

    public void animationReportXY() {

    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(HttpAction.TIMER_RELOAD)) {
                loadData();
            }
        }
    };

}
