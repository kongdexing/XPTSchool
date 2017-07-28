package com.xptschool.parent.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by dexing on 2016/12/20.
 * No1
 */
@Entity
public class BeanParent {

    private String login_name;
    private String sp_id;
    private String parent_name;
    private String parent_phone;
    private String relation;
    private String sex;
    private String u_id;
    private String address;
    private String work_unit;
    private String family_tel;
    private String email;
    private String api_id;
    private String security_key;

    @Generated(hash = 944454190)
    public BeanParent(String login_name, String sp_id, String parent_name, String parent_phone,
            String relation, String sex, String u_id, String address, String work_unit,
            String family_tel, String email, String api_id, String security_key) {
        this.login_name = login_name;
        this.sp_id = sp_id;
        this.parent_name = parent_name;
        this.parent_phone = parent_phone;
        this.relation = relation;
        this.sex = sex;
        this.u_id = u_id;
        this.address = address;
        this.work_unit = work_unit;
        this.family_tel = family_tel;
        this.email = email;
        this.api_id = api_id;
        this.security_key = security_key;
    }

    @Generated(hash = 111404833)
    public BeanParent() {
    }

    public String getLoginName() {
        return login_name;
    }

    public void setLoginName(String login_name) {
        this.login_name = login_name;
    }

    public String getSp_id() {
        return sp_id;
    }

    public void setSp_id(String sp_id) {
        this.sp_id = sp_id;
    }

    public String getParent_name() {
        return parent_name;
    }

    public void setParent_name(String parent_name) {
        this.parent_name = parent_name;
    }

    public String getParent_phone() {
        return parent_phone;
    }

    public void setParent_phone(String parent_phone) {
        this.parent_phone = parent_phone;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getU_id() {
        return u_id;
    }

    public void setU_id(String u_id) {
        this.u_id = u_id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getWork_unit() {
        return work_unit;
    }

    public void setWork_unit(String work_unit) {
        this.work_unit = work_unit;
    }

    public String getFamily_tel() {
        return family_tel;
    }

    public void setFamily_tel(String family_tel) {
        this.family_tel = family_tel;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getApi_id() {
        return api_id;
    }

    public void setApi_id(String api_id) {
        this.api_id = api_id;
    }

    public String getSecurity_key() {
        return security_key;
    }

    public void setSecurity_key(String security_key) {
        this.security_key = security_key;
    }

    public String getLogin_name() {
        return this.login_name;
    }

    public void setLogin_name(String login_name) {
        this.login_name = login_name;
    }
}
