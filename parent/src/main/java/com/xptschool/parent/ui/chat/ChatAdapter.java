package com.xptschool.parent.ui.chat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.widget.view.CircularImageView;
import com.xptschool.parent.R;
import com.xptschool.parent.model.BeanChat;
import com.xptschool.parent.model.BeanTeacher;
import com.xptschool.parent.model.ContactTeacher;

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
    private ContactTeacher currentTeacher;

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
            teacherAdapterDelegate.onBindViewHolder(currentTeacher,listChat, position, holder);
        }
    }

    @Override
    public int getItemCount() {
        return listChat == null ? 0 : listChat.size();
    }

    public void loadData(List<BeanChat> chats, ContactTeacher teacher) {
        listChat = chats;
        currentTeacher = teacher;
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

}
