package com.xptschool.parent.bean;

import com.android.widget.spinner.SpinnerModel;

/**
 * Created by dexing on 2016/11/24.
 */
public class BeanClass extends SpinnerModel {

    private String c_id;  //班级id
    private String c_name;    //班级名称
    private String g_id;
    private String g_name;

    public String getC_id() {
        return c_id;
    }

    public void setC_id(String c_id) {
        this.c_id = c_id;
    }

    public String getC_name() {
        return c_name;
    }

    public void setC_name(String c_name) {
        this.c_name = c_name;
    }

    public String getG_id() {
        return g_id;
    }

    public void setG_id(String g_id) {
        this.g_id = g_id;
    }

    public String getG_name() {
        return g_name;
    }

    public void setG_name(String g_name) {
        this.g_name = g_name;
    }

    @Override
    public String getName() {
        return name == null ? (g_name + c_name) : name;
    }
}
