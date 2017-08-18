package com.xptschool.parent.imsdroid;

import android.util.Log;

import com.xptschool.parent.BuildConfig;
import com.xptschool.parent.XPTApplication;
import com.xptschool.parent.model.BeanParent;
import com.xptschool.parent.model.GreenDaoHelper;

import org.doubango.ngn.services.INgnConfigurationService;
import org.doubango.ngn.services.INgnSipService;
import org.doubango.ngn.sip.NgnSipSession;
import org.doubango.ngn.utils.NgnConfigurationEntry;

/**
 * Created by dexing on 2017/8/17 0017.
 * sip 通话调控
 * No1
 */

public class ImsSipHelper {

    private static ImsSipHelper instance;
    private final INgnSipService mSipService;
    private String TAG = "NativeSip";

    private ImsSipHelper() {
        mSipService = getEngine().getSipService();
    }

    private Engine getEngine() {
        return (Engine) Engine.getInstance();
    }

    public static ImsSipHelper getInstance() {
        if (instance == null) {
            instance = new ImsSipHelper();
        }
        return instance;
    }

    public boolean isSipRegistered() {
        return mSipService.isRegistered();
    }

    public INgnSipService getSipService() {
        return mSipService;
    }

    private void SipConfigCommit() {
        BeanParent parent = GreenDaoHelper.getInstance().getCurrentParent();
        if (parent == null) {
            Log.i(TAG, "initNgnConfig: parent is null");
            return;
        }
        INgnConfigurationService mConfigurationService = getEngine().getConfigurationService();

        String userId = parent.getU_id();
        Log.i(TAG, "initNgnConfig userId: " + userId);

        mConfigurationService.putString(NgnConfigurationEntry.IDENTITY_DISPLAY_NAME, parent.getParent_name());
        mConfigurationService.putString(NgnConfigurationEntry.IDENTITY_IMPU, "sip:" + userId + "@" + BuildConfig.CHAT_VIDEO_URL);
        mConfigurationService.putString(NgnConfigurationEntry.IDENTITY_IMPI, userId);
        mConfigurationService.putString(NgnConfigurationEntry.IDENTITY_PASSWORD, "1234");

        mConfigurationService.putString(NgnConfigurationEntry.NETWORK_REALM, "sip:" + BuildConfig.CHAT_VIDEO_URL);
        mConfigurationService.putString(NgnConfigurationEntry.NETWORK_PCSCF_HOST, BuildConfig.CHAT_VIDEO_URL);
        mConfigurationService.putInt(NgnConfigurationEntry.NETWORK_PCSCF_PORT, NgnConfigurationEntry.DEFAULT_NETWORK_PCSCF_PORT);
        mConfigurationService.putString(NgnConfigurationEntry.NETWORK_TRANSPORT, "udp");

        mConfigurationService.putBoolean(NgnConfigurationEntry.NATT_STUN_DISCO, true);
        mConfigurationService.putString(NgnConfigurationEntry.NATT_STUN_SERVER, BuildConfig.CHAT_VIDEO_URL);
        mConfigurationService.putBoolean(NgnConfigurationEntry.NATT_USE_STUN_FOR_ICE, NgnConfigurationEntry.DEFAULT_NATT_USE_STUN_FOR_ICE);

        // Compute
        if (!mConfigurationService.commit()) {
            Log.e(TAG, "Failed to commit() configuration");
        }
    }

    public void startEngine() {
        SipConfigCommit();

        Log.i(TAG, "startEngine: ");
        final Engine engine = getEngine();
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (!engine.isStarted()) {
                    Log.i(TAG, "Starts the engine from the splash screen");
                    engine.start();
                }
            }
        });
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();
    }

    public void registerSipServer() {
        Log.i(TAG, "register");
        mSipService.register(XPTApplication.getContext());
    }

    public void unRegisterSipServer() {
        Log.i(TAG, "unRegister");
        mSipService.unRegister();
    }

    private void triggerSipServer() {
        if (mSipService.getRegistrationState() == NgnSipSession.ConnectionState.CONNECTING || mSipService.getRegistrationState() == NgnSipSession.ConnectionState.TERMINATING) {
            Log.i(TAG, "stopStack");
            mSipService.stopStack();
        } else if (mSipService.isRegistered()) {
            Log.i(TAG, "unRegister");
            mSipService.unRegister();
        } else {
            Log.i(TAG, "register");
            mSipService.register(XPTApplication.getContext());
        }
    }

}
