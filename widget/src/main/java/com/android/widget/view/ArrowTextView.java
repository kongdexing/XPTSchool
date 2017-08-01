package com.android.widget.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

import com.android.widget.R;
import com.android.widget.spinner.Utils;

/**
 * Created by Administrator on 2016/11/10.
 */

public class ArrowTextView extends TextView {

    private Drawable arrowDrawable;
    private boolean hideArrow;
    private int arrowColor;
    private int textColor;
    private boolean shouldRotateUp = false;

    public ArrowTextView(Context context) {
        this(context, null);
    }

    public ArrowTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ArrowTextView);
        int defaultColor = getTextColors().getDefaultColor();
        boolean rtl = Utils.isRtl(context);
        try {
            textColor = ta.getColor(R.styleable.ArrowTextView_text_color, defaultColor);
            arrowColor = ta.getColor(R.styleable.ArrowTextView_arrow_tint, textColor);
            hideArrow = ta.getBoolean(R.styleable.ArrowTextView_hide_arrow, false);
        } finally {
            ta.recycle();
        }

        if (!hideArrow) {
            arrowDrawable = Utils.getDrawable(context, R.drawable.ms__arrow).mutate();
            arrowDrawable.setColorFilter(arrowColor, PorterDuff.Mode.SRC_IN);
            if (rtl) {
                setCompoundDrawablesWithIntrinsicBounds(arrowDrawable, null, null, null);
            } else {
                setCompoundDrawablesWithIntrinsicBounds(null, null, arrowDrawable, null);
            }
        }
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (isEnabled() && isClickable()) {
                if (!shouldRotateUp) {
                    expand();
                } else {
                    collapse();
                }
            }
        }
        return super.onTouchEvent(event);
    }

    /**
     * Show the dropdown menu
     */
    public void expand() {
        if (!hideArrow) {
            animateArrow(true);
        }
    }

    /**
     * Closes the dropdown menu
     */
    public void collapse() {
        if (!hideArrow) {
            animateArrow(false);
        }
    }

    private void animateArrow(boolean _shouldRotateUp) {
        shouldRotateUp = _shouldRotateUp;
        int start = shouldRotateUp ? 0 : 10000;
        int end = shouldRotateUp ? 10000 : 0;
        ObjectAnimator animator = ObjectAnimator.ofInt(arrowDrawable, "level", start, end);
        animator.start();
    }

}
