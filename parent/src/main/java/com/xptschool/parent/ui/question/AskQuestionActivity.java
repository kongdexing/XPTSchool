package com.xptschool.parent.ui.question;

import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.common.VolleyHttpParamsEntity;
import com.android.volley.common.VolleyHttpResult;
import com.android.volley.common.VolleyHttpService;
import com.android.widget.audiorecorder.AudioRecorderButton;
import com.android.widget.audiorecorder.MediaPlayerManager;
import com.android.widget.audiorecorder.Recorder;
import com.android.widget.spinner.MaterialSpinner;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xptschool.parent.R;
import com.xptschool.parent.common.CommonUtil;
import com.xptschool.parent.http.HttpAction;
import com.xptschool.parent.http.MyVolleyRequestListener;
import com.xptschool.parent.model.BeanStudent;
import com.xptschool.parent.model.BeanTeacher;
import com.xptschool.parent.model.GreenDaoHelper;
import com.xptschool.parent.server.SocketManager;
import com.xptschool.parent.ui.chat.BaseMessage;
import com.xptschool.parent.ui.main.BaseActivity;
import com.xptschool.parent.util.ChatUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class AskQuestionActivity extends BaseActivity {

    @BindView(R.id.scrollView)
    ScrollView scrollView;

    @BindView(R.id.spnTeacher)
    MaterialSpinner spnTeacher;

    @BindView(R.id.spnStudents)
    MaterialSpinner spnStudents;

    @BindView(R.id.edtContent)
    EditText edtContent;
    @BindView(R.id.edtQTitle)
    EditText edtQTitle;

    @BindView(R.id.id_recorder_button)
    AudioRecorderButton mAudioRecorderButton;

    @BindView(R.id.rl_recorder_length)
    RelativeLayout rl_recorder_length;
    @BindView(R.id.txt_recorder_time)
    TextView txt_recorder_time;
    @BindView(R.id.img_recorder_anim)
    ImageView img_recorder_anim;
    private Recorder recorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_question);

        setTitle(R.string.label_ask_question);
        initData();
    }

    private void initData() {
        spnStudents.setItems(GreenDaoHelper.getInstance().getStudents());
        spnStudents.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner materialSpinner, int i, long l, Object o) {
                getTeacherByStudent();
            }
        });

        spnTeacher.setItems("正在获取老师信息");

        mAudioRecorderButton.setFinishRecorderCallBack(new AudioRecorderButton.AudioFinishRecorderCallBack() {

            public void onFinish(float seconds, String filePath) {
                recorder = new Recorder(seconds, filePath);

                File file = new File(filePath);
                String str = Math.round(recorder.getTime()) + "\"" + filePath + " size:" + file.length();
                rl_recorder_length.setVisibility(View.VISIBLE);
                txt_recorder_time.setText(str);
            }
        });

        getTeacherByStudent();
    }

    private void getTeacherByStudent() {
        BeanStudent student = (BeanStudent) spnStudents.getSelectedItem();

        VolleyHttpService.getInstance().sendPostRequest(HttpAction.GET_TEACHER_BYCID,
                new VolleyHttpParamsEntity()
                        .addParam("c_id", student.getC_id())
                        .addParam("g_id", student.getG_id()),
                new MyVolleyRequestListener() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onResponse(VolleyHttpResult volleyHttpResult) {
                        super.onResponse(volleyHttpResult);
                        switch (volleyHttpResult.getStatus()) {
                            case HttpAction.SUCCESS:
                                try {
                                    Gson gson = new Gson();
                                    List<BeanTeacher> teachers = gson.fromJson(volleyHttpResult.getData().toString(), new TypeToken<List<BeanTeacher>>() {
                                    }.getType());
                                    if (teachers.size() == 0) {
                                        spnTeacher.setItems("");
                                        Toast.makeText(AskQuestionActivity.this, "该学生所在班级无执教老师", Toast.LENGTH_SHORT).show();
                                    } else {
                                        spnTeacher.setItems(teachers);
                                    }
                                } catch (Exception ex) {
                                    Log.i(TAG, "onResponse: " + ex.getMessage());
                                }
                                break;
                            default:
                                spnTeacher.setItems("获取失败");
                                break;
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        spnTeacher.setItems("获取失败");
                    }
                });
    }

    @OnClick({R.id.rl_recorder_length, R.id.id_recorder_button, R.id.btnSubmit, R.id.btnUpload})
    void viewClick(View view) {
        switch (view.getId()) {
            case R.id.rl_recorder_length:
                if (recorder == null) {
                    return;
                }
                // 声音播放动画
                if (img_recorder_anim != null) {
                    img_recorder_anim.setBackgroundResource(R.drawable.adj);
                }
                img_recorder_anim.setBackgroundResource(R.drawable.play_anim);
                AnimationDrawable animation = (AnimationDrawable) img_recorder_anim.getBackground();
                animation.start();
                // 播放录音
                MediaPlayerManager.playSound(recorder.getFilePath(), new MediaPlayer.OnCompletionListener() {

                    public void onCompletion(MediaPlayer mp) {
                        //播放完成后修改图片
                        img_recorder_anim.setBackgroundResource(R.drawable.adj);
                    }
                });
                break;
            case R.id.id_recorder_button:

                break;
            case R.id.btnSubmit:
                if (edtQTitle.getText().toString().trim().isEmpty()) {
                    Toast.makeText(this, R.string.toast_question_title_empty, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (edtContent.getText().toString().trim().isEmpty()) {
                    Toast.makeText(this, R.string.toast_question_content_empty, Toast.LENGTH_SHORT).show();
                    return;
                }
                createQuestion();
                break;
            case R.id.btnUpload:
                try {
                    BeanTeacher teacher = (BeanTeacher) spnTeacher.getSelectedItem();

//                    File file = new File(recorder.getFilePath());
                    File file = new File("/storage/emulated/0/tencent/qqfile_recv/abc123.doc");
                    BaseMessage message = new BaseMessage();
                    message.setType(ChatUtil.TYPE_AMR);
                    message.setFilename(file.getName());
                    message.setSize((int) file.length());
                    message.setParentId(GreenDaoHelper.getInstance().getCurrentParent().getU_id());
                    message.setTeacherId(teacher.getU_id());
                    FileInputStream inputStream = new FileInputStream(file);
                    final byte[] allByte = message.packData(inputStream);
                    inputStream.close();

                    if (allByte != null) {
//                        analyseData(allByte);
                        message.setAllData(allByte);
                        SocketManager.getInstance().sendMessage(message);
//                        new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                connectServerWithTCPSocket(allByte);
//                            }
//                        }).start();
                    }
                } catch (Exception ex) {
                    Log.i(TAG, "viewClick: " + ex.getMessage());
                }
                break;
        }
    }

    private void analyseData(byte[] allByte) {
        try {
            Log.i(TAG, "analyseData length : " + allByte.length);
            byte[] b_type = subBytes(allByte, 0, 1);
            Log.i(TAG, "analyseData: b_type:" + (new String(b_type)));

            byte[] b_osType = subBytes(allByte, 1, 1);
            Log.i(TAG, "analyseData: b_osType:" + new String(b_osType));

            byte[] b_size = subBytes(allByte, 2, 4);
            Log.i(TAG, "analyseData: b_size:" + ChatUtil.byteArrayToInt(b_size));

            byte[] b_pId = subBytes(allByte, 6, 4);
            Log.i(TAG, "analyseData: b_pId:" + (ChatUtil.byteArrayToInt(b_pId)));

            byte[] b_tId = subBytes(allByte, 10, 4);
            Log.i(TAG, "analyseData: b_tId:" + (ChatUtil.byteArrayToInt(b_tId)));

            byte[] b_filename = subBytes(allByte, 14, 15);
            Log.i(TAG, "analyseData: b_filename:" + (new String(b_filename)));

            byte[] b_zero = subBytes(allByte, 29, 3);
            Log.i(TAG, "analyseData: b_zero:" + (new String(b_zero)));
        } catch (Exception ex) {
            Log.i(TAG, "analyseData error: " + ex.getMessage());
        }
    }

    protected void connectServerWithTCPSocket(byte[] allByte) {
        Log.i(TAG, "connectServerWithTCPSocket: " + allByte.length);
        Socket socket = null;
        try {
            // 创建一个Socket对象，并指定服务端的IP及端口号
//            socket = new Socket("192.168.1.195", 5020);
            socket = new Socket("59.110.42.149", 50300);
            if (!socket.isConnected()) {
                Log.i(TAG, "connectServerWithTCPSocket unconnected");
                return;
            }
            if (recorder == null) {
                Log.i(TAG, "connectServerWithTCPSocket: recorder is null");
                return;
            }
//             获取Socket的OutputStream对象用于发送数据。
            OutputStream outputStream = socket.getOutputStream();
//             创建一个byte类型的buffer字节数组，用于存放读取的本地文件
//            byte buffer[] = new byte[10 * 1024];
            outputStream.write(allByte);
            // 发送读取的数据到服务端
            outputStream.flush();
            socket.close();
            Log.i(TAG, "connectServerWithTCPSocket: socket close");
//            /** 或创建一个报文，使用BufferedWriter写入,看你的需求 **/
//            String socketData = "[2143213;21343fjks;213]";
//            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
//                    socket.getOutputStream()));
//            writer.write(socketData.replace("\n", " ") + "\n");
//            writer.flush();
            /************************************************/
        } catch (Exception ex) {
            Log.i(TAG, "Exception: " + ex.getMessage());
        } finally {

        }
    }

    private void createQuestion() {
        BeanTeacher teacher = (BeanTeacher) spnTeacher.getSelectedItem();
        BeanStudent student = (BeanStudent) spnStudents.getSelectedItem();

        VolleyHttpService.getInstance().sendPostRequest(HttpAction.QUESTION_NEWADD,
                new VolleyHttpParamsEntity()
                        .addParam("token", CommonUtil.encryptToken(HttpAction.QUESTION_NEWADD))
                        .addParam("title", edtQTitle.getText().toString().trim())
                        .addParam("content", edtContent.getText().toString().trim())
                        .addParam("form_user", teacher.getU_id())
                        .addParam("a_id", student.getA_id())
                        .addParam("s_id", student.getS_id())
                        .addParam("c_id", student.getC_id())
                        .addParam("g_id", student.getG_id()),
                new MyVolleyRequestListener() {
                    @Override
                    public void onStart() {
                        showProgress(R.string.progress_add_question);
                    }

                    @Override
                    public void onResponse(VolleyHttpResult volleyHttpResult) {
                        super.onResponse(volleyHttpResult);
                        hideProgress();
                        Toast.makeText(AskQuestionActivity.this, volleyHttpResult.getInfo(), Toast.LENGTH_SHORT).show();
                        if (volleyHttpResult.getStatus() == 1) {
                            finish();
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        super.onErrorResponse(volleyError);
                        hideProgress();
                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
        MediaPlayerManager.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MediaPlayerManager.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MediaPlayerManager.release();
    }

    public static byte[] subBytes(byte[] src, int begin, int count) {
        byte[] bs = new byte[count];
        System.arraycopy(src, begin, bs, 0, count);
        return bs;
    }

}
