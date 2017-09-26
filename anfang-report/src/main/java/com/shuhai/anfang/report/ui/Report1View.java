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
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shuhai.anfang.report.R;
import com.shuhai.anfang.report.http.HttpAction;
import com.shuhai.anfang.report.module.PieAllStuCard;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dexing on 2017/9/25 0025.
 * No1
 */

public class Report1View extends LinearLayout {

    private PieChart mChart;
    private TextView txtAllCard;
    private BarChart chart_bar1, chart_bar2, chart_bar3;
    private Typeface mTfRegular;
    private Typeface mTfLight;
    private String TAG = Report1View.class.getSimpleName();

    public Report1View(Context context) {
        this(context, null);
    }

    public Report1View(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_stucard, this, true);

        mTfRegular = Typeface.createFromAsset(this.getContext().getAssets(), "OpenSans-Regular.ttf");
        mTfLight = Typeface.createFromAsset(this.getContext().getAssets(), "OpenSans-Light.ttf");

//        mChart.setDragDecelerationFrictionCoef(0.95f);
        initPieView();
        initBarView();
    }

    private void initPieView() {
        txtAllCard = (TextView) findViewById(R.id.txtAllCard);

        mChart = (PieChart) findViewById(R.id.chart1);
        mChart.setUsePercentValues(true);
        mChart.getDescription().setEnabled(false);
        mChart.setCenterTextTypeface(mTfLight);
//        mChart.setCenterText(generateCenterSpannableText());

        //外部间距
        mChart.setExtraOffsets(20.f, 0.f, 20.f, 0.f);

        //绘制中心圆
        mChart.setDrawHoleEnabled(true);
        mChart.setHoleColor(getResources().getColor(R.color.color_hole));

//        mChart.setTransparentCircleColor(Color.WHITE);
//        mChart.setTransparentCircleAlpha(110);

        //中心园半径
        mChart.setHoleRadius(58f);
        mChart.setTransparentCircleRadius(61f);

        mChart.setDrawCenterText(true);
        mChart.setCenterText("学生卡使用统计");

        mChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        mChart.setRotationEnabled(true);
        mChart.setHighlightPerTapEnabled(true);

        // mChart.setUnit(" €");
        // mChart.setDrawUnitsInChart(true);

        // add a selection listener
//        mChart.setOnChartValueSelectedListener(this);

        getPieData();
//        setPieData(2, 100);

//        mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        // mChart.spin(2000, 0, 360);

        Legend l = mChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setEnabled(false);
    }

    private void getPieData() {
        VolleyHttpService.getInstance().sendGetRequest(HttpAction.STU_CARD_PIE, new VolleyRequestListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onResponse(VolleyHttpResult volleyHttpResult) {
                switch (volleyHttpResult.getStatus()) {
                    case HttpAction.SUCCESS:
                        Gson gson = new Gson();
                        PieAllStuCard pieAllStuCard = gson.fromJson(volleyHttpResult.getData().toString(),
                                new TypeToken<PieAllStuCard>() {
                                }.getType());
                        setPieData(pieAllStuCard);
                        Log.i(TAG, "onResponse: " + pieAllStuCard.toString());
                        break;
                }
            }

            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
    }

    private void setPieData(PieAllStuCard pieAllStuCard) {

        txtAllCard.setText(pieAllStuCard.getTotal() + "");
        float mult = 100;

        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.

        List<PieAllStuCard.StuCardInfo> cardUsed = pieAllStuCard.getInfo();
        for (int i = 0; i < cardUsed.size(); i++) {
            entries.add(new PieEntry((float) cardUsed.get(i).getValue() / pieAllStuCard.getTotal(),
                    cardUsed.get(i).getName() + "\n" + cardUsed.get(i).getValue()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "学生卡总量统计");
//        dataSet.setSliceSpace(0f);
//        dataSet.setSelectionShift(0f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<Integer>();

        colors.add(getResources().getColor(R.color.color_used));
        colors.add(getResources().getColor(R.color.color_unused));

        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);

        dataSet.setValueLinePart1OffsetPercentage(70.f);
        dataSet.setValueLinePart1Length(0.3f);
        dataSet.setValueLinePart2Length(0.5f);
        dataSet.setValueLineColor(Color.WHITE);
        //dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.WHITE);
        data.setValueTypeface(mTfRegular);
        mChart.setData(data);

        // undo all highlights
        mChart.highlightValues(null);
        mChart.invalidate();
    }

    private void initBarView() {
        chart_bar1 = (BarChart) findViewById(R.id.chart_bar1);
        chart_bar2 = (BarChart) findViewById(R.id.chart_bar2);
        chart_bar3 = (BarChart) findViewById(R.id.chart_bar3);

        chart_bar1.getDescription().setEnabled(false);

        // scaling can now only be done on x- and y-axis separately
        chart_bar1.setPinchZoom(false);

        chart_bar1.setDrawBarShadow(false);

        chart_bar1.setDrawGridBackground(false);

        //图例
        Legend l = chart_bar1.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(true);
        l.setTypeface(mTfLight);
        l.setYOffset(0f);
        l.setXOffset(10f);
        l.setYEntrySpace(0f);
        l.setTextSize(8f);

        XAxis xAxis = chart_bar1.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTypeface(mTfLight);
        xAxis.setGranularity(1.0f);  //粒度
        xAxis.setDrawAxisLine(true);
        xAxis.setTextSize(3.0f);
        xAxis.setDrawGridLines(true);
        xAxis.setDrawLabels(true);
        xAxis.setCenterAxisLabels(true);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return String.valueOf((int) value);
            }
        });

        YAxis leftAxis = chart_bar1.getAxisLeft();
        leftAxis.setTypeface(mTfLight);
        leftAxis.setValueFormatter(new LargeValueFormatter());
        leftAxis.setDrawGridLines(false);
        leftAxis.setSpaceTop(35f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        chart_bar1.getAxisRight().setEnabled(false);
        getBarData();
        setBarData();
    }

    private void getBarData() {
        VolleyHttpService.getInstance().sendGetRequest(HttpAction.STU_CARD_BAR, new VolleyRequestListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onResponse(VolleyHttpResult volleyHttpResult) {
                switch (volleyHttpResult.getStatus()) {
                    case HttpAction.SUCCESS:
//                        Gson gson = new Gson();
//                        PieAllStuCard pieAllStuCard = gson.fromJson(volleyHttpResult.getData().toString(),
//                                new TypeToken<PieAllStuCard>() {
//                                }.getType());
//                        setPieData(pieAllStuCard);
//                        Log.i(TAG, "onResponse: " + pieAllStuCard.toString());
                        break;
                }
            }

            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
    }

    private void setBarData() {

        float groupSpace = 0.08f;
        float barSpace = 0.06f; // x2 DataSet
        float barWidth = 0.4f; // x2 DataSet
        // (0.4 + 0.06) * 2 + 0.08 = 1.00 -> interval per "group"

        int groupCount = 11 + 1;
        int startYear = 1980;
        int endYear = startYear + groupCount;

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
        ArrayList<BarEntry> yVals2 = new ArrayList<BarEntry>();

        float randomMultiplier = 100 * 100000f;

        for (int i = startYear; i < endYear; i++) {
            yVals1.add(new BarEntry(i, (float) (Math.random() * randomMultiplier)));
            yVals2.add(new BarEntry(i, (float) (Math.random() * randomMultiplier)));
        }

        BarDataSet set1, set2;

        if (chart_bar1.getData() != null && chart_bar1.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) chart_bar1.getData().getDataSetByIndex(0);
            set2 = (BarDataSet) chart_bar1.getData().getDataSetByIndex(1);
            set1.setValues(yVals1);
            set2.setValues(yVals2);
            chart_bar1.getData().notifyDataChanged();
            chart_bar1.notifyDataSetChanged();

        } else {
            // create 2 DataSets
            set1 = new BarDataSet(yVals1, "Company A");
            set1.setColor(Color.rgb(104, 241, 175));
            set2 = new BarDataSet(yVals2, "Company B");
            set2.setColor(Color.rgb(164, 228, 251));

            BarData data = new BarData(set1, set2);
            data.setValueFormatter(new LargeValueFormatter());
            data.setValueTypeface(mTfLight);

            chart_bar1.setData(data);
        }

        // specify the width each bar should have
        chart_bar1.getBarData().setBarWidth(barWidth);

        // restrict the x-axis range
        chart_bar1.getXAxis().setAxisMinimum(startYear);

        // barData.getGroupWith(...) is a helper that calculates the width each group needs based on the provided parameters
//        chart_bar1.getXAxis().setAxisMaximum(startYear + chart_bar1.getBarData().getGroupWidth(groupSpace, barSpace) * groupCount);
        chart_bar1.getXAxis().setAxisMaximum(endYear);
        chart_bar1.groupBars(startYear, groupSpace, barSpace);

        chart_bar1.invalidate();

    }
}
