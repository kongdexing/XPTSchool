package com.xptschool.parent.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2016/10/29.
 */
//问题
public class BeanQuestion implements Parcelable {

    private String id;
    private String title;
    private String content;
    private String p_id;
    private String c_name;
    private String g_name;
    private String sender_name;
    private String sender_id;
    private String sender_sex;
    private String receiver_name;
    private String receiver_id;
    private String receiver_sex;
    private String create_time;

    private MessageSendStatus sendStatus = MessageSendStatus.SUCCESS;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getP_id() {
        return p_id;
    }

    public void setP_id(String p_id) {
        this.p_id = p_id;
    }

    public String getC_name() {
        return c_name;
    }

    public void setC_name(String c_name) {
        this.c_name = c_name;
    }

    public String getG_name() {
        return g_name;
    }

    public void setG_name(String g_name) {
        this.g_name = g_name;
    }

    public String getSender_name() {
        return sender_name;
    }

    public void setSender_name(String sender_name) {
        this.sender_name = sender_name;
    }

    public String getReceiver_name() {
        return receiver_name;
    }

    public void setReceiver_name(String receiver_name) {
        this.receiver_name = receiver_name;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public MessageSendStatus getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(MessageSendStatus sendStatus) {
        this.sendStatus = sendStatus;
    }

    public String getSender_sex() {
        return sender_sex;
    }

    public void setSender_sex(String sender_sex) {
        this.sender_sex = sender_sex;
    }

    public String getReceiver_sex() {
        return receiver_sex;
    }

    public void setReceiver_sex(String receiver_sex) {
        this.receiver_sex = receiver_sex;
    }

    public String getSender_id() {
        return sender_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    public String getReceiver_id() {
        return receiver_id;
    }

    public void setReceiver_id(String receiver_id) {
        this.receiver_id = receiver_id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.title);
        dest.writeString(this.content);
        dest.writeString(this.p_id);
        dest.writeString(this.c_name);
        dest.writeString(this.g_name);
        dest.writeString(this.sender_name);
        dest.writeString(this.sender_id);
        dest.writeString(this.sender_sex);
        dest.writeString(this.receiver_name);
        dest.writeString(this.receiver_id);
        dest.writeString(this.receiver_sex);
        dest.writeString(this.create_time);
        dest.writeInt(this.sendStatus == null ? -1 : this.sendStatus.ordinal());
    }

    public BeanQuestion() {
    }

    protected BeanQuestion(Parcel in) {
        this.id = in.readString();
        this.title = in.readString();
        this.content = in.readString();
        this.p_id = in.readString();
        this.c_name = in.readString();
        this.g_name = in.readString();
        this.sender_name = in.readString();
        this.sender_id = in.readString();
        this.sender_sex = in.readString();
        this.receiver_name = in.readString();
        this.receiver_id = in.readString();
        this.receiver_sex = in.readString();
        this.create_time = in.readString();
        int tmpSendStatus = in.readInt();
        this.sendStatus = tmpSendStatus == -1 ? null : MessageSendStatus.values()[tmpSendStatus];
    }

    public static final Creator<BeanQuestion> CREATOR = new Creator<BeanQuestion>() {
        @Override
        public BeanQuestion createFromParcel(Parcel source) {
            return new BeanQuestion(source);
        }

        @Override
        public BeanQuestion[] newArray(int size) {
            return new BeanQuestion[size];
        }
    };
}
