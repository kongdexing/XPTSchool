package com.xptschool.parent.ui.wallet.pocket;

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
//        mHolder.txt_record_detail.setText(record.getMemo());
//        String payment_id = record.getPayment_id();
//        if (payment_id.equals("0")) {
//            mHolder.txt_pay_type.setText("支付宝");
//            mHolder.txt_pay_type.setBackgroundResource(R.drawable.bg_pay_alipay);
//        } else if (payment_id.equals("1")) {
//            mHolder.txt_pay_type.setText("微信");
//            mHolder.txt_pay_type.setBackgroundResource(R.drawable.bg_pay_wxpay);
//        } else if (payment_id.equals("2")) {
//            mHolder.txt_pay_type.setText("银联在线");
//            mHolder.txt_pay_type.setBackgroundResource(R.drawable.bg_pay_uppay);
//        } else {
//            mHolder.txt_pay_type.setText("未知");
//        }
//        if (record.getIs_paid().equals("0")) {
//            mHolder.txt_pay_status.setText("失败");
//            mHolder.txt_pay_status.setBackgroundResource(R.drawable.bg_pay_failed);
//        } else {
//            mHolder.txt_pay_status.setText("成功");
//            mHolder.txt_pay_status.setBackgroundResource(R.drawable.bg_pay_success);
//        }
//        mHolder.txt_tn.setText("订单号：" + record.getNotice_sn());
//        mHolder.txt_time.setText("订单时间：" + record.getCreate_time());

        //充值
        mHolder.txt_amount.setTextColor(mContext.getResources().getColor(R.color.colorAccent2));
        mHolder.txt_amount.setText("+" + record.getMoney());

    }

    @Override
    public int getItemCount() {
        return beanPocketRecords == null ? 0 : beanPocketRecords.size();
    }

    class ViewHolder extends RecyclerViewHolderBase {

        private Unbinder unbinder;

        @BindView(R.id.txt_record_detail)
        TextView txt_record_detail;

        @BindView(R.id.txt_pay_type)
        TextView txt_pay_type;

        @BindView(R.id.txt_pay_status)
        TextView txt_pay_status;

        @BindView(R.id.txt_tn)
        TextView txt_tn;
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
