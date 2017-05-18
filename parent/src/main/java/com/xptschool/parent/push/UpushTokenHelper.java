package com.xptschool.parent.push;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.volley.VolleyError;
import com.android.volley.common.VolleyHttpResult;
import com.android.volley.common.VolleyHttpService;
import com.android.volley.common.VolleyRequestListener;
import com.xptschool.parent.XPTApplication;
import com.xptschool.parent.common.CommonUtil;
import com.xptschool.parent.http.HttpAction;
import com.xptschool.parent.http.MyVolleyRequestListener;
import com.xptschool.parent.model.BeanDeviceToken;
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
        param = getTokenParamString(students, parent, device_token, jsonArray);
        if (param.isEmpty() || jsonArray.length() == 0) {
            Log.i(TAG, "uploadDevicesToken: param " + param);
            Log.i(TAG, "uploadDevicesToken: jsonArray length " + jsonArray.length());
            return;
        }

        BeanDeviceToken deviceToken = new BeanDeviceToken();
        deviceToken.setPhone(parent.getParent_phone());
        deviceToken.setDeviceToken(device_token);
        deviceToken.setParamToken(param);
        Log.i(TAG, "uploadDevicesToken param : " + param);
        //上传成功后保存device_token
        pushTokenOnline(HttpAction.Push_Token + "?type=1&data=" + deviceToken.getParamToken(), deviceToken);

        try {
            JSONArray chatArray = new JSONArray();
            JSONObject chatObject = new JSONObject();
            chatObject.put("mobilenumber", parent.getParent_phone());
            chatObject.put("mobtype", "1");
            chatObject.put("devicetoken", device_token);
            chatObject.put("parentid", parent.getU_id());
            chatObject.put("token", CommonUtil.md5(device_token + parent.getParent_phone() + "shuhaixinxi"));
            chatArray.put(chatObject);

            pushTokenOnline(HttpAction.Push_Token_ForChat + "?data=" + chatArray.toString(), null);
        } catch (Exception ex) {

        }
    }

    private static void pushTokenOnline(String url, final BeanDeviceToken deviceToken) {
//        try {
//            url = URLEncoder.encode(url, "utf-8");
//        } catch (Exception ex) {
//            return;
//        }
        Log.i(TAG, "pushTokenOnline: " + url);
        if (url == null || url.isEmpty()) {
            return;
        }

        VolleyHttpService.getInstance().sendGetRequest(url, new VolleyRequestListener() {
            @Override
            public void onStart() {
                Log.i(TAG, "onStart: ");
            }

            @Override
            public void onResponse(VolleyHttpResult volleyHttpResult) {
                Log.i(TAG, "onResponse: " + volleyHttpResult.toString());
                try {
                    Log.i(TAG, "onResponse: info " + volleyHttpResult.toString());
                    if (volleyHttpResult.getInfo().contains("SUCCESS")) {
                        if (deviceToken != null) {
                            Log.i(TAG, "onResponse: success insert db");
                            GreenDaoHelper.getInstance().insertOrUpdateToken(deviceToken);
                        }
                    }
                } catch (Exception ex) {
                    Log.i(TAG, "onResponse: error " + ex.getMessage());
                }
            }

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.i(TAG, "onErrorResponse: " + volleyError.getMessage());
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
                    object.put("mobtype", "2");
                    object.put("mobilenumber", parent.getParent_phone());
                    object.put("mobmac", DEVICE_ID);
                    object.put("mobinfo", Build.MODEL);
                    object.put("ntime", CommonUtil.getCurrentDateHms());
                    String token = CommonUtil.md5(imei + local_device_token + parent.getParent_phone() + "shuhaixinxi" + DEVICE_ID);
                    object.put("token", token.toLowerCase());
                    object.put("stuname", URLEncoder.encode(students.get(i).getStu_name(), "utf-8"));
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
        pushTokenOnline(HttpAction.Push_Token + "?type=3&data=" + deviceToken.getParamToken(), deviceToken);
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

        BeanParent parent = GreenDaoHelper.getInstance().getCurrentParent();
        if (parent == null) {
            Log.i(TAG, "switchAccount: parent is null");
            return;
        }
        String local_device_token = GreenDaoHelper.getInstance().getTokenByPhone(parent.getParent_phone());
        JSONArray jsonArray = new JSONArray();

        param = getTokenParamString(students, parent, local_device_token, jsonArray);
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
        pushTokenOnline(HttpAction.Push_Token + "?type=2&data=" + deviceToken.getParamToken(), deviceToken);
    }

    private static String getTokenParamString(List<BeanStudent> students, BeanParent parent, String local_device_token, JSONArray jsonArray) {
        String param;
        try {
            String DEVICE_ID = CommonUtil.getDeviceId();
            if (DEVICE_ID == "") {
                return "";
            }
            for (int i = 0; i < students.size(); i++) {
                JSONObject object = new JSONObject();
                String imei = students.get(i).getImei_id();
                if (imei != null && !imei.isEmpty()) {
                    object.put("imei", imei);
                    object.put("devicetoken", local_device_token);
                    object.put("mobtype", "2");
                    object.put("mobilenumber", parent.getParent_phone());
                    object.put("mobmac", DEVICE_ID);
                    object.put("mobinfo", URLEncoder.encode(Build.MODEL, "utf-8"));
                    object.put("ntime", CommonUtil.getCurrentDateHms());
                    String token = CommonUtil.md5(imei + local_device_token + parent.getParent_phone() + "shuhaixinxi" + DEVICE_ID);
                    object.put("token", token.toLowerCase());
                    object.put("stuname", URLEncoder.encode(students.get(i).getStu_name(), "utf-8"));
                    jsonArray.put(object);
                }
            }
            param = jsonArray.toString();
        } catch (Exception ex) {
            param = "";
        }
        return param;
    }

}
