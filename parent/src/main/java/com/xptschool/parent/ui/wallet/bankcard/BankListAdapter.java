package com.xptschool.parent.ui.wallet.bankcard;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xptschool.parent.R;
import com.xptschool.parent.adapter.BaseRecycleAdapter;
import com.xptschool.parent.adapter.RecyclerViewHolderBase;
import com.xptschool.parent.ui.wallet.bill.BeanCadBill;
import com.xptschool.parent.ui.wallet.bill.BillAdapter;
import com.xptschool.parent.ui.wallet.pocket.BeanPocketRecord;
import com.xptschool.parent.ui.wallet.pocket.PocketDetailAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by dexing on 2017/4/15.
 * No1
 */

public class BankListAdapter extends BaseRecycleAdapter {

    private List<BeanBankCard> bankCards = new ArrayList<>();
    private MyItemClickListener listener;

    public void refreshData(List<BeanBankCard> beanBankCards) {
        Log.i(TAG, "refreshData: ");
        bankCards = beanBankCards;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public BankListAdapter(Context context, MyItemClickListener clickListener) {
        super(context);
        listener = clickListener;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ViewHolder mHolder = (ViewHolder) holder;
        final BeanBankCard bankCard = bankCards.get(position);
        mHolder.llItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(v, bankCard);
                }
            }
        });
        mHolder.txt_bank_name.setText(bankCard.getBankname());
        mHolder.txt_username.setText("持卡人：" + bankCard.getCardholder());
        String cardNum = bankCard.getCard_no();
        if (cardNum.length() > 4) {
            cardNum = cardNum.substring(cardNum.length() - 4, cardNum.length());
        }
        mHolder.txt_bank_num.setText("**** **** **** " + cardNum);
        if (bankCard.getCard_type().equals("0")) {
            mHolder.txt_bank_type.setText("借记卡");
        } else if (bankCard.getCard_type().equals("1")) {
            mHolder.txt_bank_type.setText("信用卡");
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.i(TAG, "onCreateViewHolder: ");
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bank_card, parent, false);
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

        @BindView(R.id.txt_bank_type)
        TextView txt_bank_type;

        @BindView(R.id.txt_username)
        TextView txt_username;

        @BindView(R.id.txt_bank_num)
        TextView txt_bank_num;

        public ViewHolder(View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this, itemView);
        }

    }

    interface MyItemClickListener {
        public void onItemClick(View view, BeanBankCard bankCard);
    }

}
