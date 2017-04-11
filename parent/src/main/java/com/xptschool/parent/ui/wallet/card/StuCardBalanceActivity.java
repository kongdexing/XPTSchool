package com.xptschool.parent.ui.wallet.card;

import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;

import com.android.widget.view.LoadMoreRecyclerView;
import com.xptschool.parent.R;
import com.xptschool.parent.ui.main.BaseListActivity;

import butterknife.BindView;

/**
 * 学生卡余额
 */
public class StuCardBalanceActivity extends BaseListActivity {

    @BindView(R.id.recyclerview)
    LoadMoreRecyclerView recyclerView;

    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    private BalanceAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance);
        setTitle(R.string.label_stu_card);
        initView();

    }

    private void initView() {
        initRecyclerView(recyclerView, swipeRefreshLayout);
        adapter = new BalanceAdapter(this);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                resultPage.setPage(1);
            }
        });
        recyclerView.setLoadMoreListener(new LoadMoreRecyclerView.LoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (resultPage.getPage() < resultPage.getTotal_page()) {
                    resultPage.setPage(resultPage.getPage() + 1);
//                    getHomeWorkList();
                }
            }
        });
        recyclerView.setAdapter(adapter);

    }

}
