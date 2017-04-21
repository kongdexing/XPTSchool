package com.xptschool.parent.ui.wallet.pocket.bill;

/**
 * Created by dexing on 2017/4/21.
 * No1
 */

public class BeanOrder {

    private String total_price;
    private String memo;
    private String payment_id;
    private String order_status;
    private String create_time;
    private String notice_sn;

    public String getTotal_price() {
        return total_price;
    }

    public void setTotal_price(String total_price) {
        this.total_price = total_price;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getPayment_id() {
        return payment_id;
    }

    public void setPayment_id(String payment_id) {
        this.payment_id = payment_id;
    }

    public String getOrder_status() {
        return order_status;
    }

    public void setOrder_status(String order_status) {
        this.order_status = order_status;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getNotice_sn() {
        return notice_sn;
    }

    public void setNotice_sn(String notice_sn) {
        this.notice_sn = notice_sn;
    }
}
