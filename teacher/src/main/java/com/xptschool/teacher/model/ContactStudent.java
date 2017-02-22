package com.xptschool.teacher.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.ToMany;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dexing on 2016/12/8.
 * No1
 */
@Entity
public class ContactStudent implements Serializable{

    private String g_id;
    private String c_id;
    @Id
    private String stu_id;
    private String stu_name;
    private String sex;
    private String birth_date;
    private int age;
    @ToMany(referencedJoinProperty = "stu_id")
    private List<ContactParent> parent;
    private String g_name;
    private String c_name;
    /** Used for active entity operations. */
    @Generated(hash = 1048537117)
    private transient ContactStudentDao myDao;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    @Generated(hash = 852814482)
    public ContactStudent(String g_id, String c_id, String stu_id, String stu_name, String sex,
            String birth_date, int age, String g_name, String c_name) {
        this.g_id = g_id;
        this.c_id = c_id;
        this.stu_id = stu_id;
        this.stu_name = stu_name;
        this.sex = sex;
        this.birth_date = birth_date;
        this.age = age;
        this.g_name = g_name;
        this.c_name = c_name;
    }

    @Generated(hash = 235573954)
    public ContactStudent() {
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

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBirth_date() {
        return birth_date;
    }

    public void setBirth_date(String birth_date) {
        this.birth_date = birth_date;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Keep
    public List<ContactParent> getParent() {
        return parent;
    }

    public void setParent(List<ContactParent> parent) {
        this.parent = parent;
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

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1630755503)
    public synchronized void resetParent() {
        parent = null;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 658892750)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getContactStudentDao() : null;
    }

}
