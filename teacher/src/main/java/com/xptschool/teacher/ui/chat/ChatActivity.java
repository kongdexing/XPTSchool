package com.xptschool.teacher.ui.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.widget.audiorecorder.AudioRecorderButton;
import com.android.widget.audiorecorder.Recorder;
import com.xptschool.teacher.R;
import com.xptschool.teacher.common.BroadcastAction;
import com.xptschool.teacher.common.CommonUtil;
import com.xptschool.teacher.common.ExtraKey;
import com.xptschool.teacher.model.BeanChat;
import com.xptschool.teacher.model.BeanTeacher;
import com.xptschool.teacher.model.ContactParent;
import com.xptschool.teacher.model.GreenDaoHelper;
import com.xptschool.teacher.server.SocketManager;
import com.xptschool.teacher.ui.main.BaseActivity;
import com.xptschool.teacher.util.ChatUtil;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class ChatActivity extends BaseActivity {

    @BindView(R.id.recycleView)
    RecyclerView recycleView;

    @BindView(R.id.imgVoiceOrText)
    ImageView imgVoiceOrText;

    @BindView(R.id.id_recorder_button)
    AudioRecorderButton mAudioRecorderButton;

    @BindView(R.id.edtContent)
    EditText edtContent;

    @BindView(R.id.btnSend)
    Button btnSend;

    private ChatAdapter adapter = null;
    private ContactParent parent;
    private BeanTeacher currentTeacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            parent = (ContactParent) bundle.get(ExtraKey.CHAT_PARENT);
            if (parent != null) {
                setTitle(parent.getName());
            }
        }
        currentTeacher = GreenDaoHelper.getInstance().getCurrentTeacher();
        if (currentTeacher == null || parent == null) {
            return;
        }
        initView();

        IntentFilter filter = new IntentFilter();
        filter.addAction(BroadcastAction.MESSAGE_SEND_START);
        filter.addAction(BroadcastAction.MESSAGE_SEND_SUCCESS);
        filter.addAction(BroadcastAction.MESSAGE_SEND_FAILED);
        this.registerReceiver(messageReceiver, filter);
    }

    private void initView() {
        ChatUtil.showInputWindow(ChatActivity.this, edtContent);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recycleView.setLayoutManager(linearLayoutManager);
        adapter = new ChatAdapter(this);
        recycleView.setAdapter(adapter);

        List<BeanChat> chats = GreenDaoHelper.getInstance().getChatsByParentId(parent.getUser_id());
        adapter.loadData(chats, parent);

        recycleView.setItemAnimator(new DefaultItemAnimator());
        recycleView.smoothScrollToPosition(chats.size());

        mAudioRecorderButton.setFinishRecorderCallBack(new AudioRecorderButton.AudioFinishRecorderCallBack() {

            public void onFinish(float seconds, String filePath) {
                Recorder recorder = new Recorder(seconds, filePath);
                File file = new File(recorder.getFilePath());
                try {
//                    File file = new File("/storage/emulated/0/netease/cloudmusic/Music/andthewinne.mp3");
                    BaseMessage message = new BaseMessage();
                    message.setType(ChatUtil.TYPE_AMR);
                    message.setFilename(file.getName());
                    message.setSecond(Math.round(seconds));
                    message.setSize((int) file.length());
                    message.setParentId(parent.getUser_id());
                    message.setTeacherId(currentTeacher.getU_id());
                    FileInputStream inputStream = new FileInputStream(file);
                    final byte[] allByte = message.packData(inputStream);
                    inputStream.close();
                    if (allByte != null) {
                        message.setAllData(allByte);
                        SocketManager.getInstance().sendMessage(message);
                    }
                } catch (Exception ex) {
                    Log.i(TAG, "viewClick: " + ex.getMessage());
                }

            }
        });

    }

    @OnClick({R.id.id_recorder_button, R.id.imgVoiceOrText, R.id.btnSend})
    void viewClick(View view) {
        switch (view.getId()) {
            case R.id.imgVoiceOrText:
                if (edtContent.getVisibility() == View.GONE) {
                    edtContent.setVisibility(View.VISIBLE);
                    btnSend.setVisibility(View.VISIBLE);
                    edtContent.requestFocus();
                    ChatUtil.showInputWindow(ChatActivity.this, edtContent);
                    mAudioRecorderButton.setVisibility(View.GONE);
                } else {
                    edtContent.setVisibility(View.GONE);
                    btnSend.setVisibility(View.GONE);
                    mAudioRecorderButton.setVisibility(View.VISIBLE);
                    ChatUtil.hideInputWindow(ChatActivity.this, edtContent);
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
                message.setParentId(parent.getUser_id());
                message.setTeacherId(currentTeacher.getU_id());
                message.setContent(msg);
                final byte[] allByte = message.packData(msg);
                if (allByte != null) {
                    message.setAllData(allByte);
                    edtContent.setText("");
                    SocketManager.getInstance().sendMessage(message);
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(messageReceiver);
        } catch (Exception ex) {

        }
    }

    public BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle == null) {
                return;
            }
            String action = intent.getAction();
            Log.i(TAG, "onReceive: " + action);

            if (action.equals(BroadcastAction.MESSAGE_RECEIVED)) {
                BeanChat chat = (BeanChat) bundle.getSerializable("chat");
                Log.i(TAG, "onReceive: " + chat.getParentId() + " " + parent.getUser_id() + "  " + chat.getContent());
                //判断是否为当前正在聊天家长发来的信息
                if (chat.getParentId().equals(parent.getUser_id())) {
                    adapter.addData(chat);
                    recycleView.smoothScrollToPosition(adapter.getItemCount());
                }
            } else {
                BaseMessage sendMsg = (BaseMessage) bundle.get("message");

                BeanChat chat = new BeanChat();
                chat.parseMessageToChat(sendMsg);
                chat.setHasRead(true);
                chat.setTime(CommonUtil.getCurrentDateHms());

                if (action.equals(BroadcastAction.MESSAGE_SEND_START)) {
                    chat.setSendStatus(ChatUtil.STATUS_SENDING);
                    adapter.addData(chat);
                    recycleView.smoothScrollToPosition(adapter.getItemCount());
                    GreenDaoHelper.getInstance().insertChat(chat);
                } else if (action.equals(BroadcastAction.MESSAGE_SEND_SUCCESS)) {
                    chat.setSendStatus(ChatUtil.STATUS_SUCCESS);
                    adapter.updateData(chat);
                    GreenDaoHelper.getInstance().updateChat(chat);

                } else if (action.equals(BroadcastAction.MESSAGE_SEND_FAILED)) {
                    chat.setSendStatus(ChatUtil.STATUS_FAILED);
                    adapter.updateData(chat);
                    GreenDaoHelper.getInstance().updateChat(chat);

                }
            }
        }
    };

}
