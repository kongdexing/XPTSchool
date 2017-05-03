package com.xptschool.parent.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RelativeLayout;

/**
 * Created by dexing on 2017/4/10.
 * No1
 */

public class SquareLayout extends RelativeLayout {

    private String TAG = SquareLayout.class.getSimpleName();

    public SquareLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public SquareLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareLayout(Context context) {
        super(context);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.i(TAG, "onMeasure: widthMeasureSpec " + widthMeasureSpec + " heightMeasureSpec:" + heightMeasureSpec);
//        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));
//
//        int childWidthSize = getMeasuredWidth();
//        int childHeightSize = getMeasuredHeight();
//        Log.i(TAG, "onMeasure: childWidthSize " + childWidthSize + " childHeightSize:" + childHeightSize);
//
//        heightMeasureSpec = widthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY);

        Log.i(TAG, "makeMeasureSpec: heightMeasureSpec " + heightMeasureSpec);

        super.onMeasure(widthMeasureSpec, widthMeasureSpec);

        Log.i(TAG, "onMeasure");

    }
}
