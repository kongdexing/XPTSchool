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
import com.android.widget.mygridview.MyGridView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xptschool.parent.R;
import com.xptschool.parent.http.HttpAction;
import com.xptschool.parent.http.MyVolleyRequestListener;
import com.xptschool.parent.model.BeanLearningModule;
import com.xptschool.parent.ui.main.BaseActivity;
import com.xptschool.parent.ui.wallet.bankcard.BankListActivity;
import com.xptschool.parent.ui.wallet.card.StuCardActivity;
import com.xptschool.parent.ui.wallet.pocket.BalanceUtil;
import com.xptschool.parent.ui.wallet.pocket.PocketActivity;
import com.xptschool.parent.util.ParentUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 钱包
 */
public class WalletActivity extends BaseActivity {

    @BindView(R.id.gridview)
    MyGridView gridview;
    LearningGridAdapter adapter;

    @BindView(R.id.txt_no_learning)
    TextView txt_no_learning;

    @BindView(R.id.txt_pocket_money)
    TextView txt_pocket_money;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        setTitle(R.string.label_my_wellet);

        initView();
        getLearningServer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPocketBalance();
    }

    private void initView() {
        adapter = new LearningGridAdapter(this);
        gridview.setAdapter(adapter);
    }

    @OnClick({R.id.rl_stu_card, R.id.rlPocketMoney, R.id.rlBankCad})
    void viewOnClick(View view) {
        switch (view.getId()) {
            case R.id.rl_stu_card:
                startActivity(new Intent(this, StuCardActivity.class));
                break;
            case R.id.rlPocketMoney:
                Intent intent = new Intent(this, PocketActivity.class);
                startActivity(intent);
                break;
            case R.id.rlBankCad:
                startActivity(new Intent(this, BankListActivity.class));
                break;
        }
    }

    private void getPocketBalance() {
        if (txt_pocket_money != null) {
            txt_pocket_money.setText("¥ " + BalanceUtil.getParentBalance());
        }
        BalanceUtil.getBalance(new BalanceUtil.BalanceCallBack() {
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess() {
                if (txt_pocket_money != null) {
                    txt_pocket_money.setText("¥ " + BalanceUtil.getParentBalance());
                }
            }

            @Override
            public void onFailed(String error) {
                if (txt_pocket_money != null) {
                    txt_pocket_money.setText("获取失败");
                }
            }
        });
    }

    private void getLearningServer() {
        VolleyHttpService.getInstance().sendPostRequest(HttpAction.Learning_Server, new VolleyHttpParamsEntity()
                .addParam("s_id", ParentUtil.getStuSid()), new MyVolleyRequestListener() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onResponse(VolleyHttpResult volleyHttpResult) {
                super.onResponse(volleyHttpResult);
                switch (volleyHttpResult.getStatus()) {
                    case HttpAction.SUCCESS:
                        try {
                            String info = volleyHttpResult.getData().toString();
                            Log.i(TAG, "onResponse: data " + info);
                            Gson gson = new Gson();
                            List<BeanLearningModule> learningModules = gson.fromJson(info, new TypeToken<List<BeanLearningModule>>() {
                            }.getType());
                            adapter.reloadModule(learningModules);
                            if (txt_no_learning != null) {
                                if (learningModules.size() == 0) {
                                    txt_no_learning.setVisibility(View.VISIBLE);
                                } else {
                                    txt_no_learning.setVisibility(View.GONE);
                                }
                            }
                        } catch (Exception ex) {
                            Log.i(TAG, "onResponse: error " + ex.getMessage());
                            //错误
                            if (txt_no_learning != null) {
                                txt_no_learning.setVisibility(View.VISIBLE);
                            }
                        }
                        break;
                }
            }

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                super.onErrorResponse(volleyError);
                if (txt_no_learning != null) {
                    txt_no_learning.setVisibility(View.VISIBLE);
                }
            }
        });
    }

}
