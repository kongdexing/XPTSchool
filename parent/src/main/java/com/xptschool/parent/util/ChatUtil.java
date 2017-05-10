package com.xptschool.parent.util;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.xptschool.parent.XPTApplication;
import com.xptschool.parent.common.CommonUtil;

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
    public static int STATUS_SENDING = 0;
    public static int STATUS_SUCCESS = 1;
    public static int STATUS_FAILED = 2;

    public static String TYPE_TEXT = "0"; //0文字，1文件，2语音
    public static String TYPE_FILE = "1";
    public static String TYPE_AMR = "2";

    public static String getFileName(String parentId) {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        TelephonyManager tm = (TelephonyManager) XPTApplication.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
        String DEVICE_ID = tm.getDeviceId();

        String fileName = DEVICE_ID + parentId + sDateFormat.format(new Date());
        fileName = CommonUtil.md5(fileName) + ".amr";

        if (fileName.length() >= fileNameLength) {
            fileName = fileName.substring(fileName.length() - fileNameLength, fileName.length());
        } else {
//            fileName =
            String prefix = "";
            for (int i = 0; i < fileNameLength - fileName.length(); i++) {
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
        for (int i = 0; i < fileNameLength - createTime.length(); i++) {
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

}
