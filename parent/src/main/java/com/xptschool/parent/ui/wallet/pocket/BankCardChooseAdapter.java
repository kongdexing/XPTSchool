package com.xptschool.parent.ui.wallet.pocket;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.widget.view.SmoothCheckBox;
import com.xptschool.parent.R;
import com.xptschool.parent.adapter.BaseRecycleAdapter;
import com.xptschool.parent.adapter.RecyclerViewHolderBase;
import com.xptschool.parent.ui.wallet.bankcard.BeanBankCard;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by dexing on 2017/4/15.
 * No1
 */

public class BankCardChooseAdapter extends BaseRecycleAdapter {

    private List<BeanBankCard> bankCards = new ArrayList<>();
    private MyItemClickListener listener;
    private BeanBankCard card = null;

    public void setCard(BeanBankCard card) {
        this.card = card;
    }

    public void refreshData(List<BeanBankCard> beanBankCards) {
        Log.i(TAG, "refreshData: ");
        bankCards = beanBankCards;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public BankCardChooseAdapter(Context context, MyItemClickListener clickListener) {
        super(context);
        listener = clickListener;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ViewHolder mHolder = (ViewHolder) holder;
        final BeanBankCard bankCard = bankCards.get(position);
        mHolder.cbx_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mHolder.cbx_choose.isChecked()) {
                    if (listener != null) {
                        mHolder.cbx_choose.setChecked(true);
                        listener.onItemClick(v, bankCard);
                    }
                }
            }
        });
        mHolder.llItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    mHolder.cbx_choose.setChecked(true);
                    listener.onItemClick(v, bankCard);
                }
            }
        });
        if (bankCard == null) {
            mHolder.txt_bank_name.setText("添加新银行卡");
            mHolder.cbx_choose.setVisibility(View.GONE);
        } else {
            mHolder.txt_bank_name.setText(PocketHelper.getBankShortName(mContext, bankCard));
            if (card != null) {
                if (bankCard.getId().equals(card.getId())) {
                    mHolder.cbx_choose.setChecked(true);
                } else {
                    mHolder.cbx_choose.setChecked(false);
                }
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.i(TAG, "onCreateViewHolder: ");
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bank_card_choose, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return bankCards == null ? 0 : bankCards.size();
    }

    class ViewHolder extends RecyclerViewHolderBase {

        private Unbinder unbinder;

        @BindView(R.id.llItem)
        LinearLayout llItem;

        @BindView(R.id.txt_bank_name)
        TextView txt_bank_name;

        @BindView(R.id.cbx_choose)
        SmoothCheckBox cbx_choose;

        public ViewHolder(View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this, itemView);
        }

    }

    interface MyItemClickListener {
        public void onItemClick(View view, BeanBankCard bankCard);
    }

}
