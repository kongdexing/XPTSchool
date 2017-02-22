package com.xptschool.parent.push;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.volley.VolleyError;
import com.android.volley.common.VolleyHttpResult;
import com.android.volley.common.VolleyHttpService;
import com.xptschool.parent.XPTApplication;
import com.xptschool.parent.http.HttpAction;
import com.xptschool.parent.http.MyVolleyRequestListener;
import com.xptschool.parent.model.BeanDeviceToken;
import com.xptschool.parent.model.BeanParent;
import com.xptschool.parent.model.BeanStudent;
import com.xptschool.parent.model.GreenDaoHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

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

        List<BeanStudent> students = GreenDaoHelper.getInstance().getStudents();
        if (students.size() == 0) {
            Log.i(TAG, "uploadDevicesToken: students size is 0");
            return;
        }

        String param = "";
        JSONArray jsonArray = new JSONArray();
        BeanParent parent = GreenDaoHelper.getInstance().getCurrentParent();
        if (parent == null) {
            Log.i(TAG, "uploadDevicesToken: parent is null");
            return;
        }
        try {
            TelephonyManager tm = (TelephonyManager) XPTApplication.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
            String DEVICE_ID = tm.getDeviceId();

            for (int i = 0; i < students.size(); i++) {
                JSONObject object = new JSONObject();
                String imei = students.get(i).getImei_id();
                if (imei != null && !imei.isEmpty()) {
                    object.put("imei", imei);
                    object.put("devicetoken", device_token);
                    object.put("mobtype", "2");
                    object.put("mobilenumber", parent.getParent_phone());
                    object.put("mobmac", DEVICE_ID);
                    object.put("online", "1");
                    jsonArray.put(object);
                }
            }
            param = jsonArray.toString();
        } catch (Exception ex) {
            param = "";
        }
        if (param.isEmpty() || jsonArray.length() == 0) {
            Log.i(TAG, "uploadDevicesToken: param " + param);
            Log.i(TAG, "uploadDevicesToken: jsonArray length " + jsonArray.length());
            return;
        }

        //判断与本地device_token是否相同
        String local_device_paramtoken = GreenDaoHelper.getInstance().getParamTokenByPhone(parent.getParent_phone());
        Log.i(TAG, "uploadDevicesToken: local param " + local_device_paramtoken);
        if (local_device_paramtoken.equals(param)) {
            Log.i(TAG, "uploadDevicesToken: local equal");
            return;
        }
        BeanDeviceToken deviceToken = new BeanDeviceToken();
        deviceToken.setPhone(parent.getParent_phone());
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
                    if (volleyHttpResult.getInfo().toString().equals(deviceToken.getParamToken())) {
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
        List<BeanStudent> students = GreenDaoHelper.getInstance().getStudents();
        if (students.size() == 0) {
            Log.i(TAG, "exitAccount: students size is 0");
            return;
        }

        String param = "";
        JSONArray jsonArray = new JSONArray();
        BeanParent parent = GreenDaoHelper.getInstance().getCurrentParent();
        if (parent == null) {
            Log.i(TAG, "exitAccount: parent is null");
            return;
        }
        String local_device_token = "";
        try {
            TelephonyManager tm = (TelephonyManager) XPTApplication.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
            String DEVICE_ID = tm.getDeviceId();
            local_device_token = GreenDaoHelper.getInstance().getTokenByPhone(parent.getParent_phone());

            for (int i = 0; i < students.size(); i++) {
                JSONObject object = new JSONObject();
                String imei = students.get(i).getImei_id();
                if (imei != null && !imei.isEmpty()) {
                    object.put("imei", imei);
                    object.put("devicetoken", local_device_token);
                    object.put("mobilenumber", parent.getParent_phone());
                    object.put("mobmac", DEVICE_ID);
                    object.put("online", "0");
                    jsonArray.put(object);
                }
            }
            param = jsonArray.toString();
        } catch (Exception ex) {
            param = "";
        }
        if (param.isEmpty() || jsonArray.length() == 0) {
            Log.i(TAG, "exitAccount: param " + param);
            Log.i(TAG, "exitAccount: jsonArray length " + jsonArray.length());
            return;
        }

        BeanDeviceToken deviceToken = new BeanDeviceToken();
        deviceToken.setPhone(parent.getParent_phone());
        deviceToken.setDeviceToken(local_device_token);
        deviceToken.setParamToken(param);
        GreenDaoHelper.getInstance().insertOrUpdateToken(deviceToken);
        Log.i(TAG, "exitAccount: " + param);
        //不关心是否退出请求成功，先保存
        pushTokenOnline(HttpAction.Push_Token + "?type=2&data=" + deviceToken.getParamToken(), deviceToken);
    }

    /**
     * 切换账号
     */
    public static void switchAccount() {
        List<BeanStudent> students = GreenDaoHelper.getInstance().getStudents();
        if (students.size() == 0) {
            Log.i(TAG, "switchAccount: students size is 0");
            return;
        }

        String param = "";
        JSONArray jsonArray = new JSONArray();
        BeanParent parent = GreenDaoHelper.getInstance().getCurrentParent();
        if (parent == null) {
            Log.i(TAG, "switchAccount: parent is null");
            return;
        }
        String local_device_token = "";
        try {
            TelephonyManager tm = (TelephonyManager) XPTApplication.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
            String DEVICE_ID = tm.getDeviceId();
            local_device_token = GreenDaoHelper.getInstance().getTokenByPhone(parent.getParent_phone());

            for (int i = 0; i < students.size(); i++) {
                JSONObject object = new JSONObject();
                String imei = students.get(i).getImei_id();
                if (imei != null && !imei.isEmpty()) {
                    object.put("imei", imei);
                    object.put("devicetoken", local_device_token);
                    object.put("mobtype", "2");
                    object.put("mobilenumber", parent.getParent_phone());
                    object.put("mobmac", DEVICE_ID);
                    object.put("online", "0");
                    jsonArray.put(object);
                }
            }
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
        deviceToken.setPhone(parent.getParent_phone());
        deviceToken.setDeviceToken(local_device_token);
        deviceToken.setParamToken(param);
        GreenDaoHelper.getInstance().insertOrUpdateToken(deviceToken);
        Log.i(TAG, "switchAccount: " + param);
        //不关心是否退出请求成功，先保存
        pushTokenOnline(HttpAction.Push_Token + "?type=3&data=" + deviceToken.getParamToken(), deviceToken);
    }

}
