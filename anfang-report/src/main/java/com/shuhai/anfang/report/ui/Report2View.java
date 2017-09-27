package com.shuhai.anfang.report.ui;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.android.volley.VolleyError;
import com.android.volley.common.VolleyHttpResult;
import com.android.volley.common.VolleyHttpService;
import com.android.volley.common.VolleyRequestListener;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shuhai.anfang.report.R;
import com.shuhai.anfang.report.custom.MyFillFormatter;
import com.shuhai.anfang.report.http.HttpAction;
import com.shuhai.anfang.report.module.BarProvinceInfo;
import com.shuhai.anfang.report.module.LineAttendance;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dexing on 2017/9/25 0025.
 * No1
 */

public class Report2View extends BaseReportView {

    private LineChart lineChart1, lineChart2, lineChart3;

    public Report2View(Context context) {
        this(context, null);
    }

    public Report2View(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_report2, this, true);

        initView();
        getAttendance();
    }

    private void initView() {
        lineChart1 = (LineChart) findViewById(R.id.chart1);
        lineChart2 = (LineChart) findViewById(R.id.chart2);
        lineChart3 = (LineChart) findViewById(R.id.chart3);


    }

    private void getAttendance() {
        VolleyHttpService.getInstance().sendGetRequest(HttpAction.ATTENDANCE_INFO, new VolleyRequestListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onResponse(VolleyHttpResult volleyHttpResult) {
                switch (volleyHttpResult.getStatus()) {
                    case HttpAction.SUCCESS:
                        try {
                            Gson gson = new Gson();
                            LineAttendance lineAttendance = gson.fromJson(volleyHttpResult.getData().toString(),
                                    new TypeToken<LineAttendance>() {
                                    }.getType());
                            setLineChartData(lineChart2, lineAttendance.getSignin());
                            setLineChartData(lineChart3, lineAttendance.getSignout());
//                            splitProvinceData(provinceInfo);
                            Log.i(TAG, "onResponse: " + lineAttendance.toString());
                        } catch (Exception ex) {
                            Log.i(TAG, "onResponse error: " + ex.getMessage());
                        }
                        break;
                }
            }

            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
    }

    private void setLineChartData(LineChart mChart, List<int[]> values) {
        // no description text
        mChart.getDescription().setEnabled(false);

        // enable touch gestures
        mChart.setTouchEnabled(true);

        mChart.setDragDecelerationFrictionCoef(0.9f);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);
        mChart.setHighlightPerDragEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        mChart.animateX(2500);

        // get the legend (only possible after setting data)
        mChart.getLegend().setEnabled(false);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setTypeface(mTfLight);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(2f);
        xAxis.setLabelCount(24);
        xAxis.setAxisLineWidth(0.5f);
        xAxis.setAxisLineColor(Color.WHITE);
        xAxis.setTextColor(getResources().getColor(R.color.color_block_line));
        xAxis.setDrawGridLines(false);

        int hourCount = 24;
        int maxYVal = 0;
        int[] yVals = new int[hourCount];
        for (int i = 0; i < values.size(); i++) {
            int val = values.get(i)[1];
            if (val > maxYVal) {
                maxYVal = val;
            }
            yVals[i] = val;
        }

        maxYVal += maxYVal * 0.2;

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTypeface(mTfLight);
        leftAxis.setTextColor(ColorTemplate.getHoloBlue());
        leftAxis.setAxisLineColor(Color.WHITE);
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setAxisLineWidth(0.5f);
        leftAxis.setAxisMaximum((int) maxYVal);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setLabelCount(4);
        leftAxis.setTextSize(3f);
//        leftAxis.setGridLineWidth(0.5f);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(true);

        mChart.getAxisRight().setEnabled(false);


        setData(mChart, hourCount, yVals);
    }

    private void setData(LineChart mChart, int count, int[] values) {

        ArrayList<Entry> yVals1 = new ArrayList<Entry>();

        for (int i = 0; i < count; i++) {
            yVals1.add(new Entry(i, values[i]));
        }

        LineDataSet set1;

        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) mChart.getData().getDataSetByIndex(0);
            set1.setValues(yVals1);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(yVals1, "");

            set1.setAxisDependency(YAxis.AxisDependency.LEFT);
            set1.setColor(getResources().getColor(R.color.color_line_chart));
            set1.setCircleColor(getResources().getColor(R.color.color_line_dot));
            set1.setLineWidth(0.5f);
            set1.setCircleRadius(1f);
            set1.setFillAlpha(65);
            set1.setValueTextSize(3f);
            set1.setFillColor(ColorTemplate.getHoloBlue());
            set1.setHighLightColor(Color.rgb(244, 117, 117));

            // create a data object with the datasets
            LineData data = new LineData(set1);
//            data.setValueTextColor(Color.WHITE);
//            data.setValueTypeface(mTfLight);
//            data.setValueTextSize(4f);
//            data.setValueFormatter(new LargeValueFormatter());
            data.setDrawValues(false);

            // set data
            mChart.setData(data);
        }
    }

}
