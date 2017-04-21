package com.xptschool.parent.ui.wallet.pocket;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by dexing on 2017/4/17.
 * No1
 */

public class BeanPocketRecord implements Parcelable{

    private String id;
    private String log_info;
    private String log_time;
    private String money;
    private String user_id;
    private String type;
    private String order_id;
    private String payment_id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLog_info() {
        return log_info;
    }

    public void setLog_info(String log_info) {
        this.log_info = log_info;
    }

    public String getLog_time() {
        return log_time;
    }

    public void setLog_time(String log_time) {
        this.log_time = log_time;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getPayment_id() {
        return payment_id;
    }

    public void setPayment_id(String payment_id) {
        this.payment_id = payment_id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.log_info);
        dest.writeString(this.log_time);
        dest.writeString(this.money);
        dest.writeString(this.user_id);
        dest.writeString(this.type);
        dest.writeString(this.order_id);
        dest.writeString(this.payment_id);
    }

    public BeanPocketRecord() {
    }

    protected BeanPocketRecord(Parcel in) {
        this.id = in.readString();
        this.log_info = in.readString();
        this.log_time = in.readString();
        this.money = in.readString();
        this.user_id = in.readString();
        this.type = in.readString();
        this.order_id = in.readString();
        this.payment_id = in.readString();
    }

    public static final Creator<BeanPocketRecord> CREATOR = new Creator<BeanPocketRecord>() {
        @Override
        public BeanPocketRecord createFromParcel(Parcel source) {
            return new BeanPocketRecord(source);
        }

        @Override
        public BeanPocketRecord[] newArray(int size) {
            return new BeanPocketRecord[size];
        }
    };
}
