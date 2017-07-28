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
        Log.i(TAG, "uploadDevicesToken: " + device_token);
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
                        .addParam("techer_mobile", teacher.getLoginName())
                        .addParam("mobtype", "2") // 2Android 1ios
                        .addParam("teacher_id", teacher.getU_id())
                        .addParam("device_token_teacher", device_token)
                        .addParam("mobilemodel", android.os.Build.MODEL)
                        .addParam("pushtype", push)
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
                        .addParam("techer_mobile", teacher.getLoginName())
                        .addParam("mobiletype", "2") //2Android 1ios
                        .addParam("teacher_id", teacher.getU_id())
                , null);

    }

}
