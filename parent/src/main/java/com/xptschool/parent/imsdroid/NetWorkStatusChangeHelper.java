package com.xptschool.parent.imsdroid;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.github.pwittchen.networkevents.library.BusWrapper;
import com.github.pwittchen.networkevents.library.ConnectivityStatus;
import com.github.pwittchen.networkevents.library.NetworkEvents;
import com.github.pwittchen.networkevents.library.event.ConnectivityChanged;
import com.xptschool.parent.XPTApplication;
import com.xptschool.parent.common.SharedPreferencesUtil;
import com.xptschool.parent.server.ServerManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by dexing on 2017/8/1 0001.
 * No1
 */

public class NetWorkStatusChangeHelper {

    private String TAG = NetWorkStatusChangeHelper.class.getSimpleName();
    private static NetWorkStatusChangeHelper instance;
    private BusWrapper busWrapper;
    private NetworkEvents networkEvents;

    private NetWorkStatusChangeHelper() {
    }

    public static NetWorkStatusChangeHelper getInstance() {
        if (instance == null) {
            instance = new NetWorkStatusChangeHelper();
        }
        return instance;
    }

    public void initNetWorkChange() {
        final EventBus bus = new EventBus();
        busWrapper = getGreenRobotBusWrapper(bus);
        networkEvents = new NetworkEvents(XPTApplication.getInstance(), busWrapper).enableInternetCheck()
                .enableWifiScan();
        busWrapper.register(this);
        networkEvents.setPingParameters("www.baidu.com", 80, 30);
        networkEvents.register();
    }

    @NonNull
    private BusWrapper getGreenRobotBusWrapper(final EventBus bus) {
        return new BusWrapper() {
            @Override
            public void register(Object object) {
                if (!bus.isRegistered(object)) {
                    bus.register(object);
                }
            }

            @Override
            public void unregister(Object object) {
                bus.unregister(object);
            }

            @Override
            public void post(Object event) {
                bus.post(event);
            }
        };
    }

    private void stop() {
        busWrapper.unregister(this);
        networkEvents.unregister();
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onEvent(ConnectivityChanged event) {
        String connect_status = event.getConnectivityStatus().toString();
        Log.i(TAG, "onEvent: " + connect_status);
        //登录状态下判断
        if (!SharedPreferencesUtil.getData(XPTApplication.getInstance(), SharedPreferencesUtil.KEY_PWD, "").equals("")) {
            Log.i(TAG, "onEvent: login success");
            if (connect_status.equals(ConnectivityStatus.WIFI_CONNECTED_HAS_INTERNET.toString()) ||
                    connect_status.equals(ConnectivityStatus.MOBILE_CONNECTED.toString())) {
                Log.i(TAG, "onEvent: register native service");
                ServerManager.getInstance().stopNativeService(XPTApplication.getInstance());
                ServerManager.getInstance().startNativeService(XPTApplication.getInstance());
            }
        }
    }
}
