package com.xptschool.parent.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/29.
 */

public class BeanNotice implements Parcelable{

    private String m_id;          //公告标识
    private String a_id;
    private String s_id;
    private String g_id;
    private String c_id;
    private String m_type;
    private String title;
    private String content;
    private String jjcd;
    private String m_tzfs;
    private String create_time;
    private String to_user;
    private String user_id;
    private String fsdx;
    private String type;
    private String fs_type;
    private List<BeanClassInfo> classInfo = new ArrayList<>();

    public String getM_id() {
        return m_id;
    }

    public void setM_id(String m_id) {
        this.m_id = m_id;
    }

    public String getA_id() {
        return a_id;
    }

    public void setA_id(String a_id) {
        this.a_id = a_id;
    }

    public String getS_id() {
        return s_id;
    }

    public void setS_id(String s_id) {
        this.s_id = s_id;
    }

    public String getG_id() {
        return g_id;
    }

    public void setG_id(String g_id) {
        this.g_id = g_id;
    }

    public String getC_id() {
        return c_id;
    }

    public void setC_id(String c_id) {
        this.c_id = c_id;
    }

    public String getM_type() {
        return m_type;
    }

    public void setM_type(String m_type) {
        this.m_type = m_type;
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

    public String getJjcd() {
        return jjcd;
    }

    public void setJjcd(String jjcd) {
        this.jjcd = jjcd;
    }

    public String getM_tzfs() {
        return m_tzfs;
    }

    public void setM_tzfs(String m_tzfs) {
        this.m_tzfs = m_tzfs;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getTo_user() {
        return to_user;
    }

    public void setTo_user(String to_user) {
        this.to_user = to_user;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getFsdx() {
        return fsdx;
    }

    public void setFsdx(String fsdx) {
        this.fsdx = fsdx;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFs_type() {
        return fs_type;
    }

    public void setFs_type(String fs_type) {
        this.fs_type = fs_type;
    }

    public List<BeanClassInfo> getClassInfo() {
        return classInfo;
    }

    public void setClassInfo(List<BeanClassInfo> classInfo) {
        this.classInfo = classInfo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.m_id);
        dest.writeString(this.a_id);
        dest.writeString(this.s_id);
        dest.writeString(this.g_id);
        dest.writeString(this.c_id);
        dest.writeString(this.m_type);
        dest.writeString(this.title);
        dest.writeString(this.content);
        dest.writeString(this.jjcd);
        dest.writeString(this.m_tzfs);
        dest.writeString(this.create_time);
        dest.writeString(this.to_user);
        dest.writeString(this.user_id);
        dest.writeString(this.fsdx);
        dest.writeString(this.type);
        dest.writeString(this.fs_type);
        dest.writeTypedList(this.classInfo);
    }

    public BeanNotice() {
    }

    protected BeanNotice(Parcel in) {
        this.m_id = in.readString();
        this.a_id = in.readString();
        this.s_id = in.readString();
        this.g_id = in.readString();
        this.c_id = in.readString();
        this.m_type = in.readString();
        this.title = in.readString();
        this.content = in.readString();
        this.jjcd = in.readString();
        this.m_tzfs = in.readString();
        this.create_time = in.readString();
        this.to_user = in.readString();
        this.user_id = in.readString();
        this.fsdx = in.readString();
        this.type = in.readString();
        this.fs_type = in.readString();
        this.classInfo = in.createTypedArrayList(BeanClassInfo.CREATOR);
    }

    public static final Creator<BeanNotice> CREATOR = new Creator<BeanNotice>() {
        @Override
        public BeanNotice createFromParcel(Parcel source) {
            return new BeanNotice(source);
        }

        @Override
        public BeanNotice[] newArray(int size) {
            return new BeanNotice[size];
        }
    };
}
