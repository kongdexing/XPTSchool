package com.xptschool.parent.ui.leave;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xptschool.parent.R;
import com.xptschool.parent.adapter.BaseRecycleAdapter;
import com.xptschool.parent.adapter.RecyclerViewHolderBase;
import com.xptschool.parent.bean.BeanLeave;
import com.xptschool.parent.common.ExtraKey;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2016/11/2.
 */
public class LeaveAdapter extends BaseRecycleAdapter {

    private List<BeanLeave> beanLeaves = new ArrayList<>();

    public LeaveAdapter(Context context) {
        super(context);
    }

    public void refreshData(List<BeanLeave> leaves) {
        Log.i(TAG, "refreshData: ");
        beanLeaves.clear();
        beanLeaves = leaves;
    }

    public void appendData(List<BeanLeave> leaves) {
        Log.i(TAG, "appendData: ");
        beanLeaves.addAll(leaves);
    }

    public int updateBeanLeave(BeanLeave leave) {
        Log.i(TAG, "updateBeanLeave: " + leave.getLeave_memo());
        for (int i = 0; i < beanLeaves.size(); i++) {
            if (beanLeaves.get(i).getId().equals(leave.getId())) {
                if (beanLeaves.get(i).getStu_id().equals(leave.getStu_id())) {
                    beanLeaves.set(i, leave);
                    return i;
                } else {
                    Toast.makeText(mContext, "该请假已移至【" + leave.getStu_name() + "】的请假列表中", Toast.LENGTH_SHORT).show();
                    return -1;
                }
            }
        }
        return 0;
    }

    public int deleteData(BeanLeave leave) {
        for (int i = 0; i < beanLeaves.size(); i++) {
            if (beanLeaves.get(i).getId().equals(leave.getId())) {
                beanLeaves.remove(i);
                Log.i(TAG, "deleteData:  " + i);
                return i;
            }
        }
        return -1;
    }

    @Override
    public RecyclerViewHolderBase onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_leave, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ViewHolder mHolder = (ViewHolder) holder;
        final BeanLeave beanLeave = beanLeaves.get(position);
        mHolder.txtTeacherName.setText("审批老师：" + beanLeave.getT_name());
        mHolder.txtStartTime.setText("请假时间：" + beanLeave.getStart_time());
        mHolder.txtEndTime.setText("销假时间：" + beanLeave.getEnd_time());
        mHolder.txtLeaveName.setText("请假类型：" + beanLeave.getLeave_name());

        //1=>'已批准',2=>'已驳回',0=>'已提交'
        if (beanLeave.getStatus().equals("1")) {
            mHolder.txtStatus.setTextColor(mContext.getResources().getColor(R.color.color_leave_1));
        } else if (beanLeave.getStatus().equals("2")) {
            mHolder.txtStatus.setTextColor(mContext.getResources().getColor(R.color.color_leave_2));
        } else {
            mHolder.txtStatus.setTextColor(mContext.getResources().getColor(R.color.color_leave_0));
        }
        mHolder.txtStatus.setText(beanLeave.getStatus_name());

        mHolder.txtContent.setText(beanLeave.getLeave_memo());
        mHolder.txtReply.setText(beanLeave.getReply());

        mHolder.llLeaveItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, LeaveDetailActivity.class);
                intent.putExtra(ExtraKey.LEAVE_DETAIL, beanLeave);
                ((LeaveActivity) mContext).startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    public int getItemCount() {
        return beanLeaves.size();
    }

    public class ViewHolder extends RecyclerViewHolderBase {
        private Unbinder unbinder;
        @BindView(R.id.llLeaveItem)
        LinearLayout llLeaveItem;

        @BindView(R.id.txtTeacherName)
        TextView txtTeacherName;

        @BindView(R.id.txtStartTime)
        TextView txtStartTime;

        @BindView(R.id.txtEndTime)
        TextView txtEndTime;

        @BindView(R.id.txtLeaveName)
        TextView txtLeaveName;

        @BindView(R.id.txtStatus)
        TextView txtStatus;

        @BindView(R.id.txtContent)
        TextView txtContent;

        @BindView(R.id.llReply)
        LinearLayout llReply;

        @BindView(R.id.txtReply)
        TextView txtReply;

        public ViewHolder(View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this, itemView);
        }

    }

}
