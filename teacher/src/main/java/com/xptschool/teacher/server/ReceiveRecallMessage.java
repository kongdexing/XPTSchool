package com.xptschool.teacher.server;

import android.content.Intent;
import android.util.Log;

import com.android.volley.VolleyError;
import com.android.volley.common.VolleyHttpParamsEntity;
import com.android.volley.common.VolleyHttpResult;
import com.android.volley.common.VolleyHttpService;
import com.android.volley.common.VolleyRequestListener;
import com.xptschool.teacher.XPTApplication;
import com.xptschool.teacher.common.BroadcastAction;
import com.xptschool.teacher.common.SharedPreferencesUtil;
import com.xptschool.teacher.http.HttpAction;
import com.xptschool.teacher.model.BeanChat;
import com.xptschool.teacher.model.BeanTeacher;
import com.xptschool.teacher.model.GreenDaoHelper;
import com.xptschool.teacher.util.ChatUtil;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by dexing on 2017/8/29 0029.
 * No1
 */

public class ReceiveRecallMessage {

    private static String TAG = ReceiveRecallMessage.class.getSimpleName();

    public static void receiveRecallMessage() {
        final String user_id = (String) SharedPreferencesUtil.getData(XPTApplication.getContext(), SharedPreferencesUtil.KEY_UID, "");
        if (user_id == null || user_id.isEmpty()) {
            Log.i(TAG, "receiveRecallMessage teacher is null or userId is null ");
            return;
        }

        VolleyHttpService.getInstance().sendPostRequest(HttpAction.MESSAGE_RECALL_SHOW,
                new VolleyHttpParamsEntity()
                        .addParam("user_id", user_id)
                        .addParam("user_type", "3"), new VolleyRequestListener() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onResponse(VolleyHttpResult volleyHttpResult) {
                        switch (volleyHttpResult.getStatus()) {
                            case HttpAction.SUCCESS:
                                try {
                                    JSONArray array = new JSONArray(volleyHttpResult.getData().toString());
                                    Log.i(TAG, "onResponse array length: " + array.length());
                                    for (int i = 0; i < array.length(); i++) {
                                        JSONObject object = array.getJSONObject(i);
                                        BeanChat chat = new BeanChat();
                                        chat.setChatId(object.getString("chatid"));
                                        chat.setMsgId(object.getString("chatid"));
                                        chat.setTeacherId(user_id);
                                        chat.setParentId(object.getString("sender_id"));
                                        chat.setSendStatus(ChatUtil.STATUS_RECALL);
                                        chat.setTime("20" + object.getString("recvtime"));
                                        GreenDaoHelper.getInstance().insertChat(chat);
                                        //发送广播，通知该聊天消息已撤回
                                        Intent intent = new Intent();
                                        intent.putExtra("chatId", chat.getChatId());
                                        intent.putExtra("status", "success");
                                        intent.setAction(BroadcastAction.MESSAGE_RECALL);
                                        XPTApplication.getInstance().sendBroadcast(intent);
                                    }
                                } catch (Exception ex) {
                                    Log.i(TAG, "onResponse: " + ex.getMessage());
                                }
                                break;
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                });
    }

}
