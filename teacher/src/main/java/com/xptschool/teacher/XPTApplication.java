package com.xptschool.teacher;

import android.content.Context;
import android.os.Environment;
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
import com.tencent.tinker.loader.app.TinkerApplication;
import com.tencent.tinker.loader.shareutil.ShareConstants;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.MsgConstant;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;
import com.xptschool.teacher.common.LocalImageHelper;
import com.xptschool.teacher.model.GreenDaoHelper;
import com.xptschool.teacher.push.MyUmengMessageHandler;

import java.io.File;

/**
 * Created by Administrator on 2016/10/18 0018.
 */

public class XPTApplication extends TinkerApplication {

    private static XPTApplication mInstance;
    private Display display;
    private static String TAG = XPTApplication.class.getSimpleName();

    public XPTApplication() {
        super(ShareConstants.TINKER_ENABLE_ALL, "com.xptschool.teacher.XPTTeacherApplication",
                "com.tencent.tinker.loader.TinkerLoader", false);
        Log.i(TAG, "XPTApplication: ");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        init();
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
        mPushAgent.setNotificationPlaySound(MsgConstant.NOTIFICATION_PLAY_SDK_ENABLE);
        mPushAgent.setDisplayNotificationNumber(0);
        mPushAgent.setMessageHandler(new MyUmengMessageHandler());

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

//        File file = ImageLoader.getInstance().getDiskCache().getDirectory();
//        if (file.exists()) {
//            if (file.isDirectory()) {
//                File[] files = file.listFiles();
//                for (int i = 0; i < files.length; i++) {
//                    Log.i(TAG, "initImageLoader: " + files[i].getAbsolutePath());
//                }
//            }
//        }

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

}
