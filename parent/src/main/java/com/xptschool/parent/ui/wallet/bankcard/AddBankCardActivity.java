package com.xptschool.parent.ui.wallet.bankcard;

import android.os.Bundle;

import com.xptschool.parent.R;
import com.xptschool.parent.ui.main.BaseActivity;

/**
 * 添加银行卡
 */
public class AddBankCardActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bank_card);
        setTitle(R.string.label_bankcard_addcard);


    }


}
