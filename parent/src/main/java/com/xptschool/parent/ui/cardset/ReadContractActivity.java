package com.xptschool.parent.ui.cardset;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.xptschool.parent.R;
import com.xptschool.parent.ui.main.BaseActivity;

public class ReadContractActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_contract);
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


}
