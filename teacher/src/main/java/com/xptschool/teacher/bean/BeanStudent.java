package com.xptschool.teacher.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2016/11/10.
 */

public class BeanStudent implements Parcelable {

    private String stu_id;
    private String stu_name;
    private String stu_no;
    //0 女 1 男
    private String sex;

    public String getStu_id() {
        return stu_id;
    }

    public void setStu_id(String stu_id) {
        this.stu_id = stu_id;
    }

    public String getStu_name() {
        return stu_name == null ? "" : stu_name.trim();
    }

    public void setStu_name(String stu_name) {
        this.stu_name = stu_name;
    }

    public String getStu_no() {
        return stu_no == null ? "" : stu_no.trim();
    }

    public void setStu_no(String stu_no) {
        this.stu_no = stu_no;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.stu_id);
        dest.writeString(this.stu_name);
        dest.writeString(this.stu_no);
        dest.writeString(this.sex);
    }

    public BeanStudent() {
    }

    protected BeanStudent(Parcel in) {
        this.stu_id = in.readString();
        this.stu_name = in.readString();
        this.stu_no = in.readString();
        this.sex = in.readString();
    }

    public static final Creator<BeanStudent> CREATOR = new Creator<BeanStudent>() {
        @Override
        public BeanStudent createFromParcel(Parcel source) {
            return new BeanStudent(source);
        }

        @Override
        public BeanStudent[] newArray(int size) {
            return new BeanStudent[size];
        }
    };
}
