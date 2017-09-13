package com.xptschool.teacher.common;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtil {

    public static final String KEY_SPLASH_INIT = "splash_init";

    public static final String KEY_USER_NAME = "username";
    public static final String KEY_PWD = "password";
    public static final String KEY_UID = "userid";

    /**
     * 保存数据到文件
     *
     * @param context
     * @param key
     * @param data
     */
    public static void saveData(Context context, String key, Object data) {
        MyModulePreference myModulePreference = new MyModulePreference(context);

        String type = data.getClass().getSimpleName();

        if ("Integer".equals(type)) {
            myModulePreference.put(key, (Integer) data);
        } else if ("Boolean".equals(type)) {
            myModulePreference.put(key, (Boolean) data);
        } else if ("String".equals(type)) {
            myModulePreference.put(key, (String) data);
        } else if ("Float".equals(type)) {
            myModulePreference.put(key, (Float) data);
        } else if ("Long".equals(type)) {
            myModulePreference.put(key, (Long) data);
        }
    }

    /**
     * 从文件中读取数据
     *
     * @param context
     * @param key
     * @param defValue
     * @return
     */
    public static Object getData(Context context, String key, Object defValue) {

        MyModulePreference myModulePreference = new MyModulePreference(context);

        String type = defValue.getClass().getSimpleName();

        //defValue为为默认值，如果当前获取不到数据就返回它
        if ("Integer".equals(type)) {
            return myModulePreference.getInt(key, (Integer) defValue);
        } else if ("Boolean".equals(type)) {
            return myModulePreference.getBoolean(key, (Boolean) defValue);
        } else if ("String".equals(type)) {
            return myModulePreference.getString(key, (String) defValue);
        } else if ("Float".equals(type)) {
            return myModulePreference.getFloat(key, (Float) defValue);
        } else if ("Long".equals(type)) {
            return myModulePreference.getLong(key, (Long) defValue);
        }
        return null;
    }

    public static void clearUserInfo(Context context) {
        saveData(context, KEY_PWD, "");
        saveData(context, KEY_UID, "");
    }

}
