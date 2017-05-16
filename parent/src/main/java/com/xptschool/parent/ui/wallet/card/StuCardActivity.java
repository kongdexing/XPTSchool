package com.xptschool.parent.ui.wallet.card;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.common.VolleyHttpParamsEntity;
import com.android.volley.common.VolleyHttpResult;
import com.android.volley.common.VolleyHttpService;
import com.android.widget.view.LoadMoreRecyclerView;
import com.xptschool.parent.R;
import com.xptschool.parent.common.BroadcastAction;
import com.xptschool.parent.common.CommonUtil;
import com.xptschool.parent.http.HttpAction;
import com.xptschool.parent.http.MyVolleyRequestListener;
import com.xptschool.parent.model.GreenDaoHelper;
import com.xptschool.parent.ui.main.BaseListActivity;
import com.xptschool.parent.ui.wallet.pocket.BalanceUtil;

import java.util.List;

import butterknife.BindView;

/**
 * 学生卡管理
 */
public class StuCardActivity extends BaseListActivity {

    @BindView(R.id.recyclerview)
    LoadMoreRecyclerView recyclerView;

    private BalanceAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance);
        setTitle(R.string.label_stu_card);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void initView() {
        initRecyclerView(recyclerView, null);
        adapter = new BalanceAdapter(this);
        recyclerView.setAdapter(adapter);
        IntentFilter filter = new IntentFilter(BroadcastAction.CARD_FREEZE);
        registerReceiver(freezeReceiver, filter);
    }

    private void initData() {
        getStuCardBalance();
    }

    private void getStuCardBalance() {
        BalanceUtil.getBalance(new BalanceUtil.BalanceCallBack() {
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess() {
                List<BeanCardBalance> cardBalances = BalanceUtil.getCardBalances();

                recyclerView.removeAllViews();
                adapter.reloadData(cardBalances, GreenDaoHelper.getInstance().getStudents());
            }

            @Override
            public void onFailed(String error) {
                if (error != null && !error.isEmpty()) {
                    Toast.makeText(StuCardActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            this.unregisterReceiver(freezeReceiver);
        } catch (Exception ex) {

        }
    }

    BroadcastReceiver freezeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BroadcastAction.CARD_FREEZE)) {
                String stu_id = intent.getStringExtra("stu_id");
                String freeze_type = intent.getStringExtra("freeze");

                VolleyHttpService.getInstance().sendPostRequest(HttpAction.STU_CARD_FREEZE, new VolleyHttpParamsEntity()
                        .addParam("stu_id", stu_id)
                        .addParam("type", freeze_type)
                        .addParam("token", CommonUtil.encryptToken(HttpAction.STU_CARD_FREEZE)), new MyVolleyRequestListener() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        showProgress("");
                    }

                    @Override
                    public void onResponse(VolleyHttpResult volleyHttpResult) {
                        super.onResponse(volleyHttpResult);
                        hideProgress();
                        Toast.makeText(StuCardActivity.this, volleyHttpResult.getInfo(), Toast.LENGTH_SHORT).show();
                        switch (volleyHttpResult.getStatus()) {
                            case HttpAction.SUCCESS:
                                getStuCardBalance();
                                break;
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        super.onErrorResponse(volleyError);
                        hideProgress();
                    }
                });
            }
        }
    };

}
