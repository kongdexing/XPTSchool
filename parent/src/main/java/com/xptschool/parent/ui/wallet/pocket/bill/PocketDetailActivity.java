package com.xptschool.parent.ui.wallet.pocket.bill;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.common.VolleyHttpParamsEntity;
import com.android.volley.common.VolleyHttpResult;
import com.android.volley.common.VolleyHttpService;
import com.android.widget.view.LoadMoreRecyclerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xptschool.parent.R;
import com.xptschool.parent.common.CommonUtil;
import com.xptschool.parent.http.HttpAction;
import com.xptschool.parent.http.HttpErrorMsg;
import com.xptschool.parent.http.MyVolleyRequestListener;
import com.xptschool.parent.ui.main.BaseListActivity;
import com.xptschool.parent.ui.wallet.pocket.BeanPocketRecord;

import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;

/**
 * 零钱明细
 */
public class PocketDetailActivity extends BaseListActivity {

    @BindView(R.id.recyclerview)
    LoadMoreRecyclerView recyclerView;

    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    private PocketDetailAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pocket_detail);
        setTitle(R.string.label_pocket_bill);

        initView();
        getPocketRechargeDetail();
    }

    private void initView() {
        initRecyclerView(recyclerView, swipeRefreshLayout);

        adapter = new PocketDetailAdapter(this);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                resultPage.setPage(1);
                getPocketRechargeDetail();
            }
        });
        recyclerView.setLoadMoreListener(new LoadMoreRecyclerView.LoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (resultPage.getPage() < resultPage.getTotal_page()) {
                    resultPage.setPage(resultPage.getPage() + 1);
                    getPocketRechargeDetail();
                }
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void getPocketRechargeDetail() {
        VolleyHttpService.getInstance().sendPostRequest(HttpAction.POCKET_BILLS, new VolleyHttpParamsEntity()
                .addParam("page", resultPage.getPage() + "")
                .addParam("token", CommonUtil.encryptToken(HttpAction.POCKET_BILLS)), new MyVolleyRequestListener() {
            @Override
            public void onStart() {
                super.onStart();
                if (swipeRefreshLayout != null && resultPage.getPage() == 1) {
                    swipeRefreshLayout.setRefreshing(true);
                }
            }

            @Override
            public void onResponse(VolleyHttpResult volleyHttpResult) {
                super.onResponse(volleyHttpResult);
                if (swipeRefreshLayout != null && resultPage.getPage() == 1) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                switch (volleyHttpResult.getStatus()) {
                    case HttpAction.SUCCESS:
                        try {
                            JSONObject jsonObject = new JSONObject(volleyHttpResult.getData().toString());
                            resultPage.setPage(jsonObject.getInt("page"));
                            resultPage.setTotal_page(jsonObject.getInt("total_page"));
                            resultPage.setTotal_count(jsonObject.getInt("total_count"));

                            if (resultPage.getTotal_page() > resultPage.getPage()) {
                                recyclerView.setAutoLoadMoreEnable(true);
                            } else {
                                recyclerView.setAutoLoadMoreEnable(false);
                            }

                            Gson gson = new Gson();
                            List<BeanPocketRecord> homeWorks = gson.fromJson(jsonObject.getJSONArray("content").toString(),
                                    new TypeToken<List<BeanPocketRecord>>() {
                                    }.getType());

                            if (resultPage.getPage() > 1) {
                                adapter.appendData(homeWorks);
                            } else {
                                //第一页数据
                                if (homeWorks.size() == 0) {
                                    Toast.makeText(PocketDetailActivity.this, R.string.toast_data_empty, Toast.LENGTH_SHORT).show();
                                }
                                recyclerView.removeAllViews();
                                adapter.refreshData(homeWorks);
                            }
                            recyclerView.notifyMoreFinish(resultPage.getTotal_page() > resultPage.getPage());
                        } catch (Exception ex) {
                            Log.i(TAG, "onResponse: " + ex.getMessage());
                            Toast.makeText(PocketDetailActivity.this, HttpErrorMsg.ERROR_JSON, Toast.LENGTH_SHORT).show();
                        }
                        break;
                    default:
                        Toast.makeText(PocketDetailActivity.this, volleyHttpResult.getInfo(), Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                super.onErrorResponse(volleyError);
                if (swipeRefreshLayout != null && resultPage.getPage() == 1) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

}
