package com.xptschool.parent.server;

import android.content.Intent;
import android.util.Log;

import com.xptschool.parent.XPTApplication;
import com.xptschool.parent.common.BroadcastAction;
import com.xptschool.parent.model.BeanChat;
import com.xptschool.parent.model.BeanParent;
import com.xptschool.parent.model.GreenDaoHelper;
import com.xptschool.parent.util.ChatUtil;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URLDecoder;

/**
 * Created by dexing on 2017/5/12.
 * 聊天消息接收线程
 */

public class SocketReceiveThread implements Runnable, Cloneable {

    private String TAG = "PSocketService";

    public SocketReceiveThread() {
        super();
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
            BeanParent parent = GreenDaoHelper.getInstance().getCurrentParent();
            if (parent == null || parent.getU_id() == null || parent.getU_id().isEmpty()) {
                Log.i(TAG, "receiver run parent is null ");
                return;
            }
            mSocket = new Socket(SocketService.socketIP, SocketService.socketReceiverPort);
            if (!mSocket.isConnected()) {
                Log.i(TAG, "connectServerWithTCPSocket unconnected");
                return;
            }
            outputStream = mSocket.getOutputStream();
            JSONObject object = new JSONObject();
            object.put("tertype", "2"); //1老师端，2家长端
            object.put("id", parent.getU_id());
            Log.i(TAG, "receiver run write :" + object.toString());
            outputStream.write(object.toString().getBytes());
            outputStream.flush();
            mSocket.shutdownOutput();

            mmInStream = mSocket.getInputStream();
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
                //无数据，返回
                if (0 >= chat.getSize()) {
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
                    return;
                }

                byte[] b_second = new byte[4];
                if (mmInStream.read(b_second) != -1) {
                    chat.setSeconds(ChatUtil.byteArray2Int(b_second) + "");
                    Log.i(TAG, "b_second:" + chat.getSeconds());
                }

                byte[] b_time = new byte[20];
                if (mmInStream.read(b_time) != -1) {
                    chat.setTime("20" + new String(b_time).trim());
                    Log.i(TAG, "b_time:" + chat.getTime());
                }

                byte[] b_filename = new byte[ChatUtil.fileNameLength_receive];
                if (mmInStream.read(b_filename) != -1) {
                    String name = new String(b_filename);
                    chat.setFileName(name.replace("/", "").trim());
                    Log.i(TAG, "b_filename:" + chat.getFileName());
                }

                if (chat.getSize() > 0) {
                    chat.setHasRead(false);

                    Log.i(TAG, "run: " + chat.getType());
                    char type = chat.getType().toCharArray()[0];
                    if (ChatUtil.TYPE_AMR == type || ChatUtil.TYPE_FILE == type || ChatUtil.TYPE_VIDEO == type) {
                        //创建文件
                        File file = new File(XPTApplication.getInstance().getCachePath() + "/" + chat.getFileName());
                        if (!file.exists()) {
                            file.createNewFile();
                        }
                        byte[] buffer = new byte[10 * 1024];
                        FileOutputStream os = new FileOutputStream(file);

                        int n = 0;
                        int sum = 0;
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
//                                System.out.println("disconnected " + e.getMessage());
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
