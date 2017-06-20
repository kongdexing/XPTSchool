package com.xptschool.teacher.ui.chat;

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
import com.xptschool.teacher.BuildConfig;
import com.xptschool.teacher.XPTApplication;
import com.xptschool.teacher.model.ContactParent;
import com.xptschool.teacher.ui.chat.video.CallBaseScreen;
import com.xptschool.teacher.ui.main.BaseListActivity;
import com.xptschool.teacher.util.ChatUtil;

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
    private NgnEngine mEngine;
    private INgnConfigurationService mConfigurationService;
    private INgnSipService mSipService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getTakePhoto().onCreate(savedInstanceState);
        TFileUtils.setCacheFile(XPTApplication.getInstance().getCachePath());

        mEngine = NgnEngine.getInstance();
        mConfigurationService = mEngine.getConfigurationService();
        mSipService = mEngine.getSipService();

        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        getTakePhoto().onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1000) {
            //拍照，录像
            if (resultCode == 1001) {
                String path = data.getStringExtra("path");
                takeSuccess(path, ChatUtil.TYPE_FILE, 0);
            } else if (resultCode == 1002) {
                String path = data.getStringExtra("path");
                long duration = data.getLongExtra("duration", 0);
                videoSuccess(path, duration);
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
        Log.i(TAG, getResources().getString(com.jph.takephoto.R.string.msg_operation_canceled));
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
        TakePhoto takePhoto = getTakePhoto();
        configCompress(takePhoto);
        configTakePhotoOption(takePhoto);
        takePhoto.onPickFromGallery();
    }

    public void takePhoto() {
        startActivityForResult(new Intent(this, RecordVideoActivity.class), 1000);
    }

    public boolean startVideo(ContactParent parent) {
        if (!mSipService.isRegistered()) {
            Toast.makeText(this, "登录失败", Toast.LENGTH_SHORT).show();
            return false;
        }
        final String validUri = NgnUriUtils.makeValidSipUri(String.format("sip:%s@%s", parent.getUser_id(), BuildConfig.CHAT_VIDEO_URL));
        if (validUri == null) {
            Toast.makeText(this, "呼叫失败", Toast.LENGTH_SHORT).show();
//            mTvLog.setText("failed to normalize sip uri '" + phoneNumber + "'");
            return false;
        }
        NgnAVSession avSession = NgnAVSession.createOutgoingSession(mSipService.getSipStack(), NgnMediaType.AudioVideo);
        Intent i = new Intent();
        i.setClass(this, CallBaseScreen.class);
        i.putExtra(CallBaseScreen.EXTRAT_CALL_TYPE, "outgoing");
        i.putExtra(CallBaseScreen.EXTRAT_SIP_SESSION_ID, avSession.getId());
        i.putExtra(CallBaseScreen.EXTRAT_PARENT_ID, parent);
        startActivity(i);
        return avSession.makeCall(validUri);
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


}
