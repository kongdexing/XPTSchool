package com.android.widget.audiorecorder;

/**
 * @param
 * @author ldm
 * @description 录音实体类
 * @time 2016/6/25 11:04
 */
public class Recorder {
    float time;//时间长度
    String filePath;//文件路径

    public Recorder(float time, String filePath) {
        super();
        this.time = time;
        this.filePath = filePath;
    }

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

}