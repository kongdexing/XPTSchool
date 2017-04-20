package com.xptschool.parent.ui.wallet.bankcard;

import android.os.Parcel;
import android.os.Parcelable;

import com.android.widget.spinner.SpinnerModel;

/**
 * Created by dexing on 2017/4/17.
 * No1
 */

public class BeanBankCard extends SpinnerModel implements Parcelable{

    private String id;
    private String u_id;
    private String card_no;
    private String card_type;
    private String bankname;
    private String cardholder;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getU_id() {
        return u_id;
    }

    public void setU_id(String u_id) {
        this.u_id = u_id;
    }

    public String getCard_no() {
        return card_no;
    }

    public void setCard_no(String card_no) {
        this.card_no = card_no;
    }

    public String getCard_type() {
        return card_type;
    }

    public void setCard_type(String card_type) {
        this.card_type = card_type;
    }

    public String getBankname() {
        return bankname;
    }

    public void setBankname(String bankname) {
        this.bankname = bankname;
    }

    public String getCardholder() {
        return cardholder;
    }

    public void setCardholder(String cardholder) {
        this.cardholder = cardholder;
    }

    @Override
    public String getName() {
        String cardNum = "";
        if (card_no.length() > 4) {
            cardNum = card_no.substring(card_no.length() - 4, card_no.length());
        }
        cardNum = bankname + "(" + cardNum + ")";
        return cardNum;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.u_id);
        dest.writeString(this.card_no);
        dest.writeString(this.card_type);
        dest.writeString(this.bankname);
        dest.writeString(this.cardholder);
    }

    public BeanBankCard() {
    }

    protected BeanBankCard(Parcel in) {
        this.id = in.readString();
        this.u_id = in.readString();
        this.card_no = in.readString();
        this.card_type = in.readString();
        this.bankname = in.readString();
        this.cardholder = in.readString();
    }

    public static final Creator<BeanBankCard> CREATOR = new Creator<BeanBankCard>() {
        @Override
        public BeanBankCard createFromParcel(Parcel source) {
            return new BeanBankCard(source);
        }

        @Override
        public BeanBankCard[] newArray(int size) {
            return new BeanBankCard[size];
        }
    };
}
