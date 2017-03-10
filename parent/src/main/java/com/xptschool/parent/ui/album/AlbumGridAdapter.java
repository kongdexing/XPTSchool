package com.xptschool.parent.ui.album;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.xptschool.parent.R;
import com.xptschool.parent.common.LocalImageHelper;
import com.xptschool.parent.view.FilterImageView;

import java.util.ArrayList;
import java.util.List;

public class AlbumGridAdapter extends BaseAdapter {
    private Context mContext;
    private String TAG = getClass().getSimpleName();
    public List<String> imgPaths = new ArrayList<>();
    private MyGridViewClickListener myGridViewClickListener;
    private boolean canDelete = true;
    private DisplayImageOptions options;

    public AlbumGridAdapter(Context mContext, MyGridViewClickListener listener) {
        super();
        this.mContext = mContext;
        this.myGridViewClickListener = listener;
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .showImageForEmptyUri(R.mipmap.pictures_no)
                .showImageOnFail(R.mipmap.pictures_no)
                .showImageOnLoading(R.mipmap.pictures_no)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new SimpleBitmapDisplayer()).build();
    }

    public void reloadPicture(List<String> imgs) {
        this.imgPaths = imgs;
        notifyDataSetChanged();
    }

    public void initDate(List<String> imgs, boolean canDel) {
        this.imgPaths = imgs;
        this.canDelete = canDel;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return canDelete ? imgPaths.size() + 1 : imgPaths.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Log.i(TAG, "getView: "+position);
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_gridview_album, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.imageView = (FilterImageView) convertView.findViewById(R.id.post_add_pic);
            viewHolder.imgDelete = (ImageView) convertView.findViewById(R.id.imgDelete);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myGridViewClickListener != null) {
                    myGridViewClickListener.onGridViewItemClick(position, position == 0 ?
                            (canDelete ? "" : imgPaths.get(position )) : (canDelete ? imgPaths.get(position - 1) : imgPaths.get(position)));
                }
            }
        });
        viewHolder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocalImageHelper.getInstance().removeItemByOriginalUri(imgPaths.get(position - 1));
                imgPaths.remove(position - 1);
                notifyDataSetChanged();
            }
        });
        if (position == 0) {
            viewHolder.imgDelete.setVisibility(View.GONE);
            if (!canDelete){
                ImageLoader.getInstance().displayImage(imgPaths.get(position), new ImageViewAware(viewHolder.imageView),options);
            }
        } else {
            viewHolder.imgDelete.setVisibility(canDelete ? View.VISIBLE : View.GONE);
            ImageLoader.getInstance().displayImage(imgPaths.get(position), new ImageViewAware(viewHolder.imageView),options);
        }
        return convertView;
    }

    class ViewHolder {
        FilterImageView imageView;
        ImageView imgDelete;
    }

    public interface MyGridViewClickListener {
        void onGridViewItemClick(int position, String imgPath);
    }

}
