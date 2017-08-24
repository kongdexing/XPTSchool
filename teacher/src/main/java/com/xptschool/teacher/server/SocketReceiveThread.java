package com.xptschool.teacher.server;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.xptschool.teacher.R;
import com.xptschool.teacher.XPTApplication;
import com.xptschool.teacher.common.ActivityTaskHelper;
import com.xptschool.teacher.common.BroadcastAction;
import com.xptschool.teacher.model.BeanChat;
import com.xptschool.teacher.model.BeanTeacher;
import com.xptschool.teacher.model.GreenDaoHelper;
import com.xptschool.teacher.receiver.ChatNotificationReceiver;
import com.xptschool.teacher.ui.chat.ChatActivity;
import com.xptschool.teacher.util.ChatUtil;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URLDecoder;

/**
 * Created by dexing on 2017/5/12.
 * No1
 */

public class SocketReceiveThread implements Runnable, Cloneable {

    private static String TAG = "TSocketService";

    public SocketReceiveThread() {
        super();
        Log.i(TAG, "SocketReceiveThread: " + this.hashCode());
    }

    public SocketReceiveThread cloneReceiveThread() {
        try {
            return (SocketReceiveThread) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    @Override
    public void run() {
        Socket mSocket = null;
        OutputStream outputStream = null;
        InputStream mmInStream = null;
        try {
            BeanTeacher teacher = GreenDaoHelper.getInstance().getCurrentTeacher();
            if (teacher == null || teacher.getU_id() == null || teacher.getU_id().isEmpty()) {
                Log.i(TAG, "receiver run teacher is null ");
                return;
            }
            mSocket = new Socket(SocketService.socketIP, SocketService.socketReceiverPort);
            if (!mSocket.isConnected()) {
                Log.i(TAG, "connectServerWithTCPSocket unconnected");
                return;
            }
            outputStream = mSocket.getOutputStream();
            mmInStream = mSocket.getInputStream();

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
                    chat.setSize(ChatUtil.byteArray2Int(b_size));
                    Log.i(TAG, "b_size:" + chat.getSize());
                }

                if (chat.getType() == null) {
                    Log.i(TAG, "receive type is null");
                    return;
                }
                //无数据，并且不为撤回消息，则返回
                if (0 >= chat.getSize() && !chat.getType().equals(ChatUtil.TYPE_REVERT + "")) {
                    return;
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
                //判断chatID是否存在，存在则
                if (GreenDaoHelper.getInstance().isExistChat(chat.getChatId())) {
                    if (chat.getType().equals(ChatUtil.TYPE_REVERT + "")) {
                        //将数据改为已撤回
                        BeanChat localChat = GreenDaoHelper.getInstance().getChatByChatId(chat.getChatId());
                        localChat.setSendStatus(ChatUtil.STATUS_REVERT);
                        //本地先更新，防止广播接收不到情况（广播只有在当前聊天界面才可收到）
                        GreenDaoHelper.getInstance().updateChat(localChat);
                        //发送撤回广播
                        Intent intent = new Intent(BroadcastAction.MESSAGE_REVERT_SUCCESS);
                        intent.putExtra("chatId", chat.getChatId());
                        XPTApplication.getInstance().sendBroadcast(intent);
                    }
                    return;
                }

                byte[] b_second = new byte[4];
                if (mmInStream.read(b_second) != -1) {
                    chat.setSeconds(ChatUtil.byteArray2Int(b_second) + "");
                    Log.i(TAG, "b_second:" + chat.getSeconds());
                }

                byte[] b_time = new byte[20];
                if (mmInStream.read(b_time) != -1) {
                    chat.setTime("20" + new String(b_time));
                    Log.i(TAG, "b_time:" + chat.getTime().trim());
                }

                byte[] b_filename = new byte[ChatUtil.fileNameLength_receive];
                if (mmInStream.read(b_filename) != -1) {
                    String name = new String(b_filename);
                    chat.setFileName(name.replace("/", "").trim());
                    Log.i(TAG, "b_filename:" + chat.getFileName());
                }

                if (chat.getSize() > 0) {
                    chat.setHasRead(false);
                    char type = chat.getType().toCharArray()[0];
                    if (ChatUtil.TYPE_AMR == type || ChatUtil.TYPE_FILE == type || ChatUtil.TYPE_VIDEO == type) {
                        //创建文件
                        File file = new File(XPTApplication.getInstance().getCachePath() + "/" + chat.getFileName());
                        if (!file.exists()) {
                            file.createNewFile();
                        }
                        byte[] buffer = new byte[20 * 1024];
                        FileOutputStream os = new FileOutputStream(file);

                        int sum = 0;
                        int n = 0;
                        while (chat.getSize() > sum) {
                            try {
                                n = mmInStream.read(buffer);
                                sum += n;
                                os.write(buffer, 0, n);
                                Log.i(TAG, "receiver sum : " + sum);
                                // Send the obtained bytes to the UI Activity
                            } catch (Exception e) {
                                System.out.println("disconnected " + e.getMessage());
                                break;
                            }
                        }
//                        while ((n = mmInStream.read(buffer)) != -1) {
//                            try {
//                                sum += n;
//                                os.write(buffer, 0, n);
//                                Log.i(TAG, "receiver sum : " + sum);
//                                // Send the obtained bytes to the UI Activity
//                            } catch (Exception e) {
//                                break;
//                            }
//                        }
                    } else if (ChatUtil.TYPE_TEXT == type) {
                        byte[] buffer = new byte[chat.getSize()];
                        String content = "";
                        while (mmInStream.read(buffer) != -1) {
                            try {
                                content += URLDecoder.decode(new String(buffer), "utf-8");
                                Log.i(TAG, "receive content: " + content);
                                // Send the obtained bytes to the UI Activity
                            } catch (Exception e) {
                                System.out.println("disconnected " + e.getMessage());
                                break;
                            }
                        }
                        chat.setContent(content);
                    }
                    GreenDaoHelper.getInstance().insertChat(chat);
                    //send broadcast
                    Intent intent = new Intent(BroadcastAction.MESSAGE_RECEIVED);
                    intent.putExtra("chat", chat);
                    XPTApplication.getInstance().sendBroadcast(intent);
                    Log.i(TAG, "receive data success ");
//                    showNotify();
                }
            } catch (Exception ex) {
                Log.i(TAG, "receive data error:" + ex.getMessage());
            }
        } catch (Exception ex) {
            Log.i(TAG, "SocketReceiveThread Exception: " + ex.getMessage());
        } finally {
            Log.i(TAG, "finally SocketReceiveThread closed ");
            closeSocket(mSocket, outputStream, mmInStream);
        }
    }

    private void showNotify() {
        String topActName = ActivityTaskHelper.getRunningActivityName(XPTApplication.getInstance());
        //判断ChatActivity是否在运行，不在当前运行，则弹出提示信息
        if (!topActName.equals(ChatActivity.class.getName())) {
            Log.i("BaseAct", "showNotify: ");
            //点击通知栏后发送广播
            Intent mainIntent = new Intent(XPTApplication.getInstance(), ChatNotificationReceiver.class);
            PendingIntent mainPendingIntent = PendingIntent.getBroadcast(XPTApplication.getInstance(), this.hashCode(), mainIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            //消息提醒
            NotificationCompat.Builder builder = new NotificationCompat.Builder(XPTApplication.getInstance())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("消息提醒")
                    .setContentText("您有新未读聊天消息，请注意查看")
                    .setContentIntent(mainPendingIntent)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true);

            NotificationManager mNotifyManager = (NotificationManager) XPTApplication.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
            mNotifyManager.notify(1, builder.build());
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
