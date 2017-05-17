package com.xptschool.parent.ui.chat;

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
import com.xptschool.parent.util.ChatUtil;

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
    private ContactTeacher teacher;
    private BeanParent currentParent;

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

        setRightImage(R.drawable.user_defaulthead);
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

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recycleView.setLayoutManager(linearLayoutManager);
        adapter = new ChatAdapter(this);
        recycleView.setAdapter(adapter);

        List<BeanChat> chats = GreenDaoHelper.getInstance().getChatsByTeacherId(teacher.getU_id());
        adapter.loadData(chats, teacher);

        recycleView.setItemAnimator(new DefaultItemAnimator());
        recycleView.smoothScrollToPosition(chats.size());

        mAudioRecorderButton.setFinishRecorderCallBack(new AudioRecorderButton.AudioFinishRecorderCallBack() {

            public void onFinish(float seconds, String filePath) {
                Recorder recorder = new Recorder(seconds, filePath);
                File file = new File(recorder.getFilePath());
                Log.i(TAG, "onFinish: " + recorder.getFilePath());
                try {
//                    File file = new File("/storage/emulated/0/netease/cloudmusic/Music/andthewinne.mp3");
                    BaseMessage message = new BaseMessage();
                    message.setType(ChatUtil.TYPE_AMR);
                    message.setFilename(file.getName());
                    message.setSecond(Math.round(seconds));
                    message.setSize((int) file.length());
                    message.setParentId(currentParent.getU_id());
                    message.setTeacherId(teacher.getU_id());
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

    }

    @OnClick({R.id.id_recorder_button, R.id.imgVoiceOrText, R.id.btnSend})
    void viewClick(View view) {
        switch (view.getId()) {
            case R.id.imgVoiceOrText:
                if (edtContent.getVisibility() == View.GONE) {
                    edtContent.setVisibility(View.VISIBLE);
                    btnSend.setVisibility(View.VISIBLE);
                    edtContent.requestFocus();
                    imgVoiceOrText.setBackgroundResource(R.drawable.icon_msg_input);
                    ChatUtil.showInputWindow(ChatActivity.this, edtContent);
                    smoothBottom();
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

    private void addSendingMsg(BaseMessage message) {
        BeanChat chat = new BeanChat();
        chat.parseMessageToChat(message);
        chat.setHasRead(true);
        chat.setTime(CommonUtil.getCurrentDateHms());
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
            chat.setTime(CommonUtil.getCurrentDateHms());

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
