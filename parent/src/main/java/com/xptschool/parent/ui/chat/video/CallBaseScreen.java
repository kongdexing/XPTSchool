package com.xptschool.parent.ui.chat.video;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.common.VolleyHttpParamsEntity;
import com.android.volley.common.VolleyHttpResult;
import com.android.volley.common.VolleyHttpService;
import com.xptschool.parent.http.HttpAction;
import com.xptschool.parent.http.MyVolleyRequestListener;
import com.xptschool.parent.model.ContactTeacher;
import com.xptschool.parent.ui.main.BaseActivity;

import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.events.NgnInviteEventArgs;
import org.doubango.ngn.sip.NgnAVSession;
import org.doubango.ngn.sip.NgnInviteSession;
import org.doubango.ngn.utils.NgnTimer;
import org.doubango.tinyWRAP.QoS;

import java.util.TimerTask;

/**
 * Created by dexing on 2017/6/21.
 * No1
 */

public class CallBaseScreen extends BaseActivity {

    public NgnEngine mEngine;
    public NgnAVSession mSession;
    public ContactTeacher contactTeacher;

    private NgnTimer mTimerInCall;
    private NgnTimer mTimerSuicide;
    private NgnTimer mTimerQoS;

    public CallBaseScreen() {
        super();
        mEngine = NgnEngine.getInstance();
        mTimerInCall = new NgnTimer();
        mTimerSuicide = new NgnTimer();
        mTimerQoS = new NgnTimer();
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

    public void pushIOSCall(ContactTeacher teacher) {
        VolleyHttpService.getInstance().sendPostRequest(HttpAction.VIDEO_CALL_IOS_PUSH,
                new VolleyHttpParamsEntity()
                        .addParam("user_id", teacher.getU_id())
                        .addParam("type", "0"), new MyVolleyRequestListener() {
                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onResponse(VolleyHttpResult volleyHttpResult) {
                        super.onResponse(volleyHttpResult);
                    }

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        super.onErrorResponse(volleyError);
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

    public final TimerTask mTimerTaskQoS = new TimerTask() {
        @Override
        public void run() {
            if (mSession != null) {
                final QoS qos = mSession.getQoSVideo();
                if (qos != null) {
                    try {
                        Log.i(TAG, "run: " + "Quality: 		" + (int) (qos.getQavg() * 100) + "%\n" +
                                "Receiving:		" + qos.getBandwidthDownKbps() + "Kbps\n" +
                                "Sending:		" + qos.getBandwidthUpKbps() + "Kbps\n" +
                                "Size in:	    " + qos.getVideoInWidth() + "x" + qos.getVideoInHeight() + "\n" +
                                "Size out:		" + qos.getVideoOutWidth() + "x" + qos.getVideoOutHeight() + "\n" +
                                "Fps in:        " + qos.getVideoInAvgFps() + "\n" +
                                "Encode time:   " + qos.getVideoEncAvgTime() + "ms / frame\n" +
                                "Decode time:   " + qos.getVideoDecAvgTime() + "ms / frame\n");
                    } catch (Exception ex) {
                        Log.i(TAG, "run: " + ex.getMessage());
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
            handleSipEvent(intent);
        }
    };

}
