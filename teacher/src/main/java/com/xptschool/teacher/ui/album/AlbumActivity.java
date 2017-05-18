package com.xptschool.teacher.ui.album;

import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.LayoutRes;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.widget.audiorecorder.AudioManager;
import com.android.widget.audiorecorder.MediaPlayerManager;
import com.android.widget.roundcornerprogressbar.RoundCornerProgressBar;
import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.CropOptions;
import com.jph.takephoto.model.TImage;
import com.jph.takephoto.model.TResult;
import com.jph.takephoto.model.TakePhotoOptions;
import com.xptschool.teacher.R;
import com.xptschool.teacher.XPTApplication;
import com.xptschool.teacher.common.LocalImageHelper;
import com.xptschool.teacher.util.ToastUtils;
import com.xptschool.teacher.view.AlbumSourceView;
import com.xptschool.teacher.view.imgloader.AlbumViewPager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Administrator on 2016/10/29.
 */
public class AlbumActivity extends TakePhotoActivity {

    public ScrollView mScrollView;
    private PopupWindow picPopup;
    public AlbumGridAdapter myPicGridAdapter;
    public int Voice_UnRecord = 0;
    public int Voice_Recording = Voice_UnRecord + 1;
    public int Voice_Stop = Voice_Recording + 1;
    public int Voice_Play = Voice_Stop + 1;
    public int VoiceStatus = Voice_UnRecord;
    //录音准备
    private static final int MSG_AUDIO_PREPARED = 0x110;
    //音量发生改变
    private static final int MSG_VOICE_CHANGED = 0x111;
    //停止录音
    private static final int MSG_AUDIO_STOP = 0x112;
    //播放录音
    private static final int MSG_VOICE_PLAY = 0x113;

    @BindView(R.id.voiceBar)
    RoundCornerProgressBar voiceBar;
    @BindView(R.id.imgDelete)
    ImageView imgDelete;
    @BindView(R.id.imgMic)
    ImageView imgMic;
    @BindView(R.id.txtProgress)
    TextView txtProgress;

    //录音管理工具类
    private AudioManager mAudioManager;
    //是否开始录音标志
    private boolean isRecording = false;
    //是否开始播放标志
    private boolean isPlaying = false;
    //记录录音时间
    private float mTime;
    //记录播放时间
    private float mPlayTime;
    private int maxLength = 120;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalImageHelper.getInstance().getLocalCheckedImgs().clear();
        mAudioManager = AudioManager.getInstance(XPTApplication.getInstance().getCachePath());
    }

    public void initProgress() {
        voiceBar.setProgressBackgroundColor(this.getResources().getColor(R.color.colorPrimaryDark));
        voiceBar.setMax(maxLength);
        voiceBar.setPadding(3);
//        voiceBar.setSecondaryProgress(voiceBar.getMax());
        voiceBar.setSecondaryProgressColor(this.getResources().getColor(R.color.color_rcBackgroundColor));
        voiceBar.setSecondaryProgress(voiceBar.getMax());

        voiceBar.setProgressColor(this.getResources().getColor(R.color.colorPrimaryDark));
    }

    public void showAlbumSource(View view) {
        //选择相片来源
        if (picPopup == null) {
            AlbumSourceView albumSourceView = new AlbumSourceView(AlbumActivity.this);
            albumSourceView.setOnAlbumSourceClickListener(new AlbumSourceView.OnAlbumSourceClickListener() {
                @Override
                public void onAlbumClick() {
                    if (LocalImageHelper.getInstance().getLocalCheckedImgs().size() >= LocalImageHelper.getInstance().getMaxChoiceSize()) {
                        Toast.makeText(AlbumActivity.this, getString(R.string.image_upline, LocalImageHelper.getInstance().getMaxChoiceSize()), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    TakePhoto takePhoto = getTakePhoto();
                    configCompress(takePhoto);
                    configTakePhotoOption(takePhoto);
                    int limit = LocalImageHelper.getInstance().getCurrentEnableMaxChoiceSize();
//                    takePhoto.onPickMultiple(limit);
                    takePhoto.onPickMultipleWithCrop(limit, getCropOptions());
                    picPopup.dismiss();
                }

                @Override
                public void onCameraClick() {
                    if (LocalImageHelper.getInstance().getLocalCheckedImgs().size() >= LocalImageHelper.getInstance().getMaxChoiceSize()) {
                        Toast.makeText(AlbumActivity.this, getString(R.string.image_upline, LocalImageHelper.getInstance().getMaxChoiceSize()), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    try {
                        //  拍照后保存图片的绝对路径
                        String cameraPath = LocalImageHelper.getInstance().setCameraImgPath();
                        File file = new File(cameraPath);
                        if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
                        Uri imageUri = Uri.fromFile(file);
                        TakePhoto takePhoto = getTakePhoto();
                        configCompress(takePhoto);
                        configTakePhotoOption(takePhoto);

                        takePhoto.onPickFromCaptureWithCrop(imageUri, getCropOptions());

                        //getTakePhoto().onPickFromCaptureWithCrop(imageUri, getCropOptions());
//                        takePhoto.onPickFromCapture(imageUri);
//                    takePhoto.onPickFromCapture(Uri.fromFile(file));
//                    AlbumActivityPermissionsDispatcher.openCameraWithCheck(AlbumActivity.this);
                    } catch (Exception ex) {
                        Toast.makeText(AlbumActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "onCameraClick: " + ex.getMessage());
                    }
                    picPopup.dismiss();
                }

                @Override
                public void onVoiceClick() {
                    picPopup.dismiss();

                }

                @Override
                public void onBack() {
                    picPopup.dismiss();
                }
            });
            picPopup = new PopupWindow(albumSourceView,
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
            picPopup.setTouchable(true);
            picPopup.setBackgroundDrawable(new ColorDrawable());
            picPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    backgroundAlpha(1.0f);
                }
            });
        }
        backgroundAlpha(0.5f);
        picPopup.showAtLocation(view, Gravity.BOTTOM, 0, 0);
    }

    private void configCompress(TakePhoto takePhoto) {
        int maxSize = 204800;
        int width = 800;
        int height = 800;
        boolean showProgressBar = true;
        boolean enableRawFile = false;
        CompressConfig config = new CompressConfig.Builder()
                .setMaxSize(maxSize)
                .setMaxPixel(width >= height ? width : height)
                .enableReserveRaw(enableRawFile)
                .create();
        takePhoto.onEnableCompress(config, showProgressBar);
    }

    public void configTakePhotoOption(TakePhoto takePhoto) {
        TakePhotoOptions.Builder builder = new TakePhotoOptions.Builder();
        builder.setWithOwnGallery(true);
        builder.setCorrectImage(true);
        takePhoto.setTakePhotoOptions(builder.create());
    }

    private CropOptions getCropOptions() {
        int height = 800;
        int width = 800;
        boolean withWonCrop = true;

        CropOptions.Builder builder = new CropOptions.Builder();

        builder.setOutputX(width).setOutputY(height);
        builder.setWithOwnCrop(withWonCrop);
        return builder.create();
    }

    @Override
    public void takeCancel() {
        super.takeCancel();
    }

    @Override
    public void takeFail(TResult result, String msg) {
        super.takeFail(result, msg);
        Log.i(TAG, "takeFail: " + msg);
    }

    @Override
    public void takeSuccess(TResult result) {
        super.takeSuccess(result);
        Log.i(TAG, "takeSuccess: ");
        showImg(result.getImages());
    }

    private void showImg(ArrayList<TImage> images) {
        for (int i = 0; i < images.size(); i++) {
            String patch = images.get(i).getCompressPath();
//            if (path.isEmpty()) {
//                path = images.get(i).getCompressPath();
//            }
            patch = "file://" + patch;
            Log.i(TAG, "showImg: " + patch);
            if (!LocalImageHelper.getInstance().getLocalCheckedImgs().contains(patch)) {
                LocalImageHelper.getInstance().getLocalCheckedImgs().add(patch);
            }
        }
        myPicGridAdapter.reloadPicture(LocalImageHelper.getInstance().getLocalCheckedImgs());
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                mScrollView.fullScroll(ScrollView.FOCUS_DOWN);//滚动到底部
            }
        });
    }

    //显示大图pager
    public void showViewPager(AlbumViewPager albumviewpager, int index) {
        if (albumviewpager == null)
            return;
        Log.i(TAG, "showViewPager: ");
        albumviewpager.setVisibility(View.VISIBLE);
//        albumviewpager.setAdapter(albumviewpager.new LocalViewPagerAdapter(LocalImageHelper.getInstance().getLocalCheckedImgs()));
        albumviewpager.setAdapter(albumviewpager.new LocalViewPagerAdapter(myPicGridAdapter.getImgPaths()));
        albumviewpager.setCurrentItem(index);
        AnimationSet set = new AnimationSet(true);
        ScaleAnimation scaleAnimation = new ScaleAnimation((float) 0.9, 1, (float) 0.9, 1, albumviewpager.getWidth() / 2, albumviewpager.getHeight() / 2);
        scaleAnimation.setDuration(200);
        set.addAnimation(scaleAnimation);
        AlphaAnimation alphaAnimation = new AlphaAnimation((float) 0.1, 1);
        alphaAnimation.setDuration(200);
        set.addAnimation(alphaAnimation);
        albumviewpager.startAnimation(set);
    }

    //显示大图pager
    public void showNetImgViewPager(AlbumViewPager albumviewpager, List<String> imgUris, int index) {
        if (albumviewpager == null)
            return;
        Log.i(TAG, "showNetImgViewPager: ");
        albumviewpager.setVisibility(View.VISIBLE);
        albumviewpager.setAdapter(albumviewpager.new NetViewPagerAdapter(imgUris));
        albumviewpager.setCurrentItem(index);
        AnimationSet set = new AnimationSet(true);
        ScaleAnimation scaleAnimation = new ScaleAnimation((float) 0.9, 1, (float) 0.9, 1, albumviewpager.getWidth() / 2, albumviewpager.getHeight() / 2);
        scaleAnimation.setDuration(200);
        set.addAnimation(scaleAnimation);
        AlphaAnimation alphaAnimation = new AlphaAnimation((float) 0.1, 1);
        alphaAnimation.setDuration(200);
        set.addAnimation(alphaAnimation);
        albumviewpager.startAnimation(set);
    }

    //关闭大图显示
    public void hideViewPager(AlbumViewPager albumviewpager) {
        if (albumviewpager == null)
            return;
        if (albumviewpager.getVisibility() == View.VISIBLE) {
            albumviewpager.setVisibility(View.GONE);
            AnimationSet set = new AnimationSet(true);
            ScaleAnimation scaleAnimation = new ScaleAnimation(1, (float) 0.9, 1, (float) 0.9, albumviewpager.getWidth() / 2, albumviewpager.getHeight() / 2);
            scaleAnimation.setDuration(200);
            set.addAnimation(scaleAnimation);
            AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
            alphaAnimation.setDuration(200);
            set.addAnimation(alphaAnimation);
            albumviewpager.startAnimation(set);
        }
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
                    maxLength = 120;
                    initProgress();
                    break;
                case MSG_VOICE_CHANGED:
                    Log.i(TAG, "handleMessage: MSG_VOICE_CHANGED");
                    //更新声音
                    if (mTime > voiceBar.getMax()) {
                        stopRecord();
                    }
                    if (isRecording) {
                        voiceBar.setProgress(mTime);
                        txtProgress.setText(Math.round(mTime) + "\"/" + maxLength + "\"");
                    }
                    break;
                case MSG_AUDIO_STOP:
                    Log.i(TAG, "handleMessage: MSG_AUDIO_STOP");
                    break;
                case MSG_VOICE_PLAY:

                    voiceBar.setProgress(mPlayTime);
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
                    mHandler.sendEmptyMessage(MSG_VOICE_CHANGED);//每0.1秒发送消息
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public void recorderOrPlayVoice() {
        if (VoiceStatus == Voice_UnRecord) {
//                    imgMic.setBackgroundResource(R.drawable.selector_micphone);
            //start record
            Log.i(TAG, "recorderOrPlayVoice: start record");
            isRecording = true;
            mAudioManager.setOnAudioStateListener(new AudioManager.AudioStateListener() {
                public void wellPrepared() {
                    Log.i(TAG, "wellPrepared: ");
                    mHandler.sendEmptyMessage(MSG_AUDIO_PREPARED);
                }

                public void onRelease() {
                    Log.i(TAG, "onRelease: ");
//                    mHandler.sendEmptyMessage(MSG_AUDIO_STOP);
                    //停止录制
                    imgDelete.setVisibility(View.VISIBLE);
                    maxLength = Math.round(mTime);
                    voiceBar.setProgress(maxLength);
                    txtProgress.setText(Math.round(mTime) + "\"");
                    initProgress();
                    reset();
                }
            });

            mAudioManager.prepareAudio();
            imgMic.setBackgroundResource(R.drawable.selector_voice_stop);
            VoiceStatus = Voice_Recording;
        } else if (VoiceStatus == Voice_Recording) {
            if (!isRecording || mTime < 0.6f) {//如果时间少于0.6s，则提示录音过短
                mAudioManager.cancel();
                // 延迟显示对话框
                ToastUtils.showToast(this, "录音过短，请重新录制");
                imgMic.setBackgroundResource(R.drawable.selector_micphone);
                VoiceStatus = Voice_UnRecord;
            } else {
                //stop record
                stopRecord();
            }
        } else if (VoiceStatus == Voice_Stop) {
            //play voice
            //开始播放动画

            String filePath = XPTApplication.getInstance().getCachePath() + "/" + mAudioManager.getCurrentFilePath();
            Log.i(TAG, "recorderOrPlayVoice: Voice_Stop play voice "+filePath);

            // 播放录音
            MediaPlayerManager.playSound(filePath, new MediaPlayer.OnCompletionListener() {

                public void onCompletion(MediaPlayer mp) {
                    //播放完成后改为播放状态
                    imgMic.setBackgroundResource(R.drawable.selector_voice_play);
                    VoiceStatus = Voice_Stop;
                    reset();
                }
            });

            //改为停止状态
            imgMic.setBackgroundResource(R.drawable.selector_voice_stop);
            VoiceStatus = Voice_Play;
        } else if (VoiceStatus == Voice_Play) {
            //stop play

            //改为播放状态
            imgMic.setBackgroundResource(R.drawable.selector_voice_play);
            VoiceStatus = Voice_Stop;
        }
    }

    private void stopRecord() {
        Log.i(TAG, "stopRecord: ");
        mAudioManager.release();

        imgMic.setBackgroundResource(R.drawable.selector_voice_play);
        VoiceStatus = Voice_Stop;
    }

    private void reset() {
        Log.i(TAG, "reset: ");
        isRecording = false;
        mTime = 0;
        isPlaying = false;
        mPlayTime = 0;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        LocalImageHelper.getInstance().clear();
    }

}
