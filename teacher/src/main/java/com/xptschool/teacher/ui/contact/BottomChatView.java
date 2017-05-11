package com.xptschool.teacher.ui.contact;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.xptschool.teacher.R;

/**
 * Created by Administrator on 2016/10/28.
 */

public class BottomChatView extends LinearLayout implements View.OnClickListener {

    private String TAG = BottomChatView.class.getSimpleName();
    private LinearLayout llCall;
    private LinearLayout llChat;
    private LinearLayout rlBack;
    private OnBottomChatClickListener clickListener;

    public BottomChatView(Context context) {
        this(context, null);
    }

    public BottomChatView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.widget_bottom_chat, this, true);
        try {
            llCall = (LinearLayout) view.findViewById(R.id.llCall);
            llChat = (LinearLayout) view.findViewById(R.id.llChat);
            rlBack = (LinearLayout) view.findViewById(R.id.rlBack);
            llCall.setOnClickListener(this);
            llChat.setOnClickListener(this);
            rlBack.setOnClickListener(this);
        } catch (Exception ex) {
            Log.i(TAG, "AlbumSourceView: " + ex.getMessage());
        }
    }

    public void setOnBottomChatClickListener(OnBottomChatClickListener listener) {
        clickListener = listener;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.llCall:
                if (clickListener != null) {
                    clickListener.onCallClick();
                }
                break;
            case R.id.llChat:
                if (clickListener != null) {
                    clickListener.onChatClick();
                }
                break;
            case R.id.rlBack:
                if (clickListener != null) {
                    clickListener.onBack();
                }
                break;
        }
    }

    public interface OnBottomChatClickListener {
        void onCallClick();

        void onChatClick();

        void onBack();
    }

}
