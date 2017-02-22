package com.xptschool.teacher.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

import java.io.Serializable;

/**
 * Created by dexing on 2016/12/8.
 * No1
 */
@Entity
public class ContactTeacher implements Serializable {

    private String t_id;
    private String s_id;
    private String a_id;
    private String name;
    private String sex;
    private String phone;
    private String school_name;
    private String area_name;

    @Generated(hash = 1415018313)
    public ContactTeacher(String t_id, String s_id, String a_id, String name,
            String sex, String phone, String school_name, String area_name) {
        this.t_id = t_id;
        this.s_id = s_id;
        this.a_id = a_id;
        this.name = name;
        this.sex = sex;
        this.phone = phone;
        this.school_name = school_name;
        this.area_name = area_name;
    }

    @Generated(hash = 1177439914)
    public ContactTeacher() {
    }

    public String getT_id() {
        return t_id;
    }

    public void setT_id(String t_id) {
        this.t_id = t_id;
    }

    public String getS_id() {
        return s_id;
    }

    public void setS_id(String s_id) {
        this.s_id = s_id;
    }

    public String getA_id() {
        return a_id;
    }

    public void setA_id(String a_id) {
        this.a_id = a_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSchool_name() {
        return school_name;
    }

    public void setSchool_name(String school_name) {
        this.school_name = school_name;
    }

    public String getArea_name() {
        return area_name;
    }

    public void setArea_name(String area_name) {
        this.area_name = area_name;
    }

}
