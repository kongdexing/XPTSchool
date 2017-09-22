package com.xptschool.parent.server;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.xptschool.parent.XPTApplication;
import com.xptschool.parent.imsdroid.ImsSipHelper;
import com.xptschool.parent.imsdroid.NativeService;
import com.xptschool.parent.imsdroid.NetWorkStatusChangeHelper;
import com.xptschool.parent.model.ToSendMessage;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by dexing on 2017/5/8.
 * 管理聊天服务，视频通话服务
 * 1.启动服务
 * 2.停止服务
 * 3.发送聊天消息
 */

public class ServerManager {

    private static ServerManager mInstance = new ServerManager();
    private SocketService mSocketService;
    private Timer mTimer;
    public static ExecutorService receiverThreadPool = Executors.newSingleThreadExecutor();
    public static ExecutorService sendThreadPool = Executors.newFixedThreadPool(5);
    public static ServerManager getInstance() {
        return mInstance;
    }

    public void startService() {
        mTimer = new Timer();

        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(XPTApplication.getContext(), SocketService.class);
                XPTApplication.getContext().startService(intent);
            }
        }, 1000, 2 * 1000);

        ImsSipHelper.getInstance().startEngine();
    }

    public void stopService(Context context) {
        context.stopService(new Intent(context, SocketService.class));
        if (mTimer != null) {
            mTimer.cancel();
        }

        ImsSipHelper.getInstance().stopSipServer();
        NetWorkStatusChangeHelper.getInstance().disableNetWorkChange();
    }

    public void sendMessage(ToSendMessage message) {
        if (mSocketService == null) {
            mSocketService = new SocketService();
        }
        if (message != null && message.getAllData() != null &&
                message.getAllData().length > 0) {
            mSocketService.sendMessage(message);
        }
    }


}
