package com.xptschool.teacher.bean;

import com.android.widget.spinner.SpinnerModel;

/**
 * Created by dexing on 2016/12/1.
 * No1
 */

public class BeanExam extends SpinnerModel {

    private String e_id;
    private String type;

    public String getE_id() {
        return e_id;
    }

    public void setE_id(String e_id) {
        this.e_id = e_id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 1 单考,2统考
     *
     * @return
     */
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
