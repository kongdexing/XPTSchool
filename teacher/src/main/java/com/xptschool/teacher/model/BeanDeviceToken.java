package com.xptschool.teacher.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by dexing on 2017/1/19.
 * No1
 */
@Entity
public class BeanDeviceToken {

    @Id
    private String userId;
    private String deviceToken;
    private String paramToken;

    @Generated(hash = 884653492)
    public BeanDeviceToken(String userId, String deviceToken, String paramToken) {
        this.userId = userId;
        this.deviceToken = deviceToken;
        this.paramToken = paramToken;
    }

    @Generated(hash = 2136968226)
    public BeanDeviceToken() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
