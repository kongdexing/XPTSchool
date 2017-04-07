package com.xptschool.parent.ui.album;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.xptschool.parent.R;
import com.xptschool.parent.XPTApplication;
import com.xptschool.parent.common.LocalFile;
import com.xptschool.parent.common.LocalImageHelper;

import java.util.List;

/**
 * Created by Administrator on 2016/10/28.
 */

public class AlbumGridViewAdapter extends BaseAdapter {

    DisplayImageOptions options;
    List<LocalFile> paths;
    private Context mContext;
    private AlbumGridViewClickListener clickListener;

    public AlbumGridViewAdapter(Context context, AlbumGridViewClickListener listener) {
        mContext = context;
        clickListener = listener;
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(false)
                .showImageForEmptyUri(R.drawable.pictures_no)
                .showImageOnFail(R.drawable.pictures_no)
                .showImageOnLoading(R.drawable.pictures_no)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .setImageSize(new ImageSize(((XPTApplication) context.getApplicationContext()).getQuarterWidth(), 0))
                .displayer(new SimpleBitmapDisplayer()).build();
    }


    public void reloadPaths(List<LocalFile> paths) {
        this.paths = paths;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return paths == null ? 0 : paths.size();
    }

    @Override
    public LocalFile getItem(int i) {
        return paths.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (convertView == null || convertView.getTag() == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.simple_list_item, null);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
            viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.checkbox);
            viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (clickListener != null) {
                        clickListener.onCheckBoxChecked(compoundButton, b);
                    }
                }
            });
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final LocalFile localFile = paths.get(i);
        ImageLoader.getInstance().displayImage(localFile.getThumbnailUri(), new ImageViewAware(viewHolder.imageView), options,
                loadingListener, null);
        viewHolder.checkBox.setTag(localFile);
        viewHolder.checkBox.setChecked(LocalImageHelper.getInstance().getCheckedItems().contains(localFile));
        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                showViewPager(i);
                if (clickListener != null) {
                    clickListener.onImageViewClicked(localFile, i);
                }
            }
        });
        return convertView;
    }

    private class ViewHolder {
        ImageView imageView;
        CheckBox checkBox;
    }

    SimpleImageLoadingListener loadingListener = new SimpleImageLoadingListener() {
        @Override
        public void onLoadingComplete(String imageUri, View view, final Bitmap bm) {
            if (TextUtils.isEmpty(imageUri)) {
                return;
            }
            //由于很多图片是白色背景，在此处加一个#eeeeee的滤镜，防止checkbox看不清
            try {
                ((ImageView) view).getDrawable().setColorFilter(Color.argb(0xff, 0xee, 0xee, 0xee), PorterDuff.Mode.MULTIPLY);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public interface AlbumGridViewClickListener {
        void onCheckBoxChecked(CompoundButton compoundButton, boolean b);

        void onImageViewClicked(LocalFile localFile, int position);
    }
}