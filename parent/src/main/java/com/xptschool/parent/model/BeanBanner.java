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
    private String type;  //1.展示计费 0天数计费 2 点击计费
    private String turn_type; //跳转类型 1代表网页跳转  2代表 程序功能跳转
    private String modelname_android;
    private String modelname_ios;
    private String region_id;
    private String sid;

    @Generated(hash = 17296028)
    public BeanBanner(String id, String img, String url, String title, String type, String turn_type,
            String modelname_android, String modelname_ios, String region_id, String sid) {
        this.id = id;
        this.img = img;
        this.url = url;
        this.title = title;
        this.type = type;
        this.turn_type = turn_type;
        this.modelname_android = modelname_android;
        this.modelname_ios = modelname_ios;
        this.region_id = region_id;
        this.sid = sid;
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

    /**
     * 1.展示计费 0天数计费 2 点击计费
     * @return
     */
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRegion_id() {
        return this.region_id;
    }

    public void setRegion_id(String region_id) {
        this.region_id = region_id;
    }

    public String getModelname_ios() {
        return this.modelname_ios;
    }

    public void setModelname_ios(String modelname_ios) {
        this.modelname_ios = modelname_ios;
    }

    public String getModelname_android() {
        return this.modelname_android;
    }

    public void setModelname_android(String modelname_android) {
        this.modelname_android = modelname_android;
    }

    /**
     * 跳转类型 1代表网页跳转  2代表 程序功能跳转
     * @return
     */
    public String getTurn_type() {
        return this.turn_type;
    }

    public void setTurn_type(String turn_type) {
        this.turn_type = turn_type;
    }

    public String getSid() {
        return this.sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }
}
