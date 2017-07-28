package com.xptschool.parent.push;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.volley.VolleyError;
import com.android.volley.common.VolleyHttpParamsEntity;
import com.android.volley.common.VolleyHttpResult;
import com.android.volley.common.VolleyHttpService;
import com.android.volley.common.VolleyRequestListener;
import com.xptschool.parent.XPTApplication;
import com.xptschool.parent.common.CommonUtil;
import com.xptschool.parent.http.HttpAction;
import com.xptschool.parent.model.BeanParent;
import com.xptschool.parent.model.BeanStudent;
import com.xptschool.parent.model.GreenDaoHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.List;

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
                        .addParam("mobilenum", parent.getLoginName())
                        .addParam("mobiletype", "1") //1Android 0ios
                        .addParam("parentid", parent.getU_id())
                        .addParam("devicetoken", device_token)
                        .addParam("mobilemodel", android.os.Build.MODEL)
                        .addParam("pushtype", push)
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
                        .addParam("mobilenum", parent.getLoginName())
                        .addParam("mobiletype", "1") //1Android 0ios
                        .addParam("parentid", parent.getU_id())
                , null);

    }

}
