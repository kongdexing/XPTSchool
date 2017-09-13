package com.xptschool.teacher.push;

import android.util.Log;

import com.android.volley.common.VolleyHttpParamsEntity;
import com.android.volley.common.VolleyHttpService;
import com.xptschool.teacher.http.HttpAction;
import com.xptschool.teacher.model.BeanTeacher;
import com.xptschool.teacher.model.GreenDaoHelper;

/**
 * Created by dexing on 2017/2/4.
 * No1
 */

public class UpushTokenHelper {

    private static String TAG = UpushTokenHelper.class.getSimpleName();

    /**
     * @param device_token
     */
    public static void uploadDevicesToken(String device_token, String push) {
        Log.i(TAG, "uploadDevicesToken: " + device_token + " " + push);
        if (device_token == null || device_token.isEmpty()) {
            return;
        }
        BeanTeacher teacher = GreenDaoHelper.getInstance().getCurrentTeacher();
        if (teacher == null) {
            Log.i(TAG, "uploadDevicesToken: teacher is null");
            return;
        }

        VolleyHttpService.getInstance().sendPostRequest(HttpAction.HOOK_PUSH_TOKEN,
                new VolleyHttpParamsEntity()
                        .addParam("status", "1")    //1登录 2其他(切换\退出)
                        .addParam("user_name", teacher.getLoginName())
                        .addParam("system_model", "1") //1Android 0ios
                        .addParam("user_id", teacher.getU_id())
                        .addParam("device_token", device_token)
                        .addParam("mobile_model", android.os.Build.MODEL)
                        .addParam("push_name", push)
                        .addParam("user_type", "3")
                , null);
    }

    /**
     * 退出账号
     */
    public static void exitAccount() {
        BeanTeacher teacher = GreenDaoHelper.getInstance().getCurrentTeacher();
        if (teacher == null) {
            Log.i(TAG, "exitAccount: teacher is null");
            return;
        }

        VolleyHttpService.getInstance().sendPostRequest(HttpAction.HOOK_PUSH_TOKEN,
                new VolleyHttpParamsEntity()
                        .addParam("status", "2")    //1登录 2其他(切换\退出)
                        .addParam("user_name", teacher.getLoginName())
                        .addParam("system_model", "1") //1Android 0ios
                        .addParam("user_id", teacher.getU_id())
                        .addParam("user_type", "3")
                , null);

    }

}
