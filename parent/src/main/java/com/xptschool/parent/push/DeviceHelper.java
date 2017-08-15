package com.xptschool.parent.push;

import android.app.ActivityManager;
import android.content.Context;
import android.text.TextUtils;

import java.util.List;

public class DeviceHelper {

    public static String M_XIAOMI = "XIAOMI";
    public static String M_HUAWEI = "HUAWEI";
    public static String M_MEIZU = "MEIZU";

    public static String P_XIAOMI = "MIPush";
    public static String P_HUAWEI = "HWPush";
    public static String P_MEIZU = "MZPush";
    public static String P_UMENG = "UPush";


    public static boolean isServiceRunning(Context context, String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(Integer.MAX_VALUE);
        if (serviceList == null || serviceList.isEmpty())
            return false;
        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className) && TextUtils.equals(
                    serviceList.get(i).service.getPackageName(), context.getPackageName())) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

}
