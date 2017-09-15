package com.xptschool.teacher.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.xptschool.teacher.R;
import com.xptschool.teacher.common.CommonUtil;
import com.xptschool.teacher.common.ExtraKey;
import com.xptschool.teacher.model.BeanBanner;
import com.xptschool.teacher.push.BannerHelper;
import com.xptschool.teacher.ui.main.WebViewActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dexing on 2017/1/13.
 * No1
 */

public class MyTopPagerAdapter extends PagerAdapter {

    List<BeanBanner> beanBanners = new ArrayList<>();
    private Context mContext;
    private DisplayImageOptions options;

    public MyTopPagerAdapter(Context context) {
        super();
        mContext = context;
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .showImageForEmptyUri(R.drawable.tip_ad_def)
                .showImageOnFail(R.drawable.tip_ad_def)
                .showImageOnLoading(R.drawable.pictures_no)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new SimpleBitmapDisplayer()).build();
    }

    public void reloadData(List<BeanBanner> tops) {
        if (tops.size() == 0) {
            beanBanners.clear();
            BeanBanner banner = new BeanBanner();
            banner.setImg("");
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
        ImageView view = new ImageView(mContext);
        view.setScaleType(ImageView.ScaleType.CENTER_CROP);
        final BeanBanner banner = beanBanners.get(position);
        if (banner != null) {
            ImageLoader.getInstance().displayImage(banner.getImg(),
                    new ImageViewAware(view), options);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (banner.getTurn_type().equals("1")) {
                        Intent intent = new Intent(mContext, WebViewActivity.class);
                        intent.putExtra(ExtraKey.WEB_URL, banner.getUrl());
                        mContext.startActivity(intent);
                        BannerHelper.postShowBanner(banner, "2");
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