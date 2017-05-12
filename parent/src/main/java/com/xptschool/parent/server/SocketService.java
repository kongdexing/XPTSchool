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
    private static final String RECONNECT_ALARM = "com.xptschool.parent.RECONNECT_ALARM";
    private Intent mAlarmIntent = new Intent(RECONNECT_ALARM);
    //    private static Socket mSocket = null;
    private static SocketReceiveThread receiveThread;
    private static SocketSendThread sendThread;

    private PendingIntent mPAlarmIntent;
    private AlarmManager alarmManager;
    private Timer mTimer;

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
        if (alarmManager == null) {
            mPAlarmIntent = PendingIntent.getBroadcast(this, 0, mAlarmIntent, 0);
            alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

            registerReceiver(mAlarmReceiver, new IntentFilter(RECONNECT_ALARM));
//            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, System.currentTimeMillis(), 3 * 1000, mPAlarmIntent);
        }

        if (mTimer == null) {
            mTimer = new Timer();
        }
        setTimerTask();
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void setTimerTask() {
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                receiveMessage();
            }
        }, 3 * 1000, 1000);
    }

    private void receiveMessage() {
        //线程池稍后添加
        if (receiveThread != null) {
            receiveThread = null;
        }
        receiveThread = new SocketReceiveThread();
        receiveThread.start();
    }

    private synchronized void disconnect() {
        alarmManager.cancel(mPAlarmIntent);// 取消重连闹钟
        try {
            unregisterReceiver(mAlarmReceiver);// 注销广播监听
        } catch (Exception ex) {
            Log.e(TAG, "unregister alarmReceiver error " + ex.getMessage());
        }
        if (mTimer != null) {
            mTimer.cancel();
        }
    }

    public void sendMessage(BaseMessage message) {
        Log.i(TAG, "sendMessage: " + message.getAllData().length);
        if (sendThread != null) {
            sendThread = null;
        }
        sendThread = new SocketSendThread(message);
        sendThread.start();
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
            intent.putExtra("message", message);
            XPTApplication.getInstance().sendBroadcast(intent);
            Socket mSocket = null;
            OutputStream outputStream = null;
            try {
//                Socket mSocket = new Socket("192.168.1.195", 5021);
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

    BroadcastReceiver mAlarmReceiver = new BroadcastReceiver() {
        public void onReceive(Context ctx, Intent intent) {
            if (RECONNECT_ALARM.equals(intent.getAction())) {
                receiveMessage();
            }
        }
    };
}
