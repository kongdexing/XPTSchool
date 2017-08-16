package com.xptschool.teacher.server;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.xptschool.teacher.imsdroid.NativeService;
import com.xptschool.teacher.imsdroid.NetWorkStatusChangeHelper;
import com.xptschool.teacher.ui.chat.ToSendMessage;

/**
 * Created by dexing on 2017/5/8.
 * No1
 */

public class ServerManager {

    private static ServerManager mInstance = new ServerManager();
    private SocketService mSocketService;

    public static ServerManager getInstance() {
        return mInstance;
    }

    /**
     * 登录成功后，启动socket服务
     *
     * @param context
     */
    public void startSocketServer(Context context) {
        Intent intent = new Intent(context, SocketService.class);
        context.startService(intent);

        startNativeService(context);
    }

    /**
     * 用户退出后停止服务
     */
    public void stopSocketServer(Context context) {
        Log.i("Native", "stopServer: ");
        context.stopService(new Intent(context, SocketService.class));

        stopNativeService(context);

        NetWorkStatusChangeHelper.getInstance().disableNetWorkChange();
    }

    public void startNativeService(Context context) {
        Log.i("Event", "startNativeService: ");
        Intent intent = new Intent(context, NativeService.class);
        intent.putExtra(NativeService.ENGINE_TYPE, true);
        context.startService(intent);
    }

    public void stopNativeService(Context context) {
        Log.i("Event", "stopNativeService: ");
        Intent intent = new Intent(context, NativeService.class);
        intent.putExtra(NativeService.ENGINE_TYPE, false);
        context.startService(intent);
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
