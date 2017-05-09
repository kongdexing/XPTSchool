package com.xptschool.teacher.util;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

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

    public static String getFileName(String parentId) {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String fileName = parentId + sDateFormat.format(new Date()) + ".amr";
        if (fileName.length() >= 29) {
            fileName = fileName.substring(fileName.length() - 29, fileName.length());
        } else {
//            fileName =
            String prefix = "";
            for (int i = 0; i < 29 - fileName.length(); i++) {
                prefix += " ";
            }
            System.out.println("prefix length :" + prefix.length());
            fileName = prefix + fileName;
        }
        return fileName;
    }

    public static String getCurrentDateHms() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String createTime = sDateFormat.format(new Date());
        String prefix = "";
        for (int i = 0; i < 29 - createTime.length(); i++) {
            prefix += " ";
        }
        createTime = prefix + createTime;
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

}
