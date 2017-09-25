package com.shuhai.anfang.report;

import android.app.Application;
import android.support.v7.app.AppCompatActivity;

import com.android.volley.common.VolleyHttpService;

/**
 * Created by dexing on 2017/9/25 0025.
 * No1
 */

public class ReportApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        VolleyHttpService.init(this);
    }
}
