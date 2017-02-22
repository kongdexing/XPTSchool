package com.xptschool.teacher;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Environment;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.tencent.tinker.loader.app.DefaultApplicationLike;
import com.xptschool.teacher.ui.main.MainActivity;

/**
 * 自定义ApplicationLike类.
 * <p>
 * 注意：这个类是Application的代理类，以前所有在Application的实现必须要全部拷贝到这里<br/>
 *
 * @author wenjiewu
 * @since 2016/11/7
 */
public class XPTTeacherApplication extends DefaultApplicationLike {

    public static final String APP_ID = "3a3021ce3c"; // TODO 替换成bugly上注册的appid
    public static final String TAG = XPTTeacherApplication.class.getSimpleName();

    public XPTTeacherApplication(Application application, int tinkerFlags,
                                 boolean tinkerLoadVerifyFlag, long applicationStartElapsedTime,
                                 long applicationStartMillisTime, Intent tinkerResultIntent) {
        super(application, tinkerFlags, tinkerLoadVerifyFlag, applicationStartElapsedTime,
                applicationStartMillisTime, tinkerResultIntent);
        Log.i(TAG, "XPTTeacherApplication: ");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate: ");
        // TODO: 这里进行Bugly初始化
        // 设置开发设备
        Bugly.setIsDevelopmentDevice(getApplication(), true);
        // 这里实现SDK初始化，appId替换成你的在Bugly平台申请的appId

        Beta.autoInit = true;
        Beta.autoCheckUpgrade = true;
        Beta.initDelay = 3 * 1000;
        Beta.largeIconId = R.mipmap.ic_launcher;
        Beta.smallIconId = R.mipmap.ic_launcher;
        Beta.defaultBannerId = R.mipmap.ic_launcher;
        Beta.storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        Beta.showInterruptedStrategy = true;
        Beta.canShowUpgradeActs.add(MainActivity.class);
        Beta.autoDownloadOnWifi = false;
        Bugly.init(getApplication(), APP_ID, true);
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onBaseContextAttached(Context base) {
        super.onBaseContextAttached(base);
        Log.i(TAG, "onBaseContextAttached: ");
        // you must install multiDex whatever tinker is installed!
        MultiDex.install(base);

        // TODO: 安装tinker
        Beta.installTinker(this);
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void registerActivityLifecycleCallback(Application.ActivityLifecycleCallbacks callbacks) {
        getApplication().registerActivityLifecycleCallbacks(callbacks);
    }

}
