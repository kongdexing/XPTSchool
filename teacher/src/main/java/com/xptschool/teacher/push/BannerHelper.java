package com.xptschool.teacher.push;

import android.util.Log;

import com.android.volley.VolleyError;
import com.android.volley.common.VolleyHttpResult;
import com.android.volley.common.VolleyHttpService;
import com.xptschool.teacher.common.CommonUtil;
import com.xptschool.teacher.http.HttpAction;
import com.xptschool.teacher.http.MyVolleyRequestListener;
import com.xptschool.teacher.model.BeanBanner;
import com.xptschool.teacher.model.BeanTeacher;
import com.xptschool.teacher.model.GreenDaoHelper;

/**
 * Created by dexing on 2017/3/24.
 * No1
 */

public class BannerHelper {

    private static String TAG = BannerHelper.class.getSimpleName();

    public static void postShowBanner(BeanBanner banner, String statisticsType) {
        if (banner == null) {
            return;
        }
        Log.i(TAG, "postShowBanner: ");

        String ntime = CommonUtil.getCurrentDateHms();
        String adid = banner.getId();
        String region_id = banner.getRegion_id();
        String s_id = banner.getSid();
        BeanTeacher teacher = GreenDaoHelper.getInstance().getCurrentTeacher();
        String phone = "";
        if (teacher != null) {
            phone = teacher.getPhone();
        }
        int random_int = (int) (Math.random() * 100);

        String token = CommonUtil.md5(adid + region_id + s_id + "school.xinpingtai.com" + ntime + random_int);

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
                Log.i(TAG, "onResponse: " + volleyHttpResult.getInfo());
            }

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.i(TAG, "onErrorResponse: " + volleyError.getMessage());

            }
        });
    }

}
