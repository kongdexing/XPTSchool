package com.xptschool.parent.ui.chat;

import android.view.View;

import com.xptschool.parent.model.ToSendMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dexing on 2017/6/26.
 * No1
 */
//用于发送消息的保存

public class ChatMessageHelper {

    private List<ToSendMessage> sendMessages = new ArrayList<ToSendMessage>();
    private static ChatMessageHelper mInstance = null;

    public static ChatMessageHelper getInstance() {
        if (mInstance == null) {
            mInstance = new ChatMessageHelper();
        }
        return mInstance;
    }

    public void putMessage(ToSendMessage message) {
        if (sendMessages != null && !sendMessages.contains(message)) {
            sendMessages.add(message);
        }
    }

    public ToSendMessage getMessageById(String msgId) {
        if (sendMessages != null) {
            int size = sendMessages.size();
            for (int i = 0; i < size; i++) {
                ToSendMessage message = sendMessages.get(i);
                if (message.getId().equals(msgId)) {
                    return message;
                }
            }
        }
        return null;
    }

    public void reset() {
        if (sendMessages != null) {
            sendMessages.clear();
        }
    }

}
