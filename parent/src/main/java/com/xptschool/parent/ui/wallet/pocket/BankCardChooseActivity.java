package com.xptschool.parent.ui.wallet.pocket;

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
import com.xptschool.parent.ui.wallet.bankcard.AddBankCardActivity;
import com.xptschool.parent.ui.wallet.bankcard.BankBtmPopView;
import com.xptschool.parent.ui.wallet.bankcard.BankListAdapter;
import com.xptschool.parent.ui.wallet.bankcard.BeanBankCard;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 银行卡列表选择
 */
public class BankCardChooseActivity extends BaseListActivity {

    @BindView(R.id.recyclerview)
    LoadMoreRecyclerView recyclerView;

    private BankCardChooseAdapter adapter;
    private ArrayList<BeanBankCard> listCards = new ArrayList<>();
    private BeanBankCard currentCard = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bankchoose_list);
        setTitle(R.string.label_bankcard_list);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            listCards = bundle.getParcelableArrayList("listCard");
            currentCard = bundle.getParcelable("card");
        }
        initView();
    }

    private void initView() {
        initRecyclerView(recyclerView, null);

        adapter = new BankCardChooseAdapter(this, new BankCardChooseAdapter.MyItemClickListener() {
            @Override
            public void onItemClick(View view, BeanBankCard bankCard) {
                if (bankCard == null) {
                    Intent intent = new Intent(BankCardChooseActivity.this, AddBankCardActivity.class);
                    intent.putExtra("card", "get");
                    startActivityForResult(intent, 0);
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("card", bankCard);
                    setResult(1, intent);
                    finish();
                }
            }
        });
        recyclerView.setAdapter(adapter);
        if (listCards == null) {
            listCards = new ArrayList<>();
        }
        listCards.add(null);
        adapter.setCard(currentCard);
        adapter.refreshData(listCards);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == 1) {
                setResult(1, data);
                finish();
            }
        }
    }
}
