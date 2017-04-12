package com.xptschool.parent.ui.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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
 * 零钱
 */
public class PocketActivity extends BaseActivity {

    @BindView(R.id.txt_pocket_money)
    TextView txt_pocket_money;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pocket);
        setTitle(R.string.label_pocket_money);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPocketBalance();
    }

    @OnClick({R.id.btn_recharge, R.id.btn_withdraw})
    void viewOnClick(View view) {
        switch (view.getId()) {
            case R.id.btn_recharge:
                startActivity(new Intent(this, RechargeActivity.class));
                break;
            case R.id.btn_withdraw:

                break;
        }
    }

    private void getPocketBalance() {
        VolleyHttpService.getInstance().sendPostRequest(HttpAction.POCKET_BALANCE, new VolleyHttpParamsEntity()
                .addParam("token", CommonUtil.encryptToken(HttpAction.POCKET_BALANCE)), new MyVolleyRequestListener() {
            @Override
            public void onStart() {
                super.onStart();
                txt_pocket_money.setText("获取中..");
            }

            @Override
            public void onResponse(VolleyHttpResult volleyHttpResult) {
                super.onResponse(volleyHttpResult);
                switch (volleyHttpResult.getStatus()) {
                    case HttpAction.SUCCESS:
                        try {
                            JSONObject object = (JSONObject) volleyHttpResult.getData();
                            String balance = object.getString("account");
                            txt_pocket_money.setText("¥ " + balance);
                        } catch (Exception ex) {
                            Log.i(TAG, "onResponse: error " + ex.getMessage());
                        }
                        break;
                }
            }

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                super.onErrorResponse(volleyError);
                txt_pocket_money.setText("获取失败");
            }
        });
    }


}
