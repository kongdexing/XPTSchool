package com.shuhai.anfang.report.module;

import java.util.Arrays;
import java.util.List;

/**
 * Created by dexing on 2017/9/26 0026.
 * No1
 */

public class BarProvinceInfo {

    private String[] prov;
    private List<ProvinceInfo> info;

    public String[] getProv() {
        return prov;
    }

    public void setProv(String[] prov) {
        this.prov = prov;
    }

    public List<ProvinceInfo> getInfo() {
        return info;
    }

    public void setInfo(List<ProvinceInfo> info) {
        this.info = info;
    }

    public class ProvinceInfo {
        private String name;
        private int[] data;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int[] getData() {
            return data;
        }

        public void setData(int[] data) {
            this.data = data;
        }
    }

    @Override
    public String toString() {
        return "BarProvinceInfo{" +
                "prov=" + Arrays.toString(prov) +
                ", info=" + info +
                '}';
    }
}
