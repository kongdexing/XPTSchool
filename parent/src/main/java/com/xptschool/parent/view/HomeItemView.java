package com.xptschool.parent.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xptschool.parent.R;

public class HomeItemView extends LinearLayout {

    private String TAG = HomeItemView.class.getSimpleName();

    public HomeItemView(Context context) {
        super(context);
        Log.i(TAG, "HomeItemView: ");
    }

    public HomeItemView(final Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.i(TAG, "HomeItemView: attrs");
        View view = LayoutInflater.from(context).inflate(R.layout.item_home_option, this, true);
        ImageView optionImg = (ImageView) view.findViewById(R.id.optionImg);
        TextView optionText = (TextView) view.findViewById(R.id.optionText);
        view.setClickable(true);
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.HomeItemView, 0, 0);
        try {
            int resId = typedArray.getResourceId(R.styleable.HomeItemView_src, R.mipmap.ic_launcher);
            String text = typedArray.getString(R.styleable.HomeItemView_text);
            optionImg.setBackgroundResource(resId);
            optionText.setText(text);
        } catch (Exception ex) {

        }

    }
}
