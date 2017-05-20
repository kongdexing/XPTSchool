package com.xptschool.teacher.server;

import android.content.Context;
import android.content.Intent;

import com.xptschool.teacher.ui.chat.BaseMessage;

/**
 * Created by dexing on 2017/5/8.
 * No1
 */

public class SocketManager {

    private static SocketManager mInstance = new SocketManager();
    private SocketService mSocketService;

    public static SocketManager getInstance() {
        return mInstance;
    }

    public void startServer(Context context) {
        Intent intent = new Intent(context, SocketService.class);
        context.startService(intent);
    }

    public void stopServer(Context context) {
        context.stopService(new Intent(context, SocketService.class));
    }

    public void sendMessage(BaseMessage message) {
        if (mSocketService == null) {
            mSocketService = new SocketService();
        }
        if (message != null && message.getAllData() != null &&
                message.getAllData().length > 0) {
            mSocketService.sendMessage(message);
        }
    }


}
