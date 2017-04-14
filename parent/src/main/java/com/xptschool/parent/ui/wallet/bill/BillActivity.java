package com.xptschool.parent.ui.wallet.bill;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.common.VolleyHttpParamsEntity;
import com.android.volley.common.VolleyHttpResult;
import com.android.volley.common.VolleyHttpService;
import com.android.widget.view.LoadMoreRecyclerView;
import com.xptschool.parent.R;
import com.xptschool.parent.common.CommonUtil;
import com.xptschool.parent.http.HttpAction;
import com.xptschool.parent.http.MyVolleyRequestListener;
import com.xptschool.parent.ui.main.BaseListActivity;

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
                .addParam("token", CommonUtil.encryptToken(HttpAction.STU_CARD_BILL)), new MyVolleyRequestListener() {
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

}
