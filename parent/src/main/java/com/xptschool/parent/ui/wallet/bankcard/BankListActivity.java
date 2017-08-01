package com.xptschool.parent.ui.wallet.bankcard;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
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

import java.util.List;

import butterknife.BindView;

/**
 * 银行卡列表
 */
public class BankListActivity extends BaseListActivity {

    @BindView(R.id.recyclerview)
    LoadMoreRecyclerView recyclerView;

    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    private PopupWindow bottomPop;
    private BankListAdapter adapter;
    private String BankType = "";

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

        adapter = new BankListAdapter(this, new BankListAdapter.MyItemClickListener() {
            @Override
            public void onItemClick(View view, BeanBankCard bankCard) {
                showBankCard(bankCard);
            }
        });
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

    @Override
    protected void onResume() {
        super.onResume();
        getBankList();
    }

    private void getBankList() {

        VolleyHttpService.getInstance().sendPostRequest(HttpAction.GET_BankCards, new VolleyHttpParamsEntity()
                .addParam("token", CommonUtil.encryptToken(HttpAction.GET_BankCards)), new MyVolleyRequestListener() {
            @Override
            public void onStart() {
                super.onStart();
                if (swipeRefreshLayout != null) {
                    swipeRefreshLayout.setRefreshing(true);
                }
            }

            @Override
            public void onResponse(VolleyHttpResult volleyHttpResult) {
                super.onResponse(volleyHttpResult);
                if (swipeRefreshLayout != null) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                switch (volleyHttpResult.getStatus()) {
                    case HttpAction.SUCCESS:
                        try {
                            Gson gson = new Gson();
                            List<BeanBankCard> bankCards = gson.fromJson(volleyHttpResult.getData().toString(),
                                    new TypeToken<List<BeanBankCard>>() {
                                    }.getType());

                            if (bankCards.size() == 0) {
                                Toast.makeText(BankListActivity.this, R.string.toast_data_empty, Toast.LENGTH_SHORT).show();
                            }
                            recyclerView.removeAllViews();
                            adapter.refreshData(bankCards);
                        } catch (Exception ex) {
                            Log.i(TAG, "onResponse: " + ex.getMessage());
                            Toast.makeText(BankListActivity.this, HttpErrorMsg.ERROR_JSON, Toast.LENGTH_SHORT).show();
                            if (recyclerView != null) {
                                recyclerView.removeAllViews();
                                adapter.refreshData(null);
                            }
                        }
                        break;
                    default:
                        Toast.makeText(BankListActivity.this, volleyHttpResult.getInfo(), Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                super.onErrorResponse(volleyError);
                if (swipeRefreshLayout != null) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    private void deleteBankCard(BeanBankCard bankCard) {
        VolleyHttpService.getInstance().sendPostRequest(HttpAction.Delete_BankCard, new VolleyHttpParamsEntity()
                .addParam("id", bankCard.getId())
                .addParam("token", CommonUtil.encryptToken(HttpAction.Delete_BankCard)), new MyVolleyRequestListener() {
            @Override
            public void onStart() {
                super.onStart();
                showProgress("正在删除银行卡");
            }

            @Override
            public void onResponse(VolleyHttpResult volleyHttpResult) {
                super.onResponse(volleyHttpResult);
                hideProgress();
                Toast.makeText(BankListActivity.this, volleyHttpResult.getInfo(), Toast.LENGTH_SHORT).show();
                if (volleyHttpResult.getStatus() == HttpAction.SUCCESS) {
                    getBankList();
                }
            }

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                super.onErrorResponse(volleyError);
                hideProgress();
            }
        });
    }

    private void showBankCard(final BeanBankCard bankCard) {
        BankBtmPopView bankBtmPopView = new BankBtmPopView(BankListActivity.this);
        bankBtmPopView.setBankCardPopClickListener(new BankBtmPopView.BankCrdPopClickListener() {
            @Override
            public void onCardDeleteClick() {
                deleteBankCard(bankCard);
                if (bottomPop != null) {
                    bottomPop.dismiss();
                }
            }

            @Override
            public void onBack() {
                if (bottomPop != null) {
                    bottomPop.dismiss();
                }
            }
        });

        bottomPop = new PopupWindow(bankBtmPopView,
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        bottomPop.setTouchable(true);
        bottomPop.setBackgroundDrawable(new ColorDrawable());
        bottomPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1.0f);
            }
        });
        backgroundAlpha(0.8f);
        bottomPop.showAtLocation(recyclerView, Gravity.BOTTOM, 0, 0);
    }

}
