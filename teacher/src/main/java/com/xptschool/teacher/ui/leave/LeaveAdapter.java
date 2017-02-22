package com.xptschool.teacher.ui.leave;

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

import com.baidu.mapapi.map.Text;
import com.xptschool.teacher.R;
import com.xptschool.teacher.adapter.BaseRecycleAdapter;
import com.xptschool.teacher.adapter.RecyclerViewHolderBase;
import com.xptschool.teacher.bean.BeanHomeWork;
import com.xptschool.teacher.bean.BeanLeave;
import com.xptschool.teacher.bean.LeaveType;
import com.xptschool.teacher.common.ExtraKey;

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
        beanLeaves = leaves;
    }

    public void appendData(List<BeanLeave> leaves) {
        Log.i(TAG, "refreshData: ");
        beanLeaves.addAll(leaves);
    }

    public int updateBeanLeave(BeanLeave leave) {
        for (int i = 0; i < beanLeaves.size(); i++) {
            if (beanLeaves.get(i).getId().equals(leave.getId())) {
                beanLeaves.set(i, leave);
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
        final LeaveAdapter.ViewHolder mHolder = (LeaveAdapter.ViewHolder) holder;
        final BeanLeave beanLeave = beanLeaves.get(position);
        mHolder.txtStudentName.setText(beanLeave.getStu_name());
        mHolder.txtStartTime.setText("请假时间：" + beanLeave.getStart_time());
        mHolder.txtEndTime.setText("销假时间：" + beanLeave.getEnd_time());
        mHolder.txtLeaveName.setText("请假类型：" + beanLeave.getLeave_name());
        String status = "";
        if (beanLeave.getStatus().equals("0")) {
            status = "状态：已提交";
        } else if (beanLeave.getStatus().equals("1")) {
            status = "状态：已批准";
        } else if (beanLeave.getStatus().equals("2")) {
            status = "状态：已驳回";
        }
        mHolder.txtStatus.setText(status);
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

        @BindView(R.id.txtStudentName)
        TextView txtStudentName;

        @BindView(R.id.txtStudentCode)
        TextView txtStudentCode;

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

        @BindView(R.id.txtReply)
        TextView txtReply;

        public ViewHolder(View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this, itemView);
        }

    }

}
