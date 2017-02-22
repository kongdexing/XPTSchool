package com.xptschool.parent.common;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.xptschool.parent.XPTApplication;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocalImageHelper {

    private static LocalImageHelper instance;
    private static Context mContext;
    final List<LocalFile> checkedItems = new ArrayList<>();
    private String TAG = LocalImageHelper.class.getSimpleName();
    //最大图片选择个数
    private int maxChoiceSize = 9;
    //拍照时指定保存图片的路径
    private String CameraImgPath;
    //大图遍历字段
    private static final String[] STORE_IMAGES = {
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.ORIENTATION
    };
    //小图遍历字段
    private static final String[] THUMBNAIL_STORE_IMAGE = {
            MediaStore.Images.Thumbnails._ID,
            MediaStore.Images.Thumbnails.DATA
    };
    final List<LocalFile> paths = new ArrayList<>();
    final Map<String, List<LocalFile>> folders = new HashMap<>();
    private boolean resultOk;
    private List<LocalImageChanged> mLocalImageChangeds = new ArrayList<LocalImageChanged>();
    private List<OnLocalImageCheckedListener> mOnCheckedListeners = new ArrayList<OnLocalImageCheckedListener>();

    public int getMaxChoiceSize() {
        return maxChoiceSize;
    }

    public void setMaxChoiceSize(int maxChoiceSize) {
        this.maxChoiceSize = maxChoiceSize;
    }

    public String getCameraImgPath() {
        return CameraImgPath;
    }

    public String setCameraImgPath() {
        String foloder = XPTApplication.getInstance().getCachePath()
                + "/PostPicture/";
        File savedir = new File(foloder);
        if (!savedir.exists()) {
            savedir.mkdirs();
        }
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss")
                .format(new Date());
        // 照片命名
        String picName = timeStamp + ".jpg";
        //  裁剪头像的绝对路径
        CameraImgPath = foloder + picName;
        return CameraImgPath;
    }

    private LocalImageHelper() {

    }

    public Map<String, List<LocalFile>> getFolderMap() {
        return folders;
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

    public boolean isInited() {
        return paths.size() > 0;
    }

    public List<LocalFile> getCheckedItems() {
        return checkedItems;
    }

    public void removeItemByOriginalUri(String uri) {
        for (LocalFile file : checkedItems) {
            if (uri != null && file.getOriginalUri().equals(uri)) {
                checkedItems.remove(file);
                break;
            }
        }
    }

    public void insertCheckedItem(LocalFile file) {
        checkedItems.add(file);
        dispatchImageCheckedState(file, true);
    }

    public void removeCheckedItem(LocalFile file) {
        checkedItems.remove(file);
        dispatchImageCheckedState(file, false);
    }

    public boolean isResultOk() {
        return resultOk;
    }

    public void setResultOk(boolean ok) {
        resultOk = ok;
    }

    public void registerLocalImageChanged(LocalImageChanged listener) {
        if (!mLocalImageChangeds.contains(listener)) {
            mLocalImageChangeds.add(listener);
        }
    }

    public void unregisterLocalImageChanged(LocalImageChanged listener) {
        mLocalImageChangeds.remove(listener);
    }

    public void registerOnImageCheckedListener(OnLocalImageCheckedListener listener) {
        if (!mOnCheckedListeners.contains(listener)) {
            mOnCheckedListeners.add(listener);
        }
    }

    public void unregisterOnImageCheckedListener(OnLocalImageCheckedListener listener) {
        mOnCheckedListeners.remove(listener);
    }

    private void dispatchImage() {
        for (LocalImageChanged changed : mLocalImageChangeds) {
            if (changed != null) {
                changed.onLocalImageChanged();
            }
        }
    }

    private void dispatchImageCheckedState(LocalFile file, boolean checked) {
        for (OnLocalImageCheckedListener listener : mOnCheckedListeners) {
            if (listener != null) {
                listener.onImageChecked(file, checked);
            }
        }
    }

    public void initImage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                getAllAlbum();
            }
        }).start();
    }

    private void getAllAlbum() {
        //获取大图的游标
        Cursor cursor = mContext.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,  // 大图URI
                STORE_IMAGES,   // 字段
                null,         // No where clause
                null,         // No where clause
                MediaStore.Images.Media.DATE_TAKEN + " DESC"); //根据时间升序
        if (cursor == null)
            return;
        paths.clear();
        folders.clear();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);//大图ID
            String path = cursor.getString(1);//大图路径
            File file = new File(path);
            //判断大图是否存在
            if (file.exists()) {
                //小图URI
                String thumbUri = getThumbnail(id, path);
                //获取大图URI
                String uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon().
                        appendPath(Integer.toString(id)).build().toString();
                if (StringUtils.isEmpty(uri))
                    continue;
                if (StringUtils.isEmpty(thumbUri))
                    thumbUri = uri;
                //获取目录名
                String folder = file.getParentFile().getName();

                LocalFile localFile = new LocalFile();
                localFile.setOriginalPath(path);
                localFile.setOriginalUri(uri);
                localFile.setThumbnailUri(thumbUri);
                int degree = cursor.getInt(2);
                if (degree != 0) {
                    degree = degree + 180;
                }
                localFile.setOrientation(360 - degree);
                localFile.setParentFileName(folder);

                paths.add(localFile);
                //判断文件夹是否已经存在
                if (folders.containsKey(folder)) {
                    folders.get(folder).add(localFile);
                } else {
                    List<LocalFile> files = new ArrayList<>();
                    files.add(localFile);
                    folders.put(folder, files);
                }
            }
        }
        folders.put("所有图片", paths);
        cursor.close();
        dispatchImage();
    }

    private String getThumbnail(int id, String path) {
        //获取大图的缩略图
        Cursor cursor = mContext.getContentResolver().query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
                THUMBNAIL_STORE_IMAGE,
                MediaStore.Images.Thumbnails.IMAGE_ID + " = ?",
                new String[]{id + ""},
                null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            int thumId = cursor.getInt(0);
            String uri = MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI.buildUpon().
                    appendPath(Integer.toString(thumId)).build().toString();
            cursor.close();
            return uri;
        }
        cursor.close();
        return null;
    }

    public List<LocalFile> getFolder(String folder) {
        return folders.get(folder);
    }

    public void clear() {
        checkedItems.clear();
        String foloder = XPTApplication.getInstance().getCachePath()
                + "/PostPicture/";
        File savedir = new File(foloder);
        if (savedir.exists()) {
            deleteFile(savedir);
        }
    }

    public void deleteFile(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    deleteFile(files[i]);
                }
            }
        } else {
        }
    }

    public interface LocalImageChanged {
        void onLocalImageChanged();
    }

    public interface OnLocalImageCheckedListener {
        void onImageChecked(LocalFile file, boolean checked);
    }

}
