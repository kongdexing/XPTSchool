package com.xptschool.parent.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.baidu.mapapi.model.LatLng;
import com.xptschool.parent.common.CommonUtil;

/**
 * Created by Administrator on 2016/11/9.
 */

public class BeanAlarm implements Parcelable {

    private String wm_id;
    private String stu_id;
    private String stu_sex;
    private String longitude;
    private String latitude;
    private String war_status;
    private String war_type;
    private String create_time;
    private String stu_name;
    private String imei;

    public String getWm_id() {
        return wm_id;
    }

    public void setWm_id(String wm_id) {
        this.wm_id = wm_id;
    }

    public String getStu_id() {
        return stu_id;
    }

    public void setStu_id(String stu_id) {
        this.stu_id = stu_id;
    }

    public String getStu_sex() {
        return stu_sex;
    }

    public void setStu_sex(String stu_sex) {
        this.stu_sex = stu_sex;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getWar_status() {
        return war_status;
    }

    public void setWar_status(String war_status) {
        this.war_status = war_status;
    }

    public String getWar_type() {
        return war_type;
    }

    public void setWar_type(String war_type) {
        this.war_type = war_type;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getStu_name() {
        return stu_name;
    }

    public void setStu_name(String stu_name) {
        this.stu_name = stu_name;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public LatLng getLatLng() {
        try {
            return CommonUtil.convertGPS2BD(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)));
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.wm_id);
        dest.writeString(this.stu_id);
        dest.writeString(this.stu_sex);
        dest.writeString(this.longitude);
        dest.writeString(this.latitude);
        dest.writeString(this.war_status);
        dest.writeString(this.war_type);
        dest.writeString(this.create_time);
        dest.writeString(this.stu_name);
        dest.writeString(this.imei);
    }

    public BeanAlarm() {
    }

    protected BeanAlarm(Parcel in) {
        this.wm_id = in.readString();
        this.stu_id = in.readString();
        this.stu_sex = in.readString();
        this.longitude = in.readString();
        this.latitude = in.readString();
        this.war_status = in.readString();
        this.war_type = in.readString();
        this.create_time = in.readString();
        this.stu_name = in.readString();
        this.imei = in.readString();
    }

    public static final Creator<BeanAlarm> CREATOR = new Creator<BeanAlarm>() {
        @Override
        public BeanAlarm createFromParcel(Parcel source) {
            return new BeanAlarm(source);
        }

        @Override
        public BeanAlarm[] newArray(int size) {
            return new BeanAlarm[size];
        }
    };
}
