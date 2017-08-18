package com.xptschool.parent.ui.chat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.app.TakePhotoImpl;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.InvokeParam;
import com.jph.takephoto.model.TContextWrap;
import com.jph.takephoto.model.TResult;
import com.jph.takephoto.model.TakePhotoOptions;
import com.jph.takephoto.permission.InvokeListener;
import com.jph.takephoto.permission.PermissionManager;
import com.jph.takephoto.permission.TakePhotoInvocationHandler;
import com.jph.takephoto.uitl.TFileUtils;
import com.xptschool.parent.BuildConfig;
import com.xptschool.parent.R;
import com.xptschool.parent.XPTApplication;
import com.xptschool.parent.common.ExtraKey;
import com.xptschool.parent.imsdroid.ImsSipHelper;
import com.xptschool.parent.imsdroid.NativeService;
import com.xptschool.parent.model.ContactTeacher;
import com.xptschool.parent.ui.chat.video.CallScreen;
import com.xptschool.parent.ui.main.BaseListActivity;
import com.xptschool.parent.util.ChatUtil;
import com.xptschool.parent.util.ToastUtils;

import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.media.NgnMediaType;
import org.doubango.ngn.services.INgnConfigurationService;
import org.doubango.ngn.services.INgnSipService;
import org.doubango.ngn.sip.NgnAVSession;
import org.doubango.ngn.utils.NgnUriUtils;

/**
 * Created by dexing on 2017/6/2.
 * No1
 */

public class ChatAppendixActivity extends BaseListActivity implements TakePhoto.TakeResultListener, InvokeListener {

    private TakePhoto takePhoto;
    private InvokeParam invokeParam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getTakePhoto().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        getTakePhoto().onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult requestCode: " + requestCode + "  resultCode:" + resultCode);

        if (requestCode == 1000) {
            if (resultCode == 1001) {
                //拍照
                String path = data.getStringExtra("path");
                takeSuccess(path, ChatUtil.TYPE_FILE, 0);
            } else if (resultCode == 1002) {
                //录像
                String path = data.getStringExtra("path");
                long duration = data.getLongExtra("duration", 0);
                videoSuccess(path, duration);
            } else if (resultCode == -1) {
                Log.i(TAG, " permission_open_camera_fail ");
                //摄像头权限未开启
                ToastUtils.showToast(this, R.string.permission_open_camera_fail);
//                Toast.makeText(this, R.string.permission_open_camera_fail, Toast.LENGTH_SHORT);
            }
        } else {
            getTakePhoto().onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.TPermissionType type = PermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.handlePermissionsResult(this, type, invokeParam, this);
    }

    /**
     * 获取TakePhoto实例
     *
     * @return
     */
    public TakePhoto getTakePhoto() {
        if (takePhoto == null) {
            takePhoto = (TakePhoto) TakePhotoInvocationHandler.of(this).bind(new TakePhotoImpl(this, this));
        }
        return takePhoto;
    }

    @Override
    public final void takeSuccess(TResult result) {
        takeSuccess(result.getImage().getCompressPath(), ChatUtil.TYPE_FILE, 0);
    }

    public void takeSuccess(String path, char type, long duration) {
        Log.i(TAG, "takeSuccess: " + path);

    }

    public void videoSuccess(String path, long duration) {
        takeSuccess(path, ChatUtil.TYPE_VIDEO, duration);
    }

    @Override
    public void takeFail(TResult result, String msg) {
        Log.i(TAG, "takeFail:" + msg);
    }

    @Override
    public void takeCancel() {
//        Log.i(TAG, getResources().getString(R.string.msg_operation_canceled));
    }

    @Override
    public PermissionManager.TPermissionType invoke(InvokeParam invokeParam) {
        PermissionManager.TPermissionType type = PermissionManager.checkPermission(TContextWrap.of(this), invokeParam.getMethod());
        if (PermissionManager.TPermissionType.WAIT.equals(type)) {
            this.invokeParam = invokeParam;
        }
        return type;
    }

    public void pickPhoto() {
        TFileUtils.setCacheFile(XPTApplication.getInstance().getCachePath());

        TakePhoto takePhoto = getTakePhoto();
        configCompress(takePhoto);
        configTakePhotoOption(takePhoto);
        takePhoto.onPickFromGallery();
    }

    public void takePhoto() {
        startActivityForResult(new Intent(this, RecordVideoActivity.class), 1000);
    }

    public void startVideo(ContactTeacher teacher) {
        if (!ImsSipHelper.getInstance().isSipRegistered()) {
            ImsSipHelper.getInstance().startEngine();
            Toast.makeText(this, "正在登录...", Toast.LENGTH_SHORT).show();
            return;
        }

        NgnAVSession avSession = NgnAVSession.createOutgoingSession(ImsSipHelper.getInstance().getSipService().getSipStack(),
                NgnMediaType.AudioVideo);
        Intent i = new Intent();
        i.setClass(this, CallScreen.class);
        i.putExtra(ExtraKey.EXTRAT_CALL_TYPE, "outgoing");
        i.putExtra(ExtraKey.EXTRAT_SIP_SESSION_ID, avSession.getId());
        i.putExtra(ExtraKey.EXTRAT_TEACHER_ID, teacher);
        startActivity(i);
    }

    private void configCompress(TakePhoto takePhoto) {
        int maxSize = 102400;
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

    //选择图片配置
    private void configTakePhotoOption(TakePhoto takePhoto) {
        TakePhotoOptions.Builder builder = new TakePhotoOptions.Builder();
        //使用自带相册
        builder.setWithOwnGallery(true);
        //
        builder.setCorrectImage(true);
        takePhoto.setTakePhotoOptions(builder.create());
    }


}
