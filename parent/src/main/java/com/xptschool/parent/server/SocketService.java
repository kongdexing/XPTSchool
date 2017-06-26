package com.xptschool.parent.server;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.coolerfall.daemon.Daemon;
import com.xptschool.parent.XPTApplication;
import com.xptschool.parent.common.BroadcastAction;
import com.xptschool.parent.model.ToSendMessage;

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

    public void sendMessage(ToSendMessage message) {
        Log.i(TAG, "sendMessage: " + message.getAllData().length);
//        SocketSendThread sendThread = new SocketSendThread(message);
//        sendThread.start();

        sendThreadPool.execute(new SocketSendThread(message));
    }

    private class SocketSendThread extends Thread {

        private ToSendMessage message;

        public SocketSendThread(ToSendMessage msg) {
            super();
            message = msg;
        }

        @Override
        public void run() {
            super.run();
            Log.i(TAG, "SocketSendThread run: " + this.getId());
            Log.i(TAG, "write start send size " + message.getSize());
            //开始发送
            Intent intent = new Intent();
            intent.putExtra("message", message.getId());
            intent.setAction(BroadcastAction.MESSAGE_SEND_START);
            XPTApplication.getInstance().sendBroadcast(intent);

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

                byte[] allData = message.getAllData();
                int n = 0;
                while (n != -1) {
                    byte[] temp = new byte[10 * 1024];
                    if (temp.length > allData.length - n) {
                        temp = new byte[allData.length - n];
                    }
                    System.arraycopy(allData, n, temp, 0, temp.length);
                    outputStream.write(temp);
                    n += temp.length;
                    Log.i(TAG, "send n=" + n);
                    if (n >= allData.length) {
                        n = -1;
                    }
                }
//                outputStream.write(message.getAllData());

                outputStream.flush();
                //发送完成
                intent.setAction(BroadcastAction.MESSAGE_SEND_SUCCESS);
                Log.i(TAG, "write success");
            } catch (Exception e) {
                intent.setAction(BroadcastAction.MESSAGE_SEND_FAILED);
                //发送失败
                Log.i(TAG, "Exception during write", e);
            } finally {
                Log.i(TAG, "finally close socket " + this.getId());
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
