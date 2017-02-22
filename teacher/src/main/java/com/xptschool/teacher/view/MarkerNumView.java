package com.xptschool.teacher.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xptschool.teacher.R;

/**
 * Created by Administrator on 2016/11/17.
 */

public class MarkerNumView extends LinearLayout {

    private TextView txtNum;

    public MarkerNumView(Context context) {
        this(context, null);
    }

    public MarkerNumView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.view_markernum, this, true);
        txtNum = (TextView) view.findViewById(R.id.txtNum);
    }

    public void setNumber(int number) {
        txtNum.setText(number + "");
    }

}
