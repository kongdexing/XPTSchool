package com.xptschool.teacher.ui.chat.adapter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
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

    public BaseAdapterDelegate(Context context) {
        this.mContext = context;
        TAG = mContext.getClass().getSimpleName();
    }

}
