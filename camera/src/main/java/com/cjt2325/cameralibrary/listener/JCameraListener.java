package com.cjt2325.cameralibrary.listener;

import android.graphics.Bitmap;

public interface JCameraListener {

    void captureSuccess(Bitmap bitmap);

    void recordSuccess(String url,long duration);

    void quit();

}
