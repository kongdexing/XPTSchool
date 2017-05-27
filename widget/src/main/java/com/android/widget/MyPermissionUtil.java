package com.android.widget;

import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.content.Context;
import android.os.Binder;
import android.os.Build;
import android.util.Log;

import java.lang.reflect.Method;

/**
 * Created by dexing on 2017/5/27.
 * No1
 */

public class MyPermissionUtil {

    public static final int OP_FINE_LOCATION = 1;
    /**
     * @hide Causing GPS to run.*-
     */
    public static final int OP_GPS = 2;
    /**
     * @hide
     */
    public static final int OP_VIBRATE = 3;
    /**
     * @hide
     */
    public static final int OP_READ_CONTACTS = 4;
    /**
     * @hide
     */
    public static final int OP_WRITE_CONTACTS = 5;
    /**
     * @hide
     */
    public static final int OP_READ_CALL_LOG = 6;
    /**
     * @hide
     */
    public static final int OP_WRITE_CALL_LOG = 7;
    /**
     * @hide
     */
    public static final int OP_READ_CALENDAR = 8;
    /**
     * @hide
     */
    public static final int OP_WRITE_CALENDAR = 9;
    /**
     * @hide
     */
    public static final int OP_WIFI_SCAN = 10;
    /**
     * @hide
     */
    public static final int OP_POST_NOTIFICATION = 11;
    /**
     * @hide
     */
    public static final int OP_NEIGHBORING_CELLS = 12;
    /**
     * @hide
     */
    public static final int OP_CALL_PHONE = 13;
    /**
     * @hide
     */
    public static final int OP_READ_SMS = 14;
    /**
     * @hide
     */
    public static final int OP_WRITE_SMS = 15;
    /**
     * @hide
     */
    public static final int OP_RECEIVE_SMS = 16;
    /**
     * @hide
     */
    public static final int OP_RECEIVE_EMERGECY_SMS = 17;
    /**
     * @hide
     */
    public static final int OP_RECEIVE_MMS = 18;
    /**
     * @hide
     */
    public static final int OP_RECEIVE_WAP_PUSH = 19;
    /**
     * @hide
     */
    public static final int OP_SEND_SMS = 20;
    /**
     * @hide
     */
    public static final int OP_READ_ICC_SMS = 21;
    /**
     * @hide
     */
    public static final int OP_WRITE_ICC_SMS = 22;
    /**
     * @hide
     */
    public static final int OP_WRITE_SETTINGS = 23;
    /**
     * @hide
     */
    public static final int OP_SYSTEM_ALERT_WINDOW = 24;
    /**
     * @hide
     */
    public static final int OP_ACCESS_NOTIFICATIONS = 25;
    /**
     * @hide
     */
    public static final int OP_CAMERA = 26;
    /**
     * @hide
     */
    public static final int OP_RECORD_AUDIO = 27;
    /**
     * @hide
     */
    public static final int OP_PLAY_AUDIO = 28;
    /**
     * @hide
     */
    public static final int OP_READ_CLIPBOARD = 29;
    /**
     * @hide
     */
    public static final int OP_WRITE_CLIPBOARD = 30;
    /**
     * @hide
     */
    public static final int OP_TAKE_MEDIA_BUTTONS = 31;
    /**
     * @hide
     */
    public static final int OP_TAKE_AUDIO_FOCUS = 32;
    /**
     * @hide
     */
    public static final int OP_AUDIO_MASTER_VOLUME = 33;
    /**
     * @hide
     */
    public static final int OP_AUDIO_VOICE_VOLUME = 34;
    /**
     * @hide
     */
    public static final int OP_AUDIO_RING_VOLUME = 35;
    /**
     * @hide
     */
    public static final int OP_AUDIO_MEDIA_VOLUME = 36;
    /**
     * @hide
     */
    public static final int OP_AUDIO_ALARM_VOLUME = 37;
    /**
     * @hide
     */
    public static final int OP_AUDIO_NOTIFICATION_VOLUME = 38;
    /**
     * @hide
     */
    public static final int OP_AUDIO_BLUETOOTH_VOLUME = 39;
    /**
     * @hide
     */
    public static final int OP_WAKE_LOCK = 40;
    /**
     * @hide Continually monitoring location data.
     */
    public static final int OP_MONITOR_LOCATION = 41;
    /**
     * @hide Continually monitoring location data with a relatively high power request.
     */
    public static final int OP_MONITOR_HIGH_POWER_LOCATION = 42;
    /**
     * @hide
     */
    public static final int _NUM_OP = 43;

    @TargetApi(19)
    public static int checkOp(Context context, int op) {
        final int version = Build.VERSION.SDK_INT;
        Log.i("Chat", "checkOp: " + version);
        if (version >= 19) {
            try {
                AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);

                Method dispatchMethod = AppOpsManager.class.getMethod(
                        "checkOp", new Class[]{int.class, int.class,
                                String.class});
                int mode = (Integer) dispatchMethod.invoke(
                        appOpsManager,
                        new Object[]{op, Binder.getCallingUid(),
                                context.getPackageName()});
                return mode;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return -1;
    }
}
