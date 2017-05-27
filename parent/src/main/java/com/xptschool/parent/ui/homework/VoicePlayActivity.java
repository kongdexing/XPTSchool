package com.xptschool.parent.ui.homework;

import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.widget.audiorecorder.MediaPlayerManager;
import com.android.widget.roundcornerprogressbar.RoundCornerProgressBar;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.util.FileDownloadUtils;
import com.xptschool.parent.R;
import com.xptschool.parent.bean.BeanHomeWork;
import com.xptschool.parent.ui.album.AlbumActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by dexing on 2017/5/19.
 * No1
 */
public class VoicePlayActivity extends AlbumActivity implements VoicePlayListener {

    public int Voice_Play = 0;
    public final int Voice_Stop = Voice_Play + 1;
    public int VoiceStatus = Voice_Stop;

    @BindView(R.id.llVoice)
    LinearLayout llVoice;
    @BindView(R.id.voiceBar)
    RoundCornerProgressBar voiceBar;
    @BindView(R.id.imgVoice)
    ImageView imgVoice;
    @BindView(R.id.txtProgress)
    TextView txtProgress;
    //是否开始播放标志
    private boolean isPlaying = false;
    //记录播放时间
    private float mPlayTime;
    public String localAmrFile = null;
    //播放录音
    private static final int MSG_VOICE_PLAY = 0x113;

    public void initVoice(BeanHomeWork homeWork) {
        //取amr文件
        String amr_file = homeWork.getAmr_file();
        Log.i(TAG, "amr file : " + amr_file);
        if (amr_file != null) {
            llVoice.setVisibility(View.VISIBLE);
            FileDownloader.getImpl().create(amr_file)
                    .setListener(createListener())
                    .setTag(amr_file)
                    .start();
        } else {
            llVoice.setVisibility(View.GONE);
        }
    }

    private void initProgress(int maxProgress, int progress) {
        Log.i(TAG, "initProgress: max " + maxProgress + " progress:" + progress);
        if (voiceBar == null) {
            return;
        }
        voiceBar.setProgressBackgroundColor(this.getResources().getColor(R.color.colorPrimaryDark));
        voiceBar.setMax(maxProgress);
        voiceBar.setPadding(3);
        voiceBar.setSecondaryProgressColor(this.getResources().getColor(R.color.color_rcBackgroundColor));
        voiceBar.setSecondaryProgress(voiceBar.getMax());
        voiceBar.setProgress(progress);
        voiceBar.setProgressColor(this.getResources().getColor(R.color.colorPrimaryDark));
    }

    private void setImgMicStatus(int status) {
        if (status == Voice_Play) {
            imgVoice.setBackgroundResource(R.drawable.selector_voice_play);
        } else if (status == Voice_Stop) {
            imgVoice.setBackgroundResource(R.drawable.selector_voice_stop);
        }
        VoiceStatus = status;
    }

    @OnClick({R.id.imgVoice})
    void viewOnClick(View view) {
        switch (view.getId()) {
            case R.id.imgVoice:
                if (VoiceStatus == Voice_Play) {
                    onPlayVoice();
                } else if (VoiceStatus == Voice_Stop) {
                    onStopPlay();
                }
                break;
        }
    }

    @Override
    public void onPlayVoice() {
        if (VoiceStatus == Voice_Play) {
            isPlaying = true;
            mPlayTime = 0;
            new Thread(playVoiceRunnable).start();
            //播放录音
            MediaPlayerManager.playSound(localAmrFile, new MediaPlayer.OnCompletionListener() {

                public void onCompletion(MediaPlayer mp) {
                    //播放完成后改为播放状态
                    setImgMicStatus(Voice_Play);
                    isPlaying = false;
                    if (voiceBar != null) {
                        initProgress((int) voiceBar.getMax(), (int) voiceBar.getMax());
                    }
                }
            });
            //改为停止状态
            setImgMicStatus(Voice_Stop);
        }
    }

    @Override
    public void onStopPlay() {
        if (VoiceStatus == Voice_Stop) {
            MediaPlayerManager.pause();
            isPlaying = false;
            //改为播放状态
            setImgMicStatus(Voice_Play);
        }
    }

    private Runnable playVoiceRunnable = new Runnable() {

        public void run() {
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

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_VOICE_PLAY:
                    if (isPlaying && voiceBar != null) {
                        voiceBar.setProgress(mPlayTime);
                    }
                    break;
            }
        }
    };

    public FileDownloadListener createListener() {
        return new FileDownloadListener() {

            @Override
            protected boolean isInvalid() {
                return isFinishing();
            }

            @Override
            protected void pending(final BaseDownloadTask task, final int soFarBytes, final int totalBytes) {
//                updateDisplay(String.format("[pending] id[%d] %d/%d", task.getId(), soFarBytes, totalBytes));
            }

            @Override
            protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
                super.connected(task, etag, isContinue, soFarBytes, totalBytes);
//                updateDisplay(String.format("[connected] id[%d] %s %B %d/%d", task.getId(), etag, isContinue, soFarBytes, totalBytes));
            }

            @Override
            protected void progress(final BaseDownloadTask task, final int soFarBytes, final int totalBytes) {
//                updateDisplay(String.format("[progress] id[%d] %d/%d", task.getId(), soFarBytes, totalBytes));
            }

            @Override
            protected void blockComplete(final BaseDownloadTask task) {
            }

            @Override
            protected void retry(BaseDownloadTask task, Throwable ex, int retryingTimes, int soFarBytes) {
                super.retry(task, ex, retryingTimes, soFarBytes);
//                updateDisplay(String.format("[retry] id[%d] %s %d %d",
//                        task.getId(), ex, retryingTimes, soFarBytes));
            }

            @Override
            protected void completed(BaseDownloadTask task) {
                int duration = getAmrDuration(task.getPath());
                if (duration == 0) {
                    imgVoice.setEnabled(false);
                } else {
                    imgVoice.setEnabled(true);
                }
                txtProgress.setText(duration + "\"");
                initProgress(duration, 0);
                setImgMicStatus(Voice_Play);

//                Log.i(TAG, "completed: " + task.getPath() + " " + task.getTargetFilePath());
//                updateDisplay(String.format("[completed] id[%d] oldFile[%B]",
//                        task.getId(),
//                        task.isReusedOldFile()));
            }

            @Override
            protected void paused(final BaseDownloadTask task, final int soFarBytes, final int totalBytes) {
//                updateDisplay(String.format("[paused] id[%d] %d/%d", task.getId(), soFarBytes, totalBytes));
//                updateDisplay(String.format("############################## %d", (Integer) task.getTag()));
            }

            @Override
            protected void error(BaseDownloadTask task, Throwable e) {
//                updateDisplay(Html.fromHtml(String.format("[error] id[%d] %s %s",
//                        task.getId(),
//                        e,
//                        FileDownloadUtils.getStack(e.getStackTrace(), false))));
//
//                updateDisplay(String.format("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! %d", (Integer) task.getTag()));
            }

            @Override
            protected void warn(BaseDownloadTask task) {
//                updateDisplay(String.format("[warn] id[%d]", task.getId()));
//                updateDisplay(String.format("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ %d", (Integer) task.getTag()));
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
        return duration > 120 ? 120 : duration;
    }

    @Override
    protected void onPause() {
        super.onPause();
        onStopPlay();
    }
}
