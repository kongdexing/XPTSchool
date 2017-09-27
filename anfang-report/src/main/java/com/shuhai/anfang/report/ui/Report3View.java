package com.shuhai.anfang.report.ui;

import android.content.Context;
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
import com.github.mikephil.charting.charts.PieChart;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shuhai.anfang.report.R;
import com.shuhai.anfang.report.http.HttpAction;
import com.shuhai.anfang.report.module.BarProvinceInfo;
import com.shuhai.anfang.report.module.LineAttendance;
import com.shuhai.anfang.report.module.UserCount;

/**
 * Created by dexing on 2017/9/25 0025.
 * No1
 */

public class Report3View extends BaseReportView {

    private TextView txtTeacherSum, txtTeacherOnline, txtParentSum, txtParentOnline;

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


}
