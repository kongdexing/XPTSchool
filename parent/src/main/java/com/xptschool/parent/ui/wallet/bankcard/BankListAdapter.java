package com.xptschool.parent.ui.wallet.bankcard;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xptschool.parent.R;
import com.xptschool.parent.adapter.BaseRecycleAdapter;
import com.xptschool.parent.adapter.RecyclerViewHolderBase;
import com.xptschool.parent.ui.wallet.bill.BillAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by dexing on 2017/4/15.
 * No1
 */

public class BankListAdapter extends BaseRecycleAdapter {

    public BankListAdapter(Context context) {
        super(context);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

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
        return 1;
    }

    class ViewHolder extends RecyclerViewHolderBase {

        private Unbinder unbinder;

        @BindView(R.id.txt_bank_name)
        TextView txt_bank_name;

        @BindView(R.id.txt_bank_account)
        TextView txt_bank_account;

        public ViewHolder(View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this, itemView);
        }

    }
}
