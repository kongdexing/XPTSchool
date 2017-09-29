package com.xptschool.parent.push;

import android.content.Intent;
import android.util.Log;

import com.android.volley.VolleyError;
import com.android.volley.common.VolleyHttpResult;
import com.android.volley.common.VolleyHttpService;
import com.xptschool.parent.XPTApplication;
import com.xptschool.parent.common.BroadcastAction;
import com.xptschool.parent.common.CommonUtil;
import com.xptschool.parent.http.HttpAction;
import com.xptschool.parent.http.MyVolleyRequestListener;
import com.xptschool.parent.model.BeanBanner;
import com.xptschool.parent.model.BeanParent;
import com.xptschool.parent.model.GreenDaoHelper;

/**
 * Created by dexing on 2017/3/24.
 * No1
 */

public class BannerHelper {

    private static String TAG = BannerHelper.class.getSimpleName();

    public static void postShowBanner(BeanBanner banner, String statisticsType) {
        if (banner == null || banner.getId().isEmpty()) {
            return;
        }
        Log.i(TAG, "postShowBanner: ");

        String ntime = CommonUtil.getCurrentDateHms();
        String adid = banner.getId();
        String region_id = banner.getRegion_id();
        String s_id = banner.getSid();
        BeanParent parent = GreenDaoHelper.getInstance().getCurrentParent();
        String phone = "";
        if (parent != null) {
            phone = parent.getParent_phone();
        }
        int random_int = (int) (Math.random() * 100);

        String token = CommonUtil.md5(adid + region_id + s_id + "school.xinpingtai.com" + ntime + random_int);

        //type 1展示|2点击
        final String url = HttpAction.SHOW_Banner + "?ntime=" + ntime + "&adid=" + adid + "&region_id=" + region_id
                + "&sid=" + s_id + "&type=" + statisticsType
                + "&mac=" + CommonUtil.getDeviceId() + "&mobile=" + phone + "&rand=" + random_int + "&token=" + token;

        VolleyHttpService.getInstance().sendGetRequest(url, new MyVolleyRequestListener() {
            @Override
            public void onStart() {
                Log.i(TAG, "onStart: " + url);
            }

            @Override
            public void onResponse(VolleyHttpResult volleyHttpResult) {
                Log.i(TAG, "onResponse: " + volleyHttpResult);
                //计数超额
                if (volleyHttpResult.getInfo().toUpperCase().equals("ERROR")) {
                    XPTApplication.getInstance().sendBroadcast(new Intent(BroadcastAction.RELOAD_BANNER));
                }
            }

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.i(TAG, "onErrorResponse: " + volleyError.getMessage());

            }
        });
    }

}
