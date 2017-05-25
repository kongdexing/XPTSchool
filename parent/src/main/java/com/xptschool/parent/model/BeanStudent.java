package com.xptschool.parent.model;

import com.android.widget.spinner.SpinnerModel;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/11/10.
 */
@Entity
public class BeanStudent extends SpinnerModel implements Serializable {

    private String s_id;
    private String a_id;
    private String g_id;
    private String c_id;
    @Id
    private String stu_id;
    private String stu_name;
    private String stu_no;
    private String imei_id;
    private String card_phone;
    private String birth_date;
    private String rx_date;
    //0 女 1 男
    private String sex;
    private String s_name;
    private String a_name;
    private String g_name;
    private String c_name;
    private String sos;
    private String whitelist;
    private String monitor;

    @Generated(hash = 1606108849)
    public BeanStudent(String s_id, String a_id, String g_id, String c_id,
                       String stu_id, String stu_name, String stu_no, String imei_id,
                       String card_phone, String birth_date, String rx_date, String sex,
                       String s_name, String a_name, String g_name, String c_name, String sos,
                       String whitelist, String monitor) {
        this.s_id = s_id;
        this.a_id = a_id;
        this.g_id = g_id;
        this.c_id = c_id;
        this.stu_id = stu_id;
        this.stu_name = stu_name;
        this.stu_no = stu_no;
        this.imei_id = imei_id;
        this.card_phone = card_phone;
        this.birth_date = birth_date;
        this.rx_date = rx_date;
        this.sex = sex;
        this.s_name = s_name;
        this.a_name = a_name;
        this.g_name = g_name;
        this.c_name = c_name;
        this.sos = sos;
        this.whitelist = whitelist;
        this.monitor = monitor;
    }

    @Generated(hash = 1456032229)
    public BeanStudent() {
    }

    public String getS_id() {
        return s_id;
    }

    public void setS_id(String s_id) {
        this.s_id = s_id;
    }

    public String getA_id() {
        return a_id;
    }

    public void setA_id(String a_id) {
        this.a_id = a_id;
    }

    public String getG_id() {
        return g_id;
    }

    public void setG_id(String g_id) {
        this.g_id = g_id;
    }

    public String getC_id() {
        return c_id;
    }

    public void setC_id(String c_id) {
        this.c_id = c_id;
    }

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

    public String getStu_no() {
        return stu_no;
    }

    public void setStu_no(String stu_no) {
        this.stu_no = stu_no;
    }

    public String getImei_id() {
        return imei_id;
    }

    public void setImei_id(String imei_id) {
        this.imei_id = imei_id;
    }

    public String getCard_phone() {
        return card_phone;
    }

    public void setCard_phone(String card_phone) {
        this.card_phone = card_phone;
    }

    public String getBirth_date() {
        return birth_date;
    }

    public void setBirth_date(String birth_date) {
        this.birth_date = birth_date;
    }

    public String getRx_date() {
        return rx_date;
    }

    public void setRx_date(String rx_date) {
        this.rx_date = rx_date;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getS_name() {
        return s_name;
    }

    public void setS_name(String s_name) {
        this.s_name = s_name;
    }

    public String getA_name() {
        return a_name;
    }

    public void setA_name(String a_name) {
        this.a_name = a_name;
    }

    public String getG_name() {
        return g_name;
    }

    public void setG_name(String g_name) {
        this.g_name = g_name;
    }

    public String getC_name() {
        return c_name;
    }

    public void setC_name(String c_name) {
        this.c_name = c_name;
    }

    public String getSos() {
        return sos;
    }

    public void setSos(String sos) {
        this.sos = sos;
    }

    public String getWhitelist() {
        return whitelist;
    }

    public void setWhitelist(String whitelist) {
        this.whitelist = whitelist;
    }

    public String getMonitor() {
        return monitor;
    }

    public void setMonitor(String monitor) {
        this.monitor = monitor;
    }

    @Override
    public String getName() {
        return stu_name;
    }


    @Override
    public String toString() {
        return "BeanStudent{" +
                "s_id='" + s_id + '\'' +
                ", a_id='" + a_id + '\'' +
                ", g_id='" + g_id + '\'' +
                ", c_id='" + c_id + '\'' +
                ", stu_id='" + stu_id + '\'' +
                ", stu_name='" + stu_name + '\'' +
                ", stu_no='" + stu_no + '\'' +
                ", imei_id='" + imei_id + '\'' +
                ", birth_date='" + birth_date + '\'' +
                ", rx_date='" + rx_date + '\'' +
                ", sex='" + sex + '\'' +
                ", s_name='" + s_name + '\'' +
                ", a_name='" + a_name + '\'' +
                ", g_name='" + g_name + '\'' +
                ", c_name='" + c_name + '\'' +
                ", sos='" + sos + '\'' +
                ", whitelist='" + whitelist + '\'' +
                ", monitor='" + monitor + '\'' +
                '}';
    }
}
