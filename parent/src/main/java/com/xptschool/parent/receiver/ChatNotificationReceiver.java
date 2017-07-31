package com.xptschool.parent.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.xptschool.parent.XPTApplication;
import com.xptschool.parent.common.ActivityTaskHelper;
import com.xptschool.parent.ui.chat.ChatActivity;
import com.xptschool.parent.ui.contact.ContactsActivity;
import com.xptschool.parent.ui.main.WelcomeActivity;

/**
 * Created by dexing on 2017/7/31 0031.
 * No1
 */

public class ChatNotificationReceiver extends BroadcastReceiver {

    private String TAG = ChatNotificationReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive: ");
        String topActName = ActivityTaskHelper.getRunningActivityName(XPTApplication.getInstance());
        //判断APP是否在运行
        if (ActivityTaskHelper.isAppRunning(context)) {
            Log.i(TAG, "onReceive: isAppRunning");
            //正在运行，直接跳转至ChatActivity
            if (!topActName.equals(ChatActivity.class.getName())) {
                Log.i(TAG, "onReceive: not chatAct");
                context.startActivity(new Intent(context, ContactsActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        } else {
            //不在运行，跳转至WelcomeAct，然后跳转至ContactsActivity
            Log.i(TAG, "onReceive: not appRunning");
            context.startActivity(new Intent(context, WelcomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }
}
