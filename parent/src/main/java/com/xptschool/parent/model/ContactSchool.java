package com.xptschool.parent.model;

import android.os.Parcel;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

import java.io.Serializable;

/**
 * Created by dexing on 2016/12/8.
 * No1
 */
@Entity
public class ContactSchool implements Serializable {

    private String s_id;
    private String s_name;
    private String a_id;
    private String a_name;
    private String main_zrr;
    private String main_phone;
    private String sub_zzr;
    private String sub_phone;
    private String address;
    private String tel;

    public String getS_id() {
        return s_id;
    }

    public void setS_id(String s_id) {
        this.s_id = s_id;
    }

    public String getS_name() {
        return s_name;
    }

    public void setS_name(String s_name) {
        this.s_name = s_name;
    }

    public String getA_id() {
        return a_id;
    }

    public void setA_id(String a_id) {
        this.a_id = a_id;
    }

    public String getA_name() {
        return a_name;
    }

    public void setA_name(String a_name) {
        this.a_name = a_name;
    }

    public String getMain_zrr() {
        return main_zrr;
    }

    public void setMain_zrr(String main_zrr) {
        this.main_zrr = main_zrr;
    }

    public String getMain_phone() {
        return main_phone;
    }

    public void setMain_phone(String main_phone) {
        this.main_phone = main_phone;
    }

    public String getSub_zzr() {
        return sub_zzr;
    }

    public void setSub_zzr(String sub_zzr) {
        this.sub_zzr = sub_zzr;
    }

    public String getSub_phone() {
        return sub_phone;
    }

    public void setSub_phone(String sub_phone) {
        this.sub_phone = sub_phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public ContactSchool() {
    }

    protected ContactSchool(Parcel in) {
        this.s_id = in.readString();
        this.s_name = in.readString();
        this.a_id = in.readString();
        this.a_name = in.readString();
        this.main_zrr = in.readString();
        this.main_phone = in.readString();
        this.sub_zzr = in.readString();
        this.sub_phone = in.readString();
        this.address = in.readString();
        this.tel = in.readString();
    }

    @Generated(hash = 437533329)
    public ContactSchool(String s_id, String s_name, String a_id, String a_name, String main_zrr,
            String main_phone, String sub_zzr, String sub_phone, String address, String tel) {
        this.s_id = s_id;
        this.s_name = s_name;
        this.a_id = a_id;
        this.a_name = a_name;
        this.main_zrr = main_zrr;
        this.main_phone = main_phone;
        this.sub_zzr = sub_zzr;
        this.sub_phone = sub_phone;
        this.address = address;
        this.tel = tel;
    }

}
