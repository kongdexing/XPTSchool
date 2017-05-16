package com.xptschool.parent.ui.chat;

import android.util.Log;
import android.view.View;

import com.android.widget.audiorecorder.MediaPlayerManager;
import com.xptschool.parent.R;
import com.xptschool.parent.model.BeanChat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dexing on 2017/5/16.
 * No1
 */

public class SoundPlayHelper {

    private List<View> playSoundViews = new ArrayList<View>();
    private static SoundPlayHelper mInstance = null;

    public static SoundPlayHelper getInstance() {
        if (mInstance == null) {
            mInstance = new SoundPlayHelper();
        }
        return mInstance;
    }

    public void insertPlayView(View view) {
        if (playSoundViews == null) {
            playSoundViews = new ArrayList<View>();
        }
        playSoundViews.add(view);
    }

    public int getPlaySoundViewSize() {
        return playSoundViews == null ? 0 : playSoundViews.size();
    }

    public void setPlaySoundViews(List<View> playSoundViews) {
        this.playSoundViews = playSoundViews;
    }

    public void stopPlay() {
        //停止声音
        MediaPlayerManager.pause();

        int size = getPlaySoundViewSize();
        for (int i = 0; i < size; i++) {
            BeanChat chat1 = (BeanChat) playSoundViews.get(i).getTag();
            if (chat1.isSend()) {
                playSoundViews.get(i).setBackgroundResource(R.drawable.adj);
            } else {
                playSoundViews.get(i).setBackgroundResource(R.drawable.adj_right);
            }
        }
    }
}
