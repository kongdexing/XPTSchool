package com.xptschool.parent.ui.cardset;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.common.VolleyHttpParamsEntity;
import com.android.volley.common.VolleyHttpResult;
import com.android.volley.common.VolleyHttpService;
import com.xptschool.parent.R;
import com.xptschool.parent.common.BroadcastAction;
import com.xptschool.parent.common.CommonUtil;
import com.xptschool.parent.common.SharedPreferencesUtil;
import com.xptschool.parent.http.HttpAction;
import com.xptschool.parent.http.MyVolleyRequestListener;
import com.xptschool.parent.model.BeanStudent;
import com.xptschool.parent.model.GreenDaoHelper;
import com.xptschool.parent.ui.main.BaseActivity;
import com.xptschool.parent.util.ContractClickListener;
import com.xptschool.parent.view.CustomDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class CardSetBaseActivity extends BaseActivity {

    public Button btnSubmit;
    public static String CARD_SOS = "sos";
    public static String CARD_WHITELIST = "whitelist";
    public static String CARD_MONITER = "monitor";
    public String CardType = "";
    public BeanStudent currentStudent;
    public String spKey = "";

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method
        if (permissions != null && permissions.length > 0) {
            Log.i(TAG, "onRequestPermissionsResult: " + permissions[0]);
        }
        CardSetBaseActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @NeedsPermission(Manifest.permission.READ_CONTACTS)
    void goToChooseContracts() {
        Log.i(TAG, "goToChooseContracts: ");

        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(intent, 0);
    }

    @OnPermissionDenied(Manifest.permission.READ_CONTACTS)
    void onContactsDenied() {
        Log.i(TAG, "onContactsDenied: ");
        // NOTE: Deal with a denied permission, e.g. by showing specific UI
        // or disabling certain functionality
        Toast.makeText(this, R.string.permission_contracts_denied, Toast.LENGTH_SHORT).show();
    }

    @OnShowRationale(Manifest.permission.READ_CONTACTS)
    void showRationaleForContacts(PermissionRequest request) {
        // NOTE: Show a rationale to explain why the permission is needed, e.g. with a dialog.
        // Call proceed() or cancel() on the provided PermissionRequest to continue or abort
        Log.i(TAG, "showRationaleForContacts: ");
        request.proceed();
    }

    @OnNeverAskAgain(Manifest.permission.READ_CONTACTS)
    void onContactsNeverAskAgain() {
        Log.i(TAG, "onContactsNeverAskAgain: ");
        Toast.makeText(this, R.string.permission_contracts_never_askagain, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
                        Toast.makeText(this, R.string.toast_get_phone_null, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Intent intent = new Intent();
                    if (CardType.equals(CARD_SOS)) {
                        intent.setAction(BroadcastAction.SOS_CONTACTS);
                        intent.putExtra("phone", contacts[1]);
                    } else if (CardType.equals(CARD_WHITELIST)) {
                        intent.setAction(BroadcastAction.WHITELIST_CONTACTS);
                        intent.putExtra("name", contacts[0]);
                        intent.putExtra("phone", contacts[1]);
                    } else if (CardType.equals(CARD_MONITER)) {
                        intent.setAction(BroadcastAction.WHITELIST_CONTACTS);
                        intent.putExtra("name", contacts[0]);
                        intent.putExtra("phone", contacts[1]);
                    }
                    sendBroadcast(intent);
                } catch (Exception ex) {
                    Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
                break;
        }
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

    public void setCardPhone(final String values) {
        sendPhoneNumber(values);
    }

    public void getCardPhone() {
        VolleyHttpService.getInstance().sendPostRequest(HttpAction.GetCard_Phone,
                new VolleyHttpParamsEntity()
                        .addParam("stu_id", currentStudent.getStu_id())
                        .addParam("token", CommonUtil.encryptToken(HttpAction.GetCard_Phone)),
                new MyVolleyRequestListener() {
                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onResponse(VolleyHttpResult volleyHttpResult) {
                        super.onResponse(volleyHttpResult);
                        switch (volleyHttpResult.getStatus()) {
                            case HttpAction.SUCCESS:
                                try {
                                    JSONArray jsonArray = new JSONArray(volleyHttpResult.getData().toString());
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject object = jsonArray.getJSONObject(i);
                                        String type = object.getString("d_type");
                                        String value = object.getString("d_value");
                                        if (CardType.equals(type)) {
                                            if (CardType.equals(CARD_SOS)) {
                                                currentStudent.setSos(value);
                                            } else if (CardType.equals(CARD_WHITELIST)) {
                                                currentStudent.setWhitelist(value);
                                            } else if (CardType.equals(CARD_MONITER)) {
                                                currentStudent.setMonitor(value);
                                            }
                                            setViewData(value);
                                            GreenDaoHelper.getInstance().updateStudent(currentStudent);
                                        }
                                    }
                                } catch (Exception ex) {
                                    Log.i(TAG, "getCardPhone onResponse: " + ex.getMessage());
                                    Toast.makeText(CardSetBaseActivity.this, "获取学生卡号码失败", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            default:
                                Toast.makeText(CardSetBaseActivity.this, volleyHttpResult.getInfo(), Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        super.onErrorResponse(volleyError);
                    }
                });
    }

    private void sendPhoneNumber(final String values) {
        VolleyHttpService.getInstance().sendPostRequest(HttpAction.SetCard_Phone, new VolleyHttpParamsEntity()
                        .addParam("stu_id", currentStudent.getStu_id())
                        .addParam("typeFlag", CardType)
                        .addParam("s_id", currentStudent.getS_id())
                        .addParam("a_id", currentStudent.getA_id())
                        .addParam("g_id", currentStudent.getA_id())
                        .addParam("c_id", currentStudent.getC_id())
                        .addParam("imei", currentStudent.getImei_id())
                        .addParam(CardType, values)
                        .addParam("token", CommonUtil.encryptToken(HttpAction.SetCard_Phone)),
                new MyVolleyRequestListener() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        showProgress(R.string.progress_loading_cn);
                        if (btnSubmit != null) {
                            btnSubmit.setEnabled(false);
                        }
                    }

                    @Override
                    public void onResponse(VolleyHttpResult volleyHttpResult) {
                        hideProgress();
                        if (btnSubmit != null) {
                            btnSubmit.setEnabled(true);
                        }
                        super.onResponse(volleyHttpResult);
                        switch (volleyHttpResult.getStatus()) {
                            case HttpAction.SUCCESS:
                                Toast.makeText(CardSetBaseActivity.this, volleyHttpResult.getInfo(), Toast.LENGTH_SHORT).show();
                                //保存在本地
                                if (CardType.equals(CARD_SOS)) {
                                    currentStudent.setSos(values);
                                } else if (CardType.equals(CARD_WHITELIST)) {
                                    currentStudent.setWhitelist(values);
                                } else if (CardType.equals(CARD_MONITER)) {
                                    currentStudent.setMonitor(values);
                                }
                                SharedPreferencesUtil.saveData(CardSetBaseActivity.this, spKey, System.currentTimeMillis() + "");
                                GreenDaoHelper.getInstance().updateStudent(currentStudent);
                                break;
                            case HttpAction.FAILED:
                                try {
                                    JSONObject jsonObject = new JSONObject(volleyHttpResult.getData().toString());
                                    final String phone = jsonObject.getString("phone");
//                                    String d_value = jsonObject.getString("d_value");
                                    final String content = jsonObject.getString("content");

                                    CustomDialog dialog = new CustomDialog(CardSetBaseActivity.this);
                                    dialog.setTitle(R.string.label_cardset);
                                    dialog.setMessage(R.string.label_card_sendsms);
                                    dialog.setAlertDialogClickListener(new CustomDialog.DialogClickListener() {
                                        @Override
                                        public void onPositiveClick() {
                                            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + phone));
                                            intent.putExtra("sms_body", content);
                                            startActivity(intent);
                                        }
                                    });
                                } catch (Exception ex) {
                                    if (volleyHttpResult.getInfo() != null) {
                                        Toast.makeText(CardSetBaseActivity.this, volleyHttpResult.getInfo(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                                break;
                            default:
                                Toast.makeText(CardSetBaseActivity.this, volleyHttpResult.getInfo(), Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        hideProgress();
                        if (btnSubmit != null) {
                            btnSubmit.setEnabled(true);
                        }
                        super.onErrorResponse(volleyError);
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public ContractClickListener contractChooseListener = new ContractClickListener() {
        @Override
        public void onContractClick() {
            CardSetBaseActivityPermissionsDispatcher.goToChooseContractsWithCheck(CardSetBaseActivity.this);
        }
    };

    protected void setViewData(String value) {
        if (currentStudent != null) {
            String parentId = GreenDaoHelper.getInstance().getCurrentParent().getU_id();
            spKey = parentId + currentStudent.getStu_id() + CardType;
        }
        Log.i(TAG, "setViewData:  " + spKey);
    }

}
