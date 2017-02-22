package com.xptschool.parent.bean;

import com.xptschool.parent.model.ContactParent;

import java.util.List;

public class BeanStudentDetail {

    private String stu_id;
    private String stu_name;
    //0 女 1 男
    private String sex;
    private String g_id;
    private String c_id;
    private String birth_date;
    private String g_name;
    private String c_name;
    private int age;
    private List<ContactParent> parent_phone;

    public String getStu_id() {
        return stu_id;
    }

    public void setStu_id(String stu_id) {
        this.stu_id = stu_id;
    }

    public String getStu_name() {
        return stu_name;
    }

    public void setStu_name(String stu_name) {
        this.stu_name = stu_name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
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

    public String getBirth_date() {
        return birth_date;
    }

    public void setBirth_date(String birth_date) {
        this.birth_date = birth_date;
    }

    public String getG_name() {
        return g_name;
    }

    public void setG_name(String g_name) {
        this.g_name = g_name;
    }

    public String getC_name() {
        return c_name;
    }

    public void setC_name(String c_name) {
        this.c_name = c_name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public List<ContactParent> getParent_phone() {
        return parent_phone;
    }

    public void setParent_phone(List<ContactParent> parent_phone) {
        this.parent_phone = parent_phone;
    }
}
