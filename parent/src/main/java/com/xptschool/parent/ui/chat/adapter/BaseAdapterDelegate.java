package com.xptschool.parent.ui.chat.adapter;

import android.content.Context;

import com.xptschool.parent.model.BeanChat;

/**
 * Created by dexing on 2017/5/16.
 * No1
 */

public class BaseAdapterDelegate {

    public String TAG = BaseAdapterDelegate.class.getSimpleName();
    public Context mContext;

    public BaseAdapterDelegate(Context context) {
        this.mContext = context;
//        TAG = "";
    }

}
