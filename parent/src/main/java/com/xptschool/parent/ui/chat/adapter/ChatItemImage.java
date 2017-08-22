package com.xptschool.parent.ui.chat.adapter;

import android.content.Context;
import android.content.Intent;
import android.media.ExifInterface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.android.widget.view.BubbleImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.xptschool.parent.R;
import com.xptschool.parent.XPTApplication;
import com.xptschool.parent.common.CommonUtil;
import com.xptschool.parent.model.BeanChat;
import com.xptschool.parent.ui.chat.ChatActivity;
import com.xptschool.parent.ui.chat.DragPhotoActivity;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by dexing on 2017/6/8.
 * No1
 */
public class ChatItemImage extends LinearLayout {

    private String TAG = ChatItemImage.class.getSimpleName();
    @BindView(R.id.bubImg)
    BubbleImageView bubView;
    private Context mContext;

    public ChatItemImage(Context context) {
        this(context, null);
    }

    public ChatItemImage(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat_parent_image, this, true);
        ButterKnife.bind(this, view);
    }

    public void setChatInfo(final BeanChat chatInfo) {
        if (chatInfo.getIsSend()) {
            bubView.setArrowLocation(BubbleImageView.LOCATION_RIGHT);
        } else {
            bubView.setArrowLocation(BubbleImageView.LOCATION_LEFT);
        }

        final File file = new File(XPTApplication.getInstance().getCachePath() + "/" + chatInfo.getFileName());
        Log.i(TAG, "setChatInfo: " + file.getPath());

        int degree = readPictureDegree(file.getPath());
        Log.i(TAG, "takeSuccess degree: " + degree);


        ImageLoader.getInstance().displayImage("file://" + file.getPath(),
                new ImageViewAware(bubView),
                CommonUtil.getDefaultImageLoaderOption());

        bubView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DragPhotoActivity.class);
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

    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }
}
