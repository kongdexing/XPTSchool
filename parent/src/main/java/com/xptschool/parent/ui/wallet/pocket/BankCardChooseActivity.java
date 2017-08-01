package com.xptschool.parent.ui.wallet.pocket;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.android.widget.view.LoadMoreRecyclerView;
import com.xptschool.parent.R;
import com.xptschool.parent.ui.main.BaseListActivity;
import com.xptschool.parent.ui.wallet.bankcard.AddBankCardActivity;
import com.xptschool.parent.ui.wallet.bankcard.BeanBankCard;

import java.util.ArrayList;

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
