package com.xptschool.parent.ui.question;

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
import com.baidu.mapapi.map.Text;
import com.xptschool.parent.R;
import com.xptschool.parent.adapter.BaseRecycleAdapter;
import com.xptschool.parent.adapter.RecyclerViewHolderBase;
import com.xptschool.parent.common.CommonUtil;
import com.xptschool.parent.common.ExtraKey;
import com.xptschool.parent.bean.BeanQuestion;
import com.xptschool.parent.model.BeanStudent;

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
    private BeanStudent currentStudent;

    public QuestionAdapter(Context context) {
        super(context);
    }

    public void setCurrentStudent(BeanStudent currentStudent) {
        this.currentStudent = currentStudent;
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
        ViewHolder mHolder = (ViewHolder) holder;
        final BeanQuestion question = listQuestions.get(position);
        if (currentStudent != null) {
            mHolder.txtClassName.setVisibility(View.VISIBLE);
            mHolder.txtClassName.setText(currentStudent.getG_name() + currentStudent.getC_name());
        } else {
            mHolder.txtClassName.setVisibility(View.GONE);
        }
        if (question.getReceiver_sex().equals("1")) {
            mHolder.imgReceiver.setImageResource(R.mipmap.teacher_man);
        } else {
            mHolder.imgReceiver.setImageResource(R.mipmap.teacher_woman);
        }

        mHolder.txtContent.setText(question.getContent());
        mHolder.txtTitle.setText(question.getTitle());
        mHolder.txtTime.setText(CommonUtil.parseDate(question.getCreate_time()));
        mHolder.txtName.setText(question.getReceiver_name());

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

        @BindView(R.id.imgReceiver)
        CircularImageView imgReceiver;

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
