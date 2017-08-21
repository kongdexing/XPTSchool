package com.xptschool.parent.ui.chat.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.xptschool.parent.R;

/**
 * Created by dexing on 2017/8/21 0021.
 * No1
 */

public class ChatOptionView extends LinearLayout {

    public ChatOptionView(Context context) {
        this(context, null);
    }

    public ChatOptionView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.chat_option_items, this, true);


    }


}
