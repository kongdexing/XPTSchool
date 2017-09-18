package com.xptschool.teacher.server;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.coolerfall.daemon.Daemon;
import com.xptschool.teacher.ui.chat.ToSendMessage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by dexing on 2017/5/8.
 * No1
 */

public class SocketService extends Service {

    private static String TAG = "TSocketService";
//    public static String socketIP = "192.168.1.195";
//    public static int socketPort = 5020;

    public static String socketIP = "chat.pcuion.com";
    public static int socketPort = 50300;
    public static int socketReceiverPort = 50301;
    private ExecutorService receiverThreadPool = Executors.newSingleThreadExecutor();
    private ExecutorService sendThreadPool = Executors.newFixedThreadPool(5);

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
        Log.i(TAG, "onStartCommand: ");
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
        receiverThreadPool.execute(socketReceiveThread);
//        SocketReceiveThread receiveThread = new SocketReceiveThread();
//        receiveThread.start();
    }

    public void sendMessage(ToSendMessage message) {
        Log.i(TAG, "sendMessage: " + message.getAllData().length);
//        SocketSendThread sendThread = new SocketSendThread(message);
//        sendThread.start();

        sendThreadPool.execute(new SocketSendThread(message));
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "receiveMessage onDestroy()");
        super.onDestroy();
    }

}
