package com.xptschool.parent.ui.wallet.card;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

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
import com.xptschool.parent.model.BeanStudent;
import com.xptschool.parent.model.GreenDaoHelper;
import com.xptschool.parent.ui.main.BaseListActivity;

import java.util.List;

import butterknife.BindView;

/**
 * 学生卡余额
 */
public class StuCardBalanceActivity extends BaseListActivity {

    @BindView(R.id.recyclerview)
    LoadMoreRecyclerView recyclerView;

    private BalanceAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance);
        setTitle(R.string.label_stu_card);
        initView();
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
        List<BeanStudent> beanStudents = GreenDaoHelper.getInstance().getStudents();
        if (beanStudents.size() == 0) {
            return;
        }
        recyclerView.removeAllViews();
        adapter.reloadData(beanStudents);
        getStuCardBalance();
    }

    private void getStuCardBalance() {
        VolleyHttpService.getInstance().sendPostRequest(HttpAction.STU_CARD_BALANCE, new VolleyHttpParamsEntity()
                .addParam("token", CommonUtil.encryptToken(HttpAction.STU_CARD_BALANCE)), new MyVolleyRequestListener() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onResponse(VolleyHttpResult volleyHttpResult) {
                super.onResponse(volleyHttpResult);

            }

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                super.onErrorResponse(volleyError);
            }
        });
    }

    BroadcastReceiver freezeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BroadcastAction.CARD_FREEZE)) {

                VolleyHttpService.getInstance().sendPostRequest(HttpAction.ACTION_LOGIN, new VolleyHttpParamsEntity()
                        .addParam("", ""), new MyVolleyRequestListener() {
                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onResponse(VolleyHttpResult volleyHttpResult) {
                        super.onResponse(volleyHttpResult);
                        switch (volleyHttpResult.getStatus()) {
                            case HttpAction.SUCCESS:

                                break;
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        super.onErrorResponse(volleyError);
                    }
                });
            }
        }
    };

}
