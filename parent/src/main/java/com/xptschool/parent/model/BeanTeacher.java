package com.xptschool.parent.model;

import com.android.widget.spinner.SpinnerModel;

/**
 * Created by dexing on 2016/12/21.
 * No1
 */

public class BeanTeacher extends SpinnerModel {

    private String t_id;
    private String u_id;
    private String charge;
    private String crs_name;

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

    @Override
    public String getName() {
        String str = name;
        if (charge.equals("1")) {
            str += "[班主任]";
        }
        str += " 代课:(" + crs_name + ")";
        return str;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCharge() {
        return charge;
    }

    public void setCharge(String charge) {
        this.charge = charge;
    }

    public String getCrs_name() {
        return crs_name;
    }

    public void setCrs_name(String crs_name) {
        this.crs_name = crs_name;
    }

}
