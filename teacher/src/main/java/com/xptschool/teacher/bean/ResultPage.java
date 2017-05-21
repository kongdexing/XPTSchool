package com.xptschool.teacher.bean;

/**
 * Created by Administrator on 2016/11/21.
 */

public class ResultPage {

    private int total_page;      //总页数
    private int total_count;     //总个数
    private int page;        //当前页索引
    private int page_count;

    public ResultPage() {
        total_page = 1;
        total_count = 0;
        page = 1;
        page_count = 15;
    }

    public int getTotal_page() {
        return total_page;
    }

    public void setTotal_page(int total_page) {
        this.total_page = total_page;
    }

    public int getTotal_count() {
        return total_count;
    }

    public void setTotal_count(int total_count) {
        this.total_count = total_count;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPage_count() {
        return page_count;
    }

    public void setPage_count(int page_count) {
        this.page_count = page_count;
    }
}
