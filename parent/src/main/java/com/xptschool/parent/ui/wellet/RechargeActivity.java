package com.xptschool.parent.ui.wellet;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.widget.view.SmoothCheckBox;
import com.xptschool.parent.R;
import com.xptschool.parent.ui.main.BaseActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 充值
 */

public class RechargeActivity extends BaseActivity {

    @BindView(R.id.txt_recharge_50)
    TextView txt_recharge_50;

    @BindView(R.id.txt_recharge_100)
    TextView txt_recharge_100;

    @BindView(R.id.txt_recharge_150)
    TextView txt_recharge_150;

    @BindView(R.id.txt_recharge_200)
    TextView txt_recharge_200;

    @BindView(R.id.txt_recharge_300)
    TextView txt_recharge_300;

    @BindView(R.id.txt_recharge_400)
    TextView txt_recharge_400;

    ArrayList<TextView> rechargeUI = new ArrayList<>();

    @BindView(R.id.cbx_alipay)
    SmoothCheckBox cbx_alipay;
    @BindView(R.id.cbx_wxpay)
    SmoothCheckBox cbx_wxpay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge);
        setTitle(R.string.label_recharge);
        initView();
    }

    private void initView() {
        rechargeUI.clear();
        rechargeUI.add(txt_recharge_50);
        rechargeUI.add(txt_recharge_100);
        rechargeUI.add(txt_recharge_150);
        rechargeUI.add(txt_recharge_200);
        rechargeUI.add(txt_recharge_300);
        rechargeUI.add(txt_recharge_400);
        cbx_alipay.setChecked(true);
        viewOnClick(txt_recharge_50);
    }

    @OnClick({R.id.rl_alipay, R.id.rl_wxpay, R.id.txt_recharge_50, R.id.txt_recharge_100, R.id.txt_recharge_150,
            R.id.txt_recharge_200, R.id.txt_recharge_300, R.id.txt_recharge_400})
    void viewOnClick(View view) {
        switch (view.getId()) {
            case R.id.txt_recharge_50:
            case R.id.txt_recharge_100:
            case R.id.txt_recharge_150:
            case R.id.txt_recharge_200:
            case R.id.txt_recharge_300:
            case R.id.txt_recharge_400:
                resetRechargeUI((TextView) view);
                break;
            case R.id.rl_alipay:
                cbx_alipay.setChecked(true);
                cbx_wxpay.setChecked(false);
                break;
            case R.id.rl_wxpay:
                cbx_alipay.setChecked(false);
                cbx_wxpay.setChecked(true);
                break;
            case R.id.btn_recharge:

                break;
        }
    }

    private void resetRechargeUI(TextView view) {
        for (int i = 0; i < rechargeUI.size(); i++) {
            TextView rechargeView = rechargeUI.get(i);
            rechargeView.setBackground(getResources().getDrawable(R.drawable.bg_recharge_money));
            rechargeView.setTextColor(getResources().getColor(R.color.colorPrimary));
        }
        view.setBackground(getResources().getDrawable(R.color.colorPrimary));
        view.setTextColor(getResources().getColor(R.color.white));
    }

}
