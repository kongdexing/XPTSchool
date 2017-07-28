package com.xptschool.parent.server;

import android.util.Log;

import com.android.volley.VolleyError;
import com.android.volley.common.VolleyHttpParamsEntity;
import com.android.volley.common.VolleyHttpResult;
import com.android.volley.common.VolleyHttpService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xptschool.parent.common.CommonUtil;
import com.xptschool.parent.http.HttpAction;
import com.xptschool.parent.http.MyVolleyRequestListener;
import com.xptschool.parent.model.ContactSchool;
import com.xptschool.parent.model.ContactTeacher;
import com.xptschool.parent.model.GreenDaoHelper;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dexing on 2017/6/20.
 * 同步服务
 * 1.来电时，如读取不到联系人信息，则同步联系人信息
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
        VolleyHttpService.getInstance().sendPostRequest(HttpAction.MyContacts_QUERY,
                new VolleyHttpParamsEntity()
                        .addParam("token", CommonUtil.encryptToken(HttpAction.MyContacts_QUERY)),
                new MyVolleyRequestListener() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onResponse(VolleyHttpResult volleyHttpResult) {
                        super.onResponse(volleyHttpResult);
                        try {
                            JSONObject jsonObject = new JSONObject(volleyHttpResult.getData().toString());
                            Gson gson = new Gson();
                            ArrayList<Object> listTeacher = gson.fromJson(jsonObject.getJSONArray("teacher").toString(),
                                    new TypeToken<List<ContactTeacher>>() {
                                    }.getType());

                            ArrayList<Object> listSchool = gson.fromJson(jsonObject.getJSONArray("school").toString(),
                                    new TypeToken<List<ContactSchool>>() {
                                    }.getType());

                            Log.i(TAG, "onResponse: listTeacher size " + listTeacher.size());
                            Log.i(TAG, "onResponse: listSchool size " + listSchool.size());
                            GreenDaoHelper.getInstance().insertContactTeacher((List) listTeacher);
                            GreenDaoHelper.getInstance().insertSchoolInfo((List) listSchool);

                            if (callBack!=null){
                                callBack.onSyncSuccess();
                            }
                        } catch (Exception ex) {
                            Log.e(TAG, "onResponse: json " + ex.getMessage());
                            if (callBack!=null){
                                callBack.onSyncError();
                            }
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        super.onErrorResponse(volleyError);
                        if (callBack!=null){
                            callBack.onSyncError();
                        }
                    }
                });
    }

}
