package com.xptschool.teacher.ui.chat;

import android.util.Log;

import com.xptschool.teacher.common.CommonUtil;
import com.xptschool.teacher.model.GreenDaoHelper;
import com.xptschool.teacher.util.ChatUtil;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;

/**
 * Created by dexing on 2017/5/4.
 * No1
 */

public class ToSendMessage {

    private String TAG = ToSendMessage.class.getSimpleName();
    private String id;
    private char type; //0文字，1文件，2语音
    private int size;   //
    private String filename;
    private int second = 0;
    private String parentId;
    private String teacherId;
    private String time;
    private String content;
    private byte[] allData;

    public ToSendMessage() {
        this.id = CommonUtil.getUUID();
        this.parentId = "0";
        this.teacherId = "0";
    }

    public byte[] packData(FileInputStream inputStream) {
        byte[] allData = null;
        try {
//            char[] char_type = new char[1];
//            char_type[0] = type;
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

            allData = getBytes(buffer);
        } catch (Exception ex) {
            allData = null;
        }
        return allData;
    }

    private byte[] getBytes(byte[] buffer) {
        byte[] allData;
        byte[] b_type = ChatUtil.charToByte(type);
        Log.i(TAG, "packData: b_type size " + b_type.length + "  " + new String(b_type));
        byte[] b_ostype = ChatUtil.charToByte('1');
        Log.i(TAG, "packData: b_ostype size " + b_ostype.length + "  " + new String(b_ostype));
        byte[] b_frm = ChatUtil.charToByte('1'); //0家长发老师，1老师发家长
        Log.i(TAG, "packData: b_frm size " + b_frm.length + "  " + new String(b_frm));
        byte[] b_size = ChatUtil.intToByteArray(size);
        Log.i(TAG, "packData: b_size size " + size + "--" + b_size.length + "  " + ChatUtil.byteArrayToInt(b_size));
        byte[] b_pId = ChatUtil.intToByteArray(Integer.parseInt(parentId));
        Log.i(TAG, "packData: b_pId size " + b_pId.length + "  " + ChatUtil.byteArrayToInt(b_pId));
        byte[] b_tId = ChatUtil.intToByteArray(Integer.parseInt(teacherId));
        Log.i(TAG, "packData: b_tId size " + b_tId.length + "  " + ChatUtil.byteArrayToInt(b_tId));
        byte[] b_second = ChatUtil.intToByteArray(second);
        Log.i(TAG, "packData: b_second size " + b_second.length + "  " + ChatUtil.byteArrayToInt(b_second));

        byte[] b_username = new byte[ChatUtil.userNameLength];
        try {
            String userName = GreenDaoHelper.getInstance().getCurrentTeacher().getName();
            ByteArrayInputStream bs_name = new ByteArrayInputStream(URLEncoder.encode(userName, "utf-8").getBytes());
            bs_name.read(b_username);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "packData: b_username size " + b_username.length + "  " + new String(b_username));

        byte[] b_filename = new byte[ChatUtil.fileNameLength];
        // 将流与字节数组关联
        int dotIndex = filename.lastIndexOf(".");
        String tempFileName = filename;
        if (dotIndex != -1) {
            String suffix = filename.substring(dotIndex);
            tempFileName = this.getId() + suffix;
        }
        ByteArrayInputStream bs = new ByteArrayInputStream(tempFileName.toString()
                .getBytes());
        try {
            // 将字符串信息写入数组中（保证字符串信息存储的都是*个字节）
            bs.read(b_filename);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.i(TAG, "packData: b_filename size " + b_filename.length + "  " + new String(b_filename));

        allData = ChatUtil.addBytes(b_type, b_ostype);
        allData = ChatUtil.addBytes(allData, b_frm);
        allData = ChatUtil.addBytes(allData, b_size);
        Log.i(TAG, "packData t o s : " + allData.length);
        allData = ChatUtil.addBytes(allData, b_pId);
        Log.i(TAG, "packData pid: " + allData.length);
        allData = ChatUtil.addBytes(allData, b_tId);
        Log.i(TAG, "packData tid: " + allData.length);
        allData = ChatUtil.addBytes(allData, b_second);
        Log.i(TAG, "packData second: " + allData.length);
        allData = ChatUtil.addBytes(allData, b_username);
        Log.i(TAG, "packData name: " + allData.length);
        allData = ChatUtil.addBytes(allData, b_filename);
        Log.i(TAG, "packData fn: " + allData.length);
        byte[] b_zero = new byte[2];
        allData = ChatUtil.addBytes(allData, b_zero);
        allData = ChatUtil.addBytes(allData, buffer);
        Log.i(TAG, "packData allData: " + allData.length + " " + new String(allData));
        return allData;
    }

    public byte[] packData(String message) {
        try {
            message = URLEncoder.encode(message, "utf-8");
            return getBytes(message.getBytes());
        } catch (Exception ex) {
            return null;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public byte[] getAllData() {
        return allData;
    }

    public void setAllData(byte[] allData) {
        this.allData = allData;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
