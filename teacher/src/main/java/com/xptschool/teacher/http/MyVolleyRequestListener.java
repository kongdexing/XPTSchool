package com.xptschool.teacher.http;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.common.VolleyHttpResult;
import com.android.volley.common.VolleyRequestListener;
import com.xptschool.teacher.XPTApplication;
import com.xptschool.teacher.common.BroadcastAction;
import com.xptschool.teacher.ui.alarm.AlarmActivity;

/**
 * Created by dexing on 2017/1/5.
 * No1
 */

public class MyVolleyRequestListener implements VolleyRequestListener {

    private String TAG = MyVolleyRequestListener.class.getSimpleName();

    @Override
    public void onStart() {
        Log.i(TAG, "onStart: ");
    }

    @Override
    public void onResponse(VolleyHttpResult volleyHttpResult) {
        Log.i(TAG, "onResponse: ");
        switch (volleyHttpResult.getStatus()) {
            case HttpAction.FAILED:
                if (volleyHttpResult.getInfo().contains(HttpAction.ACTION_LOGIN)
                        || volleyHttpResult.getInfo().contains(HttpAction.TOKEN_LOSE)) {
                    Toast.makeText(XPTApplication.getInstance(), volleyHttpResult.getInfo(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(BroadcastAction.RELOGIN);
                    XPTApplication.getInstance().sendBroadcast(intent);
                    return;
                }
                break;
        }
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        Log.i(TAG, "onErrorResponse: ");
        Toast.makeText(XPTApplication.getInstance(), "获取失败", Toast.LENGTH_SHORT).show();
    }
}
