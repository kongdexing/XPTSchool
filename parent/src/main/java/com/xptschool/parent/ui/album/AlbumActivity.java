package com.xptschool.parent.ui.album;

import android.Manifest;
import android.content.Intent;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.widget.audiorecorder.MediaPlayerManager;
import com.android.widget.roundcornerprogressbar.RoundCornerProgressBar;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.util.FileDownloadUtils;
import com.xptschool.parent.R;
import com.xptschool.parent.bean.BeanHomeWork;
import com.xptschool.parent.common.ImageUtils;
import com.xptschool.parent.common.LocalFile;
import com.xptschool.parent.common.LocalImageHelper;
import com.xptschool.parent.common.StringUtils;
import com.xptschool.parent.ui.main.BaseActivity;
import com.xptschool.parent.view.imgloader.AlbumViewPager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

/**
 * Created by Administrator on 2016/10/29.
 */
@RuntimePermissions
public class AlbumActivity extends BaseActivity {

    public ScrollView mScrollView;
    public AlbumGridAdapter myPicGridAdapter;
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

    public String localAmrFile = null;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method
        Log.i(TAG, "onRequestPermissionsResult: " + permissions[0]);
        AlbumActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @NeedsPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void toLocalAlbum() {
        Log.i(TAG, "toLocalAlbum: ");
    }

    @OnPermissionDenied({Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void onStorageDenied() {
        // NOTE: Deal with a denied permission, e.g. by showing specific UI
        // or disabling certain functionality
        Toast.makeText(this, R.string.permission_storage_denied, Toast.LENGTH_SHORT).show();
    }

    @OnShowRationale({Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void showRationaleForStorage(PermissionRequest request) {
        // NOTE: Show a rationale to explain why the permission is needed, e.g. with a dialog.
        // Call proceed() or cancel() on the provided PermissionRequest to continue or abort
        request.proceed();
    }

    @OnNeverAskAgain({Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void onStorageNeverAskAgain() {
        Log.i(TAG, "onStorageNeverAskAgain: ");
        Toast.makeText(this, R.string.permission_storage_never_askagain, Toast.LENGTH_SHORT).show();
    }

    public void initVoice(BeanHomeWork homeWork) {
        //取amr文件
        String amr_file = homeWork.getAmr_file();
        Log.i(TAG, "amr file : " + amr_file);
        if (amr_file != null) {
            llVoice.setVisibility(View.VISIBLE);
            FileDownloader.getImpl().create(amr_file)
                    .setListener(createListener())
                    .setTag(1)
                    .start();
        } else {
            llVoice.setVisibility(View.GONE);
        }
    }

    public void initProgress(int progress) {
        voiceBar.setProgressBackgroundColor(this.getResources().getColor(R.color.colorPrimaryDark));
        voiceBar.setMax(progress);
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
                    // 播放录音
                    MediaPlayerManager.playSound(localAmrFile, new MediaPlayer.OnCompletionListener() {

                        public void onCompletion(MediaPlayer mp) {
                            //播放完成后改为播放状态
                            setImgMicStatus(Voice_Play);
                        }
                    });
                    //改为停止状态
                    setImgMicStatus(Voice_Stop);
                } else if (VoiceStatus == Voice_Stop) {
                    MediaPlayerManager.pause();
                    //改为播放状态
                    setImgMicStatus(Voice_Play);
                }
                break;
        }
    }

    //显示大图pager
    public void showViewPager(AlbumViewPager albumviewpager, int index) {
        if (albumviewpager == null)
            return;
        albumviewpager.setVisibility(View.VISIBLE);
        albumviewpager.setAdapter(albumviewpager.new LocalViewPagerAdapter(LocalImageHelper.getInstance().getCheckedItems()));
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

    @Override
    protected void onPause() {
        super.onPause();
        MediaPlayerManager.pause();
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
                initProgress(duration);
                //
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
        return duration;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalImageHelper.getInstance().clear();
    }

}
