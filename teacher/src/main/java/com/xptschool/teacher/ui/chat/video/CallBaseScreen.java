package com.xptschool.teacher.ui.chat.video;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xptschool.teacher.R;
import com.xptschool.teacher.model.ContactParent;
import com.xptschool.teacher.ui.main.BaseActivity;

import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.events.NgnInviteEventArgs;
import org.doubango.ngn.sip.NgnAVSession;
import org.doubango.ngn.sip.NgnInviteSession;
import org.doubango.ngn.utils.NgnTimer;
import org.doubango.tinyWRAP.QoS;

import java.util.TimerTask;

import butterknife.BindView;

/**
 * Created by dexing on 2017/6/13.
 * No1
 */

public class CallBaseScreen extends BaseActivity {

    public static String TAG = CallBaseScreen.class.getSimpleName();
    private NgnEngine mEngine;
    private NgnAVSession mSession;
    private ContactParent contactParent;

    @BindView(R.id.screen_av_relativeLayout)
    RelativeLayout mMainLayout;

    private NgnTimer mTimerInCall;
    private NgnTimer mTimerSuicide;
    private NgnTimer mTimerQoS;

    private TextView mTvQoS;
    private CallingView mViewInCallVideo;

    public final static String EXTRAT_SIP_SESSION_ID = "SipSession";
    public final static String EXTRAT_PARENT_ID = "Parent";
    public final static String EXTRAT_CALL_TYPE = "Call_Type";

    public CallBaseScreen() {
        super();
        mEngine = NgnEngine.getInstance();
        mTimerInCall = new NgnTimer();
        mTimerSuicide = new NgnTimer();
        mTimerQoS = new NgnTimer();
    }

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
//            wakeUpAndUnlock(this);
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
        registerReceiver(mSipBroadCastRecv, intentFilter);

        loadView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mSession != null) {
            final NgnInviteSession.InviteState callState = mSession.getState();
            Log.i(TAG, "onResume: " + callState);
            if (callState == NgnInviteSession.InviteState.TERMINATING || callState == NgnInviteSession.InviteState.TERMINATED) {
                finish();
            }
        }
    }

    // listen for audio/video session state
    BroadcastReceiver mSipBroadCastRecv = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            handleSipEvent(intent);
        }
    };

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

    private void handleSipEvent(Intent intent) {
        if (mSession == null) {
            Log.e(TAG, "Invalid session object");
            return;
        }
        final String action = intent.getAction();
        if (NgnInviteEventArgs.ACTION_INVITE_EVENT.equals(action)) {
            NgnInviteEventArgs args = intent.getParcelableExtra(NgnInviteEventArgs.EXTRA_EMBEDDED);
            if (args == null) {
                Log.e(TAG, "Invalid event args");
                return;
            }
            if (args.getSessionId() != mSession.getId()) {
                return;
            }

            final NgnInviteSession.InviteState callState = mSession.getState();
//            mTvInfo.setText(getStateDesc(callState));
            Log.i(TAG, "handleSipEvent: callState " + getStateDesc(callState));

            switch (callState) {
                case REMOTE_RINGING:
                    mEngine.getSoundService().startRingBackTone();
                    break;
                case INCOMING:
                    mEngine.getSoundService().startRingTone();
                    break;
                case EARLY_MEDIA:
                    //拨打成功
                    break;
                case INCALL:
                    mEngine.getSoundService().stopRingTone();
                    mEngine.getSoundService().stopRingBackTone();
                    mSession.setSpeakerphoneOn(false);

                    loadInCallVideoView();

                    if (mSession != null) {
                        applyCamRotation(mSession.compensCamRotation(true));
                        mTimerQoS.schedule(mTimerTaskQoS, 0, 3000);
                    }

                    switch (args.getEventType()) {
                        case REMOTE_DEVICE_INFO_CHANGED: {
                            Log.d(TAG, String.format("Remote device info changed: orientation: %s", mSession.getRemoteDeviceInfo().getOrientation()));
                            break;
                        }
                        case MEDIA_UPDATED: {
                            Log.i(TAG, "handleSipEvent: MEDIA_UPDATED");
//                            if ((mIsVideoCall = (mSession.getMediaType() == NgnMediaType.AudioVideo || mSession.getMediaType() == NgnMediaType.Video))) {
//                            loadInCallVideoView();
                            loadInCallVideoView();
//                            } else {
//                                loadInCallAudioView();
//                            }
                            break;
                        }
                        default: {
                            break;
                        }
                    }
                    break;
                case TERMINATING:
                case TERMINATED:
                    mEngine.getSoundService().stopRingTone();
                    mEngine.getSoundService().stopRingBackTone();
                    finish();
                    break;
                default:
                    break;
            }
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
                mViewTrying.isInCallingView(true);
                mViewTrying.mTvInfo.setText(getString(R.string.string_call_incoming));
                break;
            case INPROGRESS:
            case REMOTE_RINGING:
            case EARLY_MEDIA:
            default:
                mViewTrying.isInCallingView(false);
                mViewTrying.mTvInfo.setText(getString(R.string.string_call_outgoing));
//                btPick.setVisibility(View.GONE);
                break;
        }

        if (contactParent != null) {
            mViewTrying.tvRemote.setText(contactParent.getName());
        } else {

            mViewTrying.tvRemote.setText(mSession.getRemotePartyDisplayName());
        }

        mMainLayout.removeAllViews();
        mMainLayout.addView(mViewTrying);
    }

    private boolean hangUpCall() {
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

    private void loadInCallVideoView() {
        Log.d(TAG, "loadInCallVideoView()");
        if (mViewInCallVideo == null) {
            mViewInCallVideo = new CallingView(this);
        }

        mViewInCallVideo.setViewClickListener(new CallingView.CallingViewClickListener() {
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

        mTvQoS = mViewInCallVideo.txtQos;

        // Video Consumer
        mViewInCallVideo.loadVideoPreview(mSession);

        // Video Producer
        mViewInCallVideo.startStopVideo(mSession);
    }

    private void applyCamRotation(int rotation) {
        if (mSession != null) {
//            mLastRotation = rotation;
            // libYUV
            mSession.setRotation(rotation);

            // FFmpeg
            /*switch (rotation) {
                case 0:
				case 90:
					mAVSession.setRotation(rotation);
					mAVSession.setProducerFlipped(false);
					break;
				case 180:
					mAVSession.setRotation(0);
					mAVSession.setProducerFlipped(true);
					break;
				case 270:
					mAVSession.setRotation(90);
					mAVSession.setProducerFlipped(true);
					break;
				}*/
        }
    }

    private final TimerTask mTimerTaskQoS = new TimerTask() {
        @Override
        public void run() {
            if (mSession != null && mTvQoS != null) {
                synchronized (mTvQoS) {
                    final QoS qos = mSession.getQoSVideo();
                    if (qos != null) {
                        CallBaseScreen.this.runOnUiThread(new Runnable() {
                            public void run() {
                                try {
                                    mTvQoS.setText(
                                            "Quality: 		" + (int) (qos.getQavg() * 100) + "%\n" +
                                                    "Receiving:		" + qos.getBandwidthDownKbps() + "Kbps\n" +
                                                    "Sending:		" + qos.getBandwidthUpKbps() + "Kbps\n" +
                                                    "Size in:	    " + qos.getVideoInWidth() + "x" + qos.getVideoInHeight() + "\n" +
                                                    "Size out:		" + qos.getVideoOutWidth() + "x" + qos.getVideoOutHeight() + "\n" +
                                                    "Fps in:        " + qos.getVideoInAvgFps() + "\n" +
                                                    "Encode time:   " + qos.getVideoEncAvgTime() + "ms / frame\n" +
                                                    "Decode time:   " + qos.getVideoDecAvgTime() + "ms / frame\n"
                                    );
                                } catch (Exception e) {
                                }
                            }
                        });
                    }
                }
            }
        }
    };

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

            mTimerTaskQoS.cancel();
        } catch (Exception ex) {
            Log.i(TAG, "onDestroy: " + ex.getMessage());
        }
    }

    public String getStateDesc(NgnInviteSession.InviteState state) {
        switch (state) {
            case NONE:
            default:
                return "Unknown";
            case INCOMING:
                return "Incoming";
            case INPROGRESS:
                return "Inprogress";
            case REMOTE_RINGING:
                return "Ringing";
            case EARLY_MEDIA:
                return "Early media";
            case INCALL:
                return "In Call";
            case TERMINATING:
                return "Terminating";
            case TERMINATED:
                return "termibated";
        }
    }

}
