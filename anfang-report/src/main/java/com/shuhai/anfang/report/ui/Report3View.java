package com.shuhai.anfang.report.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.shuhai.anfang.report.R;

/**
 * Created by dexing on 2017/9/25 0025.
 * No1
 */

public class Report3View extends LinearLayout {

    private PieChart mChart;
    private TextView txtAllCard;
    private Typeface tf;
    private String TAG = Report1View.class.getSimpleName();

    public Report3View(Context context) {
        this(context, null);
    }

    public Report3View(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_report3, this, true);

    }


}
