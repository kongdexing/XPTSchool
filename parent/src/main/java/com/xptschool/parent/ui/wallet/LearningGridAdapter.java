package com.xptschool.parent.ui.wallet;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.xptschool.parent.BuildConfig;
import com.xptschool.parent.R;
import com.xptschool.parent.common.ExtraKey;
import com.xptschool.parent.model.BeanLearningModule;
import com.xptschool.parent.ui.main.WebViewActivity;

import java.util.ArrayList;
import java.util.List;

public class LearningGridAdapter extends BaseAdapter {
    private Context mContext;
    private String TAG = getClass().getSimpleName();
    public List<BeanLearningModule> beanLearningModules = new ArrayList<>();
    private DisplayImageOptions options;

    public LearningGridAdapter(Context mContext) {
        super();
        this.mContext = mContext;
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .showImageForEmptyUri(R.drawable.pictures_no)
                .showImageOnFail(R.drawable.pictures_no)
                .showImageOnLoading(R.drawable.pictures_no)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new SimpleBitmapDisplayer()).build();
    }

    public void reloadModule(List<BeanLearningModule> modules) {
        this.beanLearningModules = modules;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return beanLearningModules == null ? 0 : beanLearningModules.size();
    }

    @Override
    public Object getItem(int position) {
        return beanLearningModules.get(position);
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
                    R.layout.item_gridview_learning, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.rlModule = (RelativeLayout) convertView.findViewById(R.id.rlModule);
            viewHolder.img_module = (ImageView) convertView.findViewById(R.id.img_module);
            viewHolder.txt_module = (TextView) convertView.findViewById(R.id.txt_module);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

//        RelativeLayout.LayoutParams rllp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, XPTApplication.getInstance().getWindowWidth() / 3);
//        viewHolder.rlModule.setLayoutParams(rllp);

        final BeanLearningModule module = (BeanLearningModule) getItem(position);
        ImageLoader.getInstance().displayImage(BuildConfig.SERVICE_URL + module.getIcon_url(), new ImageViewAware(viewHolder.img_module), options);
        viewHolder.txt_module.setText(module.getTitle());
        viewHolder.rlModule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (3 > module.getWeb_url().length()) {
                    if ("1".equals(module.getWeb_url())) {
                        Intent intent = new Intent(mContext, TelephoneFareActivity.class);
                        intent.putExtra(ExtraKey.LEARNING_MODEL, module);
                        mContext.startActivity(intent);
                    }
                } else {
                    Intent intent = new Intent(mContext, WebViewActivity.class);
                    intent.putExtra(ExtraKey.WEB_URL, module.getWeb_url());
                    intent.putExtra(ExtraKey.WEB_TITLE, module.getTitle());
                    mContext.startActivity(intent);
                }
            }
        });
        return convertView;
    }

    class ViewHolder {
        RelativeLayout rlModule;
        ImageView img_module;
        TextView txt_module;
    }

}
