package com.xptschool.parent.ui.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

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
import com.xptschool.parent.util.ParentUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class WalletActivity extends BaseActivity {

    @BindView(R.id.gridview)
    MyGridView gridview;
    LearningGridAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        setTitle(R.string.label_my_wellet);

        initView();

        getLearningServer();
    }

    private void initView() {
        adapter = new LearningGridAdapter(this);
        gridview.setAdapter(adapter);
    }

    @OnClick({R.id.rl_balance, R.id.rlPocketMoney})
    void viewOnClick(View view) {
        switch (view.getId()) {
            case R.id.rl_balance:
                startActivity(new Intent(this, BalanceActivity.class));
                break;
            case R.id.rlPocketMoney:
                startActivity(new Intent(this, PocketActivity.class));
                break;
        }
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
                        } catch (Exception ex) {
                            Log.i(TAG, "onResponse: error " + ex.getMessage());
                            //错误
                        }
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
