package com.xptschool.parent.ui.wallet.bankcard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.android.widget.view.LoadMoreRecyclerView;
import com.xptschool.parent.R;
import com.xptschool.parent.ui.main.BaseListActivity;
import com.xptschool.parent.ui.wallet.bill.BillAdapter;

import butterknife.BindView;

/**
 * 银行卡列表
 */
public class BankListActivity extends BaseListActivity {

    @BindView(R.id.recyclerview)
    LoadMoreRecyclerView recyclerView;

    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    private BankListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_list);
        setTitle(R.string.label_bankcard_list);

        setTxtRight(R.string.label_bankcard_add);
        setTextRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BankListActivity.this, AddBankCardActivity.class));
            }
        });
        initView();
    }

    private void initView() {
        initRecyclerView(recyclerView, swipeRefreshLayout);

        adapter = new BankListAdapter(this);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                resultPage.setPage(1);
                getBankList();
            }
        });
        recyclerView.setLoadMoreListener(new LoadMoreRecyclerView.LoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (resultPage.getPage() < resultPage.getTotal_page()) {
                    resultPage.setPage(resultPage.getPage() + 1);
                    getBankList();
                }
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void getBankList() {

    }

}
