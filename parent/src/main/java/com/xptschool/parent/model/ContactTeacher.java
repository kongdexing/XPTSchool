package com.xptschool.parent.model;

import android.os.Parcel;

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
    private String u_id;
    private String name;
    private String phone;
    private String s_id;
    private String s_name;
    private String a_id;
    private String a_name;
    private String d_name;
    private String education;
    private String sex;
    private String email;
    private String charge;
    private String g_id;
    private String c_id;

    public String getT_id() {
        return t_id;
    }

    public void setT_id(String t_id) {
        this.t_id = t_id;
    }

    public String getU_id() {
        return u_id;
    }

    public void setU_id(String u_id) {
        this.u_id = u_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getS_id() {
        return s_id;
    }

    public void setS_id(String s_id) {
        this.s_id = s_id;
    }

    public String getS_name() {
        return s_name;
    }

    public void setS_name(String s_name) {
        this.s_name = s_name;
    }

    public String getA_id() {
        return a_id;
    }

    public void setA_id(String a_id) {
        this.a_id = a_id;
    }

    public String getA_name() {
        return a_name;
    }

    public void setA_name(String a_name) {
        this.a_name = a_name;
    }

    public String getD_name() {
        return d_name;
    }

    public void setD_name(String d_name) {
        this.d_name = d_name;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCharge() {
        return charge;
    }

    public void setCharge(String charge) {
        this.charge = charge;
    }

    public String getG_id() {
        return g_id;
    }

    public void setG_id(String g_id) {
        this.g_id = g_id;
    }

    public String getC_id() {
        return c_id;
    }

    public void setC_id(String c_id) {
        this.c_id = c_id;
    }

    public ContactTeacher() {
    }

    protected ContactTeacher(Parcel in) {
        this.t_id = in.readString();
        this.name = in.readString();
        this.phone = in.readString();
        this.s_id = in.readString();
        this.s_name = in.readString();
        this.a_id = in.readString();
        this.a_name = in.readString();
        this.d_name = in.readString();
        this.education = in.readString();
        this.sex = in.readString();
        this.email = in.readString();
        this.charge = in.readString();
        this.g_id = in.readString();
        this.c_id = in.readString();
    }

    @Generated(hash = 2076097601)
    public ContactTeacher(String t_id, String u_id, String name, String phone, String s_id, String s_name,
            String a_id, String a_name, String d_name, String education, String sex, String email, String charge,
            String g_id, String c_id) {
        this.t_id = t_id;
        this.u_id = u_id;
        this.name = name;
        this.phone = phone;
        this.s_id = s_id;
        this.s_name = s_name;
        this.a_id = a_id;
        this.a_name = a_name;
        this.d_name = d_name;
        this.education = education;
        this.sex = sex;
        this.email = email;
        this.charge = charge;
        this.g_id = g_id;
        this.c_id = c_id;
    }
}
