package com.xptschool.parent.ui.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.android.widget.audiorecorder.MediaPlayerManager;
import com.xptschool.parent.R;
import com.xptschool.parent.common.BroadcastAction;
import com.xptschool.parent.model.BeanChat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dexing on 2017/5/16.
 * No1
 */

public class BaseAdapterDelegate {

    public String TAG = BaseAdapterDelegate.class.getSimpleName();
    public Context mContext;
    public BeanChat currentChat;

    public BaseAdapterDelegate(Context context) {
        this.mContext = context;
//        TAG = "";
    }

}
