package com.xptschool.parent.bean;

import com.baidu.mapapi.model.LatLng;
import com.xptschool.parent.common.CommonUtil;

/**
 * Created by dexing on 2016/12/7.
 * No1
 */

public class BeanHTLocation {

    private String longitude;
    private String latitude;
    private String create_time;
    private String type;
    private String gps_type;
    private String imei;

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

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LatLng getLatLng(boolean isBindRoadForHistoryTrack) {
        try {
            if (isBindRoadForHistoryTrack) {
                //百度鹰眼绑路，接口自动返回百度坐标
                return new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
            } else {
                return CommonUtil.convertGPS2BD(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)));
            }
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public String toString() {
        return "BeanHTLocation{" +
                "longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                ", create_time='" + create_time + '\'' +
                ", type='" + type + '\'' +
                ", gps_type='" + gps_type + '\'' +
                ", imei='" + imei + '\'' +
                '}';
    }
}
