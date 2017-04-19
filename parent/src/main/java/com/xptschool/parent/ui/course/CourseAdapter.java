package com.xptschool.parent.ui.course;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.xptschool.parent.R;
import com.xptschool.parent.common.LocalImageHelper;
import com.xptschool.parent.util.ToastUtils;
import com.xptschool.parent.view.FilterImageView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CourseAdapter extends BaseAdapter {
    private Context mContext;
    private String TAG = CourseAdapter.class.getSimpleName();

    public List<String> courses = new ArrayList<>();
    public List<String> courseWeek = new ArrayList<>();

    public CourseAdapter(Context mContext) {
        super();
        this.mContext = mContext;
    }

    public void loadDate(LinkedHashMap<String, LinkedHashMap<String, String>> linkedCourse) {
        courses.clear();
        courseWeek.clear();
        courses.add("节次");
        courses.add("周一");
        courses.add("周二");
        courses.add("周三");
        courses.add("周四");
        courses.add("周五");
        courses.add("周六");
        courses.add("周日");
        courseWeek.addAll(courses);
        courseWeek.add("1");
        courseWeek.add("2");
        courseWeek.add("3");
        courseWeek.add("4");
        courseWeek.add("5");
        courseWeek.add("6");
        courseWeek.add("7");
        courseWeek.add("8");

        Iterator iter = linkedCourse.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            //节次
            String key = (String) entry.getKey();
            courses.add(key);
            LinkedHashMap<String, String> val = (LinkedHashMap<String, String>) entry.getValue();
            Iterator itCourse = val.entrySet().iterator();
            while (itCourse.hasNext()) {
                try {
                    Map.Entry entryCourse = (Map.Entry) itCourse.next();
                    //星期
                    String week = (String) entryCourse.getKey();
                    courses.add((String) entryCourse.getValue());
                } catch (Exception ex) {
                    Log.i(TAG, "loadDate: " + ex.getMessage());
                }
            }
        }
        Log.i(TAG, "loadDate: courses size " + courses.size());
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return courses.size();
    }

    @Override
    public String getItem(int position) {
        return courses.get(position);
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
                    R.layout.item_gridview_course, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.txtCourse = (TextView) convertView.findViewById(R.id.txtCourse);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final String course = getItem(position);
        viewHolder.txtCourse.setText(course);

        if (course.length() > 2) {
            viewHolder.txtCourse.setTextSize(10);
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!course.isEmpty() && !courseWeek.contains(course)) {
                    ToastUtils.showToast(mContext, course);
                }
            }
        });
        return convertView;
    }

    class ViewHolder {
        TextView txtCourse;
    }

}
