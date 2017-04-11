package com.xptschool.parent.ui.wallet.card;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xptschool.parent.R;
import com.xptschool.parent.adapter.BaseRecycleAdapter;
import com.xptschool.parent.adapter.RecyclerViewHolderBase;
import com.xptschool.parent.ui.fence.FenceDrawActivity;
import com.xptschool.parent.ui.homework.HomeWorkAdapter;
import com.xptschool.parent.ui.wallet.BillActivity;
import com.xptschool.parent.view.CustomDialog;
import com.xptschool.parent.view.CustomEditDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by dexing on 2017/4/10.
 * No1
 */

public class BalanceAdapter extends BaseRecycleAdapter {

    public BalanceAdapter(Context context) {
        super(context);
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
        final TranslateAnimation mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        mShowAction.setDuration(300);
        mShowAction.setFillAfter(true);

        final TranslateAnimation mDismissAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 1.0f);
        mDismissAction.setDuration(300);
        mDismissAction.setFillAfter(true);

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
                mContext.startActivity(intent);
            }
        });
        mHolder.txt_bill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHolder.ll_bottom.startAnimation(mDismissAction);
                mHolder.ll_bottom.setVisibility(View.GONE);
                Intent intent = new Intent(mContext, BillActivity.class);
                mContext.startActivity(intent);
            }
        });
        mHolder.txt_freeze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHolder.ll_bottom.startAnimation(mDismissAction);
                mHolder.ll_bottom.setVisibility(View.GONE);

                CustomDialog dialog = new CustomDialog(mContext);
                dialog.setTitle(R.string.label_freeze);
                dialog.setMessage(R.string.msg_freeze);
                dialog.setAlertDialogClickListener(new CustomDialog.DialogClickListener() {
                    @Override
                    public void onPositiveClick() {
                        //冻结学生卡
                        mHolder.txt_freeze.setText(R.string.label_unfreeze);
                    }
                });


            }
        });
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    class ViewHolder extends RecyclerViewHolderBase {

        private Unbinder unbinder;

        @BindView(R.id.rl_stu_card)
        RelativeLayout rl_stu_card;

        @BindView(R.id.imgHead)
        ImageView imgHead;

        @BindView(R.id.txtStuName)
        TextView txtStuName;

        @BindView(R.id.txtIMEI)
        TextView txtIMEI;

        @BindView(R.id.txt_balance)
        TextView txt_balance;

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
