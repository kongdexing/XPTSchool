package com.xptschool.parent.bean;

public class BeanScore {

    private String course_name;
    private String course_score;
    private String course_score_total;

    public String getCourse_name() {
        return course_name;
    }

    public void setCourse_name(String course_name) {
        this.course_name = course_name;
    }

    public String getCourse_score() {
        return course_score;
    }

    public void setCourse_score(String course_score) {
        this.course_score = course_score;
    }

    public String getCourse_score_total() {
        return course_score_total == null ? "100" : course_score_total;
    }

    public void setCourse_score_total(String course_score_total) {
        this.course_score_total = course_score_total;
    }
}
