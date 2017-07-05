package com.xptschool.parent.ui.question;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.widget.view.CircularImageView;
import com.xptschool.parent.R;
import com.xptschool.parent.bean.BeanQuestion;
import com.xptschool.parent.bean.BeanQuestionTalk;
import com.xptschool.parent.bean.MessageSendStatus;
import com.xptschool.parent.model.BeanParent;
import com.xptschool.parent.model.GreenDaoHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/10/31.
 */
public class QuestionDetailAdapter extends BaseAdapter {

    private String TAG = QuestionDetailAdapter.class.getSimpleName();
    private List<BeanQuestionTalk> listQuestions = new ArrayList<>();
    private Context mContext;

    public QuestionDetailAdapter(Context context) {
        mContext = context;
    }

    public void insertChat(BeanQuestionTalk answer) {
        listQuestions.add(answer);
        notifyDataSetChanged();
    }

    public void updateChat(BeanQuestionTalk answer) {
        for (int i = 0; i < listQuestions.size(); i++) {
            if (listQuestions.get(i).getCreate_time().equals(answer.getCreate_time())) {
                listQuestions.get(i).setSendStatus(answer.getSendStatus());
                break;
            }
        }
        notifyDataSetChanged();
    }

    public void deleteChatItem(BeanQuestion answer) {
        listQuestions.remove(answer);
        notifyDataSetChanged();
    }

    public void refreshData(List<BeanQuestionTalk> data) {
        listQuestions = data;
        notifyDataSetChanged();
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        Log.i(TAG, "getView: " + position);
        ViewHolder mHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_chat, viewGroup, false);
            mHolder = new ViewHolder(convertView);
            convertView.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) convertView.getTag();
        }
        final BeanQuestionTalk question = listQuestions.get(position);
        BeanParent parent = GreenDaoHelper.getInstance().getCurrentParent();
        if (parent == null) {
            mHolder.rlParent.setVisibility(View.GONE);
            mHolder.rlTeacher.setVisibility(View.GONE);
            return convertView;
        }

        if (question.getSender_id().equals(parent.getU_id())) {
            mHolder.sendProgress.setVisibility(View.GONE);
            mHolder.llResend.setVisibility(View.GONE);

            mHolder.rlParent.setVisibility(View.VISIBLE);
            mHolder.rlTeacher.setVisibility(View.GONE);

            //家长提问，提问发送状态
            if (question.getSendStatus().equals(MessageSendStatus.FAILED)) {
                mHolder.llResend.setVisibility(View.VISIBLE);
                mHolder.llResend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((QuestionDetailActivity) mContext).sendAnswer(question);
                    }
                });
            } else if (question.getSendStatus().equals(MessageSendStatus.SENDING)) {
                mHolder.sendProgress.setVisibility(View.VISIBLE);
            } else {
                mHolder.sendProgress.setVisibility(View.GONE);
            }

            if (parent.getSex().equals("1")) {
                mHolder.imgParent.setImageResource(R.drawable.parent_father);
            } else {
                mHolder.imgParent.setImageResource(R.drawable.parent_mother);
            }
            mHolder.txtParent.setText(question.getContent());

        } else {
            mHolder.rlParent.setVisibility(View.GONE);
            mHolder.rlTeacher.setVisibility(View.VISIBLE);

            //老师回复
            if (question.getReceiver_sex().equals("1")) {
                mHolder.imgTeacher.setImageResource(R.drawable.teacher_man);
            } else {
                mHolder.imgTeacher.setImageResource(R.drawable.teacher_woman);
            }
            mHolder.txtTeacher.setText(question.getContent());
        }
        return convertView;
    }

    @Override
    public Object getItem(int i) {
        Log.i(TAG, "getItem: " + i);
        return listQuestions.get(i);
    }

    @Override
    public long getItemId(int i) {
        Log.i(TAG, "getItemId: " + i);
        return i;
    }

    @Override
    public int getItemViewType(int position) {
        Log.i(TAG, "getItemViewType: " + position);
        return super.getItemViewType(position);
    }

    @Override
    public int getCount() {
        Log.i(TAG, "getCount: " + listQuestions.size());
        return listQuestions.size();
    }

    public class ViewHolder {

        @BindView(R.id.rlParent)
        RelativeLayout rlParent;

        @BindView(R.id.rlTeacher)
        RelativeLayout rlTeacher;

        @BindView(R.id.imgParent)
        CircularImageView imgParent;

        @BindView(R.id.imgTeacher)
        CircularImageView imgTeacher;

        @BindView(R.id.txtParent)
        TextView txtParent;

        @BindView(R.id.txtTeacher)
        TextView txtTeacher;

        @BindView(R.id.sendProgress)
        ProgressBar sendProgress;

        @BindView(R.id.llResend)
        LinearLayout llResend;

        public ViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
        }

    }

}
