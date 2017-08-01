package com.xptschool.parent.ui.checkin;

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
import com.xptschool.parent.bean.BeanCheckin;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/11/1.
 */

public class StudentCheckinAdapter extends BaseRecycleAdapter {

    private List<BeanCheckin> listBeanCheckins = new ArrayList<>();
    private String TAG = getClass().getSimpleName();

    public StudentCheckinAdapter(Context context) {
        super(context);
        Log.i(TAG, "StudentCheckinAdapter: ");
    }

    public void insert() {
        BeanCheckin checkin = new BeanCheckin();
//        insertInternal(listBeanCheckins, checkin, getItemCount() - 1);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        Log.i(TAG, "onBindViewHolder: ");
        final ViewHolder mHolder = (ViewHolder) holder;
        final BeanCheckin beanCheckin = listBeanCheckins.get(position);
//        mHolder.txtDate.setText(beanCheckin.getDate());
//        mHolder.txtDate.setText(beanCheckin.getDate());
//        mHolder.txtInTime.setText(beanCheckin.getInSchoolTime());
//        mHolder.txtOutTime.setText(beanCheckin.getOutSchoolTime());
    }

    @Override
    public int getItemCount() {
        Log.i(TAG, "getAdapterItemCount: ");
        return listBeanCheckins.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.i(TAG, "onCreateViewHolder: ");
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_student_checkin, parent, false);
        return new ViewHolder(view);
    }

    class ViewHolder extends RecyclerViewHolderBase {

        @BindView(R.id.txtDate)
        TextView txtDate;

        @BindView(R.id.txtInTime)
        TextView txtInTime;

        @BindView(R.id.txtOutTime)
        TextView txtOutTime;

        @BindView(R.id.txtLeaveTime)
        TextView txtLeaveTime;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
