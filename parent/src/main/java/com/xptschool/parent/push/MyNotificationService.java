package com.xptschool.parent.push;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.coolerfall.daemon.Daemon;
import com.umeng.message.PushAgent;
import com.xptschool.parent.server.SocketService;

/**
 * Created by dexing on 2017/5/19.
 * No1
 */

public class MyNotificationService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        Daemon.run(MyNotificationService.this,
                MyNotificationService.class, Daemon.INTERVAL_ONE_MINUTE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final PushAgent mPushAgent = PushAgent.getInstance(this);
        //使用自定义消息
        mPushAgent.setPushIntentServiceClass(MyPushIntentService.class);
        mPushAgent.setDebugMode(false);
        mPushAgent.setDisplayNotificationNumber(10);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

}
