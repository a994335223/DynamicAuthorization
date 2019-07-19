package com.wq.dynamicauthorization;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.wq.dynamicsq.PermissionListener;
import com.wq.dynamicsq.PermissionsUtil;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestCemera();
            }
        });

        findViewById(R.id.btn_read_contact).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestReadContact();
            }
        });

        findViewById(R.id.btn_sms).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestSms();
            }
        });

        findViewById(R.id.btn_write_contact).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestWriteContact();
            }
        });
    }


    private void requestCemera() {
        PermissionsUtil.requestPermission(getApplication(),false, new PermissionListener() {
            @Override
            public void permissionGranted(@NonNull String[] permissions) {
                Toast.makeText(MainActivity.this, "摄像头授权成功", Toast.LENGTH_LONG).show();
            }

            @Override
            public void permissionDenied(@NonNull String[] permissions) {
                Toast.makeText(MainActivity.this, "摄像头授权失败", Toast.LENGTH_LONG).show();
            }
        }, Manifest.permission.CAMERA);
    }


    private void requestReadContact() {
        PermissionsUtil.TipInfo tip = new PermissionsUtil.TipInfo("呃~~~:", "看下你的通讯录，放心不会收集", "不让看", "不开不行了",true);
        PermissionsUtil.requestPermission(this, new PermissionListener() {
            @Override
            public void permissionGranted(@NonNull String[] permissions) {
                JSONArray arr = null;
                try {
                    arr = getContactInfo(MainActivity.this);
                    if (arr.length() == 0) {
                        Toast.makeText(MainActivity.this, "请确认通讯录不为空且有访问权限", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MainActivity.this, arr.toString(), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void permissionDenied(@NonNull String[] permissions) {
                Toast.makeText(MainActivity.this, "用户拒绝了读取通讯录权限", Toast.LENGTH_LONG).show();
            }
        }, new String[]{Manifest.permission.READ_CONTACTS}, true, tip);
    }

    private void requestSms() {

        PermissionsUtil.requestPermission(this, new PermissionListener() {
            @Override
            public void permissionGranted(@NonNull String[] permissions) {
                Toast.makeText(MainActivity.this, "访问消息", Toast.LENGTH_LONG).show();
            }

            @Override
            public void permissionDenied(@NonNull String[] permissions) {
                Toast.makeText(MainActivity.this, "用户拒绝了读取消息权限", Toast.LENGTH_LONG).show();
            }
        }, new String[]{Manifest.permission.READ_SMS}, false, null);
    }


    private void requestWriteContact() {
        PermissionsUtil.requestPermission(this, new PermissionListener() {
            @Override
            public void permissionGranted(@NonNull String[] permissions) {
                if (addContact("WQ", "13244859825")) {
                    Toast.makeText(MainActivity.this, "成功添加联系人", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, "添加联系人失败", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void permissionDenied(@NonNull String[] permissions) {
                Toast.makeText(MainActivity.this, "用户拒绝了写通讯录", Toast.LENGTH_LONG).show();
            }
        }, Manifest.permission.WRITE_CONTACTS);
    }


    private boolean addContact(String name, String phoneNumber) { // 一个添加联系人信息的例子
        ContentValues values = new ContentValues(); // 创建一个空的ContentValues
        Uri rawContactUri = getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, values);
        if (rawContactUri == null) {
            return false;
        }
        long rawContactId = ContentUris.parseId(rawContactUri);
        values.clear();
        values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);

        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);// 内容类型
        values.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, name);// 联系人名字
        getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);// 向联系人URI添加联系人名字
        values.clear();

        values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);

        values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNumber);// 联系人的电话号码
        values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);// 电话类型
        getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);// 向联系人电话号码URI添加电话号码
        values.clear();

        values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE);
        values.put(ContactsContract.CommonDataKinds.Email.DATA, "13244859825@163.com");// 联系人的Email地址
        values.put(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK);// 电子邮件的类型
        getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);// 向联系人Email URI添加Email数据

        return true;
    }

    private JSONArray getContactInfo(Activity activity) throws JSONException {
        ArrayList<ContactInfo> contacts = new ArrayList<>();// 获得通讯录信息 ，URI是ContactsContract.Contacts.CONTENT_URI
        String mimetype = "";
        int oldrid = -1;
        int contactId = -1;
        Cursor cursor = activity.getContentResolver().query(ContactsContract.Data.CONTENT_URI, null, null, null, ContactsContract.Data.RAW_CONTACT_ID);
        int numm = 0;
        ContactInfo contact = null;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int index = cursor.getColumnIndex(ContactsContract.Data.RAW_CONTACT_ID);
                if (index < 0) {
                    cursor.close();
                    return null;
                }
                contactId = cursor.getInt(index);
                if (oldrid != contactId) {
                    contact = new ContactInfo();
                    contacts.add(contact);
                    numm++;
                    oldrid = contactId;
                }
                mimetype = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.MIMETYPE)); // 取得mimetype类型

                /************* 获取通讯录中名字 **************/
                if (ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE.equals(mimetype)) {
                    StringBuilder name = new StringBuilder();
                    String firstName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME));
                    if (!TextUtils.isEmpty(firstName)) {
                        name.append(firstName);
                    }

                    String middleName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME));
                    if (!TextUtils.isEmpty(middleName)) {
                        name.append(middleName);
                    }

                    String lastname = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME));
                    if (!TextUtils.isEmpty(lastname)) {
                        name.append(lastname);
                    }

                    contact.n = name.toString();
                }

                /***************** 获取电话 ********************/
                if (ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE.equals(mimetype)) {
                    if (contact.p == null) {
                        contact.p = new ArrayList<>();
                    }
                    String mobile = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    if (!TextUtils.isEmpty(mobile)) {
                        contact.p.add(mobile);
                    }
                }

                /******************** 获取第一个邮件 *******************/
                if (ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE.equals(mimetype)) {
                    if (!TextUtils.isEmpty(contact.m)) {
                        continue;
                    }
                    String email = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                    contact.m = email == null ? "" : email;
                }

                /******************** 获取公司信息 *******************/
                if (ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE.equals(mimetype)) {
                    int orgType = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TYPE));  // 取出组织类型
                    if (orgType == ContactsContract.CommonDataKinds.Organization.TYPE_CUSTOM) { // 单位
                        String company = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.COMPANY));
                        contact.o = company == null ? "" : company;
                    }
                }

                /******************** 获取地址 *******************/
                if (ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE.equals(mimetype)) {
                    if (!TextUtils.isEmpty(contact.a)) {
                        continue;
                    }
                    int postalType = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE));// 取出类型
                    if (postalType == ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME) {// 住宅通讯地址
                        String homeStreet = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
                        contact.a = homeStreet == null ? "" : homeStreet;
                    }
                    if (postalType == ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK) { // 单位通讯地址
                        String street = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
                        contact.a = street == null ? "" : street;
                    }
                    if (postalType == ContactsContract.CommonDataKinds.StructuredPostal.TYPE_OTHER) {// 其他通讯地址
                        String otherStreet = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
                        contact.a = otherStreet == null ? "" : otherStreet;
                    }
                }
            }
        }
        cursor.close();
        JSONArray arr = new JSONArray();
        for (ContactInfo con : contacts) {
            StringBuilder sb = new StringBuilder();
            sb.append(con.n);
            if (con.p != null && con.p.size() > 0) {
                sb.append(con.p.get(0));
            }
            arr.put(sb.toString());
        }
        return arr;
    }
}
