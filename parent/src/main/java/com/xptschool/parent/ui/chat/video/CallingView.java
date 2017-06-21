package com.xptschool.parent.ui.chat.video;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.xptschool.parent.R;

import org.doubango.ngn.sip.NgnAVSession;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by dexing on 2017/6/16.
 * No1
 */

public class CallingView extends LinearLayout {

    private String TAG = CallingView.class.getSimpleName();
    @BindView(R.id.view_call_incall_video_FrameLayout_local_video)
    FrameLayout mViewLocalVideoPreview;
    @BindView(R.id.view_call_incall_video_FrameLayout_remote_video)
    FrameLayout mViewRemoteVideoPreview;

    @BindView(R.id.view_call_trying_imageButton_hang)
    ImageView viewHang;

    CallingViewClickListener viewClickListener;

    public CallingView(Context context) {
        this(context, null);
    }

    public CallingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.view_call_incall_video, this, true);
        ButterKnife.bind(this);
    }

    public void setViewClickListener(CallingViewClickListener viewClickListener) {
        this.viewClickListener = viewClickListener;
    }

    public void loadVideoPreview(NgnAVSession mSession) {
        mViewRemoteVideoPreview.removeAllViews();
        final View remotePreview = mSession.startVideoConsumerPreview();
        if (remotePreview != null) {
            final ViewParent viewParent = remotePreview.getParent();
            if (viewParent != null && viewParent instanceof ViewGroup) {
                ((ViewGroup) (viewParent)).removeView(remotePreview);
            }
            mViewRemoteVideoPreview.addView(remotePreview);
            mSession.setSpeakerphoneOn(true);
        }
    }

    public void startStopVideo(NgnAVSession mSession) {
        boolean bStart = mSession.isSendingVideo();
        Log.d(TAG, "startStopVideo(" + bStart + ")");
//        if (!mIsVideoCall) {
//            return;
//        }

        mSession.setSendingVideo(bStart);

        if (mViewLocalVideoPreview != null) {
            mViewLocalVideoPreview.removeAllViews();
            if (bStart) {
                final View localPreview = mSession.startVideoProducerPreview();
                if (localPreview != null) {
                    final ViewParent viewParent = localPreview.getParent();
                    if (viewParent != null && viewParent instanceof ViewGroup) {
                        ((ViewGroup) (viewParent)).removeView(localPreview);
                    }
                    if (localPreview instanceof SurfaceView) {
                        ((SurfaceView) localPreview).setZOrderOnTop(true);
                    }
                    mViewLocalVideoPreview.addView(localPreview);
                    mViewLocalVideoPreview.bringChildToFront(localPreview);
                }
            }
            mViewLocalVideoPreview.setVisibility(bStart ? View.VISIBLE : View.GONE);
            mViewLocalVideoPreview.bringToFront();
        }
    }

    @OnClick({R.id.view_call_trying_imageButton_hang, R.id.viewCamera})
    void viewOnClick(View view) {
        switch (view.getId()) {
            case R.id.view_call_trying_imageButton_hang:
                if (viewClickListener != null) {
                    viewClickListener.onHangUpClick();
                }
                break;
            case R.id.viewCamera:
                if (viewClickListener != null) {
                    viewClickListener.onCameraSwitch();
                }
                break;
        }
    }

    public interface CallingViewClickListener {
        void onHangUpClick();

        void onCameraSwitch();
    }

}
