package com.xptschool.teacher.common;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import com.android.volley.VolleyError;
import com.android.volley.common.VolleyHttpResult;
import com.android.volley.common.VolleyHttpService;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.xptschool.teacher.R;
import com.xptschool.teacher.XPTApplication;
import com.xptschool.teacher.http.HttpAction;
import com.xptschool.teacher.http.MyVolleyRequestListener;
import com.xptschool.teacher.model.BeanClass;
import com.xptschool.teacher.model.BeanCourse;
import com.xptschool.teacher.model.GreenDaoHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/10/28.
 */

public class CommonUtil {

    private static String TAG = CommonUtil.class.getSimpleName();
    private static DisplayImageOptions options;

    public static String getCurrentDate() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return sDateFormat.format(new java.util.Date());
    }

    public static String getCurrentDateTime() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return sDateFormat.format(new Date());
    }

    public static String getCurrentDateHms() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sDateFormat.format(new Date());
    }

    public static String getDate2StrBefore(int day) {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        if (7 >= dayOfMonth) {
            calendar.add(Calendar.DAY_OF_MONTH, dayOfMonth == 7 ? (-6) : -(dayOfMonth - 1));
        } else {
            calendar.add(Calendar.DAY_OF_MONTH, -day);
        }
        return sDateFormat.format(calendar.getTime());
    }

    public static Date getDateBefore(int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        if (7 >= dayOfMonth) {
            calendar.add(Calendar.DAY_OF_MONTH, dayOfMonth == 7 ? (-6) : -(dayOfMonth - 1));
        } else {
            calendar.add(Calendar.DAY_OF_MONTH, -day);
        }
        return calendar.getTime();
    }

    /**
     * 将长时间格式字符串转换为时间 yyyy-MM-dd
     *
     * @param strDate
     * @return
     */
    public static Date strToDateLong(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }

    public static Date strToDateTimeLong(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }

    public static String parseDate(String ringTime) {
        Log.i(TAG, "parseDate: " + ringTime);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatterHHmm = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        try {
            Date curDate = new Date(System.currentTimeMillis());
            ParsePosition pos = new ParsePosition(0);
            Date ringDate = formatter.parse(ringTime, pos);
            Date ringTheTime = formatterHHmm.parse(ringTime);

            SimpleDateFormat hmfmt = new SimpleDateFormat("HH:mm");
            if (formatter.format(curDate).equals(formatter.format(ringDate))) {
                return hmfmt.format(ringTheTime);
            } else {
                SimpleDateFormat dformat = new SimpleDateFormat("D");
                int interval = Integer.parseInt(dformat.format(curDate)) - Integer.parseInt(dformat.format(ringDate));

                if (interval > 0 && interval == 1) {
                    return "昨天 " + hmfmt.format(ringTheTime);
                } else if (interval > 0 && interval < 7) {
                    SimpleDateFormat eformat = new SimpleDateFormat("E");
                    return eformat.format(ringDate) + " " + hmfmt.format(ringTheTime);
                } else {
                    SimpleDateFormat tformat = new SimpleDateFormat("yyyy/MM/dd");
                    return tformat.format(ringDate);
                }
            }
        } catch (Exception ex) {
            return ringTime;
        }
    }

    public static List<String> getExamDate() {
        List<String> listDate = new ArrayList<>();
        int frontYear = 2014;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int lastYear = calendar.get(Calendar.YEAR);
        int lastMonth = calendar.get(Calendar.MONTH) + 1;
        for (int i = lastYear; i >= frontYear; i--) {
            for (int j = 12; j > 0; j--) {
                if (!(i == lastYear && j > lastMonth)) {
                    String date = i + "-" + String.format("%02d", j);
                    listDate.add(date);
                }
            }
        }
        return listDate;
    }

    public static DisplayImageOptions getDefaultImageLoaderOption() {
        if (options == null) {
            options = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .showImageForEmptyUri(R.drawable.picture_faile)
                    .showImageOnFail(R.drawable.picture_faile)
                    .showImageOnLoading(R.drawable.pictures_no)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .displayer(new SimpleBitmapDisplayer()).build();
        }
        return options;
    }

    public static String encryptToken(String action) {
        String encrypt = action.replace(HttpAction.Index, "") + getCurrentDate().replaceAll("-", "") + GreenDaoHelper.getInstance().getCurrentTeacher().getSecurity_key();
        Log.i(TAG, "encryptToken: encrypt origin :" + encrypt);
        return md5(encrypt);
    }

    public static String md5(String string) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }

    public static void initClassAndCourse() {
        VolleyHttpService.getInstance().sendPostRequest(HttpAction.GetClass, null, new MyVolleyRequestListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onResponse(VolleyHttpResult httpResult) {
                super.onResponse(httpResult);
                if (httpResult.getStatus() == 1) {
                    try {
//                        Gson gson = new Gson();
                        List<BeanClass> listClass = getBeanClassesByHttpResult(httpResult.getData().toString());
                    } catch (Exception ex) {
                        Log.e(TAG, "onResponse: getClass " + ex.getMessage());
                    }
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        VolleyHttpService.getInstance().sendPostRequest(HttpAction.GetCourse, null, new MyVolleyRequestListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onResponse(VolleyHttpResult httpResult) {
                super.onResponse(httpResult);
                if (httpResult.getStatus() == 1) {
                    try {
                        List<BeanCourse> listCourse = getBeanCoursesByHttpResult(httpResult.getData().toString());
                    } catch (Exception ex) {
                        Log.e(TAG, "onResponse: getCourse " + ex.getMessage());
                    }
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

    @NonNull
    public static List<BeanCourse> getBeanCoursesByHttpResult(String httpResult) throws JSONException {
        List<BeanCourse> listCourse = new ArrayList<BeanCourse>();
        JSONArray jsonArray = new JSONArray(httpResult);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject json = jsonArray.getJSONObject(i);
            BeanCourse course = new BeanCourse();
            course.setId(json.getString("id"));
            course.setName(json.getString("name"));
            course.setG_id(json.getString("g_id"));
            course.setG_name(json.getString("g_name"));
            listCourse.add(course);
        }
        GreenDaoHelper.getInstance().insertCourse(listCourse);
        return listCourse;
    }

    @NonNull
    public static List<BeanClass> getBeanClassesByHttpResult(String httpResult) throws JSONException {
        List<BeanClass> listClass = new ArrayList<BeanClass>();
        Gson gson = new Gson();
        listClass = gson.fromJson(httpResult.toString(), new TypeToken<List<BeanClass>>() {
        }.getType());
        GreenDaoHelper.getInstance().insertClass(listClass);
        return listClass;
    }

    /**
     * 获取当前屏幕旋转角度
     *
     * @param context
     * @return 0表示是竖屏; 90表示是左横屏; 180表示是反向竖屏; 270表示是右横屏
     */
    public static int getScreenRotationOnPhone(Context context) {
        final Display display = ((WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        switch (display.getRotation()) {
            case Surface.ROTATION_0:
                return 0;
            case Surface.ROTATION_90:
                return 90;
            case Surface.ROTATION_180:
                return 180;
            case Surface.ROTATION_270:
                return -90;
        }
        return 0;
    }

    public static final LatLng[] latlngs = new LatLng[]{
            new LatLng(39.963175, 116.400244),
            new LatLng(39.942821, 116.369199),
            new LatLng(39.939723, 116.425541),
            new LatLng(39.906965, 116.401394)
    };

    public static int getPopDateHeight() {
        return XPTApplication.getInstance().getWindowHeight() / 2;
    }

    public static LatLng convertGPS2BD(LatLng sourceLatLng) {
        // 将GPS设备采集的原始GPS坐标转换成百度坐标
        CoordinateConverter converter = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.GPS);
        // sourceLatLng待转换坐标
        converter.coord(sourceLatLng);
        return converter.convert();
    }

    public static String getDeviceId() {
        try {
            TelephonyManager tm = (TelephonyManager) XPTApplication.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
            return tm.getDeviceId();
        } catch (Exception ex) {
            return "";
        }
    }

    public static void goAppDetailSettingIntent(Context context) {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings", "com.android.setting.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
        }
        context.startActivity(localIntent);
    }

}
