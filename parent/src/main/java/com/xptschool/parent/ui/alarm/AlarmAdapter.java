package com.xptschool.parent.ui.alarm;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import com.xptschool.parent.bean.BeanAlarm;
import com.xptschool.parent.common.ExtraKey;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2016/11/09.
 */
public class AlarmAdapter extends BaseRecycleAdapter {

    private List<BeanAlarm> beanAlarms = new ArrayList<>();

    public AlarmAdapter(Context context) {
        super(context);
    }

    public void refreshData(List<BeanAlarm> beanAlarms) {
        Log.i(TAG, "refreshData: ");
        if (beanAlarms == null) {
            this.beanAlarms.clear();
        } else {
            this.beanAlarms = beanAlarms;
        }
    }

    public void appendData(List<BeanAlarm> beanAlarms) {
        Log.i(TAG, "refreshData: ");
        this.beanAlarms.addAll(beanAlarms);
    }

    public int updateData(BeanAlarm alarm) {
        for (int i = 0; i < beanAlarms.size(); i++) {
            if (beanAlarms.get(i).getWm_id().equals(alarm.getWm_id())) {
                Log.i(TAG, " update " + i + " " + alarm.getCreate_time());
                beanAlarms.set(i, alarm);
                return i;
            }
        }
        return -1;
    }

    public void clearData() {
        beanAlarms.clear();
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_alarm, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ViewHolder mHolder = (ViewHolder) holder;
        final BeanAlarm alarm = beanAlarms.get(position);

        mHolder.txtAlarmType.setText(alarm.getWar_type());
        mHolder.txtTime.setText(alarm.getCreate_time());
        if (alarm.getWar_status().equals("0")) {
            mHolder.txtStatus.setText("未处理");
            mHolder.txtStatus.setTextColor(Color.RED);
        } else {
            mHolder.txtStatus.setText("已处理");
            mHolder.txtStatus.setTextColor(Color.BLACK);
        }

        mHolder.llAlarmItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, AlarmMapActivity.class);
                intent.putExtra(ExtraKey.ALARM_DETAIL, alarm);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return beanAlarms.size();
    }

    public class ViewHolder extends RecyclerViewHolderBase {
        private Unbinder unbinder;
        @BindView(R.id.llAlarmItem)
        LinearLayout llAlarmItem;

        @BindView(R.id.txtAlarmType)
        TextView txtAlarmType;

        @BindView(R.id.txtTime)
        TextView txtTime;

        @BindView(R.id.txtStatus)
        TextView txtStatus;

        public ViewHolder(View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this, itemView);
        }

    }

}
