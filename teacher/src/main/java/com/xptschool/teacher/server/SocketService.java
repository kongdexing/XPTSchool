package com.xptschool.teacher.server;

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

import com.xptschool.teacher.XPTApplication;
import com.xptschool.teacher.common.BroadcastAction;
import com.xptschool.teacher.model.BeanTeacher;
import com.xptschool.teacher.model.GreenDaoHelper;
import com.xptschool.teacher.ui.question.BaseMessage;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by dexing on 2017/5/8.
 * No1
 */

public class SocketService extends Service {

    private static String TAG = "TSocketService";
//    private static String socketIP = "192.168.1.195";
//    private static int socketPort = 5020;

    private static String socketIP = "chat.pcuion.com";
    private static int socketPort = 50300;
    private static int socketReceiverPort = 50301;
    private static final String RECONNECT_ALARM = "com.xptschool.teacher.RECONNECT_ALARM";
    private Intent mAlarmIntent = new Intent(RECONNECT_ALARM);
    //    private static Socket mSocket = null;
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
        if (alarmManager == null) {
            mPAlarmIntent = PendingIntent.getBroadcast(this, 0, mAlarmIntent, 0);
            alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            mAlarmReceiver = new ReceiverAlarmReceiver();

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

    private void receiveMessage() {
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
    }

    public void sendMessage(BaseMessage message) {
        Log.i(TAG, "sendMessage: " + message.getAllData().length);
        if (sendThread != null) {
            Log.i(TAG, "sendMessage sendThread is null ");
            sendThread = null;
        }
        sendThread = new SocketSendThread(message);
        sendThread.start();
    }

    private class SocketReceiveThread extends Thread {

        public SocketReceiveThread() {
            super();
        }

        @Override
        public void run() {
            super.run();
            try {
                BeanTeacher teacher = GreenDaoHelper.getInstance().getCurrentTeacher();
                if (teacher == null || teacher.getU_id() == null || teacher.getU_id().isEmpty()) {
                    Log.i(TAG, "receiver run teacher is null ");
                    return;
                }
                Socket mSocket = new Socket(socketIP, socketReceiverPort);
                Log.i(TAG, "serverIP: " + mSocket.getInetAddress() + " " + socketReceiverPort + " status:" + mSocket.isConnected());
                if (!mSocket.isConnected()) {
                    Log.i(TAG, "connectServerWithTCPSocket unconnected");
                    return;
                }
                OutputStream outputStream = mSocket.getOutputStream();
                InputStream mmInStream = mSocket.getInputStream();

                JSONObject object = new JSONObject();
                object.put("tertype", "1"); //1老师端，2家长端
                object.put("id", teacher.getU_id());
                Log.i(TAG, "receiver run write :" + object.toString());
                outputStream.write(object.toString().getBytes());
                outputStream.flush();
                mSocket.shutdownOutput();
                int responseSize = mmInStream.available();
                Log.i(TAG, "run: mmInStream available " + responseSize);
                if (responseSize > 0) {
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
                }
                outputStream.close();
                mmInStream.close();
                mSocket.close();
                Log.i(TAG, "SocketReceiveThread closed ");
            } catch (Exception ex) {
                Log.i(TAG, "SocketReceiveThread Exception: " + ex.getMessage());
            } finally {
                Log.i(TAG, "run finally: ");
            }
        }

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
            try {
                Socket mSocket = new Socket(socketIP, socketPort);

                if (mSocket == null || !mSocket.isConnected()) {
                    Log.i(TAG, "SocketSendThread run: socket is null or unconnected");
                    return;
                }
                OutputStream outputStream = mSocket.getOutputStream();
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
            }
        }

    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "receiveMessage onDestroy()");
        super.onDestroy();
        disconnect();
    }

    private class ReceiverAlarmReceiver extends BroadcastReceiver {
        public void onReceive(Context ctx, Intent intent) {
            if (RECONNECT_ALARM.equals(intent.getAction())) {
                receiveMessage();
            }
        }
    }
}
