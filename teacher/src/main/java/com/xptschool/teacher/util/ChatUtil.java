package com.xptschool.teacher.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.xptschool.teacher.model.ContactParent;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by dexing on 2017/5/8.
 * No1
 */

public class ChatUtil {

    public static int fileNameLength = 39;
    public static int userNameLength = 50;
    public static int fileNameLength_receive = 59;

    public static int STATUS_SENDING = 0;
    public static int STATUS_SUCCESS = 1;
    public static int STATUS_FAILED = 2;
    public static int STATUS_REVERT = 3;

    public static char TYPE_TEXT = '0'; //0文字，1文件，2语音，3视频
    public static char TYPE_FILE = '1';
    public static char TYPE_AMR = '2';
    public static char TYPE_VIDEO = '3';
    public static ContactParent currentChatParent;

    public static String getCurrentDateHms() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String createTime = sDateFormat.format(new Date());
//        String prefix = "";
//        for (int i = 0; i < fileNameLength - createTime.length(); i++) {
//            prefix += " ";
//        }
//        createTime = prefix + createTime;
        return createTime;
    }

    public static void showInputWindow(Context mContext, View view) {
        if (mContext == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }

    public static void hideInputWindow(Context mContext, View view) {
        if (mContext == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static byte[] charToByte(char c) {
        byte[] b = new byte[1];
        b[0] = (byte) (c & 0xFF);
        return b;
    }

    public static byte[] getBytes(char[] chars) {
        Charset cs = Charset.forName("UTF-8");
        CharBuffer cb = CharBuffer.allocate(chars.length);
        cb.put(chars);
        cb.flip();
        ByteBuffer bb = cs.encode(cb);
        return bb.array();
    }

    public static byte[] intToByteArray(int i) {
        byte[] result = new byte[4];
        // 由高位到低位
        result[0] = (byte) ((i >> 24) & 0xFF);
        result[1] = (byte) ((i >> 16) & 0xFF);
        result[2] = (byte) ((i >> 8) & 0xFF);
        result[3] = (byte) (i & 0xFF);
        return result;
    }

    /**
     * byte[]转int
     *
     * @param bytes
     * @return
     */
    public static int byteArrayToInt(byte[] bytes) {
        int value = 0;
        // 由高位到低位
        for (int i = 0; i < 4; i++) {
            int shift = (4 - 1 - i) * 8;
            value += (bytes[i] & 0x000000FF) << shift;// 往高位游
        }
        return value;
    }

    public static int byteArray2Int(byte[] b) {
        return b[3] & 0xFF << 24 | (b[2] & 0xFF) << 16 | (b[1] & 0xFF) << 8
                | (b[0] & 0xFF);
    }

    /**
     * @param data1
     * @param data2
     * @return data1 与 data2拼接的结果
     */
    public static byte[] addBytes(byte[] data1, byte[] data2) {
        byte[] data3 = new byte[data1.length + data2.length];
        System.arraycopy(data1, 0, data3, 0, data1.length);
        System.arraycopy(data2, 0, data3, data1.length, data2.length);
        return data3;
    }

    public static int getChatMinWidth(Context context) {
        //获取屏幕的宽度
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        //最大宽度为屏幕宽度的百分之十五
        return (int) (outMetrics.widthPixels * 0.15f);
    }

    public static int getChatMaxWidth(Context context) {
        //获取屏幕的宽度
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        //最大宽度为屏幕宽度的百分之
        return (int) (outMetrics.widthPixels * 0.4f);
    }

}
