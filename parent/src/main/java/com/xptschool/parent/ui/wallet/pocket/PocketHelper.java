package com.xptschool.parent.ui.wallet.pocket;

import android.content.Context;

import com.xptschool.parent.R;
import com.xptschool.parent.ui.wallet.bankcard.BeanBankCard;

/**
 * Created by dexing on 2017/4/21.
 * No1
 */

public class PocketHelper {

    public static String getBankShortName(Context mContext, BeanBankCard bankCard) {
        String name = bankCard.getBankname();

        if (bankCard.getCard_type().equals("0")) {
            name += mContext.getResources().getString(R.string.label_bankcard_type1);
        } else if (bankCard.getCard_type().equals("1")) {
            name += mContext.getResources().getString(R.string.label_bankcard_type2);
        }

        String cardNum = bankCard.getCard_no();
        if (cardNum.length() > 4) {
            cardNum = cardNum.substring(cardNum.length() - 4, cardNum.length());
        }
        return name + "(" + cardNum + ")";
    }

}
