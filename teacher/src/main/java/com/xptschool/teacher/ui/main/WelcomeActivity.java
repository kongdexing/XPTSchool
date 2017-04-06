package com.xptschool.teacher.ui.main;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.common.VolleyHttpParamsEntity;
import com.android.volley.common.VolleyHttpResult;
import com.android.volley.common.VolleyHttpService;
import com.google.gson.Gson;
import com.xptschool.teacher.R;
import com.xptschool.teacher.common.CommonUtil;
import com.xptschool.teacher.common.ExtraKey;
import com.xptschool.teacher.common.LocalImageHelper;
import com.xptschool.teacher.common.SharedPreferencesUtil;
import com.xptschool.teacher.http.HttpAction;
import com.xptschool.teacher.http.MyVolleyRequestListener;
import com.xptschool.teacher.model.BeanTeacher;
import com.xptschool.teacher.model.GreenDaoHelper;
import com.xptschool.teacher.ui.album.AlbumActivity;
import com.xptschool.teacher.ui.album.TakePhotoActivity;

import org.json.JSONObject;

import java.io.File;

import butterknife.OnClick;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class WelcomeActivity extends TakePhotoActivity {

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

    @NeedsPermission(Manifest.permission.READ_PHONE_STATE)
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

    @OnPermissionDenied(Manifest.permission.READ_PHONE_STATE)
    void onReadPhoneStateDenied() {
        Log.i(TAG, "onReadPhoneStateDenied: ");
        // NOTE: Deal with a denied permission, e.g. by showing specific UI
        // or disabling certain functionality
        Toast.makeText(this, R.string.permission_readphonestate_denied, Toast.LENGTH_SHORT).show();
    }

    @OnShowRationale(Manifest.permission.READ_PHONE_STATE)
    void showRationaleForReadPhoneState(PermissionRequest request) {
        // NOTE: Show a rationale to explain why the permission is needed, e.g. with a dialog.
        // Call proceed() or cancel() on the provided PermissionRequest to continue or abort
        Log.i(TAG, "showRationaleForReadPhoneState: ");
        request.proceed();
    }

    @OnNeverAskAgain(Manifest.permission.READ_PHONE_STATE)
    void onReadPhoneStateNeverAskAgain() {
        Log.i(TAG, "onReadPhoneStateNeverAskAgain: ");
        Toast.makeText(this, R.string.permission_readphonestate_never_askagain, Toast.LENGTH_SHORT).show();
    }

    @OnClick({R.id.takePhoto})
    void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.takePhoto:
//                try {
                //  拍照后保存图片的绝对路径
                String cameraPath = LocalImageHelper.getInstance().setCameraImgPath();
                File file = new File(cameraPath);
                if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
                Uri imageUri = Uri.fromFile(file);

//                    configTakePhotoOption(getTakePhoto());
                //getTakePhoto().onPickFromCaptureWithCrop(imageUri, getCropOptions());
                getTakePhoto().onPickFromCapture(imageUri);
//                } catch (Exception ex) {
//                    Toast.makeText(WelcomeActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
//                    Log.i(TAG, "onCameraClick: " + ex.getMessage());
//                }
                break;
        }
    }

    private void login(final String account, final String password) {
        VolleyHttpService.getInstance().sendPostRequest(HttpAction.LOGIN,
                new VolleyHttpParamsEntity()
                        .addParam("username", account)
                        .addParam("password", password)
                        .addParam("type", "3"),
                new MyVolleyRequestListener() {
                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onResponse(VolleyHttpResult httpResult) {
                        super.onResponse(httpResult);
                        switch (httpResult.getStatus()) {
                            case HttpAction.SUCCESS:
                                try {
                                    JSONObject jsonData = new JSONObject(httpResult.getData().toString());
                                    CommonUtil.getBeanClassesByHttpResult(jsonData.getJSONArray("class").toString());
                                    CommonUtil.getBeanCoursesByHttpResult(jsonData.getJSONArray("course").toString());
                                    JSONObject jsonLogin = jsonData.getJSONObject("login");
                                    Gson gson = new Gson();
                                    BeanTeacher teacher = gson.fromJson(jsonLogin.toString(), BeanTeacher.class);
                                    GreenDaoHelper.getInstance().insertTeacher(teacher);
                                    startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                                    finish();
                                } catch (Exception ex) {
                                    Log.i(TAG, "onResponse: error " + ex.getMessage());
                                    Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intent.putExtra(ExtraKey.LOGIN_ORIGIN, "0");
                                    startActivity(intent);
                                    finish();
                                }
                                break;
                            default:
                                Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.putExtra(ExtraKey.LOGIN_ORIGIN, "0");
                                startActivity(intent);
                                finish();
                                break;
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra(ExtraKey.LOGIN_ORIGIN, "0");
                        startActivity(intent);
                        finish();
                    }
                });
    }

}
