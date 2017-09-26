package com.shuhai.anfang.report.custom;

import android.util.Log;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

/**
 * Created by philipp on 02/06/16.
 */
public class MyAxisValueFormatter implements IAxisValueFormatter {

    private String TAG = MyAxisValueFormatter.class.getSimpleName();
    protected String[] mValues = new String[]{};

    public MyAxisValueFormatter(String[] values) {
        this.mValues = values;
        Log.i(TAG, "MyAxisValueFormatter: " + values[0]);
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        Log.i(TAG, "getFormattedValue: " + value);
        int v = (int) value;
        if (v >= mValues.length) {
            v = mValues.length - 1;
        }
        return mValues[v];
    }

}
