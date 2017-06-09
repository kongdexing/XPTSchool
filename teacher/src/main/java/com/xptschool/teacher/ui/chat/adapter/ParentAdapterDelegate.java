package com.xptschool.teacher.ui.chat.adapter;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.widget.audiorecorder.MediaPlayerManager;
import com.android.widget.view.BubbleImageView;
import com.android.widget.view.CircularImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.xptschool.teacher.R;
import com.xptschool.teacher.XPTApplication;
import com.xptschool.teacher.common.CommonUtil;
import com.xptschool.teacher.model.BeanChat;
import com.xptschool.teacher.model.ContactParent;
import com.xptschool.teacher.model.GreenDaoHelper;
import com.xptschool.teacher.ui.chat.SoundPlayHelper;
import com.xptschool.teacher.util.ChatUtil;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.rockerhieu.emojicon.EmojiconTextView;

/**
 * Created by dexing on 2017/5/10.
 * No1
 */

public class ParentAdapterDelegate extends BaseAdapterDelegate {

    private int viewType;

    public ParentAdapterDelegate(Context context, int viewType) {
        super(context);
        this.viewType = viewType;
        this.mContext = context;
    }

    public int getViewType() {
        return viewType;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_chat_parent, parent, false));
    }

    public void onBindViewHolder(ContactParent parent, List items, int position, RecyclerView.ViewHolder holder) {
        final BeanChat chat = (BeanChat) items.get(position);
        if (parent == null) {
            return;
        }
        final MyViewHolder viewHolder = (MyViewHolder) holder;

        if (parent.getSex().equals("1")) {
            viewHolder.imgUser.setImageResource(R.drawable.parent_father);
        } else {
            viewHolder.imgUser.setImageResource(R.drawable.parent_mother);
        }

        if ((ChatUtil.TYPE_TEXT + "").equals(chat.getType())) {
            viewHolder.txtContent.setVisibility(View.VISIBLE);
            viewHolder.rlVoice.setVisibility(View.GONE);
            //聊天内容
            viewHolder.txtContent.setText(chat.getContent());
            updateReadStatus(chat, viewHolder);
        } else if ((ChatUtil.TYPE_AMR + "").equals(chat.getType())) {
            //录音
            viewHolder.txtContent.setVisibility(View.GONE);
            viewHolder.rlVoice.setVisibility(View.VISIBLE);
            viewHolder.id_recorder_time.setText(chat.getSeconds() + "\"");

            ViewGroup.LayoutParams lp = viewHolder.id_recorder_length.getLayoutParams();
            lp.width = (int) (ChatUtil.getChatMinWidth(mContext) + (ChatUtil.getChatMaxWidth(mContext) / 60f) * Integer.parseInt(chat.getSeconds()));

            final File file = new File(XPTApplication.getInstance().getCachePath() + "/" + chat.getFileName());
            if (!file.exists()) {
                viewHolder.error_file.setVisibility(View.VISIBLE);
                return;
            } else {
                viewHolder.error_file.setVisibility(View.GONE);
            }

            if (!chat.isHasRead()) {
                viewHolder.view_unRead.setVisibility(View.VISIBLE);
            } else {
                viewHolder.view_unRead.setVisibility(View.GONE);
            }

            viewHolder.img_recorder_anim.setTag(chat);
            SoundPlayHelper.getInstance().insertPlayView(viewHolder.img_recorder_anim);

            //点击播放
            viewHolder.id_recorder_length.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 声音播放动画
                    if (viewHolder.img_recorder_anim != null) {
                        viewHolder.img_recorder_anim.setBackgroundResource(R.drawable.adj_right);
                    }
                    if (!file.exists()) {
                        Log.i(TAG, "file not found ");
                        return;
                    }

                    SoundPlayHelper.getInstance().stopPlay();

                    viewHolder.img_recorder_anim.setBackgroundResource(R.drawable.play_anim_right);
                    AnimationDrawable animation = (AnimationDrawable) viewHolder.img_recorder_anim.getBackground();
                    animation.start();

                    // 播放录音
                    MediaPlayerManager.playSound(file.getPath(), new MediaPlayer.OnCompletionListener() {

                        public void onCompletion(MediaPlayer mp) {
                            //播放完成后修改图片
                            viewHolder.img_recorder_anim.setBackgroundResource(R.drawable.adj_right);
                        }
                    });
                    updateReadStatus(chat, viewHolder);
                }
            });
        } else if ((ChatUtil.TYPE_FILE + "").equals(chat.getType())) {
            updateReadStatus(chat, viewHolder);
            //文件，图片
            //file path
            final File file = new File(XPTApplication.getInstance().getCachePath() + "/" + chat.getFileName());
            Log.i(TAG, "picture: " + file.getPath());

            if (!file.exists()) {
                viewHolder.error_file.setVisibility(View.VISIBLE);
            } else {
                viewHolder.error_file.setVisibility(View.GONE);
                viewHolder.imageView.setVisibility(View.VISIBLE);
                viewHolder.imageView.setChatInfo(chat);
            }
        } else if ((ChatUtil.TYPE_VIDEO + "").equals(chat.getType())) {
            updateReadStatus(chat, viewHolder);

            final File file = new File(XPTApplication.getInstance().getCachePath() + "/" + chat.getFileName());
            Log.i(TAG, "video: " + file.getPath());

            if (!file.exists()) {
                viewHolder.error_file.setVisibility(View.VISIBLE);
            } else {
                viewHolder.error_file.setVisibility(View.GONE);
                viewHolder.videoView.setVisibility(View.VISIBLE);
                viewHolder.videoView.setChatInfo(chat);
            }
        }
    }

    private void updateReadStatus(BeanChat chat, MyViewHolder viewHolder) {
        //未读标示为已读
        if (!chat.isHasRead()) {
            chat.setHasRead(true);
            GreenDaoHelper.getInstance().updateChat(chat);
        }

        if (chat.isHasRead()) {
            viewHolder.view_unRead.setVisibility(View.GONE);
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.imgUser)
        CircularImageView imgUser;

        @BindView(R.id.txtContent)
        EmojiconTextView txtContent;

        @BindView(R.id.rlVoice)
        RelativeLayout rlVoice;

        @BindView(R.id.id_recorder_length)
        RelativeLayout id_recorder_length;

        @BindView(R.id.id_recorder_anim)
        View img_recorder_anim;

        @BindView(R.id.error_file)
        View error_file;

        @BindView(R.id.view_unRead)
        View view_unRead;

        @BindView(R.id.id_recorder_time)
        TextView id_recorder_time;

        @BindView(R.id.imageView)
        ChatItemImage imageView;

        @BindView(R.id.videoView)
        ChatItemVideo videoView;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

}
