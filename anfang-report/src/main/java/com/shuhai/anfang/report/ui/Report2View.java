package com.shuhai.anfang.report.ui;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ZoomControls;

import com.android.volley.VolleyError;
import com.android.volley.common.VolleyHttpResult;
import com.android.volley.common.VolleyHttpService;
import com.android.volley.common.VolleyRequestListener;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.HeatMap;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
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
import com.shuhai.anfang.report.module.LineAttendance;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by dexing on 2017/9/25 0025.
 * No1
 */

public class Report2View extends BaseReportView {

    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private HeatMap heatmap;
    private LineChart[] lineCharts;

    public Report2View(Context context) {
        this(context, null);
    }

    public Report2View(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_report2, this, true);
        initView();
    }

    private void initView() {
        mMapView = (MapView) findViewById(R.id.mapview);
        // 隐藏缩放控件
        mMapView.showZoomControls(false);
        //地图上比例尺
        mMapView.showScaleControl(false);
        // 隐藏logo
        View child = mMapView.getChildAt(1);
        if (child != null && (child instanceof ImageView || child instanceof ZoomControls)) {
            child.setVisibility(View.INVISIBLE);
        }

        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(5));

        lineCharts = new LineChart[3];
        lineCharts[0] = (LineChart) findViewById(R.id.chart1);
        lineCharts[1] = (LineChart) findViewById(R.id.chart2);
        lineCharts[2] = (LineChart) findViewById(R.id.chart3);

        loadData();
    }

    @Override
    public void loadData() {
        super.loadData();
        getMapLocation();
        getAttendance();
    }

    private void addHeatMap() {

    }

    public void getMapLocation() {
        VolleyHttpService.getInstance().sendGetRequest(HttpAction.LOCATION_MAP, new VolleyRequestListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onResponse(VolleyHttpResult volleyHttpResult) {
                switch (volleyHttpResult.getStatus()) {
                    case HttpAction.SUCCESS:
                        try {
                            final List<LatLng> list = new ArrayList<LatLng>();
                            LatLngBounds.Builder bounds = new LatLngBounds.Builder();

                            JSONArray array1 = new JSONArray(volleyHttpResult.getData().toString());
                            int length1 = array1.length();
                            for (int i = 0; i < length1; i++) {
                                JSONArray array2 = array1.getJSONArray(i);
                                for (int j = 0; j < array2.length(); j++) {
                                    double lat = array2.getDouble(1);
                                    double lng = array2.getDouble(0);
                                    LatLng latLng = new LatLng(lat, lng);

                                    CoordinateConverter converter = new CoordinateConverter();
                                    converter.from(CoordinateConverter.CoordType.GPS);
                                    // sourceLatLng待转换坐标
                                    converter.coord(latLng);
                                    LatLng newLatLng = converter.convert();
                                    list.add(newLatLng);
                                }
                            }

                            //中国极点坐标
                            bounds.include(new LatLng(48.38288, 135.147833));  //东
                            bounds.include(new LatLng(39.359185, 73.36971));  //西
                            bounds.include(new LatLng(2.542959, 110.753018));   //南
                            bounds.include(new LatLng(53.644295, 122.30652));   //北

                            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngBounds(bounds.build()));

                            final Handler h = new Handler() {
                                @Override
                                public void handleMessage(Message msg) {
                                    super.handleMessage(msg);
                                    mBaiduMap.addHeatMap(heatmap);
                                }
                            };
                            new Thread() {
                                @Override
                                public void run() {
                                    super.run();
//                                    List<LatLng> data = getLocations();
                                    heatmap = new HeatMap.Builder().data(list).build();
                                    h.sendEmptyMessage(0);
                                }
                            }.start();
                        } catch (Exception ex) {

                        }
                        break;
                }
            }

            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
    }

    private List<LatLng> getLocations() {
        List<LatLng> list = new ArrayList<LatLng>();
        InputStream inputStream = getResources().openRawResource(R.raw.locations);
        String json = new Scanner(inputStream).useDelimiter("\\A").next();
        JSONArray array;
        try {
            array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                double lat = object.getDouble("lat");
                double lng = object.getDouble("lng");
                list.add(new LatLng(lat, lng));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
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
                            setLineChartData(lineCharts[1], lineAttendance.getSignin());
                            setLineChartData(lineCharts[2], lineAttendance.getSignout());
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

    private void setLineChartData(LineChart mChart, List<Integer> values) {
        // no description text
        mChart.getDescription().setEnabled(false);
        mChart.setTouchEnabled(false);

        mChart.setDragDecelerationFrictionCoef(0.9f);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        // get the legend (only possible after setting data)
        mChart.getLegend().setEnabled(false);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setTypeface(mTfLight);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(8f);
        xAxis.setLabelCount(24);
        xAxis.setAxisLineWidth(0.5f);
        xAxis.setAxisLineColor(Color.WHITE);
        xAxis.setTextColor(getResources().getColor(R.color.color_x_axis));
        xAxis.setDrawGridLines(false);

        int hourCount = 24;
        int maxYVal = 0;
        int[] yVals = new int[hourCount];
        for (int i = 0; i < values.size(); i++) {
            int val = values.get(i);
            if (val > maxYVal) {
                maxYVal = val;
            }
            yVals[i] = val;
        }

        maxYVal += maxYVal * 0.2;

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTypeface(mTfLight);
        leftAxis.setTextColor(ColorTemplate.getHoloBlue());
        leftAxis.setAxisLineColor(getResources().getColor(R.color.color_y_axis));
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setAxisLineWidth(0.8f);
        leftAxis.setAxisMaximum((int) maxYVal);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setLabelCount(4);
        leftAxis.setTextSize(8f);
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
            set1.setLineWidth(1.0f);
            set1.setCircleRadius(2f);
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
        mChart.animateXY(1000, 1000);
//        animationReportXY();
    }

    @Override
    public void animationReportXY() {
        super.animationReportXY();
//        Log.i(TAG, "animationReportXY: report2");
//        for (int i = 0; i < lineCharts.length; i++) {
//            LineChart lineChart = lineCharts[i];
//            if (lineChart.getData() != null && lineChart.getData().getDataSetCount() > 0)
//                lineChart.animateXY(1000, 1000);
//        }
    }
}
