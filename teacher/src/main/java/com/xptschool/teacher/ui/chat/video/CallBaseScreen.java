package com.xptschool.teacher.ui.chat.video;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xptschool.teacher.R;
import com.xptschool.teacher.model.ContactParent;
import com.xptschool.teacher.ui.chat.ChatAppendixActivity;
import com.xptschool.teacher.ui.main.BaseActivity;

import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.events.NgnInviteEventArgs;
import org.doubango.ngn.sip.NgnAVSession;
import org.doubango.ngn.sip.NgnInviteSession;
import org.doubango.ngn.utils.NgnStringUtils;
import org.doubango.ngn.utils.NgnTimer;
import org.doubango.ngn.utils.NgnUriUtils;
import org.doubango.tinyWRAP.QoS;

import java.util.TimerTask;

import butterknife.BindView;

/**
 * Created by dexing on 2017/6/13.
 * No1
 */

public class CallBaseScreen extends BaseActivity {

    private NgnEngine mEngine;
    public NgnAVSession mSession;
    private ContactParent contactParent;

    @BindView(R.id.screen_av_relativeLayout)
    RelativeLayout mMainLayout;

    private static int mCountBlankPacket = 0;

    private LayoutInflater mInflater;
    private NgnTimer mTimerInCall;
    private NgnTimer mTimerSuicide;
    private NgnTimer mTimerBlankPacket;
    private NgnTimer mTimerQoS;

    private TextView mTvQoS;
    private View mViewInCallVideo;
    private FrameLayout mViewLocalVideoPreview;
    private FrameLayout mViewRemoteVideoPreview;

    public CallBaseScreen() {
        super();
        mEngine = NgnEngine.getInstance();
        mTimerInCall = new NgnTimer();
        mTimerSuicide = new NgnTimer();
        mTimerBlankPacket = new NgnTimer();
        mTimerQoS = new NgnTimer();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_av);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mSession = NgnAVSession.getSession(extras.getLong(ChatAppendixActivity.EXTRAT_SIP_SESSION_ID));
            contactParent = (ContactParent) extras.get(ChatAppendixActivity.EXTRAT_PARENT_ID);
        }

        if (mSession == null) {
            Log.e(TAG, "Null session");
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
        Log.d(TAG, "onResume()");
        if (mSession != null) {
            final NgnInviteSession.InviteState callState = mSession.getState();
            if (callState == NgnInviteSession.InviteState.TERMINATING || callState == NgnInviteSession.InviteState.TERMINATED) {
                finish();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mSession != null) {
            mSession.hangUpCall();
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
                        mTimerBlankPacket.schedule(mTimerTaskBlankPacket, 0, 250);
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
                        case LOCAL_TRANSFER_TRYING: {
//                            if (mTvInfo != null) {
//                                mTvInfo.setText("Call Transfer: Initiated");
//                            }
                            Log.i(TAG, "handleSipEvent: Call Transfer: Initiated");
                            break;
                        }
                        case LOCAL_TRANSFER_FAILED: {
//                            if (mTvInfo != null) {
//                                mTvInfo.setText("Call Transfer: Failed");
//                            }
                            Log.i(TAG, "handleSipEvent: Call Transfer: Failed");
                            break;
                        }
                        case LOCAL_TRANSFER_ACCEPTED: {
//                            if (mTvInfo != null) {
//                                mTvInfo.setText("Call Transfer: Accepted");
//                            }
                            Log.i(TAG, "handleSipEvent: Call Transfer: Accepted");
                            break;
                        }
                        case LOCAL_TRANSFER_COMPLETED: {
//                            if (mTvInfo != null) {
//                                mTvInfo.setText("Call Transfer: Completed");
//                            }
                            Log.i(TAG, "handleSipEvent: Call Transfer: Completed");
                            break;
                        }
                        case LOCAL_TRANSFER_NOTIFY:
                        case REMOTE_TRANSFER_NOTIFY: {
//                            if (mTvInfo != null && mSession != null) {
//                                short sipCode = intent.getShortExtra(NgnInviteEventArgs.EXTRA_SIPCODE, (short) 0);
//
//                                mTvInfo.setText("Call Transfer: " + sipCode + " " + args.getPhrase());
//                                if (sipCode >= 300 && mSession.isLocalHeld()) {
//                                    mSession.resumeCall();
//                                }
//                            }
                            break;
                        }

                        case REMOTE_TRANSFER_REQUESTED: {
                            String referToUri = intent.getStringExtra(NgnInviteEventArgs.EXTRA_REFERTO_URI);
                            if (!NgnStringUtils.isNullOrEmpty(referToUri)) {
                                String referToName = NgnUriUtils.getDisplayName(referToUri);
                                if (!NgnStringUtils.isNullOrEmpty(referToName)) {
                                    Log.i(TAG, "handleSipEvent: show dialog");
                                }
                            }
                            break;
                        }

                        case REMOTE_TRANSFER_FAILED: {
//                            if (mTransferDialog != null) {
//                                mTransferDialog.cancel();
//                                mTransferDialog = null;
//                            }
//                            mAVTransfSession = null;
                            break;
                        }
                        case REMOTE_TRANSFER_COMPLETED: {
//                            if (mTransferDialog != null) {
//                                mTransferDialog.cancel();
//                                mTransferDialog = null;
//                            }
//                            if (mAVTransfSession != null) {
//                                mAVTransfSession.setContext(mSession.getContext());
//                                mSession = mAVTransfSession;
//                                mAVTransfSession = null;
//                                loadInCallView(true);
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
//        if (mRemotePartyPhoto != null) {
//            ivAvatar.setImageBitmap(mRemotePartyPhoto);
//        }

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
            mViewInCallVideo = LayoutInflater.from(this).inflate(R.layout.view_call_incall_video, null);
            mViewLocalVideoPreview = (FrameLayout) mViewInCallVideo.findViewById(R.id.view_call_incall_video_FrameLayout_local_video);
            mViewRemoteVideoPreview = (FrameLayout) mViewInCallVideo.findViewById(R.id.view_call_incall_video_FrameLayout_remote_video);
        }

        mMainLayout.removeAllViews();
        mMainLayout.addView(mViewInCallVideo);

        mTvQoS = (TextView) mViewInCallVideo.findViewById(R.id.view_call_incall_video_textView_QoS);

        // Video Consumer
        loadVideoPreview();

        // Video Producer
        startStopVideo(mSession.isSendingVideo());
//        mCurrentView = ViewType.ViewInCall;
    }

    public void loadVideoPreview() {
        mViewRemoteVideoPreview.removeAllViews();
        final View remotePreview = mSession.startVideoConsumerPreview();
        if (remotePreview != null) {
            final ViewParent viewParent = remotePreview.getParent();
            if (viewParent != null && viewParent instanceof ViewGroup) {
                ((ViewGroup) (viewParent)).removeView(remotePreview);
            }
            mViewRemoteVideoPreview.addView(remotePreview);
            mSession.setSpeakerphoneOn(true);
        }
    }

    private void startStopVideo(boolean bStart) {
        Log.d(TAG, "startStopVideo(" + bStart + ")");
//        if (!mIsVideoCall) {
//            return;
//        }

        mSession.setSendingVideo(bStart);

        if (mViewLocalVideoPreview != null) {
            mViewLocalVideoPreview.removeAllViews();
            if (bStart) {
                cancelBlankPacket();
                final View localPreview = mSession.startVideoProducerPreview();
                if (localPreview != null) {
                    final ViewParent viewParent = localPreview.getParent();
                    if (viewParent != null && viewParent instanceof ViewGroup) {
                        ((ViewGroup) (viewParent)).removeView(localPreview);
                    }
                    if (localPreview instanceof SurfaceView) {
                        ((SurfaceView) localPreview).setZOrderOnTop(true);
                    }
                    mViewLocalVideoPreview.addView(localPreview);
                    mViewLocalVideoPreview.bringChildToFront(localPreview);
                }
            }
            mViewLocalVideoPreview.setVisibility(bStart ? View.VISIBLE : View.GONE);
            mViewLocalVideoPreview.bringToFront();
        }
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

    private void cancelBlankPacket() {
        if (mTimerBlankPacket != null) {
            mTimerBlankPacket.cancel();
            mCountBlankPacket = 0;
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

    private final TimerTask mTimerTaskBlankPacket = new TimerTask() {
        @Override
        public void run() {
            Log.d(TAG, "Resending Blank Packet " + String.valueOf(mCountBlankPacket));
            if (mCountBlankPacket < 3) {
                if (mSession != null) {
                    mSession.pushBlankPacket();
                }
                mCountBlankPacket++;
            } else {
                cancel();
                mCountBlankPacket = 0;
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

            mTimerBlankPacket.cancel();
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
