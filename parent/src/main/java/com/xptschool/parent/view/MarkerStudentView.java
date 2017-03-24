package com.xptschool.parent.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.widget.view.CircularImageView;
import com.xptschool.parent.R;

/**
 */

public class MarkerStudentView extends LinearLayout {

    private FrameLayout flContent;
    private CircularImageView imgHead;

    public MarkerStudentView(Context context) {
        this(context, null);
    }

    public MarkerStudentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.view_marker_student, this, true);
        flContent = (FrameLayout) view.findViewById(R.id.fl_Content);
        imgHead = (CircularImageView) view.findViewById(R.id.imgHead);
    }

    public void isBoy(boolean isboy) {
        if (isboy) {
            flContent.setBackgroundResource(R.mipmap.icon_marker_boy_bg);
            imgHead.setImageResource(R.mipmap.student_boy);
        } else {
            flContent.setBackgroundResource(R.mipmap.icon_marker_girl_bg);
            imgHead.setImageResource(R.mipmap.student_girl_marker);
        }
    }

}
