package com.xptschool.parent.ui.chat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewGroup;

import com.xptschool.parent.model.BeanChat;
import com.xptschool.parent.model.ContactTeacher;
import com.xptschool.parent.model.GreenDaoHelper;
import com.xptschool.parent.ui.chat.SoundPlayHelper;
import com.xptschool.parent.util.ChatUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by dexing on 2017/5/10.
 * No1
 */

public class ChatAdapter extends RecyclerView.Adapter {

    private String TAG = ChatAdapter.class.getSimpleName();
    private List<BeanChat> listChat = new ArrayList<>();
    private int VIEW_PARENT = 0;
    private int VIEW_TEACHER = 1;
    private ParentAdapterDelegate parentAdapterDelegate;
    private TeacherAdapterDelegate teacherAdapterDelegate;
    private ContactTeacher currentTeacher;

    public ChatAdapter(Context context) {
        parentAdapterDelegate = new ParentAdapterDelegate(context, VIEW_PARENT);
        teacherAdapterDelegate = new TeacherAdapterDelegate(context, VIEW_TEACHER);
        SoundPlayHelper.getInstance().setPlaySoundViews(null);
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
            parentAdapterDelegate.onBindViewHolder(listChat, position, holder, new OnItemResendListener());
        } else {
            teacherAdapterDelegate.onBindViewHolder(currentTeacher, listChat, position, holder);
        }
    }

    @Override
    public int getItemCount() {
        return listChat == null ? 0 : listChat.size();
    }

    public void setCurrentTeacher(ContactTeacher currentTeacher) {
        this.currentTeacher = currentTeacher;
    }

    public void appendData(List<BeanChat> chats) {
        if (listChat.size() == 0) {
            listChat = chats;
        } else {
            List<BeanChat> newList = new ArrayList<BeanChat>();
            for (Iterator<BeanChat> it = chats.iterator(); it.hasNext(); ) {
                newList.add(it.next());
            }
            listChat.addAll(0, newList);
        }
        notifyDataSetChanged();
    }

    //  添加数据
    public void addData(BeanChat chat) {
        Log.i(TAG, "addData: " + chat.getChatId());
        listChat.add(listChat.size(), chat);
        notifyItemInserted(listChat.size());
    }

    public void updateData(BeanChat chat) {
        GreenDaoHelper.getInstance().updateChat(chat);
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
    public void removeData(BeanChat chat) {
        if (chat == null || chat.getChatId() == null) {
            return;
        }

        for (int i = 0; i < listChat.size(); i++) {
            if (listChat.get(i).getChatId().equals(chat.getChatId())) {
                Log.i(TAG, "updateData chatId : " + chat.getChatId() + "  position:" + i);
                listChat.remove(i);
                notifyItemRemoved(i);
                break;
            }
        }
    }

    public class OnItemResendListener {
        void onResend(BeanChat chat, int position) {
            chat.setSendStatus(ChatUtil.STATUS_SENDING);
            updateData(chat);
            chat.onReSendChatToMessage();
        }
    }

}
