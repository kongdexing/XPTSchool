package com.xptschool.teacher.ui.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.android.widget.audiorecorder.MediaPlayerManager;
import com.xptschool.teacher.R;
import com.xptschool.teacher.common.BroadcastAction;
import com.xptschool.teacher.model.BeanChat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dexing on 2017/5/16.
 * No1
 */

public class BaseAdapterDelegate {

    public String TAG = "";
    public Context mContext;
    public BeanChat currentChat;
    public AnimationDrawable animation;
    public List<View> playSoundViews = new ArrayList<>();

    public BaseAdapterDelegate(Context context) {
        this.mContext = context;
        TAG = mContext.getPackageName();
    }

    public void onDetachedFromRecyclerView() {
        try {
            MediaPlayerManager.pause();
            mContext.unregisterReceiver(playSoundReceiver);
        } catch (Exception ex) {
            Log.i(TAG, "onDetachedFromRecyclerView: " + ex.getMessage());
        }
    }

    public BroadcastReceiver playSoundReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle == null) {
                return;
            }
            BeanChat chat = (BeanChat) bundle.getSerializable("chat");

            if (BroadcastAction.PLAY_SOUND.equals(intent.getAction())) {
                if (!chat.getChatId().equals(currentChat.getChatId())) {
                    //停止声音
                    MediaPlayerManager.pause();
                    //停止
                    if (animation != null) {
                        animation.stop();
                    }

                    for (int i = 0; i < playSoundViews.size(); i++) {
                        BeanChat chat1 = (BeanChat) playSoundViews.get(i).getTag();
                        if (!chat1.equals(currentChat)) {
                            if (chat1.isSend()) {
                                //老师
                                playSoundViews.get(i).setBackgroundResource(R.drawable.adj);
                            } else {
                                playSoundViews.get(i).setBackgroundResource(R.drawable.adj_right);
                            }
                        }
                    }
                }
            }
        }
    };

}
