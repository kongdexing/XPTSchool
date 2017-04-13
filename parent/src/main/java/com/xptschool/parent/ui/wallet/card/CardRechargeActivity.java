package com.xptschool.parent.ui.wallet.card;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.common.VolleyHttpParamsEntity;
import com.android.volley.common.VolleyHttpResult;
import com.android.volley.common.VolleyHttpService;
import com.xptschool.parent.R;
import com.xptschool.parent.common.CommonUtil;
import com.xptschool.parent.http.HttpAction;
import com.xptschool.parent.http.MyVolleyRequestListener;
import com.xptschool.parent.ui.main.BaseActivity;

import org.json.JSONObject;

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
    @BindView(R.id.txt_balance)
    TextView txt_balance;

    private String current_stuId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_recharge);
        setTitle(R.string.label_card_recharge);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            current_stuId = bundle.getString("stu_id");
        }
        getPocketBalance();
    }

    @OnClick({R.id.btn_recharge})
    void viewOnClick(View view) {
        switch (view.getId()) {
            case R.id.btn_recharge:
                try {
                    float balance = Float.parseFloat(txt_balance.getText().toString());
                    float recharge_sum = Float.parseFloat(edt_num.getText().toString());
                    if (recharge_sum > balance) {
                        Toast.makeText(this, R.string.msg_recharge_greater_than_balance, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    onStuCardRecharge(recharge_sum + "");
                } catch (Exception ex) {
                    Toast.makeText(this, "充值金额有误", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void getPocketBalance() {
        VolleyHttpService.getInstance().sendPostRequest(HttpAction.POCKET_BALANCE, new VolleyHttpParamsEntity()
                .addParam("token", CommonUtil.encryptToken(HttpAction.POCKET_BALANCE)), new MyVolleyRequestListener() {
            @Override
            public void onStart() {
                super.onStart();
                txt_balance.setText("获取中..");
            }

            @Override
            public void onResponse(VolleyHttpResult volleyHttpResult) {
                super.onResponse(volleyHttpResult);
                switch (volleyHttpResult.getStatus()) {
                    case HttpAction.SUCCESS:
                        try {
                            JSONObject object = (JSONObject) volleyHttpResult.getData();
                            String balance = object.getString("account");
                            txt_balance.setText(balance);
                        } catch (Exception ex) {
                            Log.i(TAG, "onResponse: error " + ex.getMessage());
                            txt_balance.setText("获取失败");
                        }
                        break;
                    default:
                        txt_balance.setText("获取失败");
                        break;
                }
            }

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                super.onErrorResponse(volleyError);
                txt_balance.setText("获取失败");
            }
        });
    }

    //学生卡充值
    private void onStuCardRecharge(String money) {

        VolleyHttpService.getInstance().sendPostRequest(HttpAction.STU_CARD_RECHARGE,
                new VolleyHttpParamsEntity()
                        .addParam("stu_id", current_stuId)
                        .addParam("account", money)
                        .addParam("token", CommonUtil.encryptToken(HttpAction.STU_CARD_RECHARGE)), new MyVolleyRequestListener() {

                    @Override
                    public void onStart() {
                        super.onStart();
                        showProgress(R.string.progress_stu_card_recharge);
                    }

                    @Override
                    public void onResponse(VolleyHttpResult volleyHttpResult) {
                        super.onResponse(volleyHttpResult);
                        hideProgress();
                        Toast.makeText(CardRechargeActivity.this, volleyHttpResult.getInfo(), Toast.LENGTH_SHORT).show();
                        if (volleyHttpResult.getStatus() == HttpAction.SUCCESS) {
                            finish();
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        super.onErrorResponse(volleyError);
                        Toast.makeText(CardRechargeActivity.this, R.string.msg_recharge_failed, Toast.LENGTH_SHORT).show();
                        hideProgress();
                    }
                });

    }

}
