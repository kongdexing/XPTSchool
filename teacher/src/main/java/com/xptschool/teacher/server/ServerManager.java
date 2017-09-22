package com.xptschool.teacher.server;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.xptschool.teacher.XPTApplication;
import com.xptschool.teacher.imsdroid.ImsSipHelper;
import com.xptschool.teacher.imsdroid.NetWorkStatusChangeHelper;
import com.xptschool.teacher.ui.chat.ToSendMessage;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by dexing on 2017/5/8.
 * No1
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

    /**
     * 登录成功后，启动socket服务
     */
    public void startSocketServer() {
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

    /**
     * 用户退出后停止服务
     */
    public void stopSocketServer(Context context) {
        Log.i("Native", "stopServer: ");
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
