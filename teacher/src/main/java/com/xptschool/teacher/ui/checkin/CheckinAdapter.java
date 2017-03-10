package com.xptschool.teacher.ui.checkin;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xptschool.teacher.R;
import com.xptschool.teacher.adapter.BaseRecycleAdapter;
import com.xptschool.teacher.adapter.RecyclerViewHolderBase;
import com.xptschool.teacher.bean.BeanCheckin;

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

    public void refreshData(List<BeanCheckin> beanCheckins) {
        Log.i(TAG, "refreshData: ");
        listBeanCheckins = beanCheckins;
    }

    public void appendData(List<BeanCheckin> beanCheckins) {
        Log.i(TAG, "refreshData: ");
        listBeanCheckins.addAll(beanCheckins);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_checkin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final CheckinAdapter.ViewHolder mHolder = (CheckinAdapter.ViewHolder) holder;
        final BeanCheckin beanCheckin = listBeanCheckins.get(position);
        mHolder.txtStuName.setText(beanCheckin.getStu_name());
        mHolder.txtSignType.setText(beanCheckin.getSchool_type());
        mHolder.txtSTime.setText(beanCheckin.getS_time());
        mHolder.txtStatus.setText(beanCheckin.getSignin_type());
        mHolder.txtInterZone.setText(beanCheckin.getShijianduan());
    }

    @Override
    public int getItemCount() {
        return listBeanCheckins.size();
    }

    public class ViewHolder extends RecyclerViewHolderBase {
        private Unbinder unbinder;

        @BindView(R.id.llCheckInItem)
        LinearLayout llCheckInItem;

        @BindView(R.id.txtStuName)
        TextView txtStuName;

        @BindView(R.id.txtSignType)
        TextView txtSignType;

        @BindView(R.id.txtSTime)
        TextView txtSTime;

        @BindView(R.id.txtStatus)
        TextView txtStatus;

        @BindView(R.id.txtInterZone)
        TextView txtInterZone;

        public ViewHolder(View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this, itemView);
        }

    }

}
