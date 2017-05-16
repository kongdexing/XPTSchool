package com.xptschool.parent.ui.wallet.card;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.widget.view.CircularImageView;
import com.xptschool.parent.R;
import com.xptschool.parent.adapter.BaseRecycleAdapter;
import com.xptschool.parent.adapter.RecyclerViewHolderBase;
import com.xptschool.parent.common.BroadcastAction;
import com.xptschool.parent.model.BeanStudent;
import com.xptschool.parent.ui.wallet.bill.BillActivity;
import com.xptschool.parent.util.ToastUtils;
import com.xptschool.parent.view.CustomDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by dexing on 2017/4/10.
 * No1
 */

public class BalanceAdapter extends BaseRecycleAdapter {

    private List<BeanCardBalance> beanCardStudents = new ArrayList<>();
    private List<BeanStudent> beanStudents = new ArrayList<>();

    public BalanceAdapter(Context context) {
        super(context);
    }

    public void reloadData(List<BeanCardBalance> cardStudents, List<BeanStudent> students) {
        beanCardStudents = cardStudents;
        beanStudents = students;
    }

    private BeanCardBalance findBalanceByStuId(String stuId) {
        for (int i = 0; i < beanCardStudents.size(); i++) {
            if (beanCardStudents.get(i).getStu_id().equals(stuId)) {
                return beanCardStudents.get(i);
            }
        }
        return null;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_consumer_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ViewHolder mHolder = (ViewHolder) holder;
        final BeanStudent student = beanStudents.get(position);
        if (student == null) {
            mHolder.fl_Content.setVisibility(View.GONE);
            return;
        }
        final BeanCardBalance balance = findBalanceByStuId(student.getStu_id());
//        if (balance == null) {
//            mHolder.fl_Content.setVisibility(View.GONE);
//            return;
//        }

        //弹起操作项动画
        final TranslateAnimation mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        mShowAction.setDuration(300);
        mShowAction.setFillAfter(true);
        //隐藏操作项动画
        final TranslateAnimation mDismissAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 1.0f);
        mDismissAction.setDuration(300);
        mDismissAction.setFillAfter(true);

        //绑定数据
        if (student.getSex().equals("1")) {
            mHolder.imgHead.setImageResource(R.drawable.student_boy);
        } else {
            mHolder.imgHead.setImageResource(R.drawable.student_girl);
        }
        mHolder.txtStuName.setText(student.getStu_name());
        mHolder.txtIMEI.setText(student.getImei_id());
        mHolder.txt_balance.setText(balance == null ? "0.00" : balance.getBalances());
        mHolder.txt_classname.setText("班级：" + student.getG_name() + student.getC_name());
        mHolder.txt_birthday.setText("出生日期：" + student.getBirth_date());
        mHolder.txt_school_time.setText("入学时间：" + student.getRx_date());
        mHolder.txt_school.setText("学校：" + student.getS_name());

        mHolder.rl_stu_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mHolder.ll_bottom.getVisibility() == View.GONE) {
                    mHolder.ll_bottom.startAnimation(mShowAction);
                    mHolder.ll_bottom.setVisibility(View.VISIBLE);
                } else {
                    mHolder.ll_bottom.startAnimation(mDismissAction);
                    mHolder.ll_bottom.setVisibility(View.GONE);
                }
            }
        });

        mHolder.txt_recharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHolder.ll_bottom.startAnimation(mDismissAction);
                mHolder.ll_bottom.setVisibility(View.GONE);
                Intent intent = new Intent(mContext, CardRechargeActivity.class);
                intent.putExtra("stu_id", student.getStu_id());
                intent.putExtra("balance", balance == null ? "0.00" : balance.getBalances());
                mContext.startActivity(intent);
            }
        });
        mHolder.txt_bill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHolder.ll_bottom.startAnimation(mDismissAction);
                mHolder.ll_bottom.setVisibility(View.GONE);
                Intent intent = new Intent(mContext, BillActivity.class);
                intent.putExtra("stu_id", student.getStu_id());
                mContext.startActivity(intent);
            }
        });

        if (balance == null) {
            mHolder.txt_freeze.setText(mContext.getResources().getString(R.string.label_freeze_unavailable));
        } else {
            final String freeze_status = balance.getFreeze();
            mHolder.txt_freeze.setText(freeze_status.equals("0") ?
                    mContext.getResources().getString(R.string.label_freeze) : mContext.getResources().getString(R.string.label_unfreeze));

            mHolder.txt_freeze.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mHolder.ll_bottom.startAnimation(mDismissAction);
                    mHolder.ll_bottom.setVisibility(View.GONE);
                    if (Double.parseDouble(balance.getBalances()) == 0) {
                        ToastUtils.showToast(mContext, "当前卡余额为0，不可操作！");
                        return;
                    }
                    CustomDialog dialog = new CustomDialog(mContext);
                    if (freeze_status.equals("0")) {
                        dialog.setTitle(R.string.label_freeze);
                        dialog.setMessage(R.string.msg_freeze);
                        dialog.setAlertDialogClickListener(new CustomDialog.DialogClickListener() {
                            @Override
                            public void onPositiveClick() {
                                //冻结学生卡
                                Intent intent = new Intent(BroadcastAction.CARD_FREEZE);
                                intent.putExtra("stu_id", student.getStu_id());
                                intent.putExtra("freeze", "1");
                                mContext.sendBroadcast(intent);
                            }
                        });
                    } else {
                        dialog.setTitle(R.string.label_unfreeze);
                        dialog.setMessage(R.string.msg_unfreeze);
                        dialog.setAlertDialogClickListener(new CustomDialog.DialogClickListener() {
                            @Override
                            public void onPositiveClick() {
                                //解冻学生卡
                                Intent intent = new Intent(BroadcastAction.CARD_FREEZE);
                                intent.putExtra("stu_id", student.getStu_id());
                                intent.putExtra("freeze", "0");
                                mContext.sendBroadcast(intent);
                            }
                        });
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return beanStudents == null ? 0 : beanStudents.size();
    }

    class ViewHolder extends RecyclerViewHolderBase {

        private Unbinder unbinder;

        @BindView(R.id.fl_Content)
        FrameLayout fl_Content;

        @BindView(R.id.rl_stu_card)
        RelativeLayout rl_stu_card;

        @BindView(R.id.imgHead)
        CircularImageView imgHead;

        @BindView(R.id.txtStuName)
        TextView txtStuName;

        @BindView(R.id.txt_imei)
        TextView txtIMEI;

        @BindView(R.id.txt_balance)
        TextView txt_balance;

        @BindView(R.id.txt_classname)
        TextView txt_classname;

        @BindView(R.id.txt_birthday)
        TextView txt_birthday;

        @BindView(R.id.txt_school_time)
        TextView txt_school_time;

        @BindView(R.id.txt_school)
        TextView txt_school;

        @BindView(R.id.ll_bottom)
        LinearLayout ll_bottom;

        @BindView(R.id.txt_recharge)
        TextView txt_recharge;

        @BindView(R.id.txt_bill)
        TextView txt_bill;

        @BindView(R.id.txt_freeze)
        TextView txt_freeze;

        public ViewHolder(View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this, itemView);
        }

    }
}
