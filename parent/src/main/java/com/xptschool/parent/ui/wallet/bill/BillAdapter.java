package com.xptschool.parent.ui.wallet.bill;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xptschool.parent.R;
import com.xptschool.parent.adapter.BaseRecycleAdapter;
import com.xptschool.parent.adapter.RecyclerViewHolderBase;
import com.xptschool.parent.ui.homework.HomeWorkAdapter;

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

    }

    @Override
    public int getItemCount() {
        return cadBills == null ? 0 : cadBills.size();
    }

    class ViewHolder extends RecyclerViewHolderBase {

        private Unbinder unbinder;

        @BindView(R.id.llhomeworkItem)
        LinearLayout llhomeworkItem;

        @BindView(R.id.txtSubject)
        TextView txtSubject;

        @BindView(R.id.txtClassName)
        TextView txtClassName;

        @BindView(R.id.txtTitle)
        TextView txtTitle;

        @BindView(R.id.txtTeacher)
        TextView txtTeacher;

        @BindView(R.id.txtTime)
        TextView txtTime;

        @BindView(R.id.txtContent)
        TextView txtContent;

        @BindView(R.id.imgDelete)
        ImageView imgDelete;

        public ViewHolder(View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this, itemView);
        }

    }
}
