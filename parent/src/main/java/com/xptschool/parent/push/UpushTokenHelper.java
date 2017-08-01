package com.xptschool.parent.push;

import android.util.Log;

import com.android.volley.common.VolleyHttpParamsEntity;
import com.android.volley.common.VolleyHttpService;
import com.xptschool.parent.http.HttpAction;
import com.xptschool.parent.model.BeanParent;
import com.xptschool.parent.model.GreenDaoHelper;

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
        BeanParent parent = GreenDaoHelper.getInstance().getCurrentParent();
        if (parent == null) {
            Log.i(TAG, "uploadDevicesToken: parent is null");
            return;
        }

        VolleyHttpService.getInstance().sendPostRequest(HttpAction.HOOK_PUSH_TOKEN,
                new VolleyHttpParamsEntity()
                        .addParam("status", "1")    //1登录 2其他(切换\退出)
                        .addParam("user_name", parent.getLoginName())
                        .addParam("system_model", "1") //1Android 0ios
                        .addParam("user_id", parent.getU_id())
                        .addParam("device_token", device_token)
                        .addParam("mobile_model", android.os.Build.MODEL)
                        .addParam("push_name", push)
                        .addParam("user_type", "4")
                , null);
    }

    /**
     * 退出账号
     */
    public static void exitAccount() {
        BeanParent parent = GreenDaoHelper.getInstance().getCurrentParent();
        if (parent == null) {
            Log.i(TAG, "exitAccount: parent is null");
            return;
        }

        VolleyHttpService.getInstance().sendPostRequest(HttpAction.HOOK_PUSH_TOKEN,
                new VolleyHttpParamsEntity()
                        .addParam("status", "2")    //1登录 2其他(切换\退出)
                        .addParam("user_name", parent.getLoginName())
                        .addParam("system_model", "1") //1Android 0ios
                        .addParam("user_id", parent.getU_id())
                        .addParam("user_type", "4")
                , null);

    }

}
