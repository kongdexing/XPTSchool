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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.common.VolleyHttpParamsEntity;
import com.android.volley.common.VolleyHttpResult;
import com.android.volley.common.VolleyHttpService;
import com.android.volley.common.VolleyRequestListener;
import com.android.widget.audiorecorder.MediaPlayerManager;
import com.android.widget.view.CircularImageView;
import com.xptschool.parent.R;
import com.xptschool.parent.XPTApplication;
import com.xptschool.parent.common.BroadcastAction;
import com.xptschool.parent.common.CommonUtil;
import com.xptschool.parent.http.HttpAction;
import com.xptschool.parent.model.BeanChat;
import com.xptschool.parent.model.BeanParent;
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

        viewHolder.imgUser.setVisibility(View.GONE);
        viewHolder.txtContent.setVisibility(View.GONE);
        viewHolder.rlVoice.setVisibility(View.GONE);
        viewHolder.imageView.setVisibility(View.GONE);
        viewHolder.videoView.setVisibility(View.GONE);

        //判断是否为撤回
        if (chat.getSendStatus() == ChatUtil.STATUS_RECALL) {
            viewHolder.llRevert.setVisibility(View.VISIBLE);
//            viewHolder.txtRevert.setText("");
            return;
        } else {
            viewHolder.llRevert.setVisibility(View.GONE);
        }

        //判断男女头像
        if (parent.getSex().equals("1")) {
            viewHolder.imgUser.setVisibility(View.VISIBLE);
            viewHolder.imgUser.setImageResource(R.drawable.parent_father);
        } else {
            viewHolder.imgUser.setVisibility(View.VISIBLE);
            viewHolder.imgUser.setImageResource(R.drawable.parent_mother);
        }

        //判断发送状态
        int sendStatus = chat.getSendStatus();
        if (sendStatus == ChatUtil.STATUS_FAILED) {
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
        } else if (sendStatus == ChatUtil.STATUS_SENDING || sendStatus == ChatUtil.STATUS_RESENDING
                || sendStatus == ChatUtil.STATUS_RECALLING) {
            viewHolder.sendProgress.setVisibility(View.VISIBLE);
            viewHolder.llResend.setVisibility(View.GONE);
        } else {
            viewHolder.sendProgress.setVisibility(View.GONE);
            viewHolder.llResend.setVisibility(View.GONE);
        }

        View longClickView = null;

        if ((ChatUtil.TYPE_TEXT + "").equals(chat.getType())) {
            viewHolder.txtContent.setVisibility(View.VISIBLE);
            //聊天内容
            viewHolder.txtContent.setText(chat.getContent());
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
                RelativeLayout.LayoutParams voiceAnimLP = (RelativeLayout.LayoutParams) viewHolder.img_recorder_anim.getLayoutParams();
                voiceAnimLP.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                voiceAnimLP.setMargins(0, 0, 30, 0);
                viewHolder.img_recorder_anim.setTag(chat);

                SoundPlayHelper.getInstance().insertPlayView(viewHolder.img_recorder_anim);
                Log.i(TAG, "onBindViewHolder: parent playSoundViews size " + SoundPlayHelper.getInstance().getPlaySoundViewSize());

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

                        Log.i(TAG, "onClick: parent playSound " + file.getPath());
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
            }
            longClickView = viewHolder.rlVoice;
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
            longClickView = viewHolder.imageView.bubView;
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
            if (chat.getSendStatus() == ChatUtil.STATUS_SENDING) {
                Log.i(TAG, "onLongClick: is sending");
                return false;
            }

            Log.i(TAG, "onLongClick chat time: " + chat.getTime());
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
                    intent.putExtra("chatId", chat.getChatId());
                    intent.setAction(BroadcastAction.MESSAGE_DELETE_SUCCESS);
                    XPTApplication.getInstance().sendBroadcast(intent);
                }
            });
            //添加删除按钮
            optionView.addAction(deleteItem);

            if (CommonUtil.isIn2Min(chat.getTime())) {
                ActionItem revertItem = new ActionItem();
                revertItem.setTitle(mContext.getString(R.string.label_chat_option_revert));
                revertItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ToastUtils.showToast(mContext, "revert " + chat.getMsgId());
                        chatPopup.dismiss();

                        //发起http请求
                        VolleyHttpService.getInstance().sendPostRequest(HttpAction.MESSAGE_RECALL,
                                new VolleyHttpParamsEntity()
                                        .addParam("chatid", chat.getMsgId()), new VolleyRequestListener() {
                                    @Override
                                    public void onStart() {
                                        Intent intent = new Intent();
                                        intent.putExtra("chatId", chat.getChatId());
                                        intent.putExtra("status", "start");
                                        intent.setAction(BroadcastAction.MESSAGE_RECALL);
                                        XPTApplication.getInstance().sendBroadcast(intent);
                                    }

                                    @Override
                                    public void onResponse(VolleyHttpResult volleyHttpResult) {
                                        Intent recallIntent = new Intent();
                                        recallIntent.setAction(BroadcastAction.MESSAGE_RECALL);
                                        recallIntent.putExtra("chatId", chat.getChatId());
                                        switch (volleyHttpResult.getStatus()) {
                                            case HttpAction.SUCCESS:
                                                recallIntent.putExtra("status", "success");
                                                break;
                                            case HttpAction.FAILED:
                                                recallIntent.putExtra("status", "failed");
                                                break;
                                        }
                                        XPTApplication.getInstance().sendBroadcast(recallIntent);
                                    }

                                    @Override
                                    public void onErrorResponse(VolleyError volleyError) {
                                        Intent recallIntent = new Intent();
                                        recallIntent.putExtra("chatId", chat.getChatId());
                                        recallIntent.putExtra("status", "failed");
                                        recallIntent.setAction(BroadcastAction.MESSAGE_RECALL);
                                        XPTApplication.getInstance().sendBroadcast(recallIntent);
                                    }
                                });

//                        ToSendMessage message = new ToSendMessage();
//                        message.setType(ChatUtil.TYPE_REVERT);
//                        message.setId(chat.getChatId());
//                        message.setFilename(chat.getMsgId());
//                        message.setAllData(message.packData(""));
//                        ServerManager.getInstance().sendMessage(message);

                    }
                });
                //两分钟之内发送的消息，添加撤回按钮
                optionView.addAction(revertItem);
            }

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
