package com.xptschool.teacher.ui.score;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xptschool.teacher.R;
import com.xptschool.teacher.bean.BeanScore;

import java.util.ArrayList;
import java.util.List;

public class ScoreGridAdapter extends BaseAdapter {
    private Context mContext;

    public List<BeanScore> classes = new ArrayList<>();

    public ScoreGridAdapter(Context mContext, List<BeanScore> beanClasses) {
        super();
        this.mContext = mContext;
        this.classes = beanClasses;
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
                    R.layout.item_gridview_score, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.txtSubject = (TextView) convertView.findViewById(R.id.txtSubject);
            viewHolder.txtScore = (TextView) convertView.findViewById(R.id.txtScore);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final BeanScore beanScore = classes.get(position);
        viewHolder.txtSubject.setText(beanScore.getCourseName());
        viewHolder.txtScore.setText(beanScore.getCourseScore());
        return convertView;
    }

    class ViewHolder {
        TextView txtSubject;
        TextView txtScore;
    }

}
