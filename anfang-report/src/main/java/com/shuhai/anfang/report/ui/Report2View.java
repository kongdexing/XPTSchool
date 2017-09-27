package com.shuhai.anfang.report.ui;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
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
import com.shuhai.anfang.report.R;
import com.shuhai.anfang.report.http.HttpAction;

import java.util.ArrayList;

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

    }

    private void initView() {
        lineChart1 = (LineChart) findViewById(R.id.chart1);
        lineChart2 = (LineChart) findViewById(R.id.chart2);
        lineChart3 = (LineChart) findViewById(R.id.chart3);

        // no description text
        lineChart1.getDescription().setEnabled(false);

        // enable touch gestures
        lineChart1.setTouchEnabled(true);

        lineChart1.setDragDecelerationFrictionCoef(0.9f);

        // enable scaling and dragging
        lineChart1.setDragEnabled(true);
        lineChart1.setScaleEnabled(true);
        lineChart1.setDrawGridBackground(false);
        lineChart1.setHighlightPerDragEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        lineChart1.setPinchZoom(true);

        // add data
        setData(24, 30);

        lineChart1.animateX(2500);

        // get the legend (only possible after setting data)
        lineChart1.getLegend().setEnabled(false);

        XAxis xAxis = lineChart1.getXAxis();
        xAxis.setTypeface(mTfLight);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(3f);
        xAxis.setLabelCount(24);
        xAxis.setAxisLineWidth(0.5f);
        xAxis.setAxisLineColor(Color.WHITE);
        xAxis.setTextColor(getResources().getColor(R.color.color_block_line));
        xAxis.setDrawGridLines(false);

        YAxis leftAxis = lineChart1.getAxisLeft();
        leftAxis.setTypeface(mTfLight);
        leftAxis.setTextColor(ColorTemplate.getHoloBlue());
        leftAxis.setAxisLineColor(Color.WHITE);
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setAxisLineWidth(0.5f);
        leftAxis.setAxisMaximum(100f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setLabelCount(4);
        leftAxis.setTextSize(3f);
        leftAxis.setGridLineWidth(0.5f);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(true);

        lineChart1.getAxisRight().setEnabled(false);
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
                        
                        break;
                }
            }

            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
    }

    private void setData(int count, float range) {

        ArrayList<Entry> yVals1 = new ArrayList<Entry>();

        for (int i = 0; i < count; i++) {
            float mult = range / 2f;
            float val = (float) (Math.random() * mult) + 50;
            yVals1.add(new Entry(i, val));
        }

        ArrayList<Entry> yVals2 = new ArrayList<Entry>();

        for (int i = 0; i < count - 1; i++) {
            float mult = range;
            int val = (int) (Math.random() * mult) + 450;
            yVals2.add(new Entry(i, val));
//            if(i == 10) {
//                yVals2.add(new Entry(i, val + 50));
//            }
        }

        ArrayList<Entry> yVals3 = new ArrayList<Entry>();

        for (int i = 0; i < count; i++) {
            float mult = range;
            float val = (float) (Math.random() * mult) + 500;
            yVals3.add(new Entry(i, val));
        }

        LineDataSet set1, set2, set3;

        if (lineChart1.getData() != null &&
                lineChart1.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) lineChart1.getData().getDataSetByIndex(0);
            set2 = (LineDataSet) lineChart1.getData().getDataSetByIndex(1);
            set3 = (LineDataSet) lineChart1.getData().getDataSetByIndex(2);
            set1.setValues(yVals1);
            set2.setValues(yVals2);
            set3.setValues(yVals3);
            lineChart1.getData().notifyDataChanged();
            lineChart1.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(yVals1, "DataSet 1");

            set1.setAxisDependency(YAxis.AxisDependency.LEFT);
            set1.setColor(ColorTemplate.getHoloBlue());
            set1.setCircleColor(Color.WHITE);
            set1.setLineWidth(1f);
            set1.setCircleRadius(1f);
            set1.setFillAlpha(65);
            set1.setValueTextSize(3f);
            set1.setFillColor(ColorTemplate.getHoloBlue());
            set1.setHighLightColor(Color.rgb(244, 117, 117));
            set1.setDrawCircleHole(false);
            //set1.setFillFormatter(new MyFillFormatter(0f));
            //set1.setDrawHorizontalHighlightIndicator(false);
            //set1.setVisible(false);
            //set1.setCircleHoleColor(Color.WHITE);

            // create a data object with the datasets
            LineData data = new LineData(set1);
            data.setValueTextColor(Color.WHITE);
            data.setValueTextSize(3f);

            // set data
            lineChart1.setData(data);
        }
    }

}
