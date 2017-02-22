package com.xptschool.teacher.common;

import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.xptschool.teacher.XPTApplication;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by linjizong on 15/6/11.
 */
public class LocalImageHelper {

    private static LocalImageHelper instance;
    private static Context mContext;
    private String TAG = LocalImageHelper.class.getSimpleName();
    //最大图片选择个数
    private int maxChoiceSize = 9;
    //拍照时指定保存图片的路径
    private String CameraImgPath;
    private List<String> localCheckedImgs = new ArrayList<>();

    public int getMaxChoiceSize() {
        return maxChoiceSize;
    }

    public String setCameraImgPath() {
        String foloder = XPTApplication.getInstance().getCachePath();
//                + "/PostPicture/";
        File savedir = new File(foloder);
        if (!savedir.exists()) {
            savedir.mkdirs();
        }
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss")
                .format(new Date());
        // 照片命名
        String picName = timeStamp + ".jpg";
        //  裁剪头像的绝对路径
        CameraImgPath = foloder + "/" + picName;
        return CameraImgPath;
    }

    private LocalImageHelper() {

    }

    public static LocalImageHelper getInstance() {
        synchronized (LocalImageHelper.class) {
            if (mContext == null) {
                return null;
            }
            if (instance == null) {
                instance = new LocalImageHelper();
            }
        }
        return instance;
    }

    public static void init(Context context) {
        mContext = context;
    }

    public List<String> getLocalCheckedImgs() {
        if (localCheckedImgs == null) {
            return new ArrayList<>();
        }
        return localCheckedImgs;
    }

    public void setMaxChoiceSize(int maxChoiceSize) {
        this.maxChoiceSize = maxChoiceSize;
    }
}
