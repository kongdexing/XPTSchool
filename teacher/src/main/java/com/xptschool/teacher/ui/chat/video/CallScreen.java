package com.xptschool.teacher.ui.chat.video;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

public class CallScreen extends CallBaseScreen {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showActionBar(false);

    }

    public void loadVideoPreview() {
        super.loadVideoPreview();
//        mMainLayout.removeAllViews();
//        final View remotePreview = mSession.startVideoConsumerPreview();
//        if (remotePreview != null) {
//            final ViewParent viewParent = remotePreview.getParent();
//            if (viewParent != null && viewParent instanceof ViewGroup) {
//                ((ViewGroup) (viewParent)).removeView(remotePreview);
//            }
//            mMainLayout.addView(remotePreview);
//        }
    }

}
