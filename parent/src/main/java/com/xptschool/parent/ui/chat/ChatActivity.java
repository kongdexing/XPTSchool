package com.xptschool.parent.ui.chat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.widget.audiorecorder.AudioRecorderButton;
import com.android.widget.audiorecorder.MediaPlayerManager;
import com.android.widget.audiorecorder.Recorder;
import com.xptschool.parent.R;
import com.xptschool.parent.common.BroadcastAction;
import com.xptschool.parent.common.CommonUtil;
import com.xptschool.parent.common.ExtraKey;
import com.xptschool.parent.model.BeanChat;
import com.xptschool.parent.model.BeanParent;
import com.xptschool.parent.model.ContactTeacher;
import com.xptschool.parent.model.GreenDaoHelper;
import com.xptschool.parent.server.SocketManager;
import com.xptschool.parent.ui.contact.ContactsDetailActivity;
import com.xptschool.parent.ui.main.BaseActivity;
import com.xptschool.parent.ui.main.BaseListActivity;
import com.xptschool.parent.util.ChatUtil;
import com.xptschool.parent.util.ToastUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.github.rockerhieu.emojicon.EmojiconEditText;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class ChatActivity extends BaseListActivity {

    @BindView(R.id.RlParent)
    RelativeLayout RlParent;

    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.recycleView)
    RecyclerView recycleView;

    @BindView(R.id.imgVoiceOrText)
    ImageView imgVoiceOrText;

    @BindView(R.id.id_recorder_button)
    AudioRecorderButton mAudioRecorderButton;

    @BindView(R.id.edtContent)
    EmojiconEditText edtContent;

    @BindView(R.id.btnSend)
    Button btnSend;

    private boolean isInputWindowShow = false;

    private ChatAdapter adapter = null;
    private ContactTeacher teacher;
    private BeanParent currentParent;
    private List<BeanChat> pageChatList;
    private int currentOffset = 0, localDataCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            teacher = (ContactTeacher) bundle.get(ExtraKey.CHAT_TEACHER);
            if (teacher != null) {
                setTitle(teacher.getName());
            }
        }
        currentParent = GreenDaoHelper.getInstance().getCurrentParent();
        if (currentParent == null || teacher == null) {
            return;
        }

        ChatUtil.currentChatTeacher = teacher;

        setRightImage(R.drawable.icon_right_head);
        setRightImageViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatActivity.this, ContactsDetailActivity.class);
                intent.putExtra(ExtraKey.CONTACT_TYPE, ExtraKey.CONTACT_TEACHER);
                intent.putExtra(ExtraKey.CONTACT, teacher);
                ChatActivity.this.startActivity(intent);
            }
        });

        initView();

        IntentFilter filter = new IntentFilter();
        filter.addAction(BroadcastAction.MESSAGE_SEND_START);
        filter.addAction(BroadcastAction.MESSAGE_SEND_SUCCESS);
        filter.addAction(BroadcastAction.MESSAGE_SEND_FAILED);
        this.registerReceiver(messageReceiver, filter);
    }

    private void initView() {
        ChatUtil.showInputWindow(ChatActivity.this, edtContent);

        initRecyclerView(recycleView, swipeRefreshLayout);

        adapter = new ChatAdapter(this);
        adapter.setCurrentTeacher(teacher);
        recycleView.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (15 >= localDataCount) {
                    swipeRefreshLayout.setRefreshing(false);
                    return;
                }
                getChatList(false);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        localDataCount = GreenDaoHelper.getInstance().getChatsByTeacherId(teacher.getU_id()).size();
        Log.i(TAG, "initView: localDataCount " + localDataCount);
        getChatList(true);

        mAudioRecorderButton.setFinishRecorderCallBack(new AudioRecorderButton.AudioFinishRecorderCallBack() {

            @Override
            public void onPermissionAsk() {
                Log.i(TAG, "onPermissionAsk: ");
                final int version = Build.VERSION.SDK_INT;
                if (version > 19) {
                    ChatActivityPermissionsDispatcher.onStartRecordingWithCheck(ChatActivity.this);
                } else {
                    ToastUtils.showToast(ChatActivity.this, R.string.permission_voice_rationale);
                    CommonUtil.goAppDetailSettingIntent(ChatActivity.this);
                }
            }

            @Override
            public void onPermissionDenied() {
                Log.i(TAG, "onPermissionDenied: ");
                final int version = Build.VERSION.SDK_INT;
                if (version > 19) {
                    ChatActivityPermissionsDispatcher.onStartRecordingWithCheck(ChatActivity.this);
                } else {
                    ToastUtils.showToast(ChatActivity.this, R.string.permission_voice_never_askagain);
                    CommonUtil.goAppDetailSettingIntent(ChatActivity.this);
                }
            }

            public void onFinish(float seconds, String filePath) {
                Recorder recorder = new Recorder(seconds, filePath);
                File file = new File(recorder.getFilePath());
                Log.i(TAG, "onFinish: " + recorder.getFilePath());
                if (file.length() == 0) {
                    return;
                }
                try {
//                    File file = new File("/storage/emulated/0/netease/cloudmusic/Music/andthewinne.mp3");
                    BaseMessage message = new BaseMessage();
                    message.setType(ChatUtil.TYPE_AMR);
                    message.setFilename(file.getName());
                    message.setSecond(Math.round(seconds));
                    message.setSize((int) file.length());
                    message.setParentId(currentParent.getU_id());
                    message.setTeacherId(teacher.getU_id());
                    message.setTime(CommonUtil.getCurrentDateHms());
                    FileInputStream inputStream = new FileInputStream(file);
                    final byte[] allByte = message.packData(inputStream);
                    inputStream.close();
                    if (allByte != null) {
                        message.setAllData(allByte);
                        addSendingMsg(message);
                        SocketManager.getInstance().sendMessage(message);
                    }
                } catch (Exception ex) {
                    Log.i(TAG, "viewClick: " + ex.getMessage());
                }

            }
        });

        RlParent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = RlParent.getRootView().getHeight();
                int height = RlParent.getHeight();
                int diff = heightDiff - height;
                if (diff > 400) {
                    //键盘弹起
                    isInputWindowShow = true;
                } else {
                    isInputWindowShow = false;
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (permissions != null && permissions.length > 0) {
            Log.i(TAG, "onRequestPermissionsResult: " + permissions[0]);
        }
        ChatActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @NeedsPermission({Manifest.permission.RECORD_AUDIO})
    void onStartRecording() {

    }

    @OnPermissionDenied({Manifest.permission.RECORD_AUDIO})
    void onStartRecordingDenied() {
        Log.i(TAG, "onStartRecordingDenied: ");
        Toast.makeText(this, R.string.permission_voice_denied, Toast.LENGTH_SHORT).show();
    }

    @OnShowRationale({Manifest.permission.RECORD_AUDIO})
    void showRationaleForStartRecording(PermissionRequest request) {
        Log.i(TAG, "showRationaleForStartRecording: ");
        request.proceed();
    }

    @OnNeverAskAgain({Manifest.permission.RECORD_AUDIO})
    void onStartRecordingNeverAskAgain() {
        Toast.makeText(this, R.string.permission_voice_never_askagain, Toast.LENGTH_SHORT).show();
        CommonUtil.goAppDetailSettingIntent(this);
    }

    @OnClick({R.id.id_recorder_button, R.id.imgVoiceOrText, R.id.btnSend, R.id.edtContent})
    void viewClick(View view) {
        switch (view.getId()) {
            case R.id.imgVoiceOrText:
                if (edtContent.getVisibility() == View.GONE) {
                    edtContent.setVisibility(View.VISIBLE);
                    btnSend.setVisibility(View.VISIBLE);
                    edtContent.requestFocus();
                    imgVoiceOrText.setBackgroundResource(R.drawable.icon_msg_input);
                    getLayoutManager().setStackFromEnd(true);
                    ChatUtil.showInputWindow(ChatActivity.this, edtContent);

                    mAudioRecorderButton.setVisibility(View.GONE);
                } else {
                    edtContent.setVisibility(View.GONE);
                    btnSend.setVisibility(View.GONE);
                    mAudioRecorderButton.setVisibility(View.VISIBLE);
                    imgVoiceOrText.setBackgroundResource(R.drawable.icon_msg_voice);
                    ChatUtil.hideInputWindow(ChatActivity.this, edtContent);
                    smoothBottom();
                }
                break;
            case R.id.edtContent:
                if (!isInputWindowShow) {
                    smoothBottom();
                }
                break;
            case R.id.btnSend:
                String msg = edtContent.getText().toString();
                if (msg.isEmpty()) {
                    return;
                }
                BaseMessage message = new BaseMessage();
                message.setType(ChatUtil.TYPE_TEXT);
                message.setFilename(ChatUtil.getCurrentDateHms());
                message.setSize(msg.length());
                message.setParentId(currentParent.getU_id());
                message.setTeacherId(teacher.getU_id());
                message.setTime(CommonUtil.getCurrentDateHms());
                final byte[] allByte = message.packData(msg);
                if (allByte != null) {
                    message.setAllData(allByte);
                    edtContent.setText("");
                    addSendingMsg(message);
                    SocketManager.getInstance().sendMessage(message);
                }
                break;
        }
    }

    private void getChatList(boolean toLast) {
        pageChatList = GreenDaoHelper.getInstance().getPageChatsByTeacherId(teacher.getU_id(), currentOffset);
        if (pageChatList.size() == 0) {
            return;
        }
        List<BeanChat> chats = new ArrayList<>();
        for (int i = pageChatList.size() - 1; i > -1; i--) {
            chats.add(pageChatList.get(i));
        }
        adapter.appendData(chats);
        currentOffset = adapter.getItemCount();
        if (toLast) {
            smoothBottom();
        } else {
            int position = pageChatList.size() - 1;
            View topView = getLayoutManager().getChildAt(position);
            int topY = topView.getTop();

            recycleView.smoothScrollBy(0, topY);
        }
    }

    private void addSendingMsg(BaseMessage message) {
        BeanChat chat = new BeanChat();
        chat.parseMessageToChat(message);
        chat.setHasRead(true);
        chat.setSendStatus(ChatUtil.STATUS_SENDING);
        adapter.addData(chat);
        smoothBottom();
        GreenDaoHelper.getInstance().insertChat(chat);
    }

    private void smoothBottom() {
        recycleView.smoothScrollToPosition(adapter.getItemCount());
    }

    @Override
    protected void onPause() {
        super.onPause();
        SoundPlayHelper.getInstance().stopPlay();
        ChatUtil.hideInputWindow(ChatActivity.this, edtContent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ChatUtil.currentChatTeacher = null;
        try {
            unregisterReceiver(messageReceiver);
        } catch (Exception ex) {

        }
    }

    @Override
    public void showMessageNotify(boolean show, BeanChat chat) {
        //判断是否为当前正在聊天老师发来的信息
        if (chat.getTeacherId().equals(teacher.getU_id())) {
            adapter.addData(chat);
            recycleView.smoothScrollToPosition(adapter.getItemCount());
        } else {
            super.showMessageNotify(show, chat);
        }
    }

    public BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i(TAG, "onReceive: " + action);
            Bundle bundle = intent.getExtras();
            if (bundle == null) {
                return;
            }

            BaseMessage sendMsg = (BaseMessage) bundle.get("message");
            BeanChat chat = new BeanChat();
            chat.parseMessageToChat(sendMsg);
            chat.setHasRead(true);

//            if (action.equals(BroadcastAction.MESSAGE_SEND_START)) {
//                chat.setSendStatus(ChatUtil.STATUS_SENDING);
//                adapter.addData(chat);
//                smoothBottom();
//                GreenDaoHelper.getInstance().insertChat(chat);
//            } else
            if (action.equals(BroadcastAction.MESSAGE_SEND_SUCCESS)) {
                chat.setSendStatus(ChatUtil.STATUS_SUCCESS);
                adapter.updateData(chat);
                GreenDaoHelper.getInstance().updateChat(chat);
            } else if (action.equals(BroadcastAction.MESSAGE_SEND_FAILED)) {
                chat.setSendStatus(ChatUtil.STATUS_FAILED);
                adapter.updateData(chat);
                GreenDaoHelper.getInstance().updateChat(chat);
            }
        }
    };

}
