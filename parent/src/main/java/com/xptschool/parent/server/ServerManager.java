package com.xptschool.parent.server;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.xptschool.parent.imsdroid.ImsSipHelper;
import com.xptschool.parent.imsdroid.NativeService;
import com.xptschool.parent.imsdroid.NetWorkStatusChangeHelper;
import com.xptschool.parent.model.ToSendMessage;

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

    public static ServerManager getInstance() {
        return mInstance;
    }

    public void startService(Context context) {
        Intent intent = new Intent(context, SocketService.class);
        context.startService(intent);

        ImsSipHelper.getInstance().startEngine();
    }

    public void stopService(Context context) {
        context.stopService(new Intent(context, SocketService.class));

        ImsSipHelper.getInstance().unRegisterSipServer();
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
