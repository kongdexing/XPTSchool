package com.xptschool.teacher.ui.album;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.xptschool.teacher.R;
import com.xptschool.teacher.common.CommonUtil;
import com.xptschool.teacher.common.LocalImageHelper;
import com.xptschool.teacher.view.FilterImageView;

import java.util.ArrayList;
import java.util.List;

public class AlbumGridAdapter extends BaseAdapter {
    private Context mContext;
    private String TAG = getClass().getSimpleName();
    public List<String> imgPaths = new ArrayList<>();
    private MyGridViewClickListener myGridViewClickListener;
    private boolean canDelete = true;

    public AlbumGridAdapter(Context mContext, MyGridViewClickListener listener) {
        super();
        this.mContext = mContext;
        this.myGridViewClickListener = listener;
    }

    public void reloadPicture(List<String> imgs) {
        List<String> tempImgs = new ArrayList<>();
        for (int j = 0; j < imgs.size(); j++) {
            if (!imgPaths.contains(imgs.get(j))) {
                tempImgs.add(imgs.get(j));
            }
        }

        this.imgPaths.addAll(tempImgs);

        notifyDataSetChanged();
    }

    public void initDate(List<String> imgs, boolean canDel) {
        this.imgPaths = imgs;
        this.canDelete = canDel;
        notifyDataSetChanged();
    }

    public List<String> getImgPaths() {
        return imgPaths;
    }

    @Override
    public int getCount() {
        int count = canDelete ? imgPaths.size() + 1 : imgPaths.size();
        LocalImageHelper.getInstance().setCurrentEnableMaxChoiceSize(
                LocalImageHelper.getInstance().getMaxChoiceSize() - imgPaths.size());
        return count;
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
        Log.i(TAG, "getView: " + position);
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
                            (canDelete ? "" : imgPaths.get(position)) : (canDelete ? imgPaths.get(position - 1) : imgPaths.get(position)));
                }
            }
        });
        viewHolder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocalImageHelper.getInstance().getLocalCheckedImgs().remove(imgPaths.get(position - 1));
                imgPaths.remove(position - 1);
                notifyDataSetChanged();
            }
        });
        if (position == 0) {
            viewHolder.imgDelete.setVisibility(View.GONE);
            if (!canDelete) {
                ImageLoader.getInstance().displayImage(imgPaths.get(position), new ImageViewAware(viewHolder.imageView), CommonUtil.getDefaultImageLoaderOption());
            } else {
                ImageLoader.getInstance().displayImage("drawable://" + R.mipmap.post_add_pic, new ImageViewAware(viewHolder.imageView), CommonUtil.getDefaultImageLoaderOption());
            }
        } else {
            viewHolder.imgDelete.setVisibility(canDelete ? View.VISIBLE : View.GONE);
            String patch = imgPaths.get(canDelete ? position - 1 : position);
            Log.i(TAG, "getView: patch " + patch);

            ImageLoader.getInstance().displayImage(patch, new ImageViewAware(viewHolder.imageView), CommonUtil.getDefaultImageLoaderOption());
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
