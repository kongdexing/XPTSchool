package com.xptschool.teacher.ui.homework;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.widget.audiorecorder.AudioManager;
import com.android.widget.audiorecorder.MediaPlayerManager;
import com.android.widget.roundcornerprogressbar.RoundCornerProgressBar;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.util.FileDownloadUtils;
import com.xptschool.teacher.R;
import com.xptschool.teacher.XPTApplication;
import com.xptschool.teacher.bean.BeanHomeWork;
import com.xptschool.teacher.ui.album.AlbumActivity;
import com.xptschool.teacher.util.ToastUtils;

import java.io.File;

import butterknife.BindView;

/**
 * Created by dexing on 2017/5/19.
 * No1
 */

public class VoiceRecordActivity extends AlbumActivity implements VoiceListener {

    public int Voice_UnRecord = 0;
    public final int Voice_Recording = Voice_UnRecord + 1;
    public final int Voice_Stop = Voice_Recording + 1;
    public final int Voice_Play = Voice_Stop + 1;
    public int VoiceStatus = Voice_UnRecord;
    //录音准备
    private static final int MSG_AUDIO_PREPARED = 0x110;
    //音量发生改变
    private static final int MSG_VOICE_CHANGED = 0x111;
    //停止录音
    private static final int MSG_AUDIO_STOP = 0x112;
    //播放录音
    private static final int MSG_VOICE_PLAY = 0x113;

    private static final int MSG_VOICE_RELEASE = 0x114;

    @BindView(R.id.voiceBar)
    RoundCornerProgressBar voiceBar;
    @BindView(R.id.imgDelete)
    ImageView imgDelete;
    @BindView(R.id.imgMic)
    ImageView imgMic;
    @BindView(R.id.txtProgress)
    TextView txtProgress;

    //录音管理工具类
    public AudioManager mAudioManager;
    //是否开始录音标志
    private boolean isRecording = false;
    //是否开始播放标志
    private boolean isPlaying = false;
    //记录录音时间
    private float mTime;
    //记录播放时间
    private float mPlayTime;
    private int MaxLength = 120;
    private int maxLength = MaxLength;
    public String localAmrFile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAudioManager = AudioManager.getInstance(XPTApplication.getInstance().getCachePath());
        Log.i(TAG, "onCreate: " + mAudioManager.getCurrentFilePath());
    }

    public void initVoice(BeanHomeWork homeWork) {
        if (homeWork == null) {
            imgMic.setEnabled(true);
            initProgress(0);
        } else {
            //取amr文件
            String amr_file = homeWork.getAmr_file();
            Log.i(TAG, "amr file : " + amr_file);
            String fileName = amr_file.substring(amr_file.lastIndexOf('/') + 1);

            if (amr_file != null) {
                FileDownloader.getImpl().create(amr_file)
                        .setListener(createListener())
                        .setPath(XPTApplication.getInstance().getCachePath() + "/" + fileName)
                        .setTag(1)
                        .start();
            } else {
                imgMic.setEnabled(false);
                initProgress(0);
            }
        }
    }

    public void showVoiceDel() {
        imgDelete.setVisibility(View.VISIBLE);
        if (VoiceStatus == Voice_Recording) {
            onStopRecording();
        } else if (VoiceStatus == Voice_Stop) {
            onStopPlay();
            initProgress(0);
        } else {

        }
    }

    public void hideVoiceDel() {
        imgDelete.setVisibility(View.GONE);
        if (VoiceStatus == Voice_Recording) {
            onStopRecording();
        } else if (VoiceStatus == Voice_Stop) {
            onStopPlay();
        }
    }

    public void initProgress(int progress) {
        Log.i(TAG, "initProgress: max " + maxLength + " progress:" + progress);
        voiceBar.setProgressBackgroundColor(this.getResources().getColor(R.color.colorPrimaryDark));
        voiceBar.setMax(maxLength);
        voiceBar.setPadding(3);
        voiceBar.setSecondaryProgressColor(this.getResources().getColor(R.color.color_rcBackgroundColor));
        voiceBar.setSecondaryProgress(voiceBar.getMax());
        voiceBar.setProgress(progress);
        voiceBar.setProgressColor(this.getResources().getColor(R.color.colorPrimaryDark));
    }

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_AUDIO_PREPARED:
                    //显示对话框
                    isRecording = true;
                    // 开启一个线程计算录音时间
                    new Thread(mGetVoiceLevelRunnable).start();
                    imgDelete.setVisibility(View.GONE);
                    maxLength = MaxLength;
                    initProgress(0);
                    break;
                case MSG_VOICE_CHANGED:
                    if (!isRecording || voiceBar == null) {
                        break;
                    }

                    if (mTime >= voiceBar.getMax()) {
                        mTime = voiceBar.getMax();
                        stopRecord();
                    }
                    //更新声音
                    voiceBar.setProgress(mTime);
                    txtProgress.setVisibility(View.VISIBLE);
                    txtProgress.setText(Math.round(mTime) + "\"/" + maxLength + "\"");
                    break;
                case MSG_AUDIO_STOP:
                    Log.i(TAG, "handleMessage: MSG_AUDIO_STOP");
                    break;
                case MSG_VOICE_PLAY:
                    if (isPlaying && voiceBar != null) {
                        voiceBar.setProgress(mPlayTime);
                    }
                    break;
                case MSG_VOICE_RELEASE:
                    imgDelete.setVisibility(View.VISIBLE);
                    maxLength = Math.round(mTime);
                    initProgress(0);
                    txtProgress.setVisibility(View.VISIBLE);
                    txtProgress.setText(maxLength + "\"");
                    reset();
                    setImgMicStatus(Voice_Play);
                    break;
            }
            super.handleMessage(msg);
        }
    };

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
            while (isPlaying) {
                try {
                    Thread.sleep(100);
                    mPlayTime += 0.1f;//录音时间计算
                    mHandler.sendEmptyMessage(MSG_VOICE_PLAY);//每0.1秒发送消息
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public void recorderOrPlayVoice() {
        if (VoiceStatus == Voice_UnRecord) {
            //start record
            Log.i(TAG, "recorderOrPlayVoice: start record");
            onStartRecording();
        } else if (VoiceStatus == Voice_Recording) {
            onStopRecording();
        } else if (VoiceStatus == Voice_Play) {
            //play voice
            onPlayVoice();
        } else if (VoiceStatus == Voice_Stop) {
            //stop play
            onStopPlay();
        }
    }

    @Override
    public void onStartRecording() {
        isRecording = true;
        localAmrFile = null;
        mAudioManager.setOnAudioStateListener(new AudioManager.AudioStateListener() {
            public void wellPrepared() {
                Log.i(TAG, "wellPrepared: ");
                mHandler.sendEmptyMessage(MSG_AUDIO_PREPARED);
            }

            public void onRelease() {
                Log.i(TAG, "onRelease: ");
//                    mHandler.sendEmptyMessage(MSG_AUDIO_STOP);
                //停止录制
                mHandler.sendEmptyMessage(MSG_VOICE_RELEASE);
            }
        });

        mAudioManager.prepareAudio();
        setImgMicStatus(Voice_Recording);
    }

    @Override
    public void onStopRecording() {
        if (VoiceStatus == Voice_Recording) {
            if (!isRecording || mTime < 0.6f) {//如果时间少于0.6s，则提示录音过短
                mAudioManager.cancel();
                // 延迟显示对话框
                ToastUtils.showToast(this, "录音过短，请重新录制");
                setImgMicStatus(Voice_UnRecord);
            } else {
                //stop record
                stopRecord();
            }
        }
    }

    @Override
    public void onPlayVoice() {
//开始播放动画
        isPlaying = true;
        mPlayTime = 0;
        new Thread(mGetVoiceLevelRunnable).start();
        String filePath = mAudioManager.getCurrentFilePath();
        if (filePath == null) {
            filePath = localAmrFile;
        }
        Log.i(TAG, "recorderOrPlayVoice: Voice_Stop play voice " + filePath);

        // 播放录音
        MediaPlayerManager.playSound(filePath, new MediaPlayer.OnCompletionListener() {

            public void onCompletion(MediaPlayer mp) {
                //播放完成后改为播放状态
                setImgMicStatus(Voice_Play);
                reset();
                initProgress(maxLength);
            }
        });

        //改为停止状态
        setImgMicStatus(Voice_Stop);
    }

    @Override
    public void onStopPlay() {
        if (VoiceStatus == Voice_Stop) {
            Log.i(TAG, "onStopPlay: ");
            MediaPlayerManager.pause();
            isPlaying = false;
            //改为播放状态
            setImgMicStatus(Voice_Play);
        }
    }

    public void deleteOldVoice() {
        try {
            String filePath = localAmrFile;
            if (filePath == null) {
                filePath = mAudioManager.getCurrentFilePath();
            }
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception ex) {

        }
        imgDelete.setVisibility(View.GONE);
        txtProgress.setVisibility(View.GONE);
        if (VoiceStatus == Voice_Stop) {
            onStopPlay();
        }
        reset();
        maxLength = MaxLength;
        initProgress(0);
        setImgMicStatus(Voice_UnRecord);
    }

    private void stopRecord() {
        Log.i(TAG, "stopRecord: ");
        mAudioManager.release();
    }

    private void reset() {
        Log.i(TAG, "reset: ");
        isRecording = false;
        mTime = 0;
        isPlaying = false;
    }

    private void setImgMicStatus(int status) {
        if (status == Voice_Play) {
            imgMic.setBackgroundResource(R.drawable.selector_voice_play);
        } else if (status == Voice_Stop) {
            imgMic.setBackgroundResource(R.drawable.selector_voice_stop);
        } else if (status == Voice_UnRecord) {
            imgMic.setBackgroundResource(R.drawable.selector_micphone);
        } else if (status == Voice_Recording) {
            imgMic.setBackgroundResource(R.drawable.selector_voice_stop);
        }
        VoiceStatus = status;
    }

    @Override
    protected void onPause() {
        super.onPause();
        onStopRecording();
        onStopPlay();
    }

    public FileDownloadListener createListener() {
        return new FileDownloadListener() {

            @Override
            protected boolean isInvalid() {
                return isFinishing();
            }

            @Override
            protected void pending(final BaseDownloadTask task, final int soFarBytes, final int totalBytes) {
                updateDisplay(String.format("[pending] id[%d] %d/%d", task.getId(), soFarBytes, totalBytes));
            }

            @Override
            protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
                super.connected(task, etag, isContinue, soFarBytes, totalBytes);
                updateDisplay(String.format("[connected] id[%d] %s %B %d/%d", task.getId(), etag, isContinue, soFarBytes, totalBytes));
            }

            @Override
            protected void progress(final BaseDownloadTask task, final int soFarBytes, final int totalBytes) {
                updateDisplay(String.format("[progress] id[%d] %d/%d", task.getId(), soFarBytes, totalBytes));
            }

            @Override
            protected void blockComplete(final BaseDownloadTask task) {
            }

            @Override
            protected void retry(BaseDownloadTask task, Throwable ex, int retryingTimes, int soFarBytes) {
                super.retry(task, ex, retryingTimes, soFarBytes);
                updateDisplay(String.format("[retry] id[%d] %s %d %d",
                        task.getId(), ex, retryingTimes, soFarBytes));
            }

            @Override
            protected void completed(BaseDownloadTask task) {
                int duration = getAmrDuration(task.getPath());
                txtProgress.setText(duration + "\"");
                maxLength = duration;
                initProgress(0);
                setImgMicStatus(Voice_Play);

                Log.i(TAG, "completed: " + task.getPath() + " " + task.getTargetFilePath());
                updateDisplay(String.format("[completed] id[%d] oldFile[%B]",
                        task.getId(),
                        task.isReusedOldFile()));
                updateDisplay(String.format("---------------------------------- %d", (Integer) task.getTag()));
            }

            @Override
            protected void paused(final BaseDownloadTask task, final int soFarBytes, final int totalBytes) {
                updateDisplay(String.format("[paused] id[%d] %d/%d", task.getId(), soFarBytes, totalBytes));
                updateDisplay(String.format("############################## %d", (Integer) task.getTag()));
            }

            @Override
            protected void error(BaseDownloadTask task, Throwable e) {
                updateDisplay(Html.fromHtml(String.format("[error] id[%d] %s %s",
                        task.getId(),
                        e,
                        FileDownloadUtils.getStack(e.getStackTrace(), false))));

                updateDisplay(String.format("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! %d", (Integer) task.getTag()));
            }

            @Override
            protected void warn(BaseDownloadTask task) {
                updateDisplay(String.format("[warn] id[%d]", task.getId()));
                updateDisplay(String.format("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ %d", (Integer) task.getTag()));
            }
        };
    }

    private void updateDisplay(final CharSequence msg) {
        Log.i(TAG, "updateDisplay: " + msg);
    }

    private int getAmrDuration(String path) {
        int duration = 0;
        try {
            localAmrFile = path;
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            float dur = (float) mediaPlayer.getDuration() / 1000;
            duration = Math.round(dur);
        } catch (Exception ex) {

        }
        return duration > maxLength ? maxLength : duration;
    }

}
