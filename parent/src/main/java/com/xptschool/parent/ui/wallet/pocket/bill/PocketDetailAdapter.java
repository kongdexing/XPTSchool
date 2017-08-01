package com.xptschool.parent.ui.wallet.pocket.bill;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xptschool.parent.R;
import com.xptschool.parent.adapter.BaseRecycleAdapter;
import com.xptschool.parent.adapter.RecyclerViewHolderBase;
import com.xptschool.parent.ui.wallet.pocket.BeanPocketRecord;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by dexing on 2017/4/14.
 * No1
 */

public class PocketDetailAdapter extends BaseRecycleAdapter {

    private List<BeanPocketRecord> beanPocketRecords = new ArrayList<>();

    public PocketDetailAdapter(Context context) {
        super(context);
    }

    public void refreshData(List<BeanPocketRecord> beanPocketRecords) {
        Log.i(TAG, "refreshData: ");
        this.beanPocketRecords = beanPocketRecords;
    }

    public void appendData(List<BeanPocketRecord> beanPocketRecords) {
        Log.i(TAG, "refreshData: ");
        this.beanPocketRecords.addAll(beanPocketRecords);
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
                .inflate(R.layout.item_charge_record, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Log.i(TAG, "showData: ");
        final ViewHolder mHolder = (ViewHolder) holder;
        final BeanPocketRecord record = beanPocketRecords.get(position);
        if (record == null) {
            mHolder.rlItem.setVisibility(View.GONE);
            return;
        }
        mHolder.rlItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, BillDetailActivity.class);
                intent.putExtra("record", record);
                mContext.startActivity(intent);
            }
        });
        mHolder.txt_record_detail.setText(record.getLog_info());
        String payment_id = record.getPayment_id();
        if ("支付宝".equals(payment_id)) {
            mHolder.img_pay_type.setBackgroundResource(R.drawable.icon_alipay);
        } else if ("微信".equals(payment_id)) {
            mHolder.img_pay_type.setBackgroundResource(R.drawable.icon_appwx_logo);
        } else if ("银联".equals(payment_id)) {
            mHolder.img_pay_type.setBackgroundResource(R.drawable.icon_uppay);
        } else {
            mHolder.img_pay_type.setBackgroundResource(0);
        }
        mHolder.txt_time.setText(record.getLog_time());

        if (record.getType().equals("充值")) {
            mHolder.txt_amount.setTextColor(mContext.getResources().getColor(R.color.colorAccent2));
        } else {
            mHolder.txt_amount.setTextColor(mContext.getResources().getColor(R.color.colorRed_def));
        }
        mHolder.txt_amount.setText(record.getMoney());

    }

    @Override
    public int getItemCount() {
        return beanPocketRecords == null ? 0 : beanPocketRecords.size();
    }

    class ViewHolder extends RecyclerViewHolderBase {

        private Unbinder unbinder;

        @BindView(R.id.rlItem)
        RelativeLayout rlItem;

        @BindView(R.id.txt_record_detail)
        TextView txt_record_detail;

        @BindView(R.id.img_pay_type)
        ImageView img_pay_type;

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
