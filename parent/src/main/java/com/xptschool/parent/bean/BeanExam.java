package com.xptschool.parent.bean;

import java.util.ArrayList;
import java.util.List;

public class BeanExam {

    private String exam_com_type;
    private String exam_type;
    private String exam_name;
    private String start_time;
    private String end_time;

    private List<BeanScore> scores = new ArrayList<>();

    public String getExam_com_type() {
        return exam_com_type;
    }

    public void setExam_com_type(String exam_com_type) {
        this.exam_com_type = exam_com_type;
    }

    public String getExam_type() {
        return exam_type;
    }

    public void setExam_type(String exam_type) {
        this.exam_type = exam_type;
    }

    public String getExam_name() {
        return exam_name;
    }

    public void setExam_name(String exam_name) {
        this.exam_name = exam_name;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public List<BeanScore> getScores() {
        return scores;
    }

    public void setScores(List<BeanScore> scores) {
        this.scores = scores;
    }
}
