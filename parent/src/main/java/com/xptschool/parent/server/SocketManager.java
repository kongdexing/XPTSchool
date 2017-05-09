package com.xptschool.parent.server;

import android.content.Context;
import android.content.Intent;

import com.xptschool.parent.ui.question.BaseMessage;

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

    public void init(Context context) {
        Intent intent = new Intent(context, SocketService.class);
        context.startService(intent);
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
