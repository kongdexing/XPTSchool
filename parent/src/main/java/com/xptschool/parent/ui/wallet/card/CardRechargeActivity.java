package com.xptschool.parent.ui.wallet.card;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.xptschool.parent.R;
import com.xptschool.parent.ui.main.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 学生卡充值
 */
public class CardRechargeActivity extends BaseActivity {

    @BindView(R.id.txt_current_num)
    TextView txt_current_num;
    @BindView(R.id.edt_num)
    EditText edt_num;
    @BindView(R.id.txt_change)
    TextView txt_change;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_recharge);
        setTitle(R.string.label_card_recharge);

    }

    @OnClick({R.id.btn_recharge})
    void viewOnClick(View view) {
        switch (view.getId()) {
            case R.id.btn_recharge:

                break;
        }
    }

}
