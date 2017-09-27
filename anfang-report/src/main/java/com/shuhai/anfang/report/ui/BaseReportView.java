package com.shuhai.anfang.report.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by dexing on 2017/9/27 0027.
 * No1
 */

public class BaseReportView extends LinearLayout {

    public Typeface mTfRegular;
    public Typeface mTfLight;

    public BaseReportView(Context context) {
        this(context, null);
    }

    public BaseReportView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mTfRegular = Typeface.createFromAsset(this.getContext().getAssets(), "OpenSans-Regular.ttf");
        mTfLight = Typeface.createFromAsset(this.getContext().getAssets(), "OpenSans-Light.ttf");
    }
}
