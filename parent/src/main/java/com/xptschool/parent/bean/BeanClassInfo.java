package com.xptschool.parent.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.android.widget.spinner.SpinnerModel;

/**
 * Created by dexing on 2017/2/15.
 * No1
 */

public class BeanClassInfo extends SpinnerModel implements Parcelable {

    private String g_id;
    private String c_id;
    private String g_name;
    private String c_name;

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

    @Override
    public String getName() {
        return name == null ? (g_name + c_name) : name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.g_id);
        dest.writeString(this.c_id);
        dest.writeString(this.g_name);
        dest.writeString(this.c_name);
    }

    public BeanClassInfo() {
    }

    protected BeanClassInfo(Parcel in) {
        this.g_id = in.readString();
        this.c_id = in.readString();
        this.g_name = in.readString();
        this.c_name = in.readString();
    }

    public static final Creator<BeanClassInfo> CREATOR = new Creator<BeanClassInfo>() {
        @Override
        public BeanClassInfo createFromParcel(Parcel source) {
            return new BeanClassInfo(source);
        }

        @Override
        public BeanClassInfo[] newArray(int size) {
            return new BeanClassInfo[size];
        }
    };
}
