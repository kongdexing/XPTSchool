package com.xptschool.teacher.ui.chat;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.widget.audiorecorder.MediaPlayerManager;
import com.android.widget.view.CircularImageView;
import com.xptschool.teacher.R;
import com.xptschool.teacher.XPTApplication;
import com.xptschool.teacher.model.BeanChat;
import com.xptschool.teacher.model.BeanTeacher;
import com.xptschool.teacher.model.GreenDaoHelper;
import com.xptschool.teacher.util.ChatUtil;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by dexing on 2017/5/10.
 * No1
 */

public class TeacherAdapterDelegate {

    private String TAG = TeacherAdapterDelegate.class.getSimpleName();
    private int viewType;
    private Context mContext;
    private BeanTeacher teacher;

    public TeacherAdapterDelegate(Context context, int viewType) {
        this.viewType = viewType;
        this.mContext = context;
        teacher = GreenDaoHelper.getInstance().getCurrentTeacher();
    }

    public int getViewType() {
        return viewType;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_chat_teacher, parent, false));
    }

    public void onBindViewHolder(List items, int position, RecyclerView.ViewHolder holder) {
        final BeanChat chat = (BeanChat) items.get(position);
        if (teacher == null) {
            return;
        }
        final MyViewHolder viewHolder = (MyViewHolder) holder;

        if (chat.getSendStatus() == ChatUtil.STATUS_FAILED) {
            viewHolder.llResend.setVisibility(View.VISIBLE);
            viewHolder.llResend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    ((QuestionDetailActivity) mContext).sendAnswer(chat);

                }
            });
        } else if (chat.getSendStatus() == ChatUtil.STATUS_SENDING) {
            viewHolder.sendProgress.setVisibility(View.VISIBLE);
        } else {
            viewHolder.sendProgress.setVisibility(View.GONE);
        }

        if (teacher.getSex().equals("1")) {
            viewHolder.imgUser.setImageResource(R.drawable.teacher_man);
        } else {
            viewHolder.imgUser.setImageResource(R.drawable.teacher_woman);
        }

        if ((ChatUtil.TYPE_TEXT + "").equals(chat.getType())) {
            viewHolder.txtContent.setVisibility(View.VISIBLE);
            viewHolder.rlVoice.setVisibility(View.GONE);
            //聊天内容
            viewHolder.txtContent.setText(chat.getContent());
        } else if ((ChatUtil.TYPE_AMR + "").equals(chat.getType())) {
            //录音
            viewHolder.txtContent.setVisibility(View.GONE);
            viewHolder.rlVoice.setVisibility(View.VISIBLE);

            viewHolder.id_recorder_time.setText(chat.getSeconds() + "'");

            ViewGroup.LayoutParams lp = viewHolder.id_recorder_length.getLayoutParams();
            lp.width = (int) (ChatUtil.getChatMinWidth(mContext) + (ChatUtil.getChatMaxWidth(mContext) / 60f) * Integer.parseInt(chat.getSeconds()));

            final File file = new File(XPTApplication.getInstance().getCachePath() + "/" + chat.getFileName());
            if (!file.exists()) {
                viewHolder.error_file.setVisibility(View.VISIBLE);
                return;
            }

            //点击播放
            viewHolder.id_recorder_length.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 声音播放动画
                    if (viewHolder.img_recorder_anim != null) {
                        viewHolder.img_recorder_anim.setBackgroundResource(R.drawable.adj);
                    }
                    Log.i(TAG, "onCompletion: " + file.getPath() + " size:" + file.length());
                    if (!file.exists()) {
                        Log.i(TAG, "file not found ");
                        return;
                    }

                    viewHolder.img_recorder_anim.setBackgroundResource(R.drawable.play_anim);
                    AnimationDrawable animation = (AnimationDrawable) viewHolder.img_recorder_anim.getBackground();
                    animation.start();
                    // 播放录音
                    MediaPlayerManager.playSound(file.getPath(), new MediaPlayer.OnCompletionListener() {

                        public void onCompletion(MediaPlayer mp) {
                            //播放完成后修改图片
                            viewHolder.img_recorder_anim.setBackgroundResource(R.drawable.adj);
                        }
                    });
                }
            });
        } else {
            //文件，图片

        }

    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.imgUser)
        CircularImageView imgUser;

        @BindView(R.id.txtContent)
        TextView txtContent;

        @BindView(R.id.rlVoice)
        RelativeLayout rlVoice;

        @BindView(R.id.id_recorder_length)
        RelativeLayout id_recorder_length;

        @BindView(R.id.id_recorder_anim)
        View img_recorder_anim;
        @BindView(R.id.error_file)
        View error_file;

        @BindView(R.id.id_recorder_time)
        TextView id_recorder_time;

        @BindView(R.id.sendProgress)
        ProgressBar sendProgress;

        @BindView(R.id.llResend)
        LinearLayout llResend;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

}
