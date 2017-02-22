package com.xptschool.parent.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2016/11/2.
 */

public class BeanLeave implements Parcelable {

    private String id;
    private String g_id;
    private String c_id;
    private String stu_id;
    private String leave_type;
    private String leave_memo;
    private String start_time;
    private String end_time;
    //1已批准 2 已驳回 0 已提交
    private String status;
    private String t_id;
    private String stu_name;
    private String t_name;
    private String g_name;
    private String c_name;
    private String leave_name;
    private String status_name;
    private String reply;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getStu_id() {
        return stu_id;
    }

    public void setStu_id(String stu_id) {
        this.stu_id = stu_id;
    }

    public String getLeave_type() {
        return leave_type;
    }

    public void setLeave_type(String leave_type) {
        this.leave_type = leave_type;
    }

    public String getLeave_memo() {
        return leave_memo;
    }

    public void setLeave_memo(String leave_memo) {
        this.leave_memo = leave_memo;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    /**
     * 1已批准 2 已驳回 0 已提交
     *
     * @return
     */
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getT_id() {
        return t_id;
    }

    public void setT_id(String t_id) {
        this.t_id = t_id;
    }

    public String getStu_name() {
        return stu_name;
    }

    public void setStu_name(String stu_name) {
        this.stu_name = stu_name;
    }

    public String getT_name() {
        return t_name;
    }

    public void setT_name(String t_name) {
        this.t_name = t_name;
    }

    public String getG_name() {
        return g_name;
    }

    public void setG_name(String g_name) {
        this.g_name = g_name;
    }

    public String getC_name() {
        return c_name;
    }

    public void setC_name(String c_name) {
        this.c_name = c_name;
    }

    public String getLeave_name() {
        return leave_name;
    }

    public void setLeave_name(String leave_name) {
        this.leave_name = leave_name;
    }

    public String getStatus_name() {
        return status_name;
    }

    public void setStatus_name(String status_name) {
        this.status_name = status_name;
    }

    public String getReply() {
        if (reply == null || reply.isEmpty()) {
            reply = "无";
        }
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.g_id);
        dest.writeString(this.c_id);
        dest.writeString(this.stu_id);
        dest.writeString(this.leave_type);
        dest.writeString(this.leave_memo);
        dest.writeString(this.start_time);
        dest.writeString(this.end_time);
        dest.writeString(this.status);
        dest.writeString(this.t_id);
        dest.writeString(this.stu_name);
        dest.writeString(this.t_name);
        dest.writeString(this.g_name);
        dest.writeString(this.c_name);
        dest.writeString(this.leave_name);
        dest.writeString(this.status_name);
        dest.writeString(this.reply);
    }

    public BeanLeave() {
    }

    protected BeanLeave(Parcel in) {
        this.id = in.readString();
        this.g_id = in.readString();
        this.c_id = in.readString();
        this.stu_id = in.readString();
        this.leave_type = in.readString();
        this.leave_memo = in.readString();
        this.start_time = in.readString();
        this.end_time = in.readString();
        this.status = in.readString();
        this.t_id = in.readString();
        this.stu_name = in.readString();
        this.t_name = in.readString();
        this.g_name = in.readString();
        this.c_name = in.readString();
        this.leave_name = in.readString();
        this.status_name = in.readString();
        this.reply = in.readString();
    }

    public static final Creator<BeanLeave> CREATOR = new Creator<BeanLeave>() {
        @Override
        public BeanLeave createFromParcel(Parcel source) {
            return new BeanLeave(source);
        }

        @Override
        public BeanLeave[] newArray(int size) {
            return new BeanLeave[size];
        }
    };
}
