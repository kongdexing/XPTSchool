package com.xptschool.parent;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.support.multidex.MultiDex;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.android.volley.common.VolleyHttpService;
import com.baidu.mapapi.SDKInitializer;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.MsgConstant;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;
import com.xptschool.parent.common.LocalImageHelper;
import com.xptschool.parent.model.GreenDaoHelper;
import com.xptschool.parent.push.MyUmengMessageHandler;
import com.xptschool.parent.ui.main.MainActivity;

import java.io.File;

/**
 * Created by dexing on 2016/12/18.
 * No1
 */
public class XPTApplication extends Application {

    public static final String APP_ID = "3e1429a7a5"; // TODO 替换成bugly上注册的appid
    private static XPTApplication mInstance;
    public static final String WXAPP_ID = "wx1af4f660ce9e6b37";
    private Display display;
    private String TAG = XPTApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        init();

        initBugly();
    }

    private void initBugly() {
        // 设置开发设备
        Bugly.setIsDevelopmentDevice(this, true);
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
        Bugly.init(this, APP_ID, true);
    }

    public static XPTApplication getInstance() {
        return mInstance;
    }

    private void init() {
        initImageLoader(getInstance());
        LocalImageHelper.init(this);
        VolleyHttpService.init(this);

        if (display == null) {
            WindowManager windowManager = (WindowManager)
                    getSystemService(Context.WINDOW_SERVICE);
            display = windowManager.getDefaultDisplay();
        }

        //baidumap
        SDKInitializer.initialize(this);
        GreenDaoHelper.getInstance().initGreenDao(this);

        MobclickAgent.setCheckDevice(true);
        //日志加密设置
        MobclickAgent.enableEncrypt(true);

        final PushAgent mPushAgent = PushAgent.getInstance(this);
        mPushAgent.setNotificationPlaySound(MsgConstant.NOTIFICATION_PLAY_SDK_ENABLE);//声音
        mPushAgent.setNotificationPlayLights(MsgConstant.NOTIFICATION_PLAY_SERVER);//呼吸灯
        mPushAgent.setNotificationPlayVibrate(MsgConstant.NOTIFICATION_PLAY_SERVER);//振动

        //通知栏数量显示
        mPushAgent.setDisplayNotificationNumber(0);
        mPushAgent.setMessageHandler(new MyUmengMessageHandler());
        mPushAgent.setDebugMode(false);
        /**
         * 自定义行为的回调处理
         * UmengNotificationClickHandler是在BroadcastReceiver中被调用，故
         * 如果需启动Activity，需添加Intent.FLAG_ACTIVITY_NEW_TASK
         * */
        UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {
            @Override
            public void dealWithCustomAction(Context context, UMessage msg) {
                Log.i(TAG, "dealWithCustomAction: " + msg.text);
                //根据msg类型判断

                Log.i(TAG, "MainActivity simpleName: " + MainActivity.class.getSimpleName());
                //判断Activity是否存在
//                if (!ActivityTaskHelper.isAppRunning(context)) {
//                    Intent[] intents = new Intent[3];
//                    intents[0] = new Intent(context, MainActivity.class);
//                    intents[1] = new Intent(context, AlarmActivity.class);
//                    intents[2] = new Intent(context, AlarmMapActivity.class);
//                    intents[0].setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    intents[1].setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    intents[2].setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    context.startActivities(intents);
//                } else {
//                    Intent intent = new Intent(context, AlarmMapActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    context.startActivity(intent);
//                }
            }
        };
        //使用自定义的NotificationHandler，来结合友盟统计处理消息通知
        //参考http://bbs.umeng.com/thread-11112-1-1.html
        //CustomNotificationHandler notificationClickHandler = new CustomNotificationHandler();
        mPushAgent.setNotificationClickHandler(notificationClickHandler);

    }

    // Initialize the image loader stratetry
    public static void initImageLoader(Context context) {
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY);
        // do not cache multiple images
        config.denyCacheImageMultipleSizesInMemory();
        config.memoryCacheSize((int) Runtime.getRuntime().maxMemory() / 4);
        // the files name generator which are cached
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//            File sdCardDir = Environment.getExternalStorageDirectory();//获取SDCard目录
//            config.diskCache(new UnlimitedDiskCache(new File(sdCardDir.getAbsolutePath() + "/XPTteacher")));
            config.diskCache(new UnlimitedDiskCache(new File(XPTApplication.getInstance().getCachePath())));
        }

        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        // the disk cache size : here is 100M
        config.diskCacheSize(100 * 1024 * 1024); // 100 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        //修改连接超时时间5秒，下载超时时间5秒
        config.imageDownloader(new BaseImageDownloader(mInstance, 5 * 1000, 5 * 1000));
        //		config.writeDebugLogs(); // Remove for release app
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
    }

    public String getCachePath() {
        File cacheDir;
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            cacheDir = getExternalCacheDir();
        else
            cacheDir = getCacheDir();
        if (!cacheDir.exists())
            cacheDir.mkdirs();
        return cacheDir.getAbsolutePath();
    }

    /**
     * @return
     * @Description： 获取当前屏幕的宽度
     */
    public int getWindowWidth() {
        return display.getWidth();
    }

    /**
     * @return
     * @Description： 获取当前屏幕的高度
     */
    public int getWindowHeight() {
        return display.getHeight();
    }

    public int getQuarterWidth() {
        return display.getWidth() / 4;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        // you must install multiDex whatever tinker is installed!
        MultiDex.install(base);

        // 安装tinker
        Beta.installTinker();
    }
}
