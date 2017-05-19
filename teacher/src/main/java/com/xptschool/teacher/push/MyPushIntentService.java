package com.xptschool.teacher.push;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.umeng.message.UmengMessageService;
import com.umeng.message.entity.UMessage;
import com.xptschool.teacher.R;
import com.xptschool.teacher.common.ExtraKey;
import com.xptschool.teacher.model.ContactParent;
import com.xptschool.teacher.model.GreenDaoHelper;
import com.xptschool.teacher.ui.chat.ChatActivity;
import com.xptschool.teacher.ui.contact.ContactsActivity;
import com.xptschool.teacher.ui.main.MainActivity;
import com.xptschool.teacher.util.ChatUtil;

import org.android.agoo.common.AgooConstants;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by dexing on 2017/5/19.
 * No1
 */

public class MyPushIntentService extends UmengMessageService {

    private static final String TAG = MyPushIntentService.class.getName();
    private static int notifyId = 0;

    @Override
    public void onMessage(Context context, Intent intent) {
        String message = intent.getStringExtra(AgooConstants.MESSAGE_BODY);
        UMessage msg = null;
        Intent mainIntent = new Intent(this, MainActivity.class);
        boolean notify = true;
        try {
            //可以通过MESSAGE_BODY取得消息体
            msg = new UMessage(new JSONObject(message));
            if (msg.extra != null) {
                Map<String, String> mapExtra = msg.extra;
                for (Map.Entry<String, String> entry : mapExtra.entrySet()) {
                    Log.i(TAG, "getNotification: key=" + entry.getKey() + ", Value = " + entry.getValue());
                }
                try {
                    String go_type = mapExtra.get("go_type");
                    String go_val = mapExtra.get("go_value");
                    // code  to handle message here
                    if (go_type.equals("1")) {
                        String[] vals = go_val.split(",");
                        //查找联系人，聊天记录
                        if (vals.length > 1) {
                            int count = GreenDaoHelper.getInstance().getChatCountByChatId(vals[1]);
                            if (count > 0) {
                                //本地存在此条消息，不予处理
                                notify = false;
                                return;
                            }
                            //查询联系人(家长)
                            ContactParent parent = GreenDaoHelper.getInstance().getStudentParentByPUId(vals[0]);
                            if (parent == null || ChatUtil.currentChatParent == null) {
                                mainIntent = new Intent(this, ContactsActivity.class);
                            } else {
                                Log.i(TAG, "parent: " + parent.toString());
                                Log.i(TAG, "currentChatParent: " + ChatUtil.currentChatParent.toString());

                                //判断当前正在聊天的家长
                                if (parent.getUser_id().equals(ChatUtil.currentChatParent.getUser_id())) {
                                    notify = false;
                                    return;
                                }
                                mainIntent = new Intent(this, ChatActivity.class);
                                mainIntent.putExtra(ExtraKey.CHAT_PARENT, parent);
                            }
                        }
                    }
                } catch (Exception ex) {
                    Log.i(TAG, "onMessage: mapExtra " + ex.getMessage());
                }
            }
        } catch (Exception ex) {
            Log.i(TAG, "onMessage: UMessage " + ex.getMessage());
        } finally {
            if (notify) {
                PendingIntent mainPendingIntent = PendingIntent.getActivity(this, notifyId++, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                //消息提醒
                NotificationManager mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(msg == null ? "新消息提醒" : msg.title)
                        .setContentText(msg == null ? "" : msg.text)
                        .setTicker(msg == null ? "" : msg.ticker)
                        .setContentIntent(mainPendingIntent)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setAutoCancel(true);
                mNotifyManager.notify(notifyId, builder.build());
            }
        }
    }
}
