package com.xptschool.parent.server;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.coolerfall.daemon.Daemon;
import com.xptschool.parent.model.ToSendMessage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by dexing on 2017/5/8.
 * No1
 */

public class SocketService extends Service {

    private static String TAG = "PSocketService";
    //    private static String socketIP = "192.168.1.195";
//    private static int socketWritePort = 5020;
    public static String socketIP = "chat.pcuion.com";
    public static int socketWritePort = 50300;
    public static int socketReadPort = 50301;

    public SocketService() {
        super();
        Log.i(TAG, "SocketService: ");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate: ");
        Daemon.run(SocketService.this,
                SocketService.class, Daemon.INTERVAL_ONE_MINUTE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        receiveMessage();
        ReceiveRecallMessage.receiveRecallMessage();
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void receiveMessage() {
        SocketReceiveThread socketReceiveThread = new SocketReceiveThread();
        ServerManager.receiverThreadPool.execute(socketReceiveThread);
    }

    public void sendMessage(ToSendMessage message) {
        Log.i(TAG, "sendMessage: " + message.getAllData().length);
        ServerManager.sendThreadPool.execute(new SocketSendThread(message));
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "receiveMessage onDestroy()");
        super.onDestroy();
    }
}
