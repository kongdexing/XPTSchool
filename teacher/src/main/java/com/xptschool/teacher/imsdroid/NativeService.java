/* Copyright (C) 2010-2011, Mamadou Diop.
*  Copyright (C) 2011, Doubango Telecom.
*
* Contact: Mamadou Diop <diopmamadou(at)doubango(dot)org>
*	
* This file is part of imsdroid Project (http://code.google.com/p/imsdroid)
*
* imsdroid is free software: you can redistribute it and/or modify it under the terms of 
* the GNU General Public License as published by the Free Software Foundation, either version 3 
* of the License, or (at your option) any later version.
*	
* imsdroid is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
* See the GNU General Public License for more details.
*	
* You should have received a copy of the GNU General Public License along 
* with this program; if not, write to the Free Software Foundation, Inc., 
* 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/
package com.xptschool.teacher.imsdroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;

import com.xptschool.teacher.BuildConfig;
import com.xptschool.teacher.R;
import com.xptschool.teacher.model.BeanTeacher;
import com.xptschool.teacher.model.GreenDaoHelper;
import com.xptschool.teacher.ui.chat.video.CallScreen;
import com.xptschool.teacher.ui.chat.video.InCallActivity;

import org.doubango.ngn.NgnNativeService;
import org.doubango.ngn.events.NgnEventArgs;
import org.doubango.ngn.events.NgnInviteEventArgs;
import org.doubango.ngn.events.NgnMessagingEventArgs;
import org.doubango.ngn.events.NgnMsrpEventArgs;
import org.doubango.ngn.events.NgnRegistrationEventArgs;
import org.doubango.ngn.events.NgnRegistrationEventTypes;
import org.doubango.ngn.media.NgnMediaType;
import org.doubango.ngn.services.INgnConfigurationService;
import org.doubango.ngn.services.INgnSipService;
import org.doubango.ngn.sip.NgnAVSession;
import org.doubango.ngn.sip.NgnMsrpSession;
import org.doubango.ngn.sip.NgnSipSession;
import org.doubango.ngn.utils.NgnConfigurationEntry;
import org.doubango.ngn.utils.NgnStringUtils;
import org.doubango.ngn.utils.NgnUriUtils;

import static com.xptschool.teacher.ui.chat.ChatAppendixActivity.EXTRAT_PARENT_ID;
import static com.xptschool.teacher.ui.chat.ChatAppendixActivity.EXTRAT_SIP_SESSION_ID;

public class NativeService extends NgnNativeService {
    private final static String TAG = NativeService.class.getCanonicalName();
    public static final String ACTION_STATE_EVENT = TAG + ".ACTION_STATE_EVENT";

    private PowerManager.WakeLock mWakeLock;
    private Engine mEngine;
    //login video chat server
    private INgnSipService mSipService;
    private INgnConfigurationService mConfigurationService;

    public NativeService() {
        super();
        mEngine = (Engine) Engine.getInstance();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");

        mSipService = getEngine().getSipService();
        this.mConfigurationService = getEngine().getConfigurationService();

        initNgnConfig();

        final PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (powerManager != null && mWakeLock == null) {
            mWakeLock = powerManager.newWakeLock(PowerManager.ON_AFTER_RELEASE
                    | PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                    | PowerManager.ACQUIRE_CAUSES_WAKEUP, TAG);
        }
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.d(TAG, "onStart()");
        //
        if (!Engine.getInstance().isStarted()) {
            final Engine engine = getEngine();
            final Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    if (!engine.isStarted()) {
                        Log.d(TAG, "Starts the engine from the splash screen");
                        engine.start();
                    }
                }
            });
            thread.setPriority(Thread.MAX_PRIORITY);
            thread.start();
        } else {
            registerVideoServer();
        }

        // register()

        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(NgnRegistrationEventArgs.ACTION_REGISTRATION_EVENT);
        intentFilter.addAction(NgnInviteEventArgs.ACTION_INVITE_EVENT);
        intentFilter.addAction(NgnMessagingEventArgs.ACTION_MESSAGING_EVENT);
        intentFilter.addAction(NgnMsrpEventArgs.ACTION_MSRP_EVENT);
        registerReceiver(mBroadcastReceiver, intentFilter);

        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null && bundle.getBoolean("autostarted")) {
                if (mEngine.start()) {
                    mEngine.getSipService().register(null);
                }
            }
        }

        // alert()
        final Intent i = new Intent(ACTION_STATE_EVENT);
        i.putExtra("started", true);
        sendBroadcast(i);
    }

    private Engine getEngine() {
        return (Engine) Engine.getInstance();
    }

    private void initNgnConfig() {
        BeanTeacher teacher = GreenDaoHelper.getInstance().getCurrentTeacher();
        if (teacher == null) {
            Log.i(TAG, "initNgnConfig: teacher is null");
            return;
        }

        mConfigurationService.putString(NgnConfigurationEntry.IDENTITY_DISPLAY_NAME, teacher.getName());
        mConfigurationService.putString(NgnConfigurationEntry.IDENTITY_IMPU, "sip:1008@" + BuildConfig.CHAT_VIDEO_URL);
        mConfigurationService.putString(NgnConfigurationEntry.IDENTITY_IMPI, "1008");
        mConfigurationService.putString(NgnConfigurationEntry.IDENTITY_PASSWORD, "1234");
        mConfigurationService.putString(NgnConfigurationEntry.NETWORK_REALM, "sip:" + BuildConfig.CHAT_VIDEO_URL);
        mConfigurationService.putString(NgnConfigurationEntry.NETWORK_PCSCF_HOST, BuildConfig.CHAT_VIDEO_URL);

//        // Compute
        if (!mConfigurationService.commit()) {
            Log.e(TAG, "Failed to commit() configuration");
        }
        Log.i(TAG, "initNgnConfig: ");
    }

    private void registerVideoServer() {
//        if (mSipService.getRegistrationState() == NgnSipSession.ConnectionState.CONNECTING || mSipService.getRegistrationState() == NgnSipSession.ConnectionState.TERMINATING) {
//            mSipService.stopStack();
//        } else if (mSipService.isRegistered()) {
//            mSipService.unRegister();
//        } else {
//        }
        Log.i(TAG, "registerVideoServer register");
        mSipService.register(this);
    }

    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @SuppressWarnings("incomplete-switch")
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            // Registration Events
            if (NgnRegistrationEventArgs.ACTION_REGISTRATION_EVENT.equals(action)) {
                NgnRegistrationEventArgs args = intent.getParcelableExtra(NgnEventArgs.EXTRA_EMBEDDED);
                final NgnRegistrationEventTypes type;
                if (args == null) {
                    Log.e(TAG, "Invalid event args");
                    return;
                }
                Log.i(TAG, "onReceive: " + args.getEventType());
                switch ((type = args.getEventType())) {
                    case REGISTRATION_OK:
                    case REGISTRATION_NOK:
                    case REGISTRATION_INPROGRESS:
                    case UNREGISTRATION_INPROGRESS:
                    case UNREGISTRATION_OK:
                    case UNREGISTRATION_NOK:
                    default:
                        final boolean bTrying = (type == NgnRegistrationEventTypes.REGISTRATION_INPROGRESS || type == NgnRegistrationEventTypes.UNREGISTRATION_INPROGRESS);
//                            if (mEngine.getSipService().isRegistered()) {
//                                mEngine.showAppNotif(bTrying ? R.drawable.bullet_ball_glass_grey_16 : R.drawable.bullet_ball_glass_green_16, null);
//                                XPTApplication.acquirePowerLock();
//                            } else {
//                                mEngine.showAppNotif(bTrying ? R.drawable.bullet_ball_glass_grey_16 : R.drawable.bullet_ball_glass_red_16, null);
//                                XPTApplication.releasePowerLock();
//                            }
                        break;
                }
            }

            // PagerMode Messaging Events
            if (NgnMessagingEventArgs.ACTION_MESSAGING_EVENT.equals(action)) {
                NgnMessagingEventArgs args = intent.getParcelableExtra(NgnMessagingEventArgs.EXTRA_EMBEDDED);
                if (args == null) {
                    Log.e(TAG, "Invalid event args");
                    return;
                }
                Log.i(TAG, "onReceive: pagerMode " + args.getEventType());
                switch (args.getEventType()) {
                    case INCOMING:
                        String dateString = intent.getStringExtra(NgnMessagingEventArgs.EXTRA_DATE);
                        String remoteParty = intent.getStringExtra(NgnMessagingEventArgs.EXTRA_REMOTE_PARTY);
                        if (NgnStringUtils.isNullOrEmpty(remoteParty)) {
                            remoteParty = NgnStringUtils.nullValue();
                        }
                        remoteParty = NgnUriUtils.getUserName(remoteParty);
//                            NgnHistorySMSEvent event = new NgnHistorySMSEvent(remoteParty, StatusType.Incoming);
//                            event.setContent(new String(args.getPayload()));
//                            event.setStartTime(NgnDateTimeUtils.parseDate(dateString).getTime());
//                            mEngine.getHistoryService().addEvent(event);
                        mEngine.showSMSNotif(R.drawable.sms_25, "New message");
                        break;
                }
            }

            // Invite Events
            if (NgnInviteEventArgs.ACTION_INVITE_EVENT.equals(action)) {
                NgnInviteEventArgs args = intent.getParcelableExtra(NgnEventArgs.EXTRA_EMBEDDED);
                if (args == null) {
                    Log.e(TAG, "Invalid event args");
                    return;
                }

                final NgnMediaType mediaType = args.getMediaType();

                switch (args.getEventType()) {
                    case TERMWAIT:
                    case TERMINATED:
                        if (NgnMediaType.isAudioVideoType(mediaType)) {
                            mEngine.refreshAVCallNotif(R.drawable.phone_call_25);
                            mEngine.getSoundService().stopRingBackTone();
                            mEngine.getSoundService().stopRingTone();
                        }
                        break;

                    case INCOMING:
                        if (NgnMediaType.isAudioVideoType(mediaType)) {
                            final NgnAVSession avSession = NgnAVSession.getSession(args.getSessionId());
                            if (avSession != null) {
                                mEngine.showAVCallNotif(R.drawable.phone_call_25, getString(R.string.string_call_incoming));
//                                    ScreenAV.receiveCall(avSession);
                                String remoteUri = avSession.getRemotePartyUri();
                                String parentId = remoteUri.substring(remoteUri.indexOf(":"), remoteUri.indexOf("@"));
                                Log.i(TAG, "onReceive: INCOMING " + parentId);
                                Intent i = new Intent();
                                i.setClass(NativeService.this, CallScreen.class);
                                i.putExtra(EXTRAT_SIP_SESSION_ID, avSession.getId());
//                                    i.putExtra(EXTRAT_PARENT_ID, null);
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);

                                if (mWakeLock != null && !mWakeLock.isHeld()) {
                                    mWakeLock.acquire(10);
                                }
//                                mEngine.getSoundService().startRingTone();

                            } else {
                                Log.e(TAG, String.format("Failed to find session with id=%ld", args.getSessionId()));
                            }
                        }
                        break;
                    case INPROGRESS:
                        if (NgnMediaType.isAudioVideoType(mediaType)) {
                            mEngine.showAVCallNotif(R.drawable.phone_call_25, getString(R.string.string_call_outgoing));
                        }
                        break;
                    case RINGING:
                        if (NgnMediaType.isAudioVideoType(mediaType)) {
                            mEngine.getSoundService().startRingBackTone();
                        }
                        break;
                    case CONNECTED:
                    case EARLY_MEDIA:
                        if (NgnMediaType.isAudioVideoType(mediaType)) {
                            mEngine.showAVCallNotif(R.drawable.phone_call_25, getString(R.string.string_incall));
                            mEngine.getSoundService().stopRingBackTone();
                            mEngine.getSoundService().stopRingTone();
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    };

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()");
        if (mBroadcastReceiver != null) {
            unregisterReceiver(mBroadcastReceiver);
            mBroadcastReceiver = null;
        }
        if (mWakeLock != null) {
            if (mWakeLock.isHeld()) {
                mWakeLock.release();
                mWakeLock = null;
            }
        }
        getEngine().stop();
        super.onDestroy();
    }
}
