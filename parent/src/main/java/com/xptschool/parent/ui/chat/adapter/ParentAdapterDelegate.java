package com.xptschool.parent.ui.chat.adapter;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.widget.audiorecorder.MediaPlayerManager;
import com.android.widget.view.CircularImageView;
import com.xptschool.parent.R;
import com.xptschool.parent.XPTApplication;
import com.xptschool.parent.model.BeanChat;
import com.xptschool.parent.model.BeanParent;
import com.xptschool.parent.model.GreenDaoHelper;
import com.xptschool.parent.ui.chat.SoundPlayHelper;
import com.xptschool.parent.util.ChatUtil;
import com.xptschool.parent.util.ToastUtils;

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
    public AnimationDrawable animation;

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

    public void onBindViewHolder(List items, final int position, RecyclerView.ViewHolder holder, final ChatAdapter.OnItemResendListener listener) {
        final BeanChat chat = (BeanChat) items.get(position);
        BeanParent parent = GreenDaoHelper.getInstance().getCurrentParent();
        if (parent == null || chat == null) {
            return;
        }
        final MyViewHolder viewHolder = (MyViewHolder) holder;

        //家长提问，提问发送状态
        if (chat.getSendStatus() == ChatUtil.STATUS_FAILED) {
            viewHolder.llResend.setVisibility(View.VISIBLE);
            viewHolder.sendProgress.setVisibility(View.GONE);
            viewHolder.llResend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onResend(chat, position);
                    }
                }
            });
        } else if (chat.getSendStatus() == ChatUtil.STATUS_SENDING) {
            viewHolder.sendProgress.setVisibility(View.VISIBLE);
            viewHolder.llResend.setVisibility(View.GONE);
        } else {
            viewHolder.sendProgress.setVisibility(View.GONE);
            viewHolder.llResend.setVisibility(View.GONE);
        }

        if (parent.getSex().equals("1")) {
            viewHolder.imgUser.setImageResource(R.drawable.parent_father);
        } else {
            viewHolder.imgUser.setImageResource(R.drawable.parent_mother);
        }
        viewHolder.txtContent.setVisibility(View.GONE);
        viewHolder.rlVoice.setVisibility(View.GONE);
        viewHolder.imageView.setVisibility(View.GONE);
        viewHolder.videoView.setVisibility(View.GONE);

        if ((ChatUtil.TYPE_TEXT + "").equals(chat.getType())) {
            viewHolder.txtContent.setVisibility(View.VISIBLE);

            //聊天内容
            viewHolder.txtContent.setText(chat.getContent());
            viewHolder.llContent.setOnLongClickListener(new MyLongClickListener(viewHolder));
        } else if ((ChatUtil.TYPE_AMR + "").equals(chat.getType())) {
            Log.i(TAG, "onBindViewHolder amr:" + chat.getFileName());
            //录音
            viewHolder.rlVoice.setVisibility(View.VISIBLE);

            viewHolder.id_recorder_time.setText(chat.getSeconds() + "\"");

            ViewGroup.LayoutParams lp = viewHolder.id_recorder_length.getLayoutParams();
            lp.width = (int) (ChatUtil.getChatMinWidth(mContext) + (ChatUtil.getChatMaxWidth(mContext) / 60f) * Integer.parseInt(chat.getSeconds()));

            final File file = new File(XPTApplication.getInstance().getCachePath() + "/" + chat.getFileName());
            if (!file.exists()) {
                viewHolder.error_file.setVisibility(View.VISIBLE);
                return;
            }

            viewHolder.img_recorder_anim.setTag(chat);
            SoundPlayHelper.getInstance().insertPlayView(viewHolder.img_recorder_anim);
            Log.i(TAG, "onBindViewHolder: parent playSoundViews size " + SoundPlayHelper.getInstance().getPlaySoundViewSize());

            viewHolder.rlVoice.getParent().requestDisallowInterceptTouchEvent(true);

            //点击播放
            viewHolder.rlVoice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 声音播放动画
                    if (viewHolder.img_recorder_anim != null) {
                        viewHolder.img_recorder_anim.setBackgroundResource(R.drawable.adj);
                    }

                    SoundPlayHelper.getInstance().stopPlay();

                    viewHolder.img_recorder_anim.setBackgroundResource(R.drawable.play_anim);
                    animation = (AnimationDrawable) viewHolder.img_recorder_anim.getBackground();
                    animation.start();

                    Log.i(TAG, "onClick: parent playSound");
                    // 播放录音
                    MediaPlayerManager.playSound(file.getPath(), new MediaPlayer.OnCompletionListener() {

                        public void onCompletion(MediaPlayer mp) {
                            Log.i(TAG, "onCompletion: ");
                            //播放完成后修改图片
                            viewHolder.img_recorder_anim.setBackgroundResource(R.drawable.adj);
                        }
                    });
                }
            });
            viewHolder.rlVoice.setOnLongClickListener(new MyLongClickListener(viewHolder));
        } else if ((ChatUtil.TYPE_FILE + "").equals(chat.getType())) {
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
            viewHolder.imageView.setImageViewLongClickListener(new MyLongClickListener(viewHolder));
        } else if ((ChatUtil.TYPE_VIDEO + "").equals(chat.getType())) {
            final File file = new File(XPTApplication.getInstance().getCachePath() + "/" + chat.getFileName());
            Log.i(TAG, "video: " + file.getPath());

            if (!file.exists()) {
                viewHolder.error_file.setVisibility(View.VISIBLE);
            } else {
                viewHolder.error_file.setVisibility(View.GONE);
                viewHolder.videoView.setVisibility(View.VISIBLE);
                viewHolder.videoView.setChatInfo(chat);
            }
            viewHolder.videoView.setVideoLongClickListener(new MyLongClickListener(viewHolder));
        }
    }

    class MyLongClickListener implements View.OnLongClickListener {

        private MyViewHolder viewHolder;

        private MyLongClickListener(MyViewHolder holder) {
            viewHolder = holder;
        }

        @Override
        public boolean onLongClick(View v) {
            final ChatOptionView optionView = new ChatOptionView(mContext);
            final PopupWindow chatPopup = new PopupWindow(optionView,
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
            chatPopup.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            chatPopup.setTouchable(true);
            chatPopup.setBackgroundDrawable(new ColorDrawable());
            //弹出操作项控件宽高
            optionView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            int v_width = optionView.getMeasuredWidth();
            int v_height = optionView.getMeasuredHeight();

            int c_width = viewHolder.llContent.getMeasuredWidth();
            int c_height = viewHolder.llContent.getMeasuredHeight();
            Log.i(TAG, "onLongClick: optionView width=" + v_width + "  height:" + v_height);
            Log.i(TAG, "onLongClick: content width:" + c_width + " height:" + c_height);

            //计算控件在屏幕的位置
            int[] location = new int[2];
            v.getLocationOnScreen(location);
            int x = location[0];
            int y = location[1];
            Log.i(TAG, "onLongClick: " + "x:" + x + " y:" + y);
            if (y > (c_height + v_height)) {
                //控件上显示
                chatPopup.showAtLocation(viewHolder.llContent, Gravity.NO_GRAVITY, (x - v_width / 2), (y - v_height));
            } else {
                //控件下显示
                chatPopup.showAtLocation(viewHolder.llContent, Gravity.NO_GRAVITY, (x - v_width / 2), (y + c_height));
            }
            return false;
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.imgUser)
        CircularImageView imgUser;

        @BindView(R.id.llContent)
        LinearLayout llContent;

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

        @BindView(R.id.id_recorder_time)
        TextView id_recorder_time;

        @BindView(R.id.sendProgress)
        ProgressBar sendProgress;

        @BindView(R.id.llResend)
        LinearLayout llResend;

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
