package com.xptschool.parent.server;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.coolerfall.daemon.Daemon;
import com.xptschool.parent.XPTApplication;
import com.xptschool.parent.common.BroadcastAction;
import com.xptschool.parent.model.BeanChat;
import com.xptschool.parent.model.BeanParent;
import com.xptschool.parent.model.GreenDaoHelper;
import com.xptschool.parent.ui.chat.BaseMessage;
import com.xptschool.parent.util.ChatUtil;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by dexing on 2017/5/8.
 * No1
 */

public class SocketService extends Service {

    private static String TAG = "PSocketService";
    //    private static String socketIP = "192.168.1.195";
//    private static int socketPort = 5020;
    public static String socketIP = "chat.pcuion.com";
    public static int socketPort = 50300;
    public static int socketReceiverPort = 50301;
    private Timer mTimer;
    private SocketReceiveThread socketReceiveThread;
    private ExecutorService receiverThreadPool = Executors.newFixedThreadPool(5);
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
        if (mTimer == null) {
            mTimer = new Timer();
            setTimerTask();
        }
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void setTimerTask() {
        socketReceiveThread = new SocketReceiveThread();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                receiveMessage();
            }
        }, 1000, 2 * 1000);
    }

    private void receiveMessage() {
        SocketReceiveThread receiveThread = socketReceiveThread.cloneReceiveThread();
        receiverThreadPool.execute(receiveThread);
//        SocketReceiveThread receiveThread = new SocketReceiveThread();
//        receiveThread.start();
    }

    private synchronized void disconnect() {
        if (mTimer != null) {
            mTimer.cancel();
        }
    }

    public void sendMessage(BaseMessage message) {
        Log.i(TAG, "sendMessage: " + message.getAllData().length);
//        SocketSendThread sendThread = new SocketSendThread(message);
//        sendThread.start();

        sendThreadPool.execute(new SocketSendThread(message));
    }

    private class SocketSendThread extends Thread {

        private BaseMessage message;

        public SocketSendThread(BaseMessage msg) {
            super();
            message = msg;
            Log.i(TAG, "SocketSendThread: ");
        }

        @Override
        public void run() {
            super.run();
            Log.i(TAG, "SocketSendThread run: ");
            Log.i(TAG, "write start ");
            //开始发送
            Intent intent = new Intent();
            intent.putExtra("message", message);
            Socket mSocket = null;
            OutputStream outputStream = null;
            try {
//                Socket mSocket = new Socket("192.168.1.195", 5021);
                mSocket = new Socket(socketIP, socketPort);
                if (mSocket == null || !mSocket.isConnected()) {
                    Log.i(TAG, "SocketSendThread run: socket is null or unconnected");
                    intent.setAction(BroadcastAction.MESSAGE_SEND_FAILED);
                    return;
                }
                outputStream = mSocket.getOutputStream();
                outputStream.write(message.getAllData());
                outputStream.flush();
                //发送完成
                intent.setAction(BroadcastAction.MESSAGE_SEND_SUCCESS);
                Log.i(TAG, "write success");
            } catch (Exception e) {
                intent.setAction(BroadcastAction.MESSAGE_SEND_FAILED);
                //发送失败
                Log.e(TAG, "Exception during write", e);
            } finally {
                closeSocket(mSocket, outputStream, null);
                XPTApplication.getInstance().sendBroadcast(intent);
            }
        }
    }

    private void closeSocket(Socket mSocket, OutputStream outputStream, InputStream mmInStream) {
        try {
            if (outputStream != null) {
                outputStream.close();
            }
        } catch (Exception ex) {

        }
        try {
            if (mmInStream != null) {
                mmInStream.close();
            }
        } catch (Exception ex) {

        }
        try {
            if (mSocket != null) {
                mSocket.close();
            }
        } catch (Exception ex) {

        }
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "receiveMessage onDestroy()");
        super.onDestroy();
        disconnect();
    }
}
