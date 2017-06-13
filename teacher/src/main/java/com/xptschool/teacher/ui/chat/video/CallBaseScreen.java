package com.xptschool.teacher.ui.chat.video;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.xptschool.teacher.R;
import com.xptschool.teacher.model.ContactParent;
import com.xptschool.teacher.ui.chat.ChatAppendixActivity;
import com.xptschool.teacher.ui.main.BaseActivity;

import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.events.NgnInviteEventArgs;
import org.doubango.ngn.sip.NgnAVSession;
import org.doubango.ngn.sip.NgnInviteSession;

import butterknife.BindView;

/**
 * Created by dexing on 2017/6/13.
 * No1
 */

public class CallBaseScreen extends BaseActivity {

    private NgnEngine mEngine;
    private NgnAVSession mSession;
    private ContactParent contactParent;
    @BindView(R.id.view_call_trying_textView_name)
    TextView mTvRemote;
    @BindView(R.id.view_call_trying_imageButton_hang)
    ImageView mBtHangUp;

    public CallBaseScreen() {
        super();
        mEngine = NgnEngine.getInstance();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_call_trying);

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

        mBtHangUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSession != null) {
                    mSession.hangUpCall();
                }
            }
        });

        mTvRemote.setText(contactParent.getName());
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
            switch (callState) {
                case REMOTE_RINGING:
                    mEngine.getSoundService().startRingBackTone();
                    break;
                case INCOMING:
                    mEngine.getSoundService().startRingTone();
                    break;
                case EARLY_MEDIA:
                case INCALL:
                    mEngine.getSoundService().stopRingTone();
                    mEngine.getSoundService().stopRingBackTone();
                    mSession.setSpeakerphoneOn(false);
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

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy()");
        if (mSipBroadCastRecv != null) {
            unregisterReceiver(mSipBroadCastRecv);
            mSipBroadCastRecv = null;
        }

        if (mSession != null) {
            mSession.setContext(null);
            mSession.decRef();
        }
        super.onDestroy();
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
