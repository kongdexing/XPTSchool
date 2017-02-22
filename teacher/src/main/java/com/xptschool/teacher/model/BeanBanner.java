package com.xptschool.teacher.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by dexing on 2017/2/8.
 * No1
 */
@Entity
public class BeanBanner {

    private String imageurl;
    private String mode;
    private String target;

    @Generated(hash = 2017185095)
    public BeanBanner(String imageurl, String mode, String target) {
        this.imageurl = imageurl;
        this.mode = mode;
        this.target = target;
    }

    @Generated(hash = 512429043)
    public BeanBanner() {
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}
