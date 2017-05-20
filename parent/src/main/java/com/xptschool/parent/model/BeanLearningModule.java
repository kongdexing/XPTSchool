package com.xptschool.parent.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

import java.io.Serializable;

/**
 * Created by dexing on 2017/4/7.
 * No1
 */
@Entity
public class BeanLearningModule implements Serializable {

    private String title;
    private String icon_url;
    private String web_url;

    @Generated(hash = 212471057)
    public BeanLearningModule(String title, String icon_url, String web_url) {
        this.title = title;
        this.icon_url = icon_url;
        this.web_url = web_url;
    }

    @Generated(hash = 1648878028)
    public BeanLearningModule() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIcon_url() {
        return icon_url;
    }

    public void setIcon_url(String icon_url) {
        this.icon_url = icon_url;
    }

    public String getWeb_url() {
        return web_url;
    }

    public void setWeb_url(String web_url) {
        this.web_url = web_url;
    }

}
