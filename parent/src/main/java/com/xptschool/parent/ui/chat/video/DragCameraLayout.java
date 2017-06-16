package com.xptschool.parent.ui.chat.video;

import android.content.Context;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.xptschool.parent.R;

/**
 * Created by dexing on 2017/6/15.
 * No1
 */

public class DragCameraLayout extends LinearLayout {

    private String TAG = DragCameraLayout.class.getSimpleName();
    private ViewDragHelper mDragger;
    private ViewDragHelper.Callback callback;
    private FrameLayout iv1;

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        iv1 = (FrameLayout) this.findViewById(R.id.view_call_incall_video_FrameLayout_local_video);
    }

    public DragCameraLayout(Context context) {
        super(context);
    }

    public DragCameraLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        callback = new DraggerCallBack();
        mDragger = ViewDragHelper.create(this, 1.0f, callback);
    }

    class DraggerCallBack extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View view, int i) {
            if (view == iv1) {
                //iv1 可以滑动
                return true;
            }
            return false;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            //取得左边界的坐标
            final int leftBound = getPaddingLeft();
            //取得右边界的坐标
            final int rightBound = getWidth() - child.getWidth() - leftBound;
            return Math.min(Math.max(left, leftBound), rightBound);
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            final int topBound = getPaddingTop();
            final int bottomBound = getHeight() - child.getHeight() - topBound;
            return Math.min(Math.max(top, topBound), bottomBound);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mDragger.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDragger.processTouchEvent(event);
        return true;
    }
}
