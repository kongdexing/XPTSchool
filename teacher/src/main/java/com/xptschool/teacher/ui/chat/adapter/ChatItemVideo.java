package com.xptschool.teacher.ui.chat.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.widget.view.BubbleImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.xptschool.teacher.R;
import com.xptschool.teacher.XPTApplication;
import com.xptschool.teacher.common.CommonUtil;
import com.xptschool.teacher.model.BeanChat;
import com.xptschool.teacher.ui.chat.ChatActivity;
import com.xptschool.teacher.ui.chat.DragVideoActivity;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by dexing on 2017/6/8.
 * No1
 */

public class ChatItemVideo extends LinearLayout {

    private String TAG = ChatItemVideo.class.getSimpleName();
    @BindView(R.id.rlContent)
    RelativeLayout rlContent;

    @BindView(R.id.bubView)
    BubbleImageView bubView;

    @BindView(R.id.txtDuration)
    TextView txtDuration;
    private Context mContext;

    public ChatItemVideo(Context context) {
        this(context, null);
    }

    public ChatItemVideo(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat_teacher_video, this, true);
        ButterKnife.bind(this, view);
    }

    public void setChatInfo(final BeanChat chatInfo) {
        if (chatInfo.isSend()) {
            bubView.setArrowLocation(BubbleImageView.LOCATION_RIGHT);
        } else {
            bubView.setArrowLocation(BubbleImageView.LOCATION_LEFT);
        }
        final File file = new File(XPTApplication.getInstance().getCachePath() + "/" + chatInfo.getFileName());
        Log.i(TAG, "setChatInfo: " + file.getPath());
        ImageLoader.getInstance().displayImage("file://" + file.getPath(),
                new ImageViewAware(bubView),
                CommonUtil.getDefaultImageLoaderOption());
        txtDuration.setText(chatInfo.getSeconds() + "s");

        rlContent.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DragVideoActivity.class);
                int location[] = new int[2];
                intent.putExtra("chat", chatInfo);
                bubView.getLocationOnScreen(location);
                intent.putExtra("left", location[0]);
                intent.putExtra("top", location[1]);
                intent.putExtra("height", bubView.getHeight());
                intent.putExtra("width", bubView.getWidth());
                mContext.startActivity(intent);
                ((ChatActivity) mContext).overridePendingTransition(0, 0);
            }
        });

    }

}
