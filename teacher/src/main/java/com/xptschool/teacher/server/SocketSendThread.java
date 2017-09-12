package com.xptschool.teacher.server;

import android.content.Intent;
import android.util.Log;

import com.xptschool.teacher.XPTApplication;
import com.xptschool.teacher.common.BroadcastAction;
import com.xptschool.teacher.ui.chat.ToSendMessage;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URLDecoder;

/**
 * Created by dexing on 2017/9/12 0012.
 * No1
 */

public class SocketSendThread extends Thread {

    private String TAG = SocketSendThread.class.getSimpleName();
    private ToSendMessage message;

    public SocketSendThread(ToSendMessage msg) {
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
        intent.putExtra("message", message.getId());
        intent.setAction(BroadcastAction.MESSAGE_SEND_START);
        XPTApplication.getInstance().sendBroadcast(intent);

        Socket mSocket = null;
        OutputStream outputStream = null;
        InputStream inputStream = null;

        try {
            mSocket = new Socket(SocketService.socketIP, SocketService.socketPort);
            if (mSocket == null || !mSocket.isConnected()) {
                Log.i(TAG, "SocketSendThread run: socket is null or unconnected");
                intent.setAction(BroadcastAction.MESSAGE_SEND_FAILED);
                XPTApplication.getInstance().sendBroadcast(intent);
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

            //发送聊天消息，发送成功接收返回的chatId
            outputStream.flush();
            mSocket.shutdownOutput();

            inputStream = mSocket.getInputStream();
            byte[] buffer = new byte[10];
            while (inputStream.read(buffer) != -1) {
                try {
                    String chatId = URLDecoder.decode(new String(buffer), "utf-8").trim();
                    Log.i(TAG, "receive chatId utf-8: " + chatId.trim());
                    intent.putExtra("chatId", chatId);
                    // Send the obtained bytes to the UI Activity
                } catch (Exception e) {
                    Log.i(TAG, "disconnected " + e.getMessage());
                    break;
                }
            }

            //发送完成
            intent.setAction(BroadcastAction.MESSAGE_SEND_SUCCESS);
            XPTApplication.getInstance().sendBroadcast(intent);
            Log.i(TAG, "write success");
        } catch (Exception e) {
            //发送失败
            intent.setAction(BroadcastAction.MESSAGE_SEND_FAILED);
            XPTApplication.getInstance().sendBroadcast(intent);
            Log.e(TAG, "Exception during write", e);
        } finally {
            Log.i(TAG, "finally close socket " + this.getId());
            closeSocket(mSocket, outputStream, inputStream);
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
}
