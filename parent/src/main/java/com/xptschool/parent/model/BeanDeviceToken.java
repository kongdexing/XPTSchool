package com.xptschool.parent.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by dexing on 2017/1/19.
 * No1
 */
@Entity
public class BeanDeviceToken {

    @Id
    private String phone;
    private String deviceToken;
    private String paramToken;

    @Generated(hash = 2033068665)
    public BeanDeviceToken(String phone, String deviceToken, String paramToken) {
        this.phone = phone;
        this.deviceToken = deviceToken;
        this.paramToken = paramToken;
    }

    @Generated(hash = 2136968226)
    public BeanDeviceToken() {
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getParamToken() {
        return paramToken;
    }

    public void setParamToken(String paramToken) {
        this.paramToken = paramToken;
    }

    public String getDeviceToken() {
        return this.deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }
}
