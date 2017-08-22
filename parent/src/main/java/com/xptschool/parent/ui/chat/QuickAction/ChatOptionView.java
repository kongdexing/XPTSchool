package com.xptschool.parent.ui.chat.QuickAction;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xptschool.parent.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by dexing on 2017/8/21 0021.
 * No1
 */

public class ChatOptionView extends LinearLayout {

    @BindView(R.id.llContent)
    LinearLayout llContent;
    private Context mContext;

    public ChatOptionView(Context context) {
        this(context, null);
        mContext = context;
    }

    public ChatOptionView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.chat_option_items, this, true);
        ButterKnife.bind(view);
    }

    public void addAction(ActionItem item) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.chat_option_item, null);
        ((TextView) view.findViewById(R.id.txtOption)).setText(item.getTitle());
        (view.findViewById(R.id.llOption)).setOnClickListener(item.getListener());
        llContent.addView(view);
    }


}
