package com.android.widget.audiorecorder;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.android.widget.MyPermissionUtil;
import com.android.widget.R;

/**
 * @param
 * @author ldm
 * @description 自定义Button
 * @time 2016/6/25 9:26
 */
public class AudioRecorderButton extends Button {
    // 按钮正常状态（默认状态）
    private static final int STATE_NORMAL = 1;
    //正在录音状态
    private static final int STATE_RECORDING = 2;
    //录音取消状态
    private static final int STATE_CANCEL = 3;
    //记录当前状态
    private int mCurrentState = STATE_NORMAL;
    //是否开始录音标志
    private boolean isRecording = false;
    //判断在Button上滑动距离，以判断 是否取消
    private static final int DISTANCE_Y_CANCEL = 50;
    //对话框管理工具类
    private DialogManager mDialogManager;
    //录音管理工具类
    private AudioManager mAudioManager;
    //记录录音时间
    private float mTime;
    // 是否触发longClick
    private boolean mReady;
    //录音准备
    private static final int MSG_AUDIO_PREPARED = 0x110;
    //音量发生改变
    private static final int MSG_VOICE_CHANGED = 0x111;
    //取消提示对话框
    private static final int MSG_DIALOG_DIMISS = 0x112;

    /**
     * @description 获取音量大小的线程
     * @author ldm
     * @time 2016/6/25 9:30
     * @param
     */
    private Runnable mGetVoiceLevelRunnable = new Runnable() {

        public void run() {
            while (isRecording) {//判断正在录音
                try {
                    Thread.sleep(100);
                    mTime += 0.1f;//录音时间计算
                    mHandler.sendEmptyMessage(MSG_VOICE_CHANGED);//每0.1秒发送消息
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_AUDIO_PREPARED:
                    //显示对话框
                    mDialogManager.showRecordingDialog();
                    isRecording = true;
                    // 开启一个线程计算录音时间
                    new Thread(mGetVoiceLevelRunnable).start();
                    break;
                case MSG_VOICE_CHANGED:
                    //更新声音
                    mDialogManager.updateVoiceLevel(mAudioManager.getVoiceLevel(DialogManager.maxLevel));
                    break;
                case MSG_DIALOG_DIMISS:
                    //取消对话框
                    mDialogManager.dimissDialog();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    public AudioRecorderButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mDialogManager = new DialogManager(context);
        //录音文件存放地址
        String dir = Environment.getExternalStorageDirectory() + "/ldm_voice";
        mAudioManager = AudioManager.getInstance(dir);
        mAudioManager.setOnAudioStateListener(new AudioManager.AudioStateListener() {
            public void wellPrepared() {
                mHandler.sendEmptyMessage(MSG_AUDIO_PREPARED);
            }

            @Override
            public void onRelease() {

            }
        });

        // 由于这个类是button所以在构造方法中添加监听事件
        setOnLongClickListener(new OnLongClickListener() {

            public boolean onLongClick(View v) {
                try {
                    mReady = true;
                    mAudioManager.prepareAudio();
                    return false;
                } catch (Exception ex) {

                }
                return true;
            }
        });
    }

    public AudioRecorderButton(Context context) {
        this(context, null);
    }

    /**
     * @param
     * @author ldm
     * @description 录音完成后的回调
     * @time 2016/6/25 11:18
     */
    public interface AudioRecorderCallBack {

        void onStartRecord();

        void onFinish(float seconds, String filePath);

        void onPermissionAsk();

        void onPermissionDenied();
    }

    private AudioRecorderCallBack audioRecorderCallBack;

    public void setAudioRecorderCallBack(AudioRecorderCallBack listener) {
        audioRecorderCallBack = listener;
    }

    /**
     * @param
     * @description 处理Button的OnTouchEvent事件
     * @author ldm
     * @time 2016/6/25 9:35
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //获取TouchEvent状态
        int action = event.getAction();
        // 获得x轴坐标
        int x = (int) event.getX();
        // 获得y轴坐标
        int y = (int) event.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN://手指按下
                try {
                    Vibrator vibrator = (Vibrator) this.getContext().getSystemService(Context.VIBRATOR_SERVICE);
                    long[] pattern = {100, 100}; // 停止 开启 停止 开启
                    vibrator.vibrate(pattern, -1); //重复两次上面的pattern 如果只想震动一次，index设为-1
                } catch (Exception ex) {

                }
                //判断权限
                int result = MyPermissionUtil.checkOp(this.getContext(), MyPermissionUtil.OP_RECORD_AUDIO);
                Log.i("Chat", "permission : " + result);
                //0 允许,4 询问,1 拒绝,-1 <4.4.4
                if (result == 4 && audioRecorderCallBack != null) {
                    audioRecorderCallBack.onPermissionAsk();
                    return true;
                } else if (result == 1 && audioRecorderCallBack != null) {
                    audioRecorderCallBack.onPermissionDenied();
                    return true;
                } else {
                    if (audioRecorderCallBack != null) {
                        audioRecorderCallBack.onStartRecord();
                    }
                    changeState(STATE_RECORDING);
                }
                break;
            case MotionEvent.ACTION_MOVE://手指移动
                if (isRecording) {
                    //根据x,y的坐标判断是否需要取消
                    if (wantToCancle(x, y)) {
                        changeState(STATE_CANCEL);
                    } else {
                        changeState(STATE_RECORDING);
                    }
                }

                break;
            case MotionEvent.ACTION_UP://手指放开
                if (!mReady) {
                    reset();
                    return super.onTouchEvent(event);
                }
                if (!isRecording || mTime < 0.6f) {//如果时间少于0.6s，则提示录音过短
                    mDialogManager.tooShort();
                    mAudioManager.cancel();
                    // 延迟显示对话框
                    mHandler.sendEmptyMessageDelayed(MSG_DIALOG_DIMISS, 1000);
                } else if (mCurrentState == STATE_RECORDING) {
                    //如果状态为正在录音，则结束录制
                    mDialogManager.dimissDialog();
                    mAudioManager.release();

                    if (audioRecorderCallBack != null) {
                        audioRecorderCallBack.onFinish(mTime, mAudioManager.getCurrentFilePath());
                    }

                } else if (mCurrentState == STATE_CANCEL) { // 想要取消
                    mDialogManager.dimissDialog();
                    mAudioManager.cancel();
                }
                reset();
                break;

        }
        return super.onTouchEvent(event);
    }

    /**
     * 恢复状态及标志位
     */
    private void reset() {
        isRecording = false;
        mTime = 0;
        mReady = false;
        changeState(STATE_NORMAL);
    }

    private boolean wantToCancle(int x, int y) {
        // 超过按钮的宽度
        if (x < 0 || x > getWidth()) {
            return true;
        }
        // 超过按钮的高度
        if (y < -DISTANCE_Y_CANCEL || y > getHeight() + DISTANCE_Y_CANCEL) {
            return true;
        }

        return false;
    }


    /**
     * @param
     * @description 根据状态改变Button显示
     * @author ldm
     * @time 2016/6/25 9:36
     */
    private void changeState(int state) {
        if (mCurrentState != state) {
            mCurrentState = state;
            switch (state) {
                case STATE_NORMAL:
                    setBackgroundResource(R.drawable.btn_recorder_normal);
                    setText(R.string.str_recorder_normal);
                    break;

                case STATE_RECORDING:
                    setBackgroundResource(R.drawable.btn_recorder_recording);
                    setText(R.string.str_recorder_recording);
                    if (isRecording) {
                        mDialogManager.recording();
                    }
                    break;

                case STATE_CANCEL:
                    setBackgroundResource(R.drawable.btn_recorder_recording);
                    mDialogManager.wantToCancel();
                    setText(R.string.str_recorder_want_cancel);
                    break;
            }
        }
    }
}
