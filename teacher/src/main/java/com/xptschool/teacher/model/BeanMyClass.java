package com.xptschool.teacher.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by dexing on 2016/12/5.
 * No1
 */
@Entity
public class BeanMyClass {

    private String c_id;
    private String s_id;
    private String a_id;
    private String g_id;
    private String name;
    private String t_id;
    private String memo;
    private String g_name;
    private String t_name;
    private String stu_count;

    @Generated(hash = 1628201600)
    public BeanMyClass(String c_id, String s_id, String a_id, String g_id, String name, String t_id,
            String memo, String g_name, String t_name, String stu_count) {
        this.c_id = c_id;
        this.s_id = s_id;
        this.a_id = a_id;
        this.g_id = g_id;
        this.name = name;
        this.t_id = t_id;
        this.memo = memo;
        this.g_name = g_name;
        this.t_name = t_name;
        this.stu_count = stu_count;
    }

    @Generated(hash = 917129970)
    public BeanMyClass() {
    }

    public String getC_id() {
        return c_id;
    }

    public void setC_id(String c_id) {
        this.c_id = c_id;
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

    public String getG_id() {
        return g_id;
    }

    public void setG_id(String g_id) {
        this.g_id = g_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getG_name() {
        return g_name;
    }

    public void setG_name(String g_name) {
        this.g_name = g_name;
    }

    public String getT_name() {
        return t_name;
    }

    public void setT_name(String t_name) {
        this.t_name = t_name;
    }

    public String getStu_count() {
        return stu_count;
    }

    public void setStu_count(String stu_count) {
        this.stu_count = stu_count;
    }

    public String getT_id() {
        return t_id;
    }

    public void setT_id(String t_id) {
        this.t_id = t_id;
    }
}
