package com.xptschool.teacher.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.widget.view.SmoothCheckBox;
import com.xptschool.teacher.R;
import com.xptschool.teacher.model.BeanClass;

import java.util.ArrayList;
import java.util.List;

public class ClassesGridAdapter extends BaseAdapter {
    private Context mContext;

    public List<BeanClass> classes = new ArrayList<>();
    private OnMyGridViewClickListener myGridViewClickListener;

    public ClassesGridAdapter(Context mContext, List<BeanClass> beanClasses, OnMyGridViewClickListener listener) {
        super();
        this.mContext = mContext;
        this.classes = beanClasses;
        this.myGridViewClickListener = listener;
    }

    @Override
    public int getCount() {
        return classes.size();
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
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_gridview_classes, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.checkBox = (SmoothCheckBox) convertView.findViewById(R.id.checkBox);
            viewHolder.txtClasses = (TextView) convertView.findViewById(R.id.txtClassName);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final BeanClass beanClass = classes.get(position);
        viewHolder.txtClasses.setText(beanClass.getName());
//        viewHolder.checkBox.setChecked(beanClass.isChecked());
        viewHolder.checkBox.setTag(beanClass);
        return convertView;
    }

    class ViewHolder {
        SmoothCheckBox checkBox;
        TextView txtClasses;
    }

    public interface OnMyGridViewClickListener {
        void onGridViewItemClick(int position, String classId);
    }

}
