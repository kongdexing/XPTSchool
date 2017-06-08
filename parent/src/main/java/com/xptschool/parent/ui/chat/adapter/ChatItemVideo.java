package com.xptschool.parent.ui.chat.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.widget.view.BubbleImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.xptschool.parent.R;
import com.xptschool.parent.XPTApplication;
import com.xptschool.parent.common.CommonUtil;
import com.xptschool.parent.model.BeanChat;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by dexing on 2017/6/8.
 * No1
 */

public class ChatItemVideo extends LinearLayout {

    private String TAG = ChatItemVideo.class.getSimpleName();
    @BindView(R.id.bubView)
    BubbleImageView bubView;

    @BindView(R.id.txtDuration)
    TextView txtDuration;

    public ChatItemVideo(Context context) {
        this(context, null);
    }

    public ChatItemVideo(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat_parent_video, this, true);
        ButterKnife.bind(this, view);
    }

    public void setChatInfo(BeanChat chatInfo) {
        final File file = new File(XPTApplication.getInstance().getCachePath() + "/" + chatInfo.getFileName());
        Log.i(TAG, "setChatInfo: " + file.getPath());

        ImageLoader.getInstance().displayImage("file://" + file.getPath(),
                new ImageViewAware(bubView),
                CommonUtil.getDefaultImageLoaderOption());

        txtDuration.setText(chatInfo.getSeconds() + "s");
    }

}
