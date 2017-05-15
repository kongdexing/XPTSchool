package com.xptschool.parent.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.xptschool.parent.XPTApplication;
import com.xptschool.parent.server.SocketManager;
import com.xptschool.parent.ui.chat.BaseMessage;
import com.xptschool.parent.util.ChatUtil;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;

/**
 * Created by dexing on 2017/5/10.
 * No1
 */
@Entity
public class BeanChat implements Serializable {

    @Id
    private String chatId;
    private String type;
    private int size;
    private String parentId;
    private String teacherId;
    private String fileName; //文件名
    private String seconds; //语音时长
    private String content; //文字信息
    private boolean isSend = false; //是否为发出
    private int sendStatus; //0发送中，1成功，2失败
    private String time;
    private boolean hasRead = true; //已读未读

    @Generated(hash = 918254695)
    public BeanChat(String chatId, String type, int size, String parentId,
                    String teacherId, String fileName, String seconds, String content,
                    boolean isSend, int sendStatus, String time, boolean hasRead) {
        this.chatId = chatId;
        this.type = type;
        this.size = size;
        this.parentId = parentId;
        this.teacherId = teacherId;
        this.fileName = fileName;
        this.seconds = seconds;
        this.content = content;
        this.isSend = isSend;
        this.sendStatus = sendStatus;
        this.time = time;
        this.hasRead = hasRead;
    }

    @Generated(hash = 1544996546)
    public BeanChat() {
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public boolean isSend() {
        return isSend;
    }

    public void setSend(boolean send) {
        isSend = send;
    }

    public int getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(int sendStatus) {
        this.sendStatus = sendStatus;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isHasRead() {
        return hasRead;
    }

    public void setHasRead(boolean hasRead) {
        this.hasRead = hasRead;
    }

    public boolean getHasRead() {
        return this.hasRead;
    }

    public boolean getIsSend() {
        return this.isSend;
    }

    public void setIsSend(boolean isSend) {
        this.isSend = isSend;
    }

    public String getSeconds() {
        return seconds;
    }

    public void setSeconds(String seconds) {
        this.seconds = seconds;
    }

    public void parseMessageToChat(BaseMessage sendMsg) {
        this.setChatId(sendMsg.getFilename());
        this.setIsSend(true);
        this.setType(sendMsg.getType() + "");
        this.setContent(sendMsg.getContent());
        this.setSeconds(sendMsg.getSecond() + "");
        this.setFileName(sendMsg.getFilename());
        this.setSize(sendMsg.getSize());
        this.setTeacherId(sendMsg.getTeacherId());
        this.setParentId(sendMsg.getParentId());
    }

    public void onReSendChatToMessage() {
        try {
            BaseMessage message = new BaseMessage();
            message.setType(this.getType().charAt(0));
            message.setFilename(this.getFileName());
            message.setSecond(Integer.parseInt(this.getSeconds()));
            message.setSize(getSize());
            message.setParentId(getParentId());
            message.setTeacherId(getTeacherId());
            byte[] allByte = null;
            if (ChatUtil.TYPE_AMR == message.getType()) {
                File file = new File(XPTApplication.getInstance().getCachePath() + "/" + getFileName());
                FileInputStream inputStream = new FileInputStream(file);
                allByte = message.packData(inputStream);
                inputStream.close();
            } else if (ChatUtil.TYPE_TEXT == message.getType()) {
                allByte = message.packData(getContent());
            }
            if (allByte != null) {
                message.setAllData(allByte);
                SocketManager.getInstance().sendMessage(message);
            }
        } catch (Exception ex) {

        }
    }

}
