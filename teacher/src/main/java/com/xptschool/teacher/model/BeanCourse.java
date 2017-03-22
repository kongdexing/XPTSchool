package com.xptschool.teacher.model;

import com.android.widget.spinner.SpinnerModel;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by dexing on 2016/11/24.
 */
@Entity
public class BeanCourse extends SpinnerModel {

    private String id = "";  //课程id
    private String name = "";
    private String g_id = "";
    private String g_name = "";

    @Generated(hash = 2040762242)
    public BeanCourse(String id, String name, String g_id, String g_name) {
        this.id = id;
        this.name = name;
        this.g_id = g_id;
        this.g_name = g_name;
    }

    @Generated(hash = 422175938)
    public BeanCourse() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getName() {
        if (name.contains(g_name)) {
            return name;
        }
        return name + "-" + g_name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
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
}
