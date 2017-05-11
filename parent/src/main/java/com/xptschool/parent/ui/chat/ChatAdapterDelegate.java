package com.xptschool.parent.ui.chat;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * Created by dexing on 2017/5/11.
 * No1
 */

public class ChatAdapterDelegate {

    //item的最小宽度
    public int mMinWidth;
    //item的最大宽度
    public int mMaxWidth;

    public ChatAdapterDelegate(Context context) {

        //获取屏幕的宽度
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        //最大宽度为屏幕宽度的百分之七十
        mMaxWidth = (int) (outMetrics.widthPixels * 0.7f);
        //最大宽度为屏幕宽度的百分之十五
        mMinWidth = (int) (outMetrics.widthPixels * 0.15f);
    }
}
