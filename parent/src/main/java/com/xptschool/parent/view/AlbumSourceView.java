package com.xptschool.parent.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.xptschool.parent.R;

/**
 * Created by Administrator on 2016/10/28.
 */

public class AlbumSourceView extends LinearLayout implements View.OnClickListener {

    private String TAG = AlbumSourceView.class.getSimpleName();
    private LinearLayout rlAlbum;
    private LinearLayout rlCamera;
    private LinearLayout rlBack;
    private OnAlbumSourceClickListener clickListener;

    public AlbumSourceView(Context context) {
        this(context, null);
    }

    public AlbumSourceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.widget_albumsource, this, true);
        try {
            rlAlbum = (LinearLayout) view.findViewById(R.id.rlAlbum);
            rlCamera = (LinearLayout) view.findViewById(R.id.rlCamera);
            rlBack = (LinearLayout) view.findViewById(R.id.rlBack);
            rlAlbum.setOnClickListener(this);
            rlCamera.setOnClickListener(this);
            rlBack.setOnClickListener(this);
        }catch (Exception ex){
            Log.i(TAG, "AlbumSourceView: "+ex.getMessage());
        }
    }

    public void setOnAlbumSourceClickListener(OnAlbumSourceClickListener listener) {
        clickListener = listener;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rlAlbum:
                if (clickListener != null) {
                    clickListener.onAlbumClick();
                }
                break;
            case R.id.rlCamera:
                if (clickListener != null) {
                    clickListener.onCameraClick();
                }
                break;
            case R.id.rlBack:
                if (clickListener != null) {
                    clickListener.onBack();
                }
                break;
        }
    }

    public interface OnAlbumSourceClickListener {
        void onAlbumClick();

        void onCameraClick();

        void onBack();
    }

}
