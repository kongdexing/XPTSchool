package com.xptschool.teacher.server;

import android.util.Log;

import com.android.volley.VolleyError;
import com.android.volley.common.VolleyHttpParamsEntity;
import com.android.volley.common.VolleyHttpResult;
import com.android.volley.common.VolleyHttpService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xptschool.teacher.common.CommonUtil;
import com.xptschool.teacher.http.HttpAction;
import com.xptschool.teacher.http.MyVolleyRequestListener;
import com.xptschool.teacher.model.ContactParent;
import com.xptschool.teacher.model.ContactStudent;
import com.xptschool.teacher.model.ContactTeacher;
import com.xptschool.teacher.model.GreenDaoHelper;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dexing on 2017/6/20.
 * No1
 */

public class SyncHelper {

    private String TAG = SyncHelper.class.getSimpleName();
    private static SyncHelper mInstance;

    public interface SyncCallBack {
        void onSyncSuccess();

        void onSyncError();
    }

    private SyncHelper() {
    }

    public static SyncHelper getInstance() {
        synchronized (SyncHelper.class) {
            if (mInstance == null) {
                mInstance = new SyncHelper();
            }
        }
        return mInstance;
    }

    public void syncContacts(final SyncCallBack callBack) {
        VolleyHttpService.getInstance().sendPostRequest(HttpAction.MyContacts_QUERY, new VolleyHttpParamsEntity()
                        .addParam("s_id", GreenDaoHelper.getInstance().getCurrentTeacher().getS_id())
                        .addParam("a_id", GreenDaoHelper.getInstance().getCurrentTeacher().getA_id())
                        .addParam("token", CommonUtil.encryptToken(HttpAction.MyContacts_QUERY)),
                new MyVolleyRequestListener() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onResponse(VolleyHttpResult volleyHttpResult) {
                        super.onResponse(volleyHttpResult);
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
                                    //teacher
                                    GreenDaoHelper.getInstance().insertContactTeacher((List) listTeacher);
                                    //parent
                                    GreenDaoHelper.getInstance().deleteParentData();
                                    for (int i = 0; i < listStudent.size(); i++) {
                                        List<ContactParent> parents = ((ContactStudent) listStudent.get(i)).getParent();
                                        for (int j = 0; j < parents.size(); j++) {
                                            Log.i(TAG, "parent info : " + parents.get(j).toString());
                                        }
                                        GreenDaoHelper.getInstance().insertContactParent(((ContactStudent) listStudent.get(i)).getParent());
                                    }
                                    //student
                                    GreenDaoHelper.getInstance().insertContactStudent((List) listStudent);
                                    if (callBack != null) {
                                        callBack.onSyncSuccess();
                                    }
                                } catch (Exception ex) {
                                    Log.e(TAG, "onResponse: json " + ex.getMessage());
                                    if (callBack != null) {
                                        callBack.onSyncError();
                                    }
                                }
                                break;
                            default:
                                if (callBack != null) {
                                    callBack.onSyncError();
                                }
                                break;
                        }

                    }

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        if (callBack != null) {
                            callBack.onSyncError();
                        }
                    }
                });
    }

}
