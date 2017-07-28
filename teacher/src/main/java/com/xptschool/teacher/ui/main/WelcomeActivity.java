package com.xptschool.teacher.ui.main;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.xptschool.teacher.R;
import com.xptschool.teacher.common.CommonUtil;
import com.xptschool.teacher.common.ExtraKey;
import com.xptschool.teacher.common.SharedPreferencesUtil;
import com.xptschool.teacher.ui.login.BaseLoginActivity;
import com.xptschool.teacher.ui.login.LoginActivity;
import com.xptschool.teacher.util.ToastUtils;

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

        setContentView(R.layout.activity_welcome);
        showActionBar(false);

        WelcomeActivityPermissionsDispatcher.canReadPhoneStateWithCheck(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method
        if (permissions != null && permissions.length > 0) {
            Log.i(TAG, "onRequestPermissionsResult: " + permissions[0]);
        }
        WelcomeActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @NeedsPermission({Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO})
    void canReadPhoneState() {
        Log.i(TAG, "canReadPhoneState: ");
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
            login(userName, password);
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
        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onLoginFailed(String msg) {
        super.onLoginFailed(msg);
        ToastUtils.showToast(this, msg);
        Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(ExtraKey.LOGIN_ORIGIN, "0");
        startActivity(intent);
        finish();
    }

}
