package com.xptschool.parent.ui.checkin;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xptschool.parent.R;
import com.xptschool.parent.adapter.BaseRecycleAdapter;
import com.xptschool.parent.adapter.RecyclerViewHolderBase;
import com.xptschool.parent.bean.BeanCheckin;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2016/10/26.
 */
public class CheckinAdapter extends BaseRecycleAdapter {

    private List<BeanCheckin> listBeanCheckins = new ArrayList<>();

    public CheckinAdapter(Context context) {
        super(context);
    }

    public void loadDate(List<BeanCheckin> dates) {
        listBeanCheckins = dates;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_checkin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ViewHolder mHolder = (ViewHolder) holder;
        final BeanCheckin beanCheckin = listBeanCheckins.get(position);
        mHolder.txtStudentName.setText(beanCheckin.getStu_name());
        mHolder.txtInTime.setText(beanCheckin.getSignin_time());
        mHolder.txtOutTime.setText(beanCheckin.getSignout_time());
        mHolder.txtLeaveTime.setText(beanCheckin.getLeave());
    }

    @Override
    public int getItemCount() {
        return listBeanCheckins.size();
    }

    public class ViewHolder extends RecyclerViewHolderBase {
        private Unbinder unbinder;

        @BindView(R.id.llCheckInItem)
        LinearLayout llCheckInItem;

        @BindView(R.id.txtStudentName)
        TextView txtStudentName;

        @BindView(R.id.txtInTime)
        TextView txtInTime;

        @BindView(R.id.txtOutTime)
        TextView txtOutTime;

        @BindView(R.id.txtLeaveTime)
        TextView txtLeaveTime;

        public ViewHolder(View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this, itemView);
        }

    }

}
