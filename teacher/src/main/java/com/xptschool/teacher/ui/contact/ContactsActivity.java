package com.xptschool.teacher.ui.contact;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.common.VolleyHttpParamsEntity;
import com.android.volley.common.VolleyHttpResult;
import com.android.volley.common.VolleyHttpService;
import com.android.widget.groupexpandable.FloatingGroupExpandableListView;
import com.android.widget.groupexpandable.WrapperExpandableListAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xptschool.teacher.R;
import com.xptschool.teacher.bean.ContactType;
import com.xptschool.teacher.common.CommonUtil;
import com.xptschool.teacher.http.HttpAction;
import com.xptschool.teacher.http.MyVolleyRequestListener;
import com.xptschool.teacher.model.ContactParent;
import com.xptschool.teacher.model.ContactStudent;
import com.xptschool.teacher.model.ContactTeacher;
import com.xptschool.teacher.model.GreenDaoHelper;
import com.xptschool.teacher.ui.main.BaseActivity;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

//通讯录
public class ContactsActivity extends BaseActivity {

    @BindView(R.id.edtContent)
    EditText edtContent;
    @BindView(R.id.progress)
    ProgressBar progress;
    @BindView(R.id.expandableview)
    FloatingGroupExpandableListView expandableview;
    @BindView(R.id.swipe_refresh_widget)
    SwipeRefreshLayout swipe_refresh_widget;
    WrapperExpandableListAdapter wrapperAdapter;
    ContactsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        setTitle(R.string.mine_contacts);
        initView();
    }

    private void initView() {
        adapter = new ContactsAdapter(this);
        wrapperAdapter = new WrapperExpandableListAdapter(adapter);
        expandableview.setAdapter(wrapperAdapter);
        swipe_refresh_widget.setColorSchemeColors(getResources().getIntArray(R.array.google_colors));
        swipe_refresh_widget.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getContacts();
            }
        });

        edtContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.i(TAG, "beforeTextChanged: ");
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.i(TAG, "onTextChanged: " + charSequence.toString());
                adapter.reloadByName(charSequence.toString());
                expandContactList();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.i(TAG, "afterTextChanged: ");
            }
        });

        ArrayList<Object> listTeacher = (ArrayList) GreenDaoHelper.getInstance().getContactTeacher();
        ArrayList<Object> listStudent = (ArrayList) GreenDaoHelper.getInstance().getContactStudent();
        for (int i = 0; i < listStudent.size(); i++) {
            ContactStudent student = (ContactStudent) listStudent.get(i);
            List<ContactParent> parents = GreenDaoHelper.getInstance().getStudentParentBySId(student.getStu_id());
            student.setParent(parents);
        }

        if (listTeacher.size() > 0 || listStudent.size() > 0) {
            setContact(listTeacher, listStudent);
        } else {
            getContacts();
        }
    }

    private void setContact(ArrayList<Object> listTeacher, ArrayList<Object> listStudent) {
        LinkedHashMap<String, ArrayList<Object>> listContacts = new LinkedHashMap<>();
        listContacts.put(ContactType.TEACHER.toString(), listTeacher);
        listContacts.put(ContactType.STUDENT.toString(), listStudent);
        adapter.loadContacts(listContacts);
        expandContactList();
    }

    private void expandContactList() {
        for (int i = 0; i < wrapperAdapter.getGroupCount(); i++) {
            expandableview.expandGroup(i);
        }
    }

    @OnClick({R.id.btnSearch})
    void viewClick(View view) {
        switch (view.getId()) {
            case R.id.btnSearch:
                String name = edtContent.getText().toString();
                adapter.reloadByName(name);
                expandContactList();
                break;
        }
    }

    private void getContacts() {
        VolleyHttpService.getInstance().sendPostRequest(HttpAction.MyContacts_QUERY, new VolleyHttpParamsEntity()
                        .addParam("s_id", GreenDaoHelper.getInstance().getCurrentTeacher().getS_id())
                        .addParam("a_id", GreenDaoHelper.getInstance().getCurrentTeacher().getA_id())
                        .addParam("token", CommonUtil.encryptToken(HttpAction.MyContacts_QUERY)),
                new MyVolleyRequestListener() {
                    @Override
                    public void onStart() {
                        if (swipe_refresh_widget != null) {
                            swipe_refresh_widget.setRefreshing(true);
                        }
                    }

                    @Override
                    public void onResponse(VolleyHttpResult volleyHttpResult) {
                        super.onResponse(volleyHttpResult);
                        if (swipe_refresh_widget != null) {
                            swipe_refresh_widget.setRefreshing(false);
                        }
                        switch (volleyHttpResult.getStatus()) {
                            case HttpAction.SUCCESS:
                                try {
                                    JSONObject jsonObject = new JSONObject(volleyHttpResult.getData().toString());
                                    Gson gson = new Gson();
                                    ArrayList<Object> listTeacher = gson.fromJson(jsonObject.getJSONArray("teacher").toString(),
                                            new TypeToken<List<ContactTeacher>>() {
                                            }.getType());

                                    ArrayList<Object> listStudent = gson.fromJson(jsonObject.getJSONArray("student").toString(),
                                            new TypeToken<List<ContactStudent>>() {
                                            }.getType());

                                    Log.i(TAG, "onResponse: listTeacher size " + listTeacher.size());
                                    Log.i(TAG, "onResponse: listStudent size " + listStudent.size());
                                    GreenDaoHelper.getInstance().insertContactTeacher((List) listTeacher);

                                    GreenDaoHelper.getInstance().deleteParentData();
                                    for (int i = 0; i < listStudent.size(); i++) {
                                        GreenDaoHelper.getInstance().insertContactParent(((ContactStudent) listStudent.get(i)).getParent());
                                    }
                                    GreenDaoHelper.getInstance().insertContactStudent((List) listStudent);

                                    setContact(listTeacher, listStudent);
                                } catch (Exception ex) {
                                    Log.e(TAG, "onResponse: json " + ex.getMessage());
                                }
                                break;
                            default:
                                Toast.makeText(ContactsActivity.this, volleyHttpResult.getInfo(), Toast.LENGTH_SHORT).show();
                                GreenDaoHelper.getInstance().deleteContacts();
                                break;
                        }

                    }

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        super.onErrorResponse(volleyError);
                        if (swipe_refresh_widget != null) {
                            swipe_refresh_widget.setRefreshing(false);
                        }
                    }
                });
    }

}
