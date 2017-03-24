package com.xptschool.parent.push;

import android.util.Log;

import com.android.volley.VolleyError;
import com.android.volley.common.VolleyHttpResult;
import com.android.volley.common.VolleyHttpService;
import com.xptschool.parent.common.CommonUtil;
import com.xptschool.parent.http.HttpAction;
import com.xptschool.parent.http.MyVolleyRequestListener;
import com.xptschool.parent.model.BeanBanner;
import com.xptschool.parent.model.BeanParent;
import com.xptschool.parent.model.GreenDaoHelper;

import java.util.Random;

/**
 * Created by dexing on 2017/3/24.
 * No1
 */

public class BannerHelper {

    private static String TAG = BannerHelper.class.getSimpleName();

    public static void postShowBanner(BeanBanner banner) {
        if (banner == null) {
            return;
        }
        Log.i(TAG, "postShowBanner: ");

        String ntime = CommonUtil.getCurrentDateHms();
        String adid = banner.getId();
        String region_id = "";
        String s_id = "";
        BeanParent parent = GreenDaoHelper.getInstance().getCurrentParent();
        String phone = "";
        if (parent != null) {
            phone = parent.getParent_phone();
        }
        Random random = new Random(10000);
        int random_int = random.nextInt();

        String token = CommonUtil.md5(adid + region_id + s_id + "school.xinpingtai.com" + ntime + random_int);

        final String url = HttpAction.SHOW_Banner + "?ntime=" + ntime + "&adid" + adid + "&region_id=" + region_id
                + "&sid=" + s_id + "&type=" + banner.getType()
                + "&mac=" + CommonUtil.getDeviceId() + "$mobile=" + phone + "&rand=" + random_int + "&token=" + token;

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
