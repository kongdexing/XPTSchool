package com.xptschool.parent.server;

import android.content.Context;
import android.content.Intent;

import com.xptschool.parent.imsdroid.NativeService;
import com.xptschool.parent.ui.chat.BaseMessage;

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

    public void startService(Context context) {
        Intent intent = new Intent(context, SocketService.class);
        context.startService(intent);

        context.startService(new Intent(context, NativeService.class));
    }

    public void stopService(Context context) {
        context.stopService(new Intent(context, SocketService.class));
        context.stopService(new Intent(context, NativeService.class));
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
