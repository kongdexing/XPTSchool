package com.xptschool.parent.ui.wallet.bankcard;

import android.os.Bundle;
import android.widget.EditText;

import com.xptschool.parent.R;
import com.xptschool.parent.ui.main.BaseActivity;

import butterknife.BindView;

/**
 * 添加银行卡
 */
public class AddBankCardActivity extends BaseActivity {

    @BindView(R.id.edt_username)
    EditText edt_username;

    @BindView(R.id.edt_card)
    EditText edt_card;

    @BindView(R.id.edt_bankname)
    EditText edt_bankname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bank_card);
        setTitle(R.string.label_bankcard_addcard);


    }


}
