package com.xptschool.teacher.ui.chat.video;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.View;

import com.android.volley.VolleyError;
import com.android.volley.common.VolleyHttpParamsEntity;
import com.android.volley.common.VolleyHttpResult;
import com.android.volley.common.VolleyHttpService;
import com.android.volley.common.VolleyRequestListener;
import com.xptschool.teacher.common.BroadcastAction;
import com.xptschool.teacher.http.HttpAction;
import com.xptschool.teacher.http.MyVolleyRequestListener;
import com.xptschool.teacher.model.ContactParent;
import com.xptschool.teacher.ui.main.BaseActivity;

import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.events.NgnInviteEventArgs;
import org.doubango.ngn.sip.NgnAVSession;
import org.doubango.ngn.sip.NgnInviteSession;
import org.doubango.ngn.utils.NgnContentType;
import org.doubango.ngn.utils.NgnTimer;
import org.doubango.tinyWRAP.QoS;

import java.util.TimerTask;

/**
 * Created by dexing on 2017/6/20.
 * No1
 */

public class CallBaseScreen extends BaseActivity {

    public NgnEngine mEngine;
    public NgnAVSession mSession;
    public ContactParent contactParent;

    private NgnTimer mTimerInCall;
    private NgnTimer mTimerSuicide;
    private NgnTimer mTimerQoS;

    public final static String EXTRAT_SIP_SESSION_ID = "SipSession";
    public final static String EXTRAT_PARENT_ID = "Parent";
    public final static String EXTRAT_CALL_TYPE = "Call_Type";

    public boolean mSendDeviceInfo;
    public int mLastOrientation; // values: portrait, landscape...
    public static int mLastRotation; // values: degrees

    private OrientationEventListener mListener;

    public CallBaseScreen() {
        super();
        mEngine = NgnEngine.getInstance();
        mTimerInCall = new NgnTimer();
        mTimerSuicide = new NgnTimer();
        mTimerQoS = new NgnTimer();
    }

    @Override
    protected void onStart() {
        super.onStart();
        View decorView = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= 19) {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        } else {
            int option = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(option);
        }
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

        if (mListener != null && mListener.canDetectOrientation()) {
            mListener.enable();
        }
    }

    public void initOrientationListener() {
        mListener = new OrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL) {
            @Override
            public void onOrientationChanged(int orient) {
                try {
                    if ((orient > 345 || orient < 15) ||
                            (orient > 75 && orient < 105) ||
                            (orient > 165 && orient < 195) ||
                            (orient > 255 && orient < 285)) {
                        int rotation = mSession.compensCamRotation(true);
                        if (rotation != mLastRotation) {
                            Log.d(TAG, "Received Screen Orientation Change setRotation[" + String.valueOf(rotation) + "]");
                            applyCamRotation(rotation);
                            if (mSendDeviceInfo && mSession != null) {
                                final Configuration conf = getResources().getConfiguration();
                                if (conf.orientation != mLastOrientation) {
                                    mLastOrientation = conf.orientation;
                                    switch (mLastOrientation) {
                                        case Configuration.ORIENTATION_LANDSCAPE:
                                            mSession.sendInfo("orientation:landscape\r\nlang:fr-FR\r\n", NgnContentType.DOUBANGO_DEVICE_INFO);
                                            break;
                                        case Configuration.ORIENTATION_PORTRAIT:
                                            mSession.sendInfo("orientation:portrait\r\nlang:fr-FR\r\n", NgnContentType.DOUBANGO_DEVICE_INFO);
                                            break;
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        if (!mListener.canDetectOrientation()) {
            Log.w(TAG, "canDetectOrientation() is equal to false");
        }
    }

    public void pushIOSCall(ContactParent parent) {
        VolleyHttpService.getInstance().sendPostRequest(HttpAction.VIDEO_CALL_IOS_PUSH,
                new VolleyHttpParamsEntity()
                        .addParam("user_id", parent.getUser_id())
                        .addParam("type", "1"), new VolleyRequestListener() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onResponse(VolleyHttpResult volleyHttpResult) {
                    }

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                    }
                });
    }

    public void hangUpCallToPush() {
        if (contactParent == null) {
            return;
        }
        VolleyHttpService.getInstance().sendPostRequest(HttpAction.VIDEO_CALL_IOS_PUSH,
                new VolleyHttpParamsEntity()
                        .addParam("user_id", contactParent.getUser_id())
                        .addParam("type", "0"), new VolleyRequestListener() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onResponse(VolleyHttpResult volleyHttpResult) {
                    }

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                    }
                });
    }

    @Override
    public void onBackPressed() {
        KeyguardManager km =
                (KeyguardManager) this.getSystemService(Context.KEYGUARD_SERVICE);
        boolean showingLocked = km.inKeyguardRestrictedInputMode();
        Log.i(TAG, "onCreate: showingLocked " + showingLocked);
        if (showingLocked) {

        } else {
//            super.onBackPressed();
        }
    }

    private void handleSipEvent(Intent intent) {
        if (mSession == null) {
            Log.e(TAG, "Invalid session object");
            return;
        }
        final String action = intent.getAction();
        if (NgnInviteEventArgs.ACTION_INVITE_EVENT.equals(action)) {
            final NgnInviteEventArgs args = intent.getParcelableExtra(NgnInviteEventArgs.EXTRA_EMBEDDED);
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

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                loadInCallVideoView();
                            } catch (Exception ex) {

                            }

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
                        }
                    }, 2000);
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

    public void loadInCallVideoView() {

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

    public void showSecondInComing(Intent intent) {

    }

    public final TimerTask mTimerTaskQoS = new TimerTask() {
        @Override
        public void run() {
            if (mSession != null) {
                final QoS qos = mSession.getQoSVideo();
                if (qos != null) {
                    try {
//                        Log.i(TAG, "run: " + "Quality: 		" + (int) (qos.getQavg() * 100) + "%\n" +
//                                "Receiving:		" + qos.getBandwidthDownKbps() + "Kbps\n" +
//                                "Sending:		" + qos.getBandwidthUpKbps() + "Kbps\n" +
//                                "Size in:	    " + qos.getVideoInWidth() + "x" + qos.getVideoInHeight() + "\n" +
//                                "Size out:		" + qos.getVideoOutWidth() + "x" + qos.getVideoOutHeight() + "\n" +
//                                "Fps in:        " + qos.getVideoInAvgFps() + "\n" +
//                                "Encode time:   " + qos.getVideoEncAvgTime() + "ms / frame\n" +
//                                "Decode time:   " + qos.getVideoDecAvgTime() + "ms / frame\n");
                    } catch (Exception e) {
                    }
                }
            }
        }
    };

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

    // listen for audio/video session state
    BroadcastReceiver mSipBroadCastRecv = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(NgnInviteEventArgs.ACTION_INVITE_EVENT)) {
                handleSipEvent(intent);
            } else if (action.equals(BroadcastAction.VIDEO_INCOMING)) {
                showSecondInComing(intent);
            } else if (action.equals(Intent.ACTION_HEADSET_PLUG)) {
                AudioManager localAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                boolean isHeadsetOn = localAudioManager.isWiredHeadsetOn();
                Log.i(TAG, "onReceive isHeadsetOn: " + isHeadsetOn);
                mSession.setSpeakerphoneOn(!isHeadsetOn);
            }
        }
    };

}
