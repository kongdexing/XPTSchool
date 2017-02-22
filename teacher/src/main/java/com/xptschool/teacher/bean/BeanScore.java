package com.xptschool.teacher.bean;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by Administrator on 2016/10/31.
 */

public class BeanScore {

    private String studentName;
    private LinkedHashMap<String,String> scoreMap = new LinkedHashMap<>();

    private String courseName;
    private String courseScore;

    public BeanScore(){}

    public BeanScore(String courseName, String courseScore) {
        this.courseName = courseName;
        this.courseScore = courseScore;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public LinkedHashMap<String, String> getScoreMap() {
        return scoreMap;
    }

    public void setScoreMap(LinkedHashMap<String, String> scoreMap) {
        this.scoreMap = scoreMap;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseScore() {
        return courseScore;
    }

    public void setCourseScore(String courseScore) {
        this.courseScore = courseScore;
    }
}
