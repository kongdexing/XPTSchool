package com.xptschool.teacher.server;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.xptschool.teacher.imsdroid.NativeService;
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

    public void startServer(Context context) {
        Intent intent = new Intent(context, SocketService.class);
        context.startService(intent);

        startNativeService(context);
    }

    public void stopServer(Context context) {
        Log.i("Native", "stopServer: ");
        context.stopService(new Intent(context, SocketService.class));
        stopNativeService(context);
    }

    public void startNativeService(Context context) {
        Log.i("Event", "startNativeService: ");
        context.startService(new Intent(context, NativeService.class));
    }

    public void stopNativeService(Context context) {
        Log.i("Event", "stopNativeService: ");
        context.stopService(new Intent(context, NativeService.class));
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
