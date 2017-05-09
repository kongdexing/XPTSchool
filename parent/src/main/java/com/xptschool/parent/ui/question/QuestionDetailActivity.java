package com.xptschool.parent.ui.question;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.common.VolleyHttpParamsEntity;
import com.android.volley.common.VolleyHttpResult;
import com.android.volley.common.VolleyHttpService;
import com.android.widget.audiorecorder.AudioRecorderButton;
import com.android.widget.audiorecorder.Recorder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xptschool.parent.R;
import com.xptschool.parent.bean.BeanQuestion;
import com.xptschool.parent.bean.BeanQuestionTalk;
import com.xptschool.parent.bean.MessageSendStatus;
import com.xptschool.parent.common.BroadcastAction;
import com.xptschool.parent.common.CommonUtil;
import com.xptschool.parent.common.ExtraKey;
import com.xptschool.parent.http.HttpAction;
import com.xptschool.parent.http.HttpErrorMsg;
import com.xptschool.parent.http.MyVolleyRequestListener;
import com.xptschool.parent.model.BeanParent;
import com.xptschool.parent.model.BeanTeacher;
import com.xptschool.parent.model.GreenDaoHelper;
import com.xptschool.parent.server.SocketManager;
import com.xptschool.parent.ui.main.BaseActivity;
import com.xptschool.parent.util.ChatUtil;

import java.io.File;
import java.io.FileInputStream;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class QuestionDetailActivity extends BaseActivity {

    @BindView(R.id.txtQstTitle)
    TextView txtQstTitle;

    @BindView(R.id.listview)
    ListView listview;

    @BindView(R.id.imgVoiceOrText)
    ImageView imgVoiceOrText;

    @BindView(R.id.id_recorder_button)
    AudioRecorderButton mAudioRecorderButton;

    @BindView(R.id.edtContent)
    EditText edtContent;

    @BindView(R.id.btnSend)
    Button btnSend;

    private QuestionDetailAdapter adapter = null;
    private BeanQuestion mQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_detail);

        initView();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mQuestion = bundle.getParcelable(ExtraKey.QUESTION);
            if (mQuestion != null) {
                setTitle(mQuestion.getReceiver_name());
                txtQstTitle.setText("标题：" + mQuestion.getTitle());
                getQuestionTalk();
            }
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(BroadcastAction.MESSAGE_SEND_START);
        filter.addAction(BroadcastAction.MESSAGE_SEND_SUCCESS);
        filter.addAction(BroadcastAction.MESSAGE_SEND_FAILED);
        this.registerReceiver(messageReceiver, filter);
    }

    private void initView() {
        ChatUtil.showInputWindow(QuestionDetailActivity.this, edtContent);

        adapter = new QuestionDetailAdapter(this);
        listview.setAdapter(adapter);
        mAudioRecorderButton.setFinishRecorderCallBack(new AudioRecorderButton.AudioFinishRecorderCallBack() {

            public void onFinish(float seconds, String filePath) {
                Recorder recorder = new Recorder(seconds, filePath);
                File file = new File(recorder.getFilePath());
                try {
//                    File file = new File("/storage/emulated/0/netease/cloudmusic/Music/andthewinne.mp3");
                    BaseMessage message = new BaseMessage();
                    message.setType('2');
                    message.setFilename(ChatUtil.getFileName(mQuestion.getSender_id()));
                    message.setSize((int) file.length());
                    message.setParentId(mQuestion.getSender_id());
                    message.setTeacherId(mQuestion.getReceiver_id());
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

//                try {
//                    AssetManager assetManager = QuestionDetailActivity.this.getAssets();
//                    AssetFileDescriptor afd = assetManager.openFd("sent_message.mp3");
//                    MediaPlayer player = new MediaPlayer();
//                    player.setDataSource(afd.getFileDescriptor(),
//                            afd.getStartOffset(), afd.getLength());
//                    player.setLooping(false);//循环播放
//                    player.prepare();
//                    player.start();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
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
                    ChatUtil.showInputWindow(QuestionDetailActivity.this, edtContent);
                    mAudioRecorderButton.setVisibility(View.GONE);
                } else {
                    edtContent.setVisibility(View.GONE);
                    btnSend.setVisibility(View.GONE);
                    mAudioRecorderButton.setVisibility(View.VISIBLE);
                    ChatUtil.hideInputWindow(QuestionDetailActivity.this, edtContent);
                }
                break;
            case R.id.btnSend:
                String msg = edtContent.getText().toString();
                if (msg.isEmpty()) {
                    return;
                }
                BaseMessage message = new BaseMessage();
                message.setType('0');
                message.setFilename(ChatUtil.getCurrentDateHms());
                message.setSize(msg.length());
                message.setParentId(mQuestion.getSender_id());
                message.setTeacherId(mQuestion.getReceiver_id());
                final byte[] allByte = message.packData(msg);
                if (allByte != null) {
                    message.setAllData(allByte);
                    SocketManager.getInstance().sendMessage(message);
                }

//                adapter.insertChat(msg);
//                edtContent.setText("");
//                BeanQuestionTalk answer = new BeanQuestionTalk();
//                answer.setSendStatus(MessageSendStatus.SENDING);
//                BeanParent parent = GreenDaoHelper.getInstance().getCurrentParent();
//                if (parent != null) {
//                    answer.setSender_id(parent.getU_id());
//                    answer.setSender_sex(parent.getSex());
//                }
//                answer.setCreate_time((new Date()) + "");
//                answer.setContent(msg);
//                adapter.insertChat(answer);
//                sendAnswer(answer);
                break;
        }
    }

    private void getQuestionTalk() {
        VolleyHttpService.getInstance().sendPostRequest(HttpAction.QUESTION_VIEW,
                new VolleyHttpParamsEntity()
                        .addParam("id", mQuestion.getId())
                        .addParam("token", CommonUtil.encryptToken(HttpAction.QUESTION_VIEW)),
                new MyVolleyRequestListener() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onResponse(VolleyHttpResult httpResult) {
                        super.onResponse(httpResult);
                        switch (httpResult.getStatus()) {
                            case HttpAction.SUCCESS:
                                try {
                                    Gson gson = new Gson();
                                    List<BeanQuestionTalk> questions = gson.fromJson(httpResult.getData().toString(), new TypeToken<List<BeanQuestionTalk>>() {
                                    }.getType());
                                    for (int i = 0; i < questions.size(); i++) {
                                        questions.get(i).setReceiver_sex(mQuestion.getReceiver_sex());
                                    }
                                    adapter.refreshData(questions);
                                    listview.setSelection(adapter.getCount() - 1);
                                } catch (Exception ex) {
                                    Toast.makeText(QuestionDetailActivity.this, HttpErrorMsg.ERROR_JSON, Toast.LENGTH_SHORT).show();
                                }
                                break;
                            default:
                                Toast.makeText(QuestionDetailActivity.this, httpResult.getInfo(), Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        super.onErrorResponse(error);
                    }
                });
    }

    public void sendAnswer(final BeanQuestionTalk answer) {

        VolleyHttpService.getInstance().sendPostRequest(HttpAction.QUESTION_ADD,
                new VolleyHttpParamsEntity()
                        .addParam("id", mQuestion.getId())
                        .addParam("content", answer.getContent())
                        .addParam("token", CommonUtil.encryptToken(HttpAction.QUESTION_ADD)),
                new MyVolleyRequestListener() {
                    @Override
                    public void onStart() {
                        answer.setSendStatus(MessageSendStatus.SENDING);
                        adapter.updateChat(answer);
                    }

                    @Override
                    public void onResponse(VolleyHttpResult httpResult) {
                        super.onResponse(httpResult);
                        switch (httpResult.getStatus()) {
                            case HttpAction.SUCCESS:
                                answer.setSendStatus(MessageSendStatus.SUCCESS);
                                adapter.updateChat(answer);
                                break;
                            default:
                                answer.setSendStatus(MessageSendStatus.FAILED);
                                adapter.updateChat(answer);
                                break;
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        answer.setSendStatus(MessageSendStatus.FAILED);
                        adapter.updateChat(answer);
                    }
                });
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
            String action = intent.getAction();
            if (action.equals(BroadcastAction.MESSAGE_SEND_START)) {

            } else if (action.equals(BroadcastAction.MESSAGE_SEND_SUCCESS)) {

            } else if (action.equals(BroadcastAction.MESSAGE_SEND_FAILED)) {

            }

        }
    };

}
