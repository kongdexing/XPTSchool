package com.shuhai.anfang.report;

import android.app.Application;
import android.support.v7.app.AppCompatActivity;

import com.android.volley.common.VolleyHttpService;
import com.baidu.mapapi.SDKInitializer;

/**
 * Created by dexing on 2017/9/25 0025.
 * No1
 */

public class ReportApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // 在使用 Baidu SDK 各组间之前初始化 context 信息，传入 ApplicationContext
        SDKInitializer.initialize(this);

        VolleyHttpService.init(this);
    }
}
