package com.xptschool.parent.receiver;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.meizu.cloud.pushinternal.DebugLogger;
import com.meizu.cloud.pushsdk.MzPushMessageReceiver;
import com.meizu.cloud.pushsdk.notification.PushNotificationBuilder;
import com.meizu.cloud.pushsdk.platform.message.PushSwitchStatus;
import com.meizu.cloud.pushsdk.platform.message.RegisterStatus;
import com.meizu.cloud.pushsdk.platform.message.SubAliasStatus;
import com.meizu.cloud.pushsdk.platform.message.SubTagsStatus;
import com.meizu.cloud.pushsdk.platform.message.UnRegisterStatus;
import com.xptschool.parent.R;
import com.xptschool.parent.push.DeviceHelper;
import com.xptschool.parent.push.UpushTokenHelper;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by liaojinlong on 15-6-28.
 */
public class MeiZuPushMsgReceiver extends MzPushMessageReceiver {
    private static final String TAG = MeiZuPushMsgReceiver.class.getSimpleName();

    @Override
    @Deprecated
    public void onRegister(Context context, String s) {
        Log.i(TAG, "onRegister pushID " + s);
    }

    @Override
    public void onMessage(Context context, String s) {
        Log.i(TAG, "onMessage " + s);
        //print(context,context.getPackageName() + " receive message " + s);
        EventBus.getDefault().post(new ThroughMessageEvent(s));
    }

    @Override
    public void onMessage(Context context, Intent intent) {
        Log.i(TAG, "flyme3 onMessage ");
        String content = intent.getExtras().toString();
    }

    @Override
    public void onMessage(Context context, String message, String platformExtra) {
        Log.i(TAG, "onMessage " + message + " platformExtra " + platformExtra);
        //print(context,context.getPackageName() + " receive message " + s);
        EventBus.getDefault().post(new ThroughMessageEvent(message + platformExtra));
    }

    @Override
    @Deprecated
    public void onUnRegister(Context context, boolean b) {
        Log.i(TAG, "onUnRegister " + b);
    }

    @Override
    public void onPushStatus(Context context, PushSwitchStatus pushSwitchStatus) {
        EventBus.getDefault().post(pushSwitchStatus);
    }

    @Override
    public void onRegisterStatus(Context context, RegisterStatus registerStatus) {
        Log.i(TAG, "onRegisterStatus " + registerStatus + " " + context.getPackageName());
        //print(this," onRegisterStatus " + registerStatus);
        UpushTokenHelper.uploadDevicesToken(registerStatus.getPushId(), DeviceHelper.P_MEIZU);
        EventBus.getDefault().post(registerStatus);
    }

    @Override
    public void onUnRegisterStatus(Context context, UnRegisterStatus unRegisterStatus) {
        Log.i(TAG, "onUnRegisterStatus " + unRegisterStatus + " " + context.getPackageName());
        EventBus.getDefault().post(unRegisterStatus);
    }

    @Override
    public void onSubTagsStatus(Context context, SubTagsStatus subTagsStatus) {
        Log.i(TAG, "onSubTagsStatus " + subTagsStatus + " " + context.getPackageName());
        EventBus.getDefault().post(subTagsStatus);
    }

    @Override
    public void onSubAliasStatus(Context context, SubAliasStatus subAliasStatus) {
        Log.i(TAG, "onSubAliasStatus " + subAliasStatus + " " + context.getPackageName());
        EventBus.getDefault().post(subAliasStatus);
    }

    @Override
    public void onUpdateNotificationBuilder(PushNotificationBuilder pushNotificationBuilder) {
        pushNotificationBuilder.setmLargIcon(R.mipmap.ic_small_launcher);
        pushNotificationBuilder.setmStatusbarIcon(R.mipmap.ic_small_launcher);
    }

    @Override
    public void onNotificationArrived(Context context, String title, String content, String selfDefineContentString) {
        DebugLogger.i(TAG, "onNotificationArrived title " + title + "content " + content + " selfDefineContentString " + selfDefineContentString);
    }

    @Override
    public void onNotificationClicked(Context context, String title, String content, String selfDefineContentString) {
        DebugLogger.i(TAG, "onNotificationClicked title " + title + "content " + content + " selfDefineContentString " + selfDefineContentString);
    }

    @Override
    public void onNotificationDeleted(Context context, String title, String content, String selfDefineContentString) {
        DebugLogger.i(TAG, "onNotificationDeleted title " + title + "content " + content + " selfDefineContentString " + selfDefineContentString);
    }

    @Override
    public void onNotifyMessageArrived(Context context, String message) {
        DebugLogger.i(TAG, "onNotifyMessageArrived messsage " + message);
    }

}

