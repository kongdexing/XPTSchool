package com.xptschool.parent.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.xptschool.parent.R;
import com.xptschool.parent.common.CommonUtil;
import com.xptschool.parent.common.ExtraKey;
import com.xptschool.parent.model.BeanBanner;
import com.xptschool.parent.ui.main.WebViewActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dexing on 2017/1/13.
 * No1
 */

public class MyTopPagerAdapter extends PagerAdapter {

    List<BeanBanner> beanBanners = new ArrayList<>();
    private Context mContext;

    public MyTopPagerAdapter(Context context) {
        super();
        mContext = context;
    }

    public void reloadData(List<BeanBanner> tops) {
        if (tops.size() == 0) {
            beanBanners.clear();
            BeanBanner banner = new BeanBanner();
            banner.setImageurl("");
            beanBanners.add(banner);
        } else {
            beanBanners = tops;
        }
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return beanBanners.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == o;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView view = new ImageView(container.getContext());
        view.setScaleType(ImageView.ScaleType.CENTER_CROP);
        final BeanBanner banner = beanBanners.get(position);
        if (banner != null) {
            ImageLoader.getInstance().displayImage(banner.getImageurl(),
                    new ImageViewAware(view), CommonUtil.getDefaultImageLoaderOption());
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (banner.getMode().equals("click")) {
                        Intent intent = new Intent(mContext, WebViewActivity.class);
                        intent.putExtra(ExtraKey.WEB_URL, banner.getTarget());
                        mContext.startActivity(intent);
                    }
                }
            });
        }
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}