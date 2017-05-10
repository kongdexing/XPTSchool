package com.xptschool.parent.ui.contact;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.widget.view.CircularImageView;
import com.xptschool.parent.R;
import com.xptschool.parent.bean.BeanQuestionTalk;
import com.xptschool.parent.bean.MessageSendStatus;
import com.xptschool.parent.model.BeanChat;
import com.xptschool.parent.model.BeanParent;
import com.xptschool.parent.model.GreenDaoHelper;
import com.xptschool.parent.ui.question.QuestionDetailActivity;
import com.xptschool.parent.ui.question.QuestionDetailAdapter;

import java.util.ArrayList;
import java.util.IllegalFormatCodePointException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by dexing on 2017/5/10.
 * No1
 */

public class ChatAdapter extends RecyclerView.Adapter {

    private String TAG = ChatAdapter.class.getSimpleName();
    private List<BeanChat> listChat;
    private int VIEW_PARENT = 0;
    private int VIEW_TEACHER = 1;
    private ParentAdapterDelegate parentAdapterDelegate;
    private TeacherAdapterDelegate teacherAdapterDelegate;

    public ChatAdapter(Context context) {
        parentAdapterDelegate = new ParentAdapterDelegate(context, VIEW_PARENT);
        teacherAdapterDelegate = new TeacherAdapterDelegate(context, VIEW_TEACHER);
    }

    @Override
    public int getItemViewType(int position) {
        return listChat.get(position).isSend() ? parentAdapterDelegate.getViewType() : teacherAdapterDelegate.getViewType();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == parentAdapterDelegate.getViewType()) {
            return parentAdapterDelegate.onCreateViewHolder(parent);
        } else {
            return teacherAdapterDelegate.onCreateViewHolder(parent);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = holder.getItemViewType();
        Log.i(TAG, "onBindViewHolder position:" + position + " viewType:" + viewType);
        if (viewType == parentAdapterDelegate.getViewType()) {
            parentAdapterDelegate.onBindViewHolder(listChat, position, holder);
        } else {
            teacherAdapterDelegate.onBindViewHolder(listChat, position, holder);
        }
    }

    @Override
    public int getItemCount() {
        return listChat == null ? 0 : listChat.size();
    }

    public void loadData(List<BeanChat> chats) {
        listChat = chats;
        notifyDataSetChanged();
    }

    //  添加数据
    public void addData(BeanChat chat) {
        Log.i(TAG, "addData: " + chat.getChatId());
        listChat.add(listChat.size(), chat);
        notifyItemInserted(listChat.size());
    }

    public void updateData(BeanChat chat) {
        Log.i(TAG, "updateData: ");
        for (int i = 0; i < listChat.size(); i++) {
            if (listChat.get(i).getChatId().equals(chat.getChatId())) {
                Log.i(TAG, "updateData chatId : " + chat.getChatId() + "  position:" + i);
                listChat.set(i, chat);
                notifyItemChanged(i);
                break;
            }
        }
    }

    //  删除数据
    public void removeData(int position) {
        listChat.remove(listChat.size() - 1);
        notifyItemRemoved(listChat.size());
    }

    /**
     * ViewHolder的类，用于缓存控件
     */
    class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.imgUser)
        CircularImageView imgUser;

        @BindView(R.id.txtContent)
        TextView txtContent;

        @BindView(R.id.rlVoice)
        RelativeLayout rlVoice;

        @BindView(R.id.id_recorder_length)
        RelativeLayout id_recorder_length;

        @BindView(R.id.id_recorder_anim)
        View id_recorder_anim;

        @BindView(R.id.id_recorder_time)
        TextView id_recorder_time;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

}
