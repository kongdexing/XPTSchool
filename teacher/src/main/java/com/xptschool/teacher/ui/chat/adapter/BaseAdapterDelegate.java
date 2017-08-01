package com.xptschool.teacher.ui.chat.adapter;

import android.content.Context;

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
