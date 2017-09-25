package com.shuhai.anfang.report.module;

import java.util.List;

/**
 * Created by dexing on 2017/9/25 0025.
 * No1
 */

public class PieAllStuCard {

    private int total;
    private List<StuCardInfo> info;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<StuCardInfo> getInfo() {
        return info;
    }

    public void setInfo(List<StuCardInfo> info) {
        this.info = info;
    }

    public class StuCardInfo {
        private String name;
        private int value;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }

    @Override
    public String toString() {
        return "PieAllStuCard{" +
                "total='" + total + '\'' +
                ", info=" + info +
                '}';
    }
}
