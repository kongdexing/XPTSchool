package com.xptschool.parent.ui.chat.video;

import android.os.Bundle;

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
