package com.xptschool.teacher.ui.chat.video;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.xptschool.teacher.R;
import com.xptschool.teacher.common.BroadcastAction;
import com.xptschool.teacher.model.ContactParent;

import org.doubango.ngn.events.NgnInviteEventArgs;
import org.doubango.ngn.sip.NgnAVSession;
import org.doubango.ngn.sip.NgnInviteSession;
import org.doubango.ngn.utils.NgnConfigurationEntry;

import java.io.IOException;

import butterknife.BindView;

/**
 * Created by dexing on 2017/6/13.
 * No1
 */

public class CallScreen extends CallBaseScreen {

    @BindView(R.id.screen_av_relativeLayout)
    RelativeLayout mMainLayout;

    private CallingView mViewInCallVideo;
    private MediaPlayer mp = new MediaPlayer();

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
            String callType = extras.getString(EXTRAT_CALL_TYPE);
            Log.i(TAG, "onCreate: callType " + callType);

            if ("outgoing".equals(callType)) {
                mSession = NgnAVSession.getSession(extras.getLong(EXTRAT_SIP_SESSION_ID));
                contactParent = (ContactParent) extras.get(EXTRAT_PARENT_ID);
                pushIOSCall(contactParent);
            } else if ("incoming".equals(callType)) {
                try {
                    long session_id = extras.getLong(EXTRAT_SIP_SESSION_ID);
                    Log.i(TAG, "session_id : " + session_id + " session size: " + NgnAVSession.getSize());
                    mSession = NgnAVSession.getSession(session_id);
                    Log.i(TAG, "avSession: " + mSession.getRemotePartyDisplayName());
                } catch (Exception ex) {
                    Log.i(TAG, "onCreate: get avSession error " + ex.getMessage());
                }
            }
        }
//
        if (mSession == null) {
            Log.i(TAG, "Null session");
            finish();
            return;
        }

        mSession.incRef();
        mSession.setContext(this);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(NgnInviteEventArgs.ACTION_INVITE_EVENT);
        intentFilter.addAction(BroadcastAction.VIDEO_INCOMING);
        intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(mSipBroadCastRecv, intentFilter);

        initOrientationListener();

        mSendDeviceInfo = mEngine.getConfigurationService().getBoolean(NgnConfigurationEntry.GENERAL_SEND_DEVICE_INFO, NgnConfigurationEntry.DEFAULT_GENERAL_SEND_DEVICE_INFO);
        mLastRotation = -1;
        mLastOrientation = -1;

        loadView();

        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
    }

    //显示第三方来电，暂时不做此功能
    //2017-06-22
    public void showSecondInComing(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            String callType = extras.getString(EXTRAT_CALL_TYPE);
            Log.i(TAG, "onCreate: callType " + callType);

            if ("incoming".equals(callType)) {
                try {
                    long session_id = extras.getLong(EXTRAT_SIP_SESSION_ID);
                    Log.i(TAG, "session_id : " + session_id + " session size: " + NgnAVSession.getSize());
                    NgnAVSession secondSession = NgnAVSession.getSession(session_id);
                    Log.i(TAG, "secondSession: " + secondSession.getRemotePartyDisplayName());

                } catch (Exception ex) {
                    Log.i(TAG, "onCreate: get avSession error " + ex.getMessage());
                }
            }
        }

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

        TryingView mViewTrying = new TryingView(this);
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
                String parentId = remoteUri.substring(remoteUri.indexOf(":") + 1, remoteUri.indexOf("@"));
                Log.i(TAG, "onReceive: INCOMING " + parentId + " " + displayName);
                mViewTrying.setParentId(parentId, new TryingView.IncomingParentCallBack() {
                    @Override
                    public void onGetParent(ContactParent parent) {
                        contactParent = parent;
                    }
                });

                mViewTrying.isInCallingView(true);
                mViewTrying.mTvInfo.setText(getString(R.string.string_call_incoming));
                break;
            case INPROGRESS:
            case REMOTE_RINGING:
            case EARLY_MEDIA:
            default:
                mViewTrying.setCallingParent(contactParent);
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
    }

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
//                // Video Producer
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
//        mSession.setSendingVideo(mSession.isSendingVideo());
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
