package com.shuhai.anfang.report.module;

import java.util.List;

/**
 * Created by dexing on 2017/9/27 0027.
 * No1
 */

public class LineAttendance {

    private List<int[]> signin;
    private List<int[]> signout;

    public List<int[]> getSignin() {
        return signin;
    }

    public void setSignin(List<int[]> signin) {
        this.signin = signin;
    }

    public List<int[]> getSignout() {
        return signout;
    }

    public void setSignout(List<int[]> signout) {
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
