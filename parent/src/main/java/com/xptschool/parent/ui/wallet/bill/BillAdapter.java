package com.xptschool.parent.ui.wallet.bill;

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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by dexing on 2017/4/13.
 * No1
 */

public class BillAdapter extends BaseRecycleAdapter {

    private List<BeanCadBill> cadBills = new ArrayList<>();

    public BillAdapter(Context context) {
        super(context);
    }

    public void refreshData(List<BeanCadBill> beanCadBills) {
        Log.i(TAG, "refreshData: ");
        cadBills = beanCadBills;
    }

    public void appendData(List<BeanCadBill> beanCadBills) {
        Log.i(TAG, "refreshData: ");
        cadBills.addAll(beanCadBills);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.i(TAG, "onCreateViewHolder: ");
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_card_bill, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Log.i(TAG, "showData: ");
        final ViewHolder mHolder = (ViewHolder) holder;
        final BeanCadBill cardBill = cadBills.get(position);
        mHolder.txt_bill_detail.setText(cardBill.getDescribe());
        mHolder.txt_time.setText(cardBill.getCreate_time());

        if (cardBill.getStatus().equals("1")) {
            //充值
            if (cardBill.getDescribe() == null || cardBill.getDescribe().isEmpty()) {
                mHolder.txt_bill_detail.setText("充值");
            }
            mHolder.txt_amount.setTextColor(mContext.getResources().getColor(R.color.colorAccent2));
            mHolder.txt_amount.setText("+" + cardBill.getBalances());
        } else {
            //消费
            if (cardBill.getDescribe() == null || cardBill.getDescribe().isEmpty()) {
                mHolder.txt_bill_detail.setText("消费");
            }
            mHolder.txt_amount.setTextColor(mContext.getResources().getColor(R.color.colorRed_def));
            mHolder.txt_amount.setText("-" + cardBill.getBalances());
        }

    }

    @Override
    public int getItemCount() {
        return cadBills == null ? 0 : cadBills.size();
    }

    class ViewHolder extends RecyclerViewHolderBase {

        private Unbinder unbinder;

        @BindView(R.id.txt_bill_detail)
        TextView txt_bill_detail;

        @BindView(R.id.txt_time)
        TextView txt_time;

        @BindView(R.id.txt_amount)
        TextView txt_amount;

        public ViewHolder(View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this, itemView);
        }

    }
}
