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
import com.xptschool.teacher.model.BeanChat;
import com.xptschool.teacher.model.BeanTeacher;
import com.xptschool.teacher.model.GreenDaoHelper;
import com.xptschool.teacher.ui.contact.BaseMessage;
import com.xptschool.teacher.util.ChatUtil;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
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
                try {
                    BeanChat chat = new BeanChat();
                    byte[] b_type = new byte[1];
                    if (mmInStream.read(b_type) != -1) {
                        chat.setType(new String(b_type));
                        Log.i(TAG, "type: " + chat.getType());
                    }
                    byte[] b_size = new byte[4];
                    if (mmInStream.read(b_size) != -1) {
                        String str = "";
                        for (int i = 0; i < b_size.length; i++) {
                            str += b_size[i] + " ";
                        }
                        Log.i(TAG, "size byte " + str);
                        chat.setSize(ChatUtil.byteArray2Int(b_size));
                        Log.i(TAG, "b_size:" + chat.getSize());
                    }

                    byte[] b_parid = new byte[4];
                    if (mmInStream.read(b_parid) != -1) {
                        chat.setParentId(ChatUtil.byteArray2Int(b_parid) + "");
                        Log.i(TAG, "b_parid:" + chat.getParentId());
                    }

                    byte[] b_terid = new byte[4];
                    if (mmInStream.read(b_terid) != -1) {
                        chat.setTeacherId(ChatUtil.byteArray2Int(b_terid) + "");
                        Log.i(TAG, "b_terid:" + chat.getTeacherId());
                    }

                    byte[] b_chatid = new byte[4];
                    if (mmInStream.read(b_chatid) != -1) {
                        chat.setChatId(ChatUtil.byteArray2Int(b_chatid) + "");
                        Log.i(TAG, "b_chatid:" + chat.getChatId());
                    }

                    byte[] b_second = new byte[4];
                    if (mmInStream.read(b_second) != -1) {
                        chat.setSeconds(ChatUtil.byteArray2Int(b_second) + "");
                        Log.i(TAG, "b_second:" + chat.getSeconds());
                    }

                    byte[] b_time = new byte[20];
                    if (mmInStream.read(b_time) != -1) {
                        chat.setTime(new String(b_time));
                        Log.i(TAG, "b_time:" + chat.getTime());
                    }

                    byte[] b_filename = new byte[ChatUtil.fileNameLength];
                    if (mmInStream.read(b_filename) != -1) {
                        chat.setFileName(new String(b_filename));
                        Log.i(TAG, "b_filename:" + chat.getFileName());
                    }

                    if (chat.getSize() > 0) {
                        if ((ChatUtil.TYPE_AMR + "").equals(chat.getType())) {
                            //创建文件
                            File file = new File(XPTApplication.getInstance().getCachePath() + "/" + chat.getFileName());
                            if (!file.exists()) {
                                file.createNewFile();
                            }
                            byte[] buffer = new byte[chat.getSize()];
                            FileOutputStream os = new FileOutputStream(file);

                            while (mmInStream.read(buffer) != -1) {
                                try {
                                    os.write(buffer);
                                    // Send the obtained bytes to the UI Activity
                                } catch (Exception e) {
                                    System.out.println("disconnected " + e.getMessage());
                                    break;
                                }
                            }
                        } else if ((ChatUtil.TYPE_FILE + "").equals(chat.getType())) {

                        } else if ((ChatUtil.TYPE_TEXT + "").equals(chat.getType())) {
                            byte[] buffer = new byte[chat.getSize()];
                            while (mmInStream.read(buffer) != -1) {
                                try {
                                    System.out.println("SocketReceiveThread read result:"
                                            + (new String(buffer)));
                                    // Send the obtained bytes to the UI Activity
                                } catch (Exception e) {
                                    System.out.println("disconnected " + e.getMessage());
                                    break;
                                }
                            }
                        }
//                        GreenDaoHelper.getInstance().insertChat(chat);
                    }
                } catch (Exception ex) {

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
