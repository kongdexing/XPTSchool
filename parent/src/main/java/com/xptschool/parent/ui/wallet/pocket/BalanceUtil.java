package com.xptschool.parent.ui.wallet.pocket;

import android.util.Log;

import com.android.volley.VolleyError;
import com.android.volley.common.VolleyHttpParamsEntity;
import com.android.volley.common.VolleyHttpResult;
import com.android.volley.common.VolleyHttpService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xptschool.parent.bean.BeanHomeWork;
import com.xptschool.parent.common.CommonUtil;
import com.xptschool.parent.http.HttpAction;
import com.xptschool.parent.http.MyVolleyRequestListener;
import com.xptschool.parent.model.BeanStudent;
import com.xptschool.parent.model.GreenDaoHelper;
import com.xptschool.parent.ui.wallet.card.BeanCardBalance;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dexing on 2017/4/13.
 * No1
 */

public class BalanceUtil {

    private static String TAG = BalanceUtil.class.getSimpleName();
    private static String parentBalance = "";
    private static List<BeanCardBalance> cardBalances = new ArrayList<>();

    public interface BalanceCallBack {
        void onStart();

        void onSuccess();

        void onFailed(String error);
    }

    public static void getBalance(final BalanceCallBack callBack) {

        VolleyHttpService.getInstance().sendPostRequest(HttpAction.POCKET_BALANCE, new VolleyHttpParamsEntity()
                .addParam("token", CommonUtil.encryptToken(HttpAction.POCKET_BALANCE)), new MyVolleyRequestListener() {
            @Override
            public void onStart() {
                super.onStart();
                if (callBack != null) {
                    callBack.onStart();
                }
            }

            @Override
            public void onResponse(VolleyHttpResult volleyHttpResult) {
                super.onResponse(volleyHttpResult);
                switch (volleyHttpResult.getStatus()) {
                    case HttpAction.SUCCESS:
                        try {
                            JSONObject object = (JSONObject) volleyHttpResult.getData();
                            JSONObject parent = object.getJSONObject("parent");
                            parentBalance = parent.getString("account");
                            JSONArray students = object.getJSONArray("student");
                            Gson gson = new Gson();
                            cardBalances = gson.fromJson(students.toString(), new TypeToken<List<BeanCardBalance>>() {
                            }.getType());
                            if (callBack != null) {
                                callBack.onSuccess();
                            }
                        } catch (Exception ex) {
                            Log.i(TAG, "onResponse: error " + ex.getMessage());
                            if (callBack != null) {
                                callBack.onFailed(ex.getMessage());
                            }
                        }
                        break;
                    default:
                        if (callBack != null) {
                            callBack.onFailed(volleyHttpResult.getInfo());
                        }
                        break;
                }
            }

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                super.onErrorResponse(volleyError);
                if (callBack != null) {
                    callBack.onFailed("");
                }
            }
        });
    }

    public static String getParentBalance() {
        return parentBalance;
    }

    public static List<BeanCardBalance> getCardBalances() {
        if (cardBalances == null) {
            return new ArrayList<>();
        }
        List<BeanStudent> beanStudents = GreenDaoHelper.getInstance().getStudents();
        for (int i = 0; i < beanStudents.size(); i++) {
            for (int j = 0; j < cardBalances.size(); j++) {
                if (beanStudents.get(i).getStu_id().equals(cardBalances.get(j).getStu_id())) {
                    cardBalances.get(j).setStudent(beanStudents.get(i));
                }
            }
        }
        return cardBalances;
    }

    public static void parseJson() {

    }

}