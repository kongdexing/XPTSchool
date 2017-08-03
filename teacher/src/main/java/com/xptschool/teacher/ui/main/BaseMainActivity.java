package com.xptschool.teacher.ui.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.huawei.hms.api.ConnectionResult;
import com.huawei.hms.api.HuaweiApiAvailability;
import com.huawei.hms.api.HuaweiApiClient;
import com.huawei.hms.support.api.client.PendingResult;
import com.huawei.hms.support.api.client.ResultCallback;
import com.huawei.hms.support.api.push.HuaweiPush;
import com.huawei.hms.support.api.push.TokenResult;
import com.meizu.cloud.pushsdk.PushManager;
import com.umeng.message.IUmengCallback;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.xiaomi.channel.commonutils.logger.LoggerInterface;
import com.xiaomi.mipush.sdk.Logger;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xptschool.teacher.XPTApplication;
import com.xptschool.teacher.common.CommonUtil;
import com.xptschool.teacher.push.MyPushIntentService;
import com.xptschool.teacher.push.UpushTokenHelper;
import com.xptschool.teacher.server.ServerManager;

/**
 * Created by dexing on 2017/6/5.
 * No1
 */

public class BaseMainActivity extends BaseActivity implements HuaweiApiClient.ConnectionCallbacks, HuaweiApiClient.OnConnectionFailedListener {

    private HuaweiApiClient client;
    private UpdateUIBroadcastReceiver HWPushBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //依据手机类型，注册不同推送平台
        String model = android.os.Build.MODEL;
        String carrier = android.os.Build.MANUFACTURER;
        Log.i(TAG, "onCreate: " + model + "  " + carrier);
        if (carrier.toUpperCase().equals("XIAOMI")) {

            MiPushClient.registerPush(this, XPTApplication.APP_MIPUSH_ID, XPTApplication.APP_MIPUSH_KEY);
            LoggerInterface newLogger = new LoggerInterface() {

                @Override
                public void setTag(String tag) {
                    // ignore
                }

                @Override
                public void log(String content, Throwable t) {
                    Log.d(TAG, content, t);
                }

                @Override
                public void log(String content) {
                    Log.d(TAG, content);
                }
            };
            Logger.setLogger(this, newLogger);
            //推送可用
            MiPushClient.enablePush(this);
        } else if (carrier.toUpperCase().equals("HUAWEI")) {
            //创建华为移动服务client实例用以使用华为push服务
            //需要指定api为HuaweiId.PUSH_API
            //连接回调以及连接失败监听
            client = new HuaweiApiClient.Builder(this)
                    .addApi(HuaweiPush.PUSH_API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            //建议在oncreate的时候连接华为移动服务
            //业务可以根据自己业务的形态来确定client的连接和断开的时机，但是确保connect和disconnect必须成对出现
            client.connect();
            registerBroadcast();
        } else if (carrier.toUpperCase().equals("MEIZU")) {
            PushManager.register(this, XPTApplication.MZ_APP_ID, XPTApplication.MZ_APP_KEY);

        } else {
            //友盟
            final PushAgent mPushAgent = PushAgent.getInstance(this);
            mPushAgent.setDebugMode(false);
            //使用自定义消息
            mPushAgent.setPushIntentServiceClass(MyPushIntentService.class);

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
                            UpushTokenHelper.uploadDevicesToken(deviceToken, "UPush");
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
    }

    private void getHWTokenAsyn() {
        if (!client.isConnected()) {
            Log.i(TAG, "获取token失败，原因：HuaweiApiClient未连接");
            client.connect();
            return;
        }

        Log.i(TAG, "异步接口获取push token");
        PendingResult<TokenResult> tokenResult = HuaweiPush.HuaweiPushApi.getToken(client);
        tokenResult.setResultCallback(new ResultCallback<TokenResult>() {

            @Override
            public void onResult(TokenResult result) {
                //这边的结果只表明接口调用成功，是否能收到响应结果只在广播中接收
                Log.i(TAG, "onResult code:" + result.getTokenRes().getRetCode() + "  token:" + result.getTokenRes().getToken());
            }
        });
    }

    @Override
    public void onConnected() {
        //华为移动服务client连接成功，在这边处理业务自己的事件
        Log.i(TAG, "HuaweiApiClient 连接成功");
        getHWTokenAsyn();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        //HuaweiApiClient断开连接的时候，业务可以处理自己的事件
        Log.i(TAG, "HuaweiApiClient 连接断开");
        //HuaweiApiClient异常断开连接, if 括号里的条件可以根据需要修改
        if (!this.isDestroyed() && !this.isFinishing()) {
            client.connect();
        }
    }

    //调用HuaweiApiAvailability.getInstance().resolveError传入的第三个参数
    //作用同startactivityforresult方法中的requestcode
    private static final int REQUEST_HMS_RESOLVE_ERROR = 1000;

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "HuaweiApiClient连接失败，错误码：" + result.getErrorCode());
        if (HuaweiApiAvailability.getInstance().isUserResolvableError(result.getErrorCode())) {
            final int errorCode = result.getErrorCode();
            new Handler(getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    // 此方法必须在主线程调用
                    HuaweiApiAvailability.getInstance().resolveError(BaseMainActivity.this, errorCode, REQUEST_HMS_RESOLVE_ERROR);
                }
            });
        } else {
            //其他错误码请参见开发指南或者API文档
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ServerManager.getInstance().startServer(this);
    }

    /**
     * 以下代码为sample自身逻辑，和业务能力不相关
     * 作用仅仅为了在sample界面上显示push相关信息
     */
    private void registerBroadcast() {
        String ACTION_UPDATEUI = "action.updateUI";

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_UPDATEUI);
        HWPushBroadcastReceiver = new UpdateUIBroadcastReceiver();
        registerReceiver(HWPushBroadcastReceiver, filter);
    }

    /**
     * 定义广播接收器（内部类）
     */
    private class UpdateUIBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int type = intent.getExtras().getInt("type");
            if (type == 1) {
                String token = intent.getExtras().getString("token");
                Log.i(TAG, "onReceive token : " + token + "  IMEI:" + CommonUtil.getDeviceId());
                UpushTokenHelper.uploadDevicesToken(token, "HWPush");
            } else if (type == 2) {
                boolean status = intent.getExtras().getBoolean("pushState");
                if (status == true) {
                    Log.i(TAG, "onReceive: 已连接");
                } else {
                    Log.i(TAG, "onReceive: 未连接");
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //建议在onDestroy的时候停止连接华为移动服务
        //业务可以根据自己业务的形态来确定client的连接和断开的时机，但是确保connect和disconnect必须成对出现
        if (client != null) {
            client.disconnect();
            try {
                unregisterReceiver(HWPushBroadcastReceiver);
            } catch (Exception ex) {

            }
        }

    }
}
