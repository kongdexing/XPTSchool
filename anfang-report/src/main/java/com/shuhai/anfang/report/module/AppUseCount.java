package com.shuhai.anfang.report.module;

import java.util.List;

/**
 * Created by dexing on 2017/9/27 0027.
 * No1
 */

public class AppUseCount {

    private List<int[]> IOSteacher;
    private List<int[]> Androidteacher;
    private List<int[]> IOSparents;
    private List<int[]> Androidparents;

    public List<int[]> getIOSteacher() {
        return IOSteacher;
    }

    public void setIOSteacher(List<int[]> IOSteacher) {
        this.IOSteacher = IOSteacher;
    }

    public List<int[]> getAndroidteacher() {
        return Androidteacher;
    }

    public void setAndroidteacher(List<int[]> androidteacher) {
        Androidteacher = androidteacher;
    }

    public List<int[]> getIOSparents() {
        return IOSparents;
    }

    public void setIOSparents(List<int[]> IOSparents) {
        this.IOSparents = IOSparents;
    }

    public List<int[]> getAndroidparents() {
        return Androidparents;
    }

    public void setAndroidparents(List<int[]> androidparents) {
        Androidparents = androidparents;
    }
}
