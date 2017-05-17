package com.xptschool.teacher.server;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.coolerfall.daemon.Daemon;
import com.xptschool.teacher.XPTApplication;
import com.xptschool.teacher.common.BroadcastAction;
import com.xptschool.teacher.ui.chat.BaseMessage;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;
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
        Log.i(TAG, "receiveMessage: ");
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
            Intent intent = new Intent(BroadcastAction.MESSAGE_SEND_START);
//            intent.putExtra("message", message);
//            XPTApplication.getInstance().sendBroadcast(intent);
            Socket mSocket = null;
            OutputStream outputStream = null;
            try {
                mSocket = new Socket(socketIP, socketPort);

                if (mSocket == null || !mSocket.isConnected()) {
                    Log.i(TAG, "SocketSendThread run: socket is null or unconnected");
                    return;
                }
                outputStream = mSocket.getOutputStream();
                outputStream.write(message.getAllData());
                outputStream.flush();
                //发送完成
                intent = new Intent(BroadcastAction.MESSAGE_SEND_SUCCESS);
                intent.putExtra("message", message);
                XPTApplication.getInstance().sendBroadcast(intent);
                Log.i(TAG, "write success");
            } catch (Exception e) {
                intent = new Intent(BroadcastAction.MESSAGE_SEND_FAILED);
                intent.putExtra("message", message);
                XPTApplication.getInstance().sendBroadcast(intent);
                //发送失败
                Log.e(TAG, "Exception during write", e);
            } finally {
                closeSocket(mSocket, outputStream, null);
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
