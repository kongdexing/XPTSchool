package com.xptschool.teacher.bean;

import java.util.List;

/**
 * Created by Administrator on 2016/10/31.
 */

public class BeanStudentScore {

    private String studentImg;
    private String studentName;
    private String studentCode;
    private List<BeanScore> scores;

    public String getStudentImg() {
        return studentImg;
    }

    public void setStudentImg(String studentImg) {
        this.studentImg = studentImg;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentCode() {
        return studentCode;
    }

    public void setStudentCode(String studentCode) {
        this.studentCode = studentCode;
    }

    public List<BeanScore> getScores() {
        return scores;
    }

    public void setScores(List<BeanScore> scores) {
        this.scores = scores;
    }
}
