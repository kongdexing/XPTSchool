package com.xptschool.parent.ui.main;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.common.VolleyHttpParamsEntity;
import com.android.volley.common.VolleyHttpResult;
import com.android.volley.common.VolleyHttpService;
import com.xptschool.parent.R;
import com.xptschool.parent.common.CommonUtil;
import com.xptschool.parent.common.ExtraKey;
import com.xptschool.parent.common.SharedPreferencesUtil;
import com.xptschool.parent.http.HttpAction;
import com.xptschool.parent.http.MyVolleyRequestListener;
import com.xptschool.parent.ui.cardset.ReadContractActivity;

import org.json.JSONObject;

public class WelcomeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_welcome);
        showActionBar(false);

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

    public void buttonOnClick(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                if (data == null) {
                    return;
                }
                try {
                    //处理返回的data,获取选择的联系人信息
                    Uri uri = data.getData();
                    String[] contacts = getPhoneContacts(uri);
                    if (contacts == null) {
                        Toast.makeText(this, "获取失败", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Toast.makeText(this, contacts[0] + " ： " + contacts[1], Toast.LENGTH_SHORT).show();

                } catch (Exception ex) {
                    Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private String[] getPhoneContacts(Uri uri) {
        try {
            String[] contact = new String[2];
            //得到ContentResolver对象
            ContentResolver cr = getContentResolver();
            //取得电话本中开始一项的光标
            Cursor cursor = cr.query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                //取得联系人姓名
                int nameFieldColumnIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                contact[0] = cursor.getString(nameFieldColumnIndex);
                //取得电话号码
                String ContactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

                Cursor phone = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Phone._ID + "=" + ContactId, null, null);

                if (phone != null) {
                    phone.moveToFirst();
                    int phoneNumberIndex = phone
                            .getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    contact[1] = phone.getString(phoneNumberIndex);
                }
                phone.close();
                cursor.close();
            } else {
                return null;
            }
            return contact;
        } catch (Exception ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void login(final String account, final String password) {
        VolleyHttpService.getInstance().sendPostRequest(HttpAction.LOGIN,
                new VolleyHttpParamsEntity()
                        .addParam("username", account)
                        .addParam("password", password)
                        .addParam("type", "4"),
                new MyVolleyRequestListener() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onResponse(VolleyHttpResult httpResult) {
                        super.onResponse(httpResult);
                        switch (httpResult.getStatus()) {
                            case HttpAction.SUCCESS:
                                try {
                                    JSONObject jsonData = new JSONObject(httpResult.getData().toString());
                                    CommonUtil.initBeanStudentByHttpResult(jsonData.getJSONArray("stuData").toString());
                                    CommonUtil.initParentInfoByHttpResult(jsonData.getJSONObject("login").toString());
                                    startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                                    finish();
                                } catch (Exception ex) {
                                    Log.i(TAG, "onResponse: error " + ex.getMessage());
                                    startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
                                    finish();
                                }
                                break;
                            default:
                                startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
                                finish();
                                break;
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, "onErrorResponse: " + error);
                        startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
                        finish();
                    }
                });
    }

}
