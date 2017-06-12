package com.xptschool.teacher.ui.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import com.umeng.message.IUmengCallback;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.xptschool.teacher.BuildConfig;
import com.xptschool.teacher.imsdroid.Engine;
import com.xptschool.teacher.imsdroid.NativeService;
import com.xptschool.teacher.model.GreenDaoHelper;
import com.xptschool.teacher.push.UpushTokenHelper;

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

    //login video chat server
    private INgnSipService mSipService;
    private INgnConfigurationService mConfigurationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSipService = getEngine().getSipService();
        this.mConfigurationService = getEngine().getConfigurationService();

        initNgnConfig();

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

        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(NgnRegistrationEventArgs.ACTION_REGISTRATION_EVENT);
        intentFilter.addAction(NativeService.ACTION_STATE_EVENT);
        registerReceiver(mSipBroadCastRecv, intentFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!Engine.getInstance().isStarted()) {
            final Engine engine = getEngine();
            final Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    if (!engine.isStarted()) {
                        Log.d(TAG, "Starts the engine from the splash screen");
                        engine.start();
                    }
                }
            });
            thread.setPriority(Thread.MAX_PRIORITY);
            thread.start();
        } else {
            registerVideoServer();
        }
    }

    protected Engine getEngine() {
        return (Engine) Engine.getInstance();
    }

    private void initNgnConfig() {
        mConfigurationService.putString(NgnConfigurationEntry.IDENTITY_DISPLAY_NAME, GreenDaoHelper.getInstance().getCurrentTeacher().getName());
        mConfigurationService.putString(NgnConfigurationEntry.IDENTITY_IMPU, "sip:1008@" + BuildConfig.CHAT_VIDEO_URL);
        mConfigurationService.putString(NgnConfigurationEntry.IDENTITY_IMPI, "1008");
        mConfigurationService.putString(NgnConfigurationEntry.IDENTITY_PASSWORD, "1234");
        mConfigurationService.putString(NgnConfigurationEntry.NETWORK_REALM, "sip:" + BuildConfig.CHAT_VIDEO_URL);
        mConfigurationService.putString(NgnConfigurationEntry.NETWORK_PCSCF_HOST, BuildConfig.CHAT_VIDEO_URL);

//        // Compute
        if (!mConfigurationService.commit()) {
            Log.e(TAG, "Failed to commit() configuration");
        }
        Log.i(TAG, "initNgnConfig: ");
    }

    private void registerVideoServer() {
        if (mSipService.getRegistrationState() == NgnSipSession.ConnectionState.CONNECTING || mSipService.getRegistrationState() == NgnSipSession.ConnectionState.TERMINATING) {
            Log.i(TAG, "registerVideoServer stopStack");
            mSipService.stopStack();
        } else if (mSipService.isRegistered()) {
            Log.i(TAG, "registerVideoServer unRegister");
            mSipService.unRegister();
        } else {
            Log.i(TAG, "registerVideoServer register");
            mSipService.register(this);
        }
    }

    @Override
    protected void onDestroy() {
        if (mSipBroadCastRecv != null) {
            unregisterReceiver(mSipBroadCastRecv);
            mSipBroadCastRecv = null;
        }
        super.onDestroy();
    }

    BroadcastReceiver mSipBroadCastRecv = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            // Registration Event
            if (NgnRegistrationEventArgs.ACTION_REGISTRATION_EVENT.equals(action)) {
                NgnRegistrationEventArgs args = intent.getParcelableExtra(NgnEventArgs.EXTRA_EMBEDDED);
                if (args == null) {
                    Log.e(TAG, "Invalid event args");
                    return;
                }
                Log.i(TAG, "onReceive: " + args.getEventType());

                switch (args.getEventType()) {
                    case REGISTRATION_NOK:
                    case UNREGISTRATION_OK:
                    case REGISTRATION_OK:
                    case REGISTRATION_INPROGRESS:
                    case UNREGISTRATION_INPROGRESS:
                    case UNREGISTRATION_NOK:
                    default:
//                        ((ScreenHomeAdapter) mGridView.getAdapter()).refresh();
                        break;
                }
            } else if (NativeService.ACTION_STATE_EVENT.equals(action)) {
                registerVideoServer();
            }
        }
    };
}
