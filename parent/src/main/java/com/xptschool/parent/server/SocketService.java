package com.xptschool.parent.server;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.xptschool.parent.XPTApplication;
import com.xptschool.parent.common.BroadcastAction;
import com.xptschool.parent.ui.question.BaseMessage;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by dexing on 2017/5/8.
 * No1
 */

public class SocketService extends Service {

    private static String TAG = SocketService.class.getSimpleName();
//    private static String socketIP = "192.168.1.195";
//    private static int socketPort = 5020;

    private static String socketIP = "chat.pcuion.com";
    private static int socketPort = 50300;
    private static final String RECONNECT_ALARM = "com.xptschool.parent.RECONNECT_ALARM";
    private Intent mAlarmIntent = new Intent(RECONNECT_ALARM);
    private static Socket mSocket = null;
    private static SocketConnectThread connectThread;
    private static SocketReceiveThread receiveThread;
    private static SocketSendThread sendThread;

    private PendingIntent mPAlarmIntent;
    private BroadcastReceiver mAlarmReceiver;
    private AlarmManager alarmManager;

    public SocketService() {
        super();
        Log.i(TAG, "SocketService: ");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate: ");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand: ");
        start();

        if (alarmManager == null) {
            mPAlarmIntent = PendingIntent.getBroadcast(this, 0, mAlarmIntent, 0);
            alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            mAlarmReceiver = new ReconnectAlarmReceiver();

            registerReceiver(mAlarmReceiver, new IntentFilter(RECONNECT_ALARM));
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 5 * 1000, 5 * 1000, mPAlarmIntent);
        }
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void connect() {
        if (mSocket != null && mSocket.isConnected()) {
            Log.i(TAG, "connect: socket is connected");
            return;
        }
        Log.i(TAG, "connect start connecting ");
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }

        if (receiveThread != null) {
            receiveThread.cancel();
            receiveThread = null;
        }

        if (sendThread != null) {
            sendThread.cancel();
            sendThread = null;
        }

        start();
    }

    private void start() {
        Log.i(TAG, "start: ");
        if (connectThread == null) {
            connectThread = new SocketConnectThread();
            connectThread.start();
        }

        if (receiveThread == null) {
            receiveThread = new SocketReceiveThread();
            receiveThread.start();
        }

        if (sendThread == null) {
            sendThread = new SocketSendThread();
            sendThread.start();
        }
    }

    private synchronized void disconnect() {
        alarmManager.cancel(mPAlarmIntent);// 取消重连闹钟
        try {
            unregisterReceiver(mAlarmReceiver);// 注销广播监听
        } catch (Exception ex) {
            Log.e(TAG, "unregister alarmReceiver error " + ex.getMessage());
        }
        try {
            mSocket.close();
        } catch (Exception ex) {

        }
        mSocket = null;
    }

    public void sendMessage(BaseMessage message) {
        Log.i(TAG, "sendMessage: " + message.getAllData().length);
        if (sendThread == null) {
            Log.i(TAG, "sendMessage sendThread is null ");
            sendThread = new SocketSendThread();
            sendThread.start();
        }
        if (sendThread != null) {
            sendThread.write(message);
        }
    }


    private class SocketConnectThread extends Thread {

        public SocketConnectThread() {
            super();
            Log.i(TAG, "SocketConnectThread: ");
        }

        @Override
        public void run() {
            try {
                mSocket = new Socket(socketIP, socketPort);
                if (mSocket == null || !mSocket.isConnected()) {
                    connect();
                    Log.i(TAG, "SocketConnectThread run: socket is null or unconnected");
                    return;
                }
                Log.i(TAG, "SocketConnectThread connected success ");
            } catch (Exception ex) {
                Log.e(TAG, "SocketConnectThread run: " + ex.getMessage());
            }
        }

        public void cancel() {
            if (mSocket != null) {
                try {
                    mSocket.close();
                } catch (Exception ex) {

                }
            }
            mSocket = null;
        }
    }

    private class SocketReceiveThread extends Thread {

        public SocketReceiveThread() {
            super();
            Log.i(TAG, "SocketReceiveThread: ");
        }

        @Override
        public void run() {
            super.run();
            Log.i(TAG, "SocketReceiveThread run: ");
            try {
                if (mSocket == null || !mSocket.isConnected()) {
                    mSocket = new Socket(socketIP, socketPort);
                    if (!mSocket.isConnected()) {
                        Log.i(TAG, "connectServerWithTCPSocket unconnected");
                        mSocket = null;
                        //retry
                        return;
                    }
                }
                InputStream mmInStream = mSocket.getInputStream();
                byte[] buffer = new byte[1024];
                while (mmInStream.read(buffer) != -1) {
                    try {
                        Log.i(TAG, "SocketReceiveThread read result:" + (new String(buffer)));
                        // Send the obtained bytes to the UI Activity
                    } catch (Exception e) {
                        Log.e(TAG, "disconnected " + e.getMessage());
                        break;
                    }
                }
            } catch (Exception ex) {
                Log.i(TAG, "SocketReceiveThread Exception: " + ex.getMessage());
            } finally {

            }
        }

        public void cancel() {
            if (mSocket != null) {
                try {
                    mSocket.close();
                } catch (Exception ex) {

                }
            }
            mSocket = null;
        }
    }

    private class SocketSendThread extends Thread {

        private OutputStream outputStream;

        public SocketSendThread() {
            super();
            Log.i(TAG, "SocketSendThread: ");
        }

        @Override
        public void run() {
            super.run();
            Log.i(TAG, "SocketSendThread run: ");
            try {
                if (mSocket == null || !mSocket.isConnected()) {
                    mSocket = new Socket(socketIP, socketPort);
                }

                if (mSocket == null || !mSocket.isConnected()) {
                    mSocket = null;
                    Log.i(TAG, "SocketSendThread run: socket is null or unconnected");
                    connect();
                    return;
                }
                outputStream = mSocket.getOutputStream();
            } catch (Exception ex) {
                Log.e(TAG, "SocketSendThread run: " + ex.getMessage());
            }

        }

        public void write(BaseMessage message) {
            if (!mSocket.isConnected()) {
                Log.i(TAG, "connectServerWithTCPSocket unconnected");
                mSocket = null;
                connect();
                return;
            }

            Log.i(TAG, "write start ");
            //开始发送
            Intent intent = new Intent(BroadcastAction.MESSAGE_SEND_START);
            intent.putExtra("message", message);
            XPTApplication.getInstance().sendBroadcast(intent);
            try {
                outputStream.write(message.getAllData());
                outputStream.flush();
                //发送完成
                intent = new Intent(BroadcastAction.MESSAGE_SEND_SUCCESS);
                intent.putExtra("message", message);
                XPTApplication.getInstance().sendBroadcast(intent);
                Log.i(TAG, "write success");
            } catch (Exception e) {
//                retryListen();
                intent = new Intent(BroadcastAction.MESSAGE_SEND_FAILED);
                intent.putExtra("message", message);
                XPTApplication.getInstance().sendBroadcast(intent);
                //发送失败
                Log.e(TAG, "Exception during write", e);
            }

        }

        public void cancel() {
            if (mSocket != null) {
                try {
                    mSocket.close();
                } catch (Exception ex) {

                }
            }
            mSocket = null;
        }
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "connect onDestroy()");
        super.onDestroy();
        disconnect();
    }

    private class ReconnectAlarmReceiver extends BroadcastReceiver {
        public void onReceive(Context ctx, Intent intent) {
            if (RECONNECT_ALARM.equals(intent.getAction())) {
                connect();
            }
        }
    }
}
