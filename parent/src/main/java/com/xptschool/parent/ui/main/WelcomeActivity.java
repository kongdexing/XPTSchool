package com.xptschool.parent.ui.main;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.xptschool.parent.R;
import com.xptschool.parent.XPTApplication;
import com.xptschool.parent.common.CommonUtil;
import com.xptschool.parent.common.ExtraKey;
import com.xptschool.parent.common.SharedPreferencesUtil;
import com.xptschool.parent.ui.login.BaseLoginActivity;
import com.xptschool.parent.ui.login.LoginActivity;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class WelcomeActivity extends BaseLoginActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(XPTApplication.TAG, "WelcomeActivity onCreate: ");

        setContentView(R.layout.activity_welcome);
        llContent.setBackgroundColor(Color.TRANSPARENT);
        showActionBar(false);

        analyLogin();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (permissions != null && permissions.length > 0) {
            Log.i(TAG, "onRequestPermissionsResult: " + permissions[0]);
        }
        WelcomeActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @NeedsPermission({Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO})
    void canReadPhoneState() {
        Log.i(TAG, "canReadPhoneState: ");
        analyLogin();
    }

    private void analyLogin() {
        final Intent intent = new Intent();
        String userName = (String) SharedPreferencesUtil.getData(this, SharedPreferencesUtil.KEY_USER_NAME, "");
        String password = (String) SharedPreferencesUtil.getData(this, SharedPreferencesUtil.KEY_PWD, "");
        String splash_init = (String) SharedPreferencesUtil.getData(this, SharedPreferencesUtil.KEY_SPLASH_INIT, "0");

        if (splash_init.equals("0")) {
            Intent[] intents = new Intent[2];
            intents[1] = new Intent(this, SplashActivity.class);
            intents[0] = new Intent(this, LoginActivity.class);
            intents[0].putExtra(ExtraKey.LOGIN_ORIGIN, "0");
            startActivities(intents);
            finish();
            return;
        }

        if (password.isEmpty() || userName.isEmpty()) {
            intent.setClass(this, LoginActivity.class);
            intent.putExtra(ExtraKey.LOGIN_ORIGIN, "0");
            startActivity(intent);
            finish();
        } else {
            //login
            login(userName, password, new DefaultRetryPolicy(4 * 1000, 0, 1));
        }
    }

    @OnPermissionDenied({Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO})
    void onReadPhoneStateDenied() {
        Log.i(TAG, "onReadPhoneStateDenied: ");
        Toast.makeText(this, R.string.permission_readphonestate_denied, Toast.LENGTH_SHORT).show();
    }

    @OnShowRationale({Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO})
    void showRationaleForReadPhoneState(PermissionRequest request) {
        Log.i(TAG, "showRationaleForReadPhoneState: ");
        request.proceed();
    }

    @OnNeverAskAgain({Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO})
    void onReadPhoneStateNeverAskAgain() {
        Log.i(TAG, "onReadPhoneStateNeverAskAgain: ");
        Toast.makeText(this, R.string.permission_never_askagain, Toast.LENGTH_SHORT).show();
        CommonUtil.goAppDetailSettingIntent(this);
    }

    @Override
    protected void onStartLogin() {
        super.onStartLogin();
    }

    @Override
    protected void onLoginSuccess() {
        super.onLoginSuccess();
        startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
        finish();
    }

    @Override
    protected void onLoginFailed(String msg) {
        super.onLoginFailed(msg);
        startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
        finish();
    }

}
