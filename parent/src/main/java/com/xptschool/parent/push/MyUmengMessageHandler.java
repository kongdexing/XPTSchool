package com.xptschool.parent.push;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.umeng.message.UTrack;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.entity.UMessage;
import com.xptschool.parent.R;
import com.xptschool.parent.common.SharedPreferencesUtil;
import com.xptschool.parent.ui.alarm.AlarmMapActivity;

/**
 * Created by dexing on 2017/1/19.
 * No1
 */

public class MyUmengMessageHandler extends UmengMessageHandler {

    private String TAG = MyUmengMessageHandler.class.getSimpleName();
//    private int pendingIndex = 0;

    /**
     * 自定义通知栏样式的回调方法
     */
    @Override
    public Notification getNotification(Context context, UMessage msg) {
        Log.i(TAG, "getNotification: builder_id=" + msg.builder_id + " text=" + msg.text + msg.title);
        try {
            switch (msg.builder_id) {
                case 1:
                    //判断通知类型

                    Intent openintent = new Intent(context, AlarmMapActivity.class);
                    openintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    PendingIntent intent = PendingIntent.getActivity(context,0, openintent, PendingIntent.FLAG_CANCEL_CURRENT);

                    Notification.Builder builder = new Notification.Builder(context);
                    RemoteViews myNotificationView = new RemoteViews(context.getPackageName(), R.layout.notification_view);
                    myNotificationView.setTextViewText(R.id.notification_title, msg.text);
                    myNotificationView.setTextViewText(R.id.notification_text, msg.title);
                    builder.setContent(myNotificationView)
                            .setSmallIcon(getSmallIconId(context, msg))
                            .setContentIntent(intent)
                            .setTicker(msg.ticker)
                            .setAutoCancel(true);

                    return builder.getNotification();
                default:
                    //默认为0，若填写的builder_id并不存在，也使用默认。
                    return super.getNotification(context, msg);
            }
        } catch (Exception ex) {
            Log.i(TAG, "getNotification: error " + ex.getMessage());
            return null;
        }
    }


}
