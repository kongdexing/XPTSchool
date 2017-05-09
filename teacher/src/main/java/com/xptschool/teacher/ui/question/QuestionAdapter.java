package com.xptschool.teacher.ui.question;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.widget.view.CircularImageView;
import com.xptschool.teacher.R;
import com.xptschool.teacher.adapter.BaseRecycleAdapter;
import com.xptschool.teacher.adapter.RecyclerViewHolderBase;
import com.xptschool.teacher.bean.BeanQuestion;
import com.xptschool.teacher.common.CommonUtil;
import com.xptschool.teacher.common.ExtraKey;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2016/10/29.
 */
public class QuestionAdapter extends BaseRecycleAdapter {

    private List<BeanQuestion> listQuestions = new ArrayList<>();

    public QuestionAdapter(Context context) {
        super(context);
    }

    public void refreshData(List<BeanQuestion> data) {
        Log.i(TAG, "refreshData: ");
        listQuestions = data;
    }

    public void appendData(List<BeanQuestion> data) {
        Log.i(TAG, "refreshData: ");
        listQuestions.addAll(data);
    }

    @Override
    public RecyclerViewHolderBase onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_question, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        QuestionAdapter.ViewHolder mHolder = (QuestionAdapter.ViewHolder) holder;
        final BeanQuestion question = listQuestions.get(position);

        if (question.getSender_sex().equals("1")) {
            mHolder.imgSender.setImageResource(R.drawable.parent_father);
        } else {
            mHolder.imgSender.setImageResource(R.drawable.parent_mother);
        }

        mHolder.txtClassName.setText(question.getG_name() + question.getC_name());
        mHolder.txtTitle.setText("标题：" + question.getTitle());
        mHolder.txtContent.setText("内容：" + question.getContent());
        mHolder.txtTime.setText(CommonUtil.parseDate(question.getCreate_time()));
        mHolder.txtName.setText(question.getSender_name());

        mHolder.rlQuestionItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, QuestionDetailActivity.class);
                intent.putExtra(ExtraKey.QUESTION, question);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listQuestions.size();
    }

    public class ViewHolder extends RecyclerViewHolderBase {
        private Unbinder unbinder;
        @BindView(R.id.rlQuestionItem)
        RelativeLayout rlQuestionItem;

        @BindView(R.id.imgSender)
        CircularImageView imgSender;

        @BindView(R.id.txtClassName)
        TextView txtClassName;

        @BindView(R.id.txtTitle)
        TextView txtTitle;

        @BindView(R.id.txtContent)
        TextView txtContent;

        @BindView(R.id.txtTime)
        TextView txtTime;

        @BindView(R.id.txtName)
        TextView txtName;

        public ViewHolder(View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this, itemView);
        }

    }

}
