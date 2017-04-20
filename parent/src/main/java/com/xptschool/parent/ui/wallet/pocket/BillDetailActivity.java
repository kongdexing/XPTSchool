package com.xptschool.parent.ui.wallet.pocket;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.xptschool.parent.R;
import com.xptschool.parent.ui.main.BaseActivity;

import butterknife.BindView;

/**
 * 账单详情
 */
public class BillDetailActivity extends BaseActivity {

    @BindView(R.id.txt_money)
    TextView txt_money;

    @BindView(R.id.txt_status)
    TextView txt_status;

    @BindView(R.id.txt_pay_type)
    TextView txt_pay_type;

    @BindView(R.id.txt_pay_info)
    TextView txt_pay_info;

    @BindView(R.id.txt_pay_time)
    TextView txt_pay_time;

    @BindView(R.id.txt_pay_tn)
    TextView txt_pay_tn;

    @BindView(R.id.txt_pay_bn)
    TextView txt_pay_bn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_detail);
        setTitle(R.string.label_pocket_bill_detail);


    }
}
