package com.xptschool.teacher.bean;

/**
 * Created by Administrator on 2016/11/1.
 */

public class BeanCheckin {

    private String stu_id;
    private String stu_name;
    private String signin_time;
    private String signout_time;
    private String leave;

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

    public String getSignin_time() {
        return signin_time;
    }

    public void setSignin_time(String signin_time) {
        this.signin_time = signin_time;
    }

    public String getSignout_time() {
        return signout_time;
    }

    public void setSignout_time(String signout_time) {
        this.signout_time = signout_time;
    }

    public String getLeave() {
        return leave;
    }

    public void setLeave(String leave) {
        this.leave = leave;
    }
}
