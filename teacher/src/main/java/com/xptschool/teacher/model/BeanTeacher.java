package com.xptschool.teacher.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by dexing on 2016/11/2.
 */
@Entity
public class BeanTeacher {

    private String api_id;
    private String security_key;
    private String t_id;
    private String name;        //名称
    private String phone;       //手机号
    private String s_id;      //学校id
    private String s_name;      //学校
    private String a_id;     //学校区域id
    private String a_name;
    private String d_id;     //部门id
    private String d_name;
    private String education;       //教育程序
    private String sex;             //性别
    //0否 1是
    private String charge;   //是否为班主任
    private String u_id;      //用户id

    @Generated(hash = 328157139)
    public BeanTeacher(String api_id, String security_key, String t_id,
            String name, String phone, String s_id, String s_name, String a_id,
            String a_name, String d_id, String d_name, String education,
            String sex, String charge, String u_id) {
        this.api_id = api_id;
        this.security_key = security_key;
        this.t_id = t_id;
        this.name = name;
        this.phone = phone;
        this.s_id = s_id;
        this.s_name = s_name;
        this.a_id = a_id;
        this.a_name = a_name;
        this.d_id = d_id;
        this.d_name = d_name;
        this.education = education;
        this.sex = sex;
        this.charge = charge;
        this.u_id = u_id;
    }

    @Generated(hash = 1398456292)
    public BeanTeacher() {
    }

    public String getApi_id() {
        return api_id;
    }

    public void setApi_id(String api_id) {
        this.api_id = api_id;
    }

    public String getSecurity_key() {
        return security_key;
    }

    public void setSecurity_key(String security_key) {
        this.security_key = security_key;
    }

    public String getT_id() {
        return t_id;
    }

    public void setT_id(String t_id) {
        this.t_id = t_id;
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

    public String getA_id() {
        return a_id;
    }

    public void setA_id(String a_id) {
        this.a_id = a_id;
    }

    public String getD_id() {
        return d_id;
    }

    public void setD_id(String d_id) {
        this.d_id = d_id;
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

    public String getCharge() {
        return charge;
    }

    public void setCharge(String charge) {
        this.charge = charge;
    }

    public String getU_id() {
        return u_id;
    }

    public void setU_id(String u_id) {
        this.u_id = u_id;
    }

    public String getS_name() {
        return s_name;
    }

    public void setS_name(String s_name) {
        this.s_name = s_name;
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
}