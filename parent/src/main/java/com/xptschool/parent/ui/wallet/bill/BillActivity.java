package com.xptschool.parent.ui.wallet.bill;

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

import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;

/**
 * 清单
 */
public class BillActivity extends BaseListActivity {

    @BindView(R.id.recyclerview)
    LoadMoreRecyclerView recyclerView;

    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    private String stu_id = "";
    private BillAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);
        setTitle(R.string.label_bill);
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            Toast.makeText(this, "参数传递错误", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        stu_id = bundle.getString("stu_id");
        initView();
        getBill();
    }

    private void initView() {
        initRecyclerView(recyclerView, swipeRefreshLayout);

        adapter = new BillAdapter(this);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                resultPage.setPage(1);
                getBill();
            }
        });
        recyclerView.setLoadMoreListener(new LoadMoreRecyclerView.LoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (resultPage.getPage() < resultPage.getTotal_page()) {
                    resultPage.setPage(resultPage.getPage() + 1);
                    getBill();
                }
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void getBill() {
        VolleyHttpService.getInstance().sendPostRequest(HttpAction.STU_CARD_BILL, new VolleyHttpParamsEntity()
                .addParam("stu_id", stu_id)
                .addParam("page", resultPage.getPage() + "")
                .addParam("token", CommonUtil.encryptToken(HttpAction.STU_CARD_BILL)), new MyVolleyRequestListener() {
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
                            List<BeanCadBill> homeWorks = gson.fromJson(jsonObject.getJSONArray("content").toString(),
                                    new TypeToken<List<BeanCadBill>>() {
                                    }.getType());

                            if (resultPage.getPage() > 1) {
                                adapter.appendData(homeWorks);
                            } else {
                                //第一页数据
                                if (homeWorks.size() == 0) {
                                    Toast.makeText(BillActivity.this, R.string.toast_data_empty, Toast.LENGTH_SHORT).show();
                                }
                                recyclerView.removeAllViews();
                                adapter.refreshData(homeWorks);
                            }
                            recyclerView.notifyMoreFinish(resultPage.getTotal_page() > resultPage.getPage());
                        } catch (Exception ex) {
                            Log.i(TAG, "onResponse: " + ex.getMessage());
                            Toast.makeText(BillActivity.this, HttpErrorMsg.ERROR_JSON, Toast.LENGTH_SHORT).show();
                        }
                        break;
                    default:
                        Toast.makeText(BillActivity.this, volleyHttpResult.getInfo(), Toast.LENGTH_SHORT).show();
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
