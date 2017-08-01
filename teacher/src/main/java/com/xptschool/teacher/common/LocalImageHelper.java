package com.xptschool.teacher.common;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by linjizong on 15/6/11.
 */
public class LocalImageHelper {

    private static LocalImageHelper instance;
    private static Context mContext;
    private String TAG = LocalImageHelper.class.getSimpleName();
    //最大图片选择个数
    private int maxChoiceSize = 9;
    private int currentEnableMaxChoiceSize = maxChoiceSize;
    //拍照时指定保存图片的路径
    private String CameraImgPath;
    private List<String> localCheckedImgs = new ArrayList<>();

    public int getMaxChoiceSize() {
        return maxChoiceSize;
    }

    public String setCameraImgPath() {
        File file = new File(Environment.getExternalStorageDirectory(), "/temp/" + System.currentTimeMillis() + ".jpg");
        if (!file.getParentFile().exists()) file.getParentFile().mkdirs();

        //  裁剪头像的绝对路径
        CameraImgPath = file.getAbsolutePath();
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

//    public void setMaxChoiceSize(int maxChoiceSize) {
//        this.currentEnableMaxChoiceSize = maxChoiceSize;
//    }

    public int getCurrentEnableMaxChoiceSize() {
        return currentEnableMaxChoiceSize;
    }

    public void setCurrentEnableMaxChoiceSize(int currentEnableMaxChoiceSize) {
        this.currentEnableMaxChoiceSize = currentEnableMaxChoiceSize;
    }
}
