package com.xptschool.parent.ui.cardset;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.xptschool.parent.R;
import com.xptschool.parent.ui.main.BaseActivity;
import com.xptschool.parent.util.ContractClickListener;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

/**
 * Created by dexing on 2017/5/20.
 * No1
 */
@RuntimePermissions
public class ContractClickActivity extends BaseActivity {

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (permissions != null && permissions.length > 0) {
            Log.i(TAG, "onRequestPermissionsResult: " + permissions[0]);
        }
        ContractClickActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
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
        Toast.makeText(this, R.string.permission_contracts_denied, Toast.LENGTH_SHORT).show();
    }

    @OnShowRationale(Manifest.permission.READ_CONTACTS)
    void showRationaleForContacts(PermissionRequest request) {
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
                    onChooseContractResult(contacts);
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

    public void onChooseContractResult(String[] contacts) {

    }

    public ContractClickListener contractChooseListener = new ContractClickListener() {
        @Override
        public void onContractClick() {
            ContractClickActivityPermissionsDispatcher.goToChooseContractsWithCheck(ContractClickActivity.this);
        }
    };
}
