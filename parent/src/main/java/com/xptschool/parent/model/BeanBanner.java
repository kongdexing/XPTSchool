package com.xptschool.parent.model;

import com.xptschool.parent.BuildConfig;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by dexing on 2017/2/8.
 * No1
 */
@Entity
public class BeanBanner {

    private String id;
    private String img;
    private String url;
    private String title;
    private String type;

    @Generated(hash = 1902433157)
    public BeanBanner(String id, String img, String url, String title, String type) {
        this.id = id;
        this.img = img;
        this.url = url;
        this.title = title;
        this.type = type;
    }

    @Generated(hash = 512429043)
    public BeanBanner() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImg() {
        if (!img.contains(BuildConfig.SERVICE_URL)) {
            img = BuildConfig.SERVICE_URL + img;
        }
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
