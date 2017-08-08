package com.xptschool.parent.ui.chat.video;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.cjt2325.cameralibrary.listener.ErrorListener;
import com.xptschool.parent.BuildConfig;
import com.xptschool.parent.R;
import com.xptschool.parent.common.CommonUtil;
import com.xptschool.parent.common.ExtraKey;
import com.xptschool.parent.model.ContactTeacher;
import com.xptschool.parent.util.ToastUtils;

import org.doubango.ngn.events.NgnInviteEventArgs;
import org.doubango.ngn.sip.NgnAVSession;
import org.doubango.ngn.sip.NgnInviteSession;
import org.doubango.ngn.utils.NgnConfigurationEntry;
import org.doubango.ngn.utils.NgnUriUtils;

import java.io.IOException;

import butterknife.BindView;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

/**
 * Created by dexing on 2017/6/13.
 * No1
 */
@RuntimePermissions
public class CallScreen extends CallBaseScreen {

    @BindView(R.id.screen_av_relativeLayout)
    RelativeLayout mMainLayout;
    private MediaPlayer mp = new MediaPlayer();
    private CallingView mViewInCallVideo;  //呼入界面

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        KeyguardManager km =
                (KeyguardManager) this.getSystemService(Context.KEYGUARD_SERVICE);
        boolean showingLocked = km.inKeyguardRestrictedInputMode();
        Log.i(TAG, "onCreate: showingLocked " + showingLocked);
        if (showingLocked) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                    | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        }
        setContentView(R.layout.screen_av);
        showActionBar(false);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String callType = extras.getString(ExtraKey.EXTRAT_CALL_TYPE);
            Log.i(TAG, "onCreate: callType " + callType);

            if ("outgoing".equals(callType)) {
                mSession = NgnAVSession.getSession(extras.getLong(ExtraKey.EXTRAT_SIP_SESSION_ID));
                contactTeacher = (ContactTeacher) extras.get(ExtraKey.EXTRAT_TEACHER_ID);
                final String validUri = NgnUriUtils.makeValidSipUri(String.format("sip:%s@%s", contactTeacher.getU_id(), BuildConfig.CHAT_VIDEO_URL));
                if (mSession == null || validUri == null) {
                    Toast.makeText(this, "呼叫失败", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                //开始呼叫
                mSession.makeCall(validUri);
                //push ios
                pushIOSCall(contactTeacher);
            } else if ("incoming".equals(callType)) {
                try {
                    long session_id = extras.getLong(ExtraKey.EXTRAT_SIP_SESSION_ID);
                    Log.i(TAG, "session_id : " + session_id + " session size: " + NgnAVSession.getSize());
                    mSession = NgnAVSession.getSession(session_id);
                    if (mSession == null) {
                        Log.e(TAG, "Null session");
                        finish();
                        return;
                    }
                    Log.i(TAG, "avSession: " + mSession.getRemotePartyDisplayName());
                } catch (Exception ex) {
                    Log.i(TAG, "onCreate: get avSession error " + ex.getMessage());
                }
            }
        }

        mSession.incRef();
        mSession.setContext(this);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(NgnInviteEventArgs.ACTION_INVITE_EVENT);
        intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(mSipBroadCastRecv, intentFilter);

        initOrientationListener();

        mSendDeviceInfo = mEngine.getConfigurationService().getBoolean(NgnConfigurationEntry.GENERAL_SEND_DEVICE_INFO, NgnConfigurationEntry.DEFAULT_GENERAL_SEND_DEVICE_INFO);
        mLastRotation = -1;
        mLastOrientation = -1;
        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
        Log.i(TAG, "onCreate: ");
        loadView();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (permissions != null && permissions.length > 0) {
            Log.i(TAG, "onRequestPermissionsResult: " + permissions[0]);
        }
        CallScreenPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @NeedsPermission({Manifest.permission.CAMERA})
    void canOpenCamera() {
        Log.i(TAG, "canOpenCamera: ");
//        loadView();
    }

    @OnPermissionDenied({Manifest.permission.CAMERA})
    void onOpenCameraDenied() {
        Log.i(TAG, "onOpenCameraDenied: ");
        Toast.makeText(this, R.string.permission_camera_denied, Toast.LENGTH_SHORT).show();
        hangUpCall();
    }

    @OnShowRationale({Manifest.permission.CAMERA})
    void showRationaleForOpenCamera(PermissionRequest request) {
        Log.i(TAG, "showRationaleForOpenCamera: ");
        request.proceed();
    }

    @OnNeverAskAgain({Manifest.permission.CAMERA})
    void onOpenCameraNeverAskAgain() {
        Log.i(TAG, "onOpenCameraNeverAskAgain: ");
        Toast.makeText(this, R.string.permission_camera_never_askagain, Toast.LENGTH_SHORT).show();
        CommonUtil.goAppDetailSettingIntent(this);
//        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mSession != null) {
            if (mSession.getState() == NgnInviteSession.InviteState.INCALL) {
                Log.i(TAG, "onResume: in calling");
                loadInCallVideoView();
            }
        }
    }

    private void loadView() {
        Log.i(TAG, "loadView: " + mSession.getState());
        switch (mSession.getState()) {
            case INCOMING:
            case INPROGRESS:
            case REMOTE_RINGING:
                loadTryingView();
                break;
            case INCALL:
            case EARLY_MEDIA:
//                loadInCallView();
                break;
            case NONE:
            case TERMINATING:
            case TERMINATED:
            default:
                break;
        }
    }

    private void loadTryingView() {
        Log.d(TAG, "loadTryingView()");

        TryingView mViewTrying = new TryingView(this, new ErrorListener() {
            @Override
            public void onError() {
                Log.i(TAG, "camera open onError: ");
                mHandler.sendEmptyMessage(1001);
            }
        });

        mViewTrying.setTryingClickListener(new TryingView.tryingClickListener() {
            @Override
            public void onHangUpClick() {
                hangUpCall();
            }

            @Override
            public void onAcceptClick() {
                acceptCall();
            }
        });
        switch (mSession.getState()) {
            case INCOMING:
                String remoteUri = mSession.getRemotePartyUri();
                String displayName = mSession.getRemotePartyDisplayName();
                String teacherId = remoteUri.substring(remoteUri.indexOf(":") + 1, remoteUri.indexOf("@"));
                Log.i(TAG, "onReceive: INCOMING " + teacherId + " " + displayName);
                mViewTrying.setTeacherId(teacherId, new TryingView.IncomingTeacherCallBack() {
                    @Override
                    public void onGetTeacher(ContactTeacher teacher) {
                        contactTeacher = teacher;
                    }
                });
                mViewTrying.isInCallingView(true);
                mViewTrying.mTvInfo.setText(getString(R.string.string_call_incoming));
                break;
            case INPROGRESS:
            case REMOTE_RINGING:
            case EARLY_MEDIA:
            default:
                mViewTrying.setCallingTeacher(contactTeacher);
                mViewTrying.isInCallingView(false);
                mViewTrying.mTvInfo.setText(getString(R.string.string_call_outgoing));
//                btPick.setVisibility(View.GONE);
                try {
                    AssetFileDescriptor fileDescriptor = CallScreen.this.getAssets().openFd("call_play.mp3");
                    mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mp.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(),
                            fileDescriptor.getLength());
                    mp.prepare();
                    mp.start();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp.start();
                    }
                });
                break;
        }

        mMainLayout.removeAllViews();
        mMainLayout.addView(mViewTrying);

        //判断权限
        CallScreenPermissionsDispatcher.canOpenCameraWithCheck(this);

    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.i(TAG, "handleMessage: " + msg.what);
            switch (msg.what) {
                case 1001:
                    ToastUtils.showToast(CallScreen.this, R.string.permission_open_camera_fail);
                    break;
            }
        }
    };

    private boolean hangUpCall() {
        super.hangUpCallToPush();
        if (mSession != null) {
            return mSession.hangUpCall();
        }
        return false;
    }

    private boolean acceptCall() {
        if (mSession != null) {
            return mSession.acceptCall();
        }
        return false;
    }

    public void loadInCallVideoView() {
        Log.d(TAG, "loadInCallVideoView()");
        if (mViewInCallVideo == null) {
            mViewInCallVideo = new CallingView(this);
        }

        if (mp != null) {
            mp.release();
        }

        mViewInCallVideo.setViewClickListener(new CallingView.CallingViewClickListener() {
            @Override
            public void onPreviewSwitch() {
                // Video Consumer
//                mViewInCallVideo.loadVideoPreview(mSession);
                // Video Producer
//                mViewInCallVideo.startStopVideo(mSession);
            }

            @Override
            public void onHangUpClick() {
                hangUpCall();
            }

            @Override
            public void onCameraSwitch() {
                mSession.toggleCamera();
            }
        });

        mMainLayout.removeAllViews();
        mMainLayout.addView(mViewInCallVideo);

        // Video Consumer
        mViewInCallVideo.loadVideoPreview(mSession);
        // Video Producer
        mViewInCallVideo.startStopVideo(mSession);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
        try {
            if (mSipBroadCastRecv != null) {
                unregisterReceiver(mSipBroadCastRecv);
                mSipBroadCastRecv = null;
            }

            if (mSession != null) {
                mSession.setContext(null);
                mSession.decRef();
            }

            if (mp != null) {
                mp.release();
            }

            mTimerTaskQoS.cancel();
        } catch (Exception ex) {
            Log.i(TAG, "onDestroy: " + ex.getMessage());
        }
    }

}
