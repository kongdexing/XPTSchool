package com.shuhai.anfang.report.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.common.VolleyHttpResult;
import com.android.volley.common.VolleyHttpService;
import com.android.volley.common.VolleyRequestListener;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shuhai.anfang.report.R;
import com.shuhai.anfang.report.http.HttpAction;
import com.shuhai.anfang.report.module.AppUseCount;
import com.shuhai.anfang.report.module.BarProvinceInfo;
import com.shuhai.anfang.report.module.LineAttendance;
import com.shuhai.anfang.report.module.UserCount;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dexing on 2017/9/25 0025.
 * No1
 */

public class Report3View extends BaseReportView {

    private TextView txtTeacherSum, txtTeacherOnline, txtParentSum, txtParentOnline;
    private LineChart lineChart1, lineChart2;

    public Report3View(Context context) {
        this(context, null);
    }

    public Report3View(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_report3, this, true);
        initView();
    }

    private void initView() {
        txtTeacherSum = (TextView) findViewById(R.id.txtTeacherSum);
        txtTeacherOnline = (TextView) findViewById(R.id.txtTeacherOnline);
        txtParentSum = (TextView) findViewById(R.id.txtParentSum);
        txtParentOnline = (TextView) findViewById(R.id.txtParentOnline);
        getUserCount();

        lineChart1 = (LineChart) findViewById(R.id.chart1);
        lineChart2 = (LineChart) findViewById(R.id.chart2);
        getAppUseCount();

    }

    private void getUserCount() {
        VolleyHttpService.getInstance().sendGetRequest(HttpAction.USER_COUNT_INFO, new VolleyRequestListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onResponse(VolleyHttpResult volleyHttpResult) {
                switch (volleyHttpResult.getStatus()) {
                    case HttpAction.SUCCESS:
                        try {
                            Gson gson = new Gson();
                            UserCount userCount = gson.fromJson(volleyHttpResult.getData().toString(),
                                    new TypeToken<UserCount>() {
                                    }.getType());
                            txtTeacherSum.setText(userCount.getTeacher() + "");
                            txtTeacherOnline.setText(userCount.getTeacher_online() + "");
                            txtParentSum.setText(userCount.getParents() + "");
                            txtParentOnline.setText(userCount.getParents_online() + "");
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

    private void getAppUseCount() {
        VolleyHttpService.getInstance().sendGetRequest(HttpAction.APP_COUNT_INFO, new VolleyRequestListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onResponse(VolleyHttpResult volleyHttpResult) {
                switch (volleyHttpResult.getStatus()) {
                    case HttpAction.SUCCESS:
                        try {
                            Gson gson = new Gson();
                            AppUseCount appUseCount = gson.fromJson(volleyHttpResult.getData().toString(),
                                    new TypeToken<AppUseCount>() {
                                    }.getType());
                            setAppUseCountChart(lineChart1, appUseCount.getIOSteacher(), appUseCount.getAndroidteacher());
                            setAppUseCountChart(lineChart2, appUseCount.getIOSparents(), appUseCount.getAndroidparents());
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

    private void setAppUseCountChart(LineChart mChart, List<Integer> IOSUser, List<Integer> AndroidUser) {
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
        Legend l = mChart.getLegend();
        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
        l.setTypeface(mTfLight);
        l.setFormLineWidth(2f);
        l.setTextSize(5f);
        l.setTextColor(Color.WHITE);

        List<LegendEntry> legendEntries = new ArrayList<>();
        LegendEntry entry1 = new LegendEntry();
        entry1.label = "苹果端";
        entry1.formColor = getResources().getColor(R.color.color_line_chart_ios);

        LegendEntry entry2 = new LegendEntry();
        entry2.label = "安卓端";
        entry2.formColor = getResources().getColor(R.color.color_line_chart);

        legendEntries.add(entry1);
        legendEntries.add(entry2);
        l.setCustom(legendEntries);

        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setTypeface(mTfLight);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(5.0f);
        xAxis.setLabelCount(24);
        xAxis.setAxisLineWidth(0.5f);
        xAxis.setAxisLineColor(Color.WHITE);
        xAxis.setTextColor(getResources().getColor(R.color.color_x_axis));
        xAxis.setDrawGridLines(false);

        int hourCount = 24;
        int maxYVal = 0;
        int[] yVals1 = new int[hourCount];
        for (int i = 0; i < hourCount; i++) {
            int val = IOSUser.get(i);
            if (val > maxYVal) {
                maxYVal = val;
            }
            yVals1[i] = val;
        }

        int[] yVals2 = new int[hourCount];
        for (int i = 0; i < hourCount; i++) {
            int val = AndroidUser.get(i);
            if (val > maxYVal) {
                maxYVal = val;
            }
            yVals2[i] = val;
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
        leftAxis.setTextSize(5f);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(true);

        mChart.getAxisRight().setEnabled(false);

        setData(mChart, hourCount, yVals1, yVals2);
    }

    private void setData(LineChart mChart, int count, int[] iosUser, int[] androidUser) {

        ArrayList<Entry> yVals1 = new ArrayList<Entry>();

        for (int i = 0; i < count; i++) {
            yVals1.add(new Entry(i, iosUser[i]));
        }

        ArrayList<Entry> yVals2 = new ArrayList<Entry>();

        for (int i = 0; i < count; i++) {
            yVals2.add(new Entry(i, androidUser[i]));
        }

        LineDataSet set1, set2;

        // create a dataset and give it a type
        set1 = new LineDataSet(yVals1, "");
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        set1.setColor(getResources().getColor(R.color.color_line_chart_ios));
        set1.setCircleColor(getResources().getColor(R.color.color_line_dot_ios));
        set1.setLineWidth(0.5f);
        set1.setCircleRadius(1f);

        set2 = new LineDataSet(yVals2, "");
        set2.setAxisDependency(YAxis.AxisDependency.LEFT);
        set2.setColor(getResources().getColor(R.color.color_line_chart));
        set2.setCircleColor(getResources().getColor(R.color.color_line_dot));
        set2.setLineWidth(0.5f);
        set2.setCircleRadius(1f);

        // create a data object with the datasets
        LineData data = new LineData(set1, set2);
//            data.setValueTextColor(Color.WHITE);
//            data.setValueTypeface(mTfLight);
//            data.setValueTextSize(4f);
//            data.setValueFormatter(new LargeValueFormatter());
        data.setDrawValues(false);
        // set data
        mChart.setData(data);
    }

}
