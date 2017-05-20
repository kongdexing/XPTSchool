package com.xptschool.parent.bean;

/**
 * Created by Administrator on 2016/10/26.
 */

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.xptschool.parent.BuildConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * 作业
 */
public class BeanHomeWork implements Parcelable {

    private String h_id;              //作业Id
    private String name;         //作业标题
    private String a_id;
    private String s_id;
    private String g_id;
    private String c_id;
    private String crs_id;
    private String g_name;
    private String c_name;
    private String crs_name;
    private String work_content;    //作业内容
    private String create_time;   //发布时间
    private String finish_time;  //完成时间
    private String user_id;
    private String user_name;
    private int subjectBgColor;
    private List<String> file_path;
    private String amr_file;

    public String getH_id() {
        return h_id;
    }

    public void setH_id(String h_id) {
        this.h_id = h_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getA_id() {
        return a_id;
    }

    public void setA_id(String a_id) {
        this.a_id = a_id;
    }

    public String getS_id() {
        return s_id;
    }

    public void setS_id(String s_id) {
        this.s_id = s_id;
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

    public String getCrs_id() {
        return crs_id;
    }

    public void setCrs_id(String crs_id) {
        this.crs_id = crs_id;
    }

    public String getWork_content() {
        return work_content == null ? "" : work_content;
    }

    public void setWork_content(String work_content) {
        this.work_content = work_content;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getFinish_time() {
        return finish_time;
    }

    public void setFinish_time(String finish_time) {
        this.finish_time = finish_time;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
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

    public String getCrs_name() {
        return crs_name;
    }

    public void setCrs_name(String crs_name) {
        this.crs_name = crs_name;
    }

    public int getSubjectBgColor() {
        return subjectBgColor;
    }

    public void setSubjectBgColor(int subjectBgColor) {
        this.subjectBgColor = subjectBgColor;
    }

    public List<String> getFile_path() {
        Log.i("HomeWork", "getFile_path: " + file_path.size());
        setFile_path(this.file_path);
        Log.i("HomeWork", "after set: " + file_path.size() + " content:" + getWork_content() + " amr:" + amr_file);
        for (int i = 0; i < file_path.size(); i++) {
            if (!file_path.get(i).contains(BuildConfig.SERVICE_URL)) {
                this.file_path.set(i, BuildConfig.SERVICE_URL + file_path.get(i));
            }
        }
        return file_path;
    }

    public void setFile_path(List<String> file_path) {
        Log.i("HomeWork", "setFile_path: " + file_path.size());
        List<String> temp_files = new ArrayList<>();
        for (int i = 0; i < file_path.size(); i++) {
            if (file_path.get(i).contains(".amr")) {
                setAmr_file(file_path.get(i));
                file_path.remove(i);
            } else {
                temp_files.add(file_path.get(i));
            }
        }
        this.file_path = temp_files;
    }

    public String getAmr_file() {
        if (amr_file != null && !amr_file.contains(BuildConfig.SERVICE_URL)) {
            this.amr_file = BuildConfig.SERVICE_URL + this.amr_file;
        }
        return amr_file;
    }

    public void setAmr_file(String amr_file) {
        this.amr_file = amr_file;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.h_id);
        dest.writeString(this.name);
        dest.writeString(this.a_id);
        dest.writeString(this.s_id);
        dest.writeString(this.g_id);
        dest.writeString(this.c_id);
        dest.writeString(this.crs_id);
        dest.writeString(this.g_name);
        dest.writeString(this.c_name);
        dest.writeString(this.crs_name);
        dest.writeString(this.work_content);
        dest.writeString(this.create_time);
        dest.writeString(this.finish_time);
        dest.writeString(this.user_id);
        dest.writeString(this.user_name);
        dest.writeInt(this.subjectBgColor);
        dest.writeStringList(this.file_path);
        dest.writeString(this.amr_file);
    }

    public BeanHomeWork() {
    }

    protected BeanHomeWork(Parcel in) {
        this.h_id = in.readString();
        this.name = in.readString();
        this.a_id = in.readString();
        this.s_id = in.readString();
        this.g_id = in.readString();
        this.c_id = in.readString();
        this.crs_id = in.readString();
        this.g_name = in.readString();
        this.c_name = in.readString();
        this.crs_name = in.readString();
        this.work_content = in.readString();
        this.create_time = in.readString();
        this.finish_time = in.readString();
        this.user_id = in.readString();
        this.user_name = in.readString();
        this.subjectBgColor = in.readInt();
        this.file_path = in.createStringArrayList();
        this.amr_file = in.readString();
    }

    public static final Creator<BeanHomeWork> CREATOR = new Creator<BeanHomeWork>() {
        @Override
        public BeanHomeWork createFromParcel(Parcel source) {
            return new BeanHomeWork(source);
        }

        @Override
        public BeanHomeWork[] newArray(int size) {
            return new BeanHomeWork[size];
        }
    };
}
