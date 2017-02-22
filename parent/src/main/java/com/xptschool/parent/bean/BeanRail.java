package com.xptschool.parent.bean;

import com.baidu.mapapi.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dexing on 2016/12/7.
 * 围栏
 */

public class BeanRail implements Serializable {

    private String sr_id;
    private String stu_id;
    private String name;
    private String point;
    private String create_time;

    public String getSr_id() {
        return sr_id;
    }

    public void setSr_id(String sr_id) {
        this.sr_id = sr_id;
    }

    public String getStu_id() {
        return stu_id;
    }

    public void setStu_id(String stu_id) {
        this.stu_id = stu_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public List<LatLng> getLatLngs() {
        List<LatLng> lls = new ArrayList<>();
        String[] points = point.split(",");
        for (int i = 0; i < points.length; i += 2) {
            lls.add(new LatLng(Double.parseDouble(points[i + 1]), Double.parseDouble(points[i])));
        }
        return lls;
    }

}
