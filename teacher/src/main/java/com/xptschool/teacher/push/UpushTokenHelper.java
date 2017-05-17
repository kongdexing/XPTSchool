package com.xptschool.teacher.push;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.volley.VolleyError;
import com.android.volley.common.VolleyHttpResult;
import com.android.volley.common.VolleyHttpService;
import com.xptschool.teacher.XPTApplication;
import com.xptschool.teacher.http.HttpAction;
import com.xptschool.teacher.http.MyVolleyRequestListener;
import com.xptschool.teacher.model.BeanDeviceToken;
import com.xptschool.teacher.model.BeanTeacher;
import com.xptschool.teacher.model.GreenDaoHelper;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by dexing on 2017/2/4.
 * No1
 */

public class UpushTokenHelper {

    private static String TAG = UpushTokenHelper.class.getSimpleName();

    /**
     * 上线
     *
     * @param device_token
     */
    public static void uploadDevicesToken(String device_token) {
        Log.i(TAG, "uploadDevicesToken: " + device_token);

        if (device_token == null || device_token.isEmpty()) {
            return;
        }

        String param = "";
        JSONArray jsonArray = new JSONArray();
        BeanTeacher teacher = GreenDaoHelper.getInstance().getCurrentTeacher();
        if (teacher == null) {
            Log.i(TAG, "uploadDevicesToken: teacher is null");
            return;
        }
        try {
            TelephonyManager tm = (TelephonyManager) XPTApplication.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
            String DEVICE_ID = tm.getDeviceId();

            JSONObject object = new JSONObject();
            object.put("device_token_teacher", device_token);
            object.put("mobtype", "2");
            object.put("techer_mobile", teacher.getPhone());
            object.put("teacher_id", teacher.getU_id());
            jsonArray.put(object);
            param = jsonArray.toString();
        } catch (Exception ex) {
            param = "";
        }
        if (param.isEmpty() || jsonArray.length() == 0) {
            Log.i(TAG, "uploadDevicesToken: param " + param);
            Log.i(TAG, "uploadDevicesToken: jsonArray length " + jsonArray.length());
            return;
        }

        BeanDeviceToken deviceToken = new BeanDeviceToken();
        deviceToken.setPhone(teacher.getPhone());
        deviceToken.setDeviceToken(device_token);
        deviceToken.setParamToken(param);
        Log.i(TAG, "uploadDevicesToken param : " + param);
        //上传成功后保存device_token
        pushTokenOnline(HttpAction.Push_Token + "?type=1&data=" + deviceToken.getParamToken(), deviceToken);
    }

    private static void pushTokenOnline(String url, final BeanDeviceToken deviceToken) {
        Log.i(TAG, "pushTokenOnline: " + url);
        VolleyHttpService.getInstance().sendGetRequest(url, new MyVolleyRequestListener() {
            @Override
            public void onStart() {
                super.onStart();
                Log.i(TAG, "onStart: ");
            }

            @Override
            public void onResponse(VolleyHttpResult volleyHttpResult) {
                super.onResponse(volleyHttpResult);
                Log.i(TAG, "onResponse: " + volleyHttpResult.toString());
                try {
                    Log.i(TAG, "onResponse: info " + volleyHttpResult.getInfo().toString());
                    if (volleyHttpResult.getInfo().toString().contains("SUCCESS")) {
                        Log.i(TAG, "onResponse: success insert db");
                        GreenDaoHelper.getInstance().insertOrUpdateToken(deviceToken);
                    }
                } catch (Exception ex) {
                    Log.i(TAG, "onResponse: error " + ex.getMessage());
                }
            }

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                super.onErrorResponse(volleyError);
                Log.i(TAG, "onErrorResponse: ");
            }
        });
    }

    /**
     * 退出账号
     */
    public static void exitAccount() {

        String param = "";
        JSONArray jsonArray = new JSONArray();
        BeanTeacher teacher = GreenDaoHelper.getInstance().getCurrentTeacher();
        if (teacher == null) {
            Log.i(TAG, "exitAccount: teacher is null");
            return;
        }
        String local_device_token = GreenDaoHelper.getInstance().getTokenByPhone(teacher.getPhone());

        BeanDeviceToken deviceToken = new BeanDeviceToken();
        deviceToken.setPhone(teacher.getPhone());
        deviceToken.setDeviceToken(local_device_token);
        deviceToken.setParamToken(param);
        GreenDaoHelper.getInstance().insertOrUpdateToken(deviceToken);
        Log.i(TAG, "exitAccount: " + param);
        //不关心是否退出请求成功，先保存
        pushTokenOnline(HttpAction.Push_Token + "?type=2&data=" + deviceToken.getDeviceToken(), deviceToken);
    }

    /**
     * 切换账号
     */
    public static void switchAccount() {

        String param = "";
        JSONArray jsonArray = new JSONArray();
        BeanTeacher teacher = GreenDaoHelper.getInstance().getCurrentTeacher();
        if (teacher == null) {
            Log.i(TAG, "switchAccount: teacher is null");
            return;
        }
        String local_device_token = "";
        try {
//            TelephonyManager tm = (TelephonyManager) XPTApplication.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
//            String DEVICE_ID = tm.getDeviceId();
            local_device_token = GreenDaoHelper.getInstance().getTokenByPhone(teacher.getPhone());
            JSONObject object = new JSONObject();

            object.put("device_token_teacher", local_device_token);
            object.put("mobtype", "2");
            object.put("techer_mobile", teacher.getPhone());
            object.put("teacher_id", teacher.getU_id());
            jsonArray.put(object);
            param = jsonArray.toString();
        } catch (Exception ex) {
            param = "";
        }
        if (param.isEmpty() || jsonArray.length() == 0) {
            Log.i(TAG, "switchAccount: param " + param);
            Log.i(TAG, "switchAccount: jsonArray length " + jsonArray.length());
            return;
        }

        BeanDeviceToken deviceToken = new BeanDeviceToken();
        deviceToken.setPhone(teacher.getPhone());
        deviceToken.setDeviceToken(local_device_token);
        deviceToken.setParamToken(param);
        GreenDaoHelper.getInstance().insertOrUpdateToken(deviceToken);
        Log.i(TAG, "switchAccount: " + param);
        //不关心是否退出请求成功，先保存
        pushTokenOnline(HttpAction.Push_Token + "?type=3&data=" + local_device_token, deviceToken);
    }

}
