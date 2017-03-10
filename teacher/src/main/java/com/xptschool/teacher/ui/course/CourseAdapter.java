package com.xptschool.teacher.ui.course;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xptschool.teacher.R;
import com.xptschool.teacher.model.BeanCourse;
import com.xptschool.teacher.model.GreenDaoHelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CourseAdapter extends BaseAdapter {
    private Context mContext;
    private String TAG = CourseAdapter.class.getSimpleName();

    public List<String> courses = new ArrayList<>();

    public CourseAdapter(Context mContext) {
        super();
        this.mContext = mContext;
    }

    public void loadDate(LinkedHashMap<String, LinkedHashMap<String, String>> linkedCourse) {
        courses.clear();
        courses.add("节次");
        courses.add("周一");
        courses.add("周二");
        courses.add("周三");
        courses.add("周四");
        courses.add("周五");
        courses.add("周六");
        courses.add("周日");

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

        List<BeanCourse> allCourse = GreenDaoHelper.getInstance().getAllCourse();
        for (int i = 0; i < allCourse.size(); i++) {
            if (allCourse.get(i).getName().equals(getItem(position))) {
                viewHolder.txtCourse.setBackgroundColor(mContext.getResources().getColor(R.color.map_railings));
                break;
            }
        }
        String course = getItem(position);
        if (course.length() > 3) {
            viewHolder.txtCourse.setTextSize(10);
        }
        viewHolder.txtCourse.setText(course);
        return convertView;
    }

    class ViewHolder {
        TextView txtCourse;
    }

}
