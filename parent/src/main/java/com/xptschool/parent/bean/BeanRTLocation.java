package com.xptschool.parent.bean;

import com.baidu.mapapi.model.LatLng;
import com.xptschool.parent.common.CommonUtil;

/**
 * Created by dexing on 2016/12/7.
 * No1
 */

public class BeanRTLocation {

    private String longitude;
    private String latitude;
    private String gps_type;
    private String time;
    private String imei;
    private String power;
    private String signal1;

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

    public String getGps_type() {
        return gps_type;
    }

    public void setGps_type(String gps_type) {
        this.gps_type = gps_type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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

    public String getPower() {
        return power;
    }

    public void setPower(String power) {
        this.power = power;
    }

    public String getSignal1() {
        return signal1;
    }

    public void setSignal1(String signal1) {
        this.signal1 = signal1;
    }
}
