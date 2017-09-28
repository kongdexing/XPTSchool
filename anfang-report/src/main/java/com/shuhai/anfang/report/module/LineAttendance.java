package com.shuhai.anfang.report.module;

import java.util.List;

/**
 * Created by dexing on 2017/9/27 0027.
 * No1
 */

public class LineAttendance {

    private List<Integer> signin;
    private List<Integer> signout;

    public List<Integer> getSignin() {
        return signin;
    }

    public void setSignin(List<Integer> signin) {
        this.signin = signin;
    }

    public List<Integer> getSignout() {
        return signout;
    }

    public void setSignout(List<Integer> signout) {
        this.signout = signout;
    }

    @Override
    public String toString() {
        return "LineAttendance{" +
                "signin=" + signin +
                ", signout=" + signout +
                '}';
    }
}
