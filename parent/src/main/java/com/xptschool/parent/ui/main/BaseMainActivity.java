package com.xptschool.parent.ui.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import com.umeng.message.IUmengCallback;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.xptschool.parent.BuildConfig;
import com.xptschool.parent.imsdroid.Engine;
import com.xptschool.parent.imsdroid.NativeService;
import com.xptschool.parent.model.GreenDaoHelper;
import com.xptschool.parent.push.UpushTokenHelper;
import com.xptschool.parent.server.ServerManager;

import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.events.NgnEventArgs;
import org.doubango.ngn.events.NgnRegistrationEventArgs;
import org.doubango.ngn.services.INgnConfigurationService;
import org.doubango.ngn.services.INgnSipService;
import org.doubango.ngn.sip.NgnSipSession;
import org.doubango.ngn.utils.NgnConfigurationEntry;

/**
 * Created by dexing on 2017/6/5.
 * No1
 */

public class BaseMainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final PushAgent mPushAgent = PushAgent.getInstance(this);
        mPushAgent.setDebugMode(false);
        Log.i(TAG, "startServer: register ");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "startServer: register start");
                //注册推送服务，每次调用register方法都会回调该接口
                mPushAgent.register(new IUmengRegisterCallback() {

                    @Override
                    public void onSuccess(String deviceToken) {
                        //注册成功会返回device token
                        Log.i(TAG, "onSuccess: deviceToken " + deviceToken);
                        UpushTokenHelper.uploadDevicesToken(deviceToken);
                    }

                    @Override
                    public void onFailure(String s, String s1) {
                        Log.i(TAG, "onFailure: " + s + "---" + s1);
                    }
                });
            }
        }).start();

        //接收通知
        mPushAgent.enable(new IUmengCallback() {
            @Override
            public void onSuccess() {
                Log.i(TAG, "PushAgent enable onSuccess: ");
            }

            @Override
            public void onFailure(String s, String s1) {
                Log.i(TAG, "PushAgent enable onFailure: " + s + " s1 " + s1);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        ServerManager.getInstance().startService(this);
    }

}
