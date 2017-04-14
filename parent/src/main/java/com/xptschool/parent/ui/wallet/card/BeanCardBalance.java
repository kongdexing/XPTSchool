package com.xptschool.parent.ui.wallet.card;

import com.xptschool.parent.model.BeanStudent;

/**
 * Created by dexing on 2017/4/13.
 * No1
 */

public class BeanCardBalance {

    private String balances;
    private String stu_id;
    private String freeze;
    private BeanStudent student;

    public String getStu_id() {
        return stu_id;
    }

    public void setStu_id(String stu_id) {
        this.stu_id = stu_id;
    }

    public String getBalances() {
        return balances;
    }

    public void setBalances(String balances) {
        this.balances = balances;
    }

    public String getFreeze() {
        return freeze;
    }

    public void setFreeze(String freeze) {
        this.freeze = freeze;
    }

    public BeanStudent getStudent() {
        return student;
    }

    public void setStudent(BeanStudent student) {
        this.student = student;
    }
}
