package com.xptschool.parent.ui.question;

import android.util.Log;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

/**
 * Created by dexing on 2017/5/4.
 * No1
 */

public class BaseMessage {

    private String TAG = BaseMessage.class.getSimpleName();
    private char type; //0文字，1文件，2语音
    private int size;   //
    private String filename;
    private String parentId;
    private String teacherId;

    public byte[] packData(FileInputStream inputStream) {
        byte[] allData = null;
        try {
//            char[] char_type = new char[1];
//            char_type[0] = type;
            byte[] b_type = charToByte(type);
            Log.i(TAG, "packData: b_type size " + b_type.length + "  " + new String(b_type));
            byte[] b_ostype = charToByte('1');
            Log.i(TAG, "packData: b_ostype size " + b_ostype.length + "  " + new String(b_ostype));
            byte[] b_size = intToByteArray(size);
            Log.i(TAG, "packData: b_size size " + size + "--" + b_size.length + "  " + byteArrayToInt(b_size));
            byte[] b_pId = intToByteArray(Integer.parseInt(parentId));
            Log.i(TAG, "packData: b_pId size " + b_pId.length + "  " + byteArrayToInt(b_pId));
            byte[] b_tId = intToByteArray(Integer.parseInt(teacherId));
            Log.i(TAG, "packData: b_tId size " + b_tId.length + "  " + byteArrayToInt(b_tId));
            byte[] b_filename = filename.getBytes();
            Log.i(TAG, "packData: b_filename size " + b_filename.length + "  " + new String(b_filename));

            allData = addBytes(b_type, b_ostype);
            allData = addBytes(allData, b_size);
            Log.i(TAG, "packData t o s : " + allData.length);
            allData = addBytes(allData, b_pId);
            Log.i(TAG, "packData pid: " + allData.length);
            allData = addBytes(allData, b_tId);
            Log.i(TAG, "packData tid: " + allData.length);
            allData = addBytes(allData, b_filename);
            Log.i(TAG, "packData fn: " + allData.length);
//            char[] char_zero = new char[1];
            byte[] b_zero = new byte[3];

            allData = addBytes(allData, b_zero);
            String byteStr = "";
            for (int i = 0; i < allData.length; i++) {
                byteStr += allData[i] + " ";
            }
            Log.i(TAG, "packData: byteStr " + byteStr);
            Log.i(TAG, "packData: fileSize " + size);
            byte buffer[] = new byte[size];
            int offset = 0;
            int numRead = 0;
            while (offset < buffer.length
                    && (numRead = inputStream.read(buffer, offset, buffer.length - offset)) >= 0) {
                offset += numRead;
                Log.i(TAG, "inputStream read: " + offset);
            }
            Log.i(TAG, "packData: offset " + offset);
            // 确保所有数据均被读取
            if (offset != buffer.length) {
                Log.i(TAG, "packData: 文件读取不全");
            }
            allData = addBytes(allData, buffer);
            Log.i(TAG, "packData allData: " + allData.length + " " + new String(allData));
        } catch (Exception ex) {
            allData = null;
        }
        return allData;
    }

    public static byte[] charToByte(char c) {
        byte[] b = new byte[1];
        b[0] = (byte) (c & 0xFF);
        return b;
    }

    private static byte[] getBytes(char[] chars) {
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

//    public static byte[] int2byte(int res) {
//        byte[] targets = new byte[4];
//
//        targets[0] = (byte) (res & 0xff);// 最低位
//        targets[1] = (byte) ((res >> 8) & 0xff);// 次低位
//        targets[2] = (byte) ((res >> 16) & 0xff);// 次高位
//        targets[3] = (byte) (res >>> 24);// 最高位,无符号右移。
//        return targets;
//    }
//
//    public static int byte2int(byte[] res) {
//        // 一个byte数据左移24位变成0x??000000，再右移8位变成0x00??0000
//
//        int targets = (res[0] & 0xff) | ((res[1] << 8) & 0xff00) // | 表示安位或
//                | ((res[2] << 24) >>> 8) | (res[3] << 24);
//        return targets;
//    }

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

    public char getType() {
        return type;
    }

    public void setType(char type) {
        this.type = type;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }
}
