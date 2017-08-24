package com.xptschool.parent.ui.chat.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.widget.audiorecorder.MediaPlayerManager;
import com.android.widget.view.CircularImageView;
import com.xptschool.parent.R;
import com.xptschool.parent.XPTApplication;
import com.xptschool.parent.common.BroadcastAction;
import com.xptschool.parent.model.BeanChat;
import com.xptschool.parent.model.ContactTeacher;
import com.xptschool.parent.model.GreenDaoHelper;
import com.xptschool.parent.ui.chat.QuickAction.ActionItem;
import com.xptschool.parent.ui.chat.QuickAction.ChatOptionView;
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

public class TeacherAdapterDelegate extends BaseAdapterDelegate {

    private int viewType;
//    public AnimationDrawable animation;

    public TeacherAdapterDelegate(Context context, int viewType) {
        super(context);
        this.viewType = viewType;
        this.mContext = context;
    }

    public int getViewType() {
        return viewType;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_chat_teacher, parent, false));
    }

    public void onBindViewHolder(ContactTeacher teacher, List items, int position, RecyclerView.ViewHolder holder) {
        final BeanChat chat = (BeanChat) items.get(position);
        if (teacher == null) {
            return;
        }
        final MyViewHolder viewHolder = (MyViewHolder) holder;

        //判断是否为撤回
        if (chat.getSendStatus() == ChatUtil.STATUS_REVERT) {
            viewHolder.llRevert.setVisibility(View.VISIBLE);
            viewHolder.txtRevert.setText("\"" + teacher.getName() + "\"撤回了一条消息");
            return;
        } else {
            viewHolder.llRevert.setVisibility(View.GONE);
        }

        viewHolder.imgUser.setVisibility(View.GONE);
        viewHolder.txtContent.setVisibility(View.GONE);
        viewHolder.rlVoice.setVisibility(View.GONE);
        viewHolder.imageView.setVisibility(View.GONE);
        viewHolder.videoView.setVisibility(View.GONE);

        if (teacher.getSex().equals("1")) {
            viewHolder.imgUser.setVisibility(View.VISIBLE);
            viewHolder.imgUser.setImageResource(R.drawable.teacher_man);
        } else {
            viewHolder.imgUser.setVisibility(View.VISIBLE);
            viewHolder.imgUser.setImageResource(R.drawable.teacher_woman);
        }

        View longClickView = null;

        if ((ChatUtil.TYPE_TEXT + "").equals(chat.getType())) {
            Log.i(TAG, "onBindViewHolder text:" + chat.getContent());
            viewHolder.txtContent.setVisibility(View.VISIBLE);
            //聊天内容
            viewHolder.txtContent.setText(chat.getContent());
            updateReadStatus(chat, viewHolder);
            longClickView = viewHolder.llContent;
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
            } else {
                viewHolder.error_file.setVisibility(View.GONE);
                viewHolder.img_recorder_anim.setTag(chat);
                SoundPlayHelper.getInstance().insertPlayView(viewHolder.img_recorder_anim);
                Log.i(TAG, "onBindViewHolder: teacher playSoundViews size " + SoundPlayHelper.getInstance().getPlaySoundViewSize());

                if (!chat.isHasRead()) {
                    viewHolder.view_unRead.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.view_unRead.setVisibility(View.GONE);
                }

                //点击播放
                viewHolder.id_recorder_length.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 声音播放动画
                        if (viewHolder.img_recorder_anim != null) {
                            viewHolder.img_recorder_anim.setBackgroundResource(R.drawable.adj_right);
                        }

                        SoundPlayHelper.getInstance().stopPlay();

                        viewHolder.img_recorder_anim.setBackgroundResource(R.drawable.play_anim_right);
                        AnimationDrawable animation = (AnimationDrawable) viewHolder.img_recorder_anim.getBackground();
                        animation.start();

                        Log.i(TAG, "onClick: teacher playSound " + file.getPath());

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
            }
            longClickView = viewHolder.rlVoice;
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
            longClickView = viewHolder.imageView.bubView;
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
            longClickView = viewHolder.videoView.bubView;
        }
        longClickView.setOnLongClickListener(new MyLongClickListener(viewHolder, chat));
    }

    class MyLongClickListener implements View.OnLongClickListener {

        private MyViewHolder viewHolder;
        private BeanChat chat;

        private MyLongClickListener(MyViewHolder holder, BeanChat chat) {
            this.viewHolder = holder;
            this.chat = chat;
        }

        @Override
        public boolean onLongClick(View v) {
            final ChatOptionView optionView = new ChatOptionView(mContext);
            final PopupWindow chatPopup = new PopupWindow(optionView,
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);

            ActionItem deleteItem = new ActionItem();
            deleteItem.setTitle(mContext.getString(R.string.label_chat_option_delete));
            deleteItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chatPopup.dismiss();

                    Intent intent = new Intent();
                    intent.putExtra("message", chat.getChatId());
                    intent.setAction(BroadcastAction.MESSAGE_DELETE_SUCCESS);
                    XPTApplication.getInstance().sendBroadcast(intent);
                }
            });
            //添加删除按钮
            optionView.addAction(deleteItem);

            chatPopup.setTouchable(true);
            chatPopup.setBackgroundDrawable(new ColorDrawable());
            //弹出操作项控件宽高
            optionView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            int v_width = optionView.getMeasuredWidth();
            int v_height = optionView.getMeasuredHeight();

            int c_width = viewHolder.llContent.getMeasuredWidth();
            int c_height = viewHolder.llContent.getMeasuredHeight();
            Log.i(TAG, "onLongClick: optionView v_width=" + v_width + "  v_height:" + v_height);
            Log.i(TAG, "onLongClick: content c_width:" + c_width + " c_height:" + c_height);

            //计算控件在屏幕的位置
            int[] location = new int[2];
            v.getLocationOnScreen(location);
            int x = location[0];
            int y = location[1];
            Log.i(TAG, "onLongClick: " + "x:" + x + " y:" + y);
            if (y > (c_height + v_height)) {
                //控件上显示
                chatPopup.showAtLocation(viewHolder.llContent, Gravity.NO_GRAVITY, (x + c_width - v_width / 2), (y - v_height));
            } else {
                //控件下显示
                chatPopup.showAtLocation(viewHolder.llContent, Gravity.NO_GRAVITY, (x + c_width - v_width / 2), (y + c_height));
            }
            return false;
        }
    }

    private void updateReadStatus(BeanChat chat, MyViewHolder viewHolder) {
        //未读标示为已读
        if (!chat.isHasRead()) {
            viewHolder.view_unRead.setVisibility(View.GONE);
            chat.setHasRead(true);
            GreenDaoHelper.getInstance().updateChat(chat);
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

        @BindView(R.id.view_unRead)
        View view_unRead;

        @BindView(R.id.id_recorder_time)
        TextView id_recorder_time;

        @BindView(R.id.imageView)
        ChatItemImage imageView;

        @BindView(R.id.videoView)
        ChatItemVideo videoView;

        @BindView(R.id.llRevert)
        LinearLayout llRevert;
        @BindView(R.id.txtRevert)
        TextView txtRevert;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

}
