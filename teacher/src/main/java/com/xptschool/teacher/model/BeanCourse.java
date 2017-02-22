package com.xptschool.teacher.model;

import com.android.widget.spinner.SpinnerModel;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by dexing on 2016/11/24.
 */
@Entity
public class BeanCourse extends SpinnerModel{

    private String id;  //课程id
    private String name;

    @Generated(hash = 2042123361)
    public BeanCourse(String id, String name) {
        this.id = id;
        this.name = name;
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
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}
