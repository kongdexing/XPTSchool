package com.xptschool.parent.ui.course;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xptschool.parent.R;
import com.xptschool.parent.util.ToastUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CourseAdapter extends BaseAdapter {
    private Context mContext;
    private String TAG = CourseAdapter.class.getSimpleName();

    private GridView mGridView;
    public List<String> courses = new ArrayList<>();
    public List<String> courseWeek = new ArrayList<>();
    public List<Integer> rowHeights = new ArrayList<>();
    private boolean reDraw = false;

    public CourseAdapter(Context mContext, GridView gridView) {
        super();
        this.mContext = mContext;
        this.mGridView = gridView;
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

        for (int i = 0; i < 8; i++) {
            courseWeek.add((i + 1) + "");
            rowHeights.add(0);
        }
        rowHeights.add(0);

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
            viewHolder.rlContent = (RelativeLayout) convertView.findViewById(R.id.rlContent);
            viewHolder.txtCourse = (TextView) convertView.findViewById(R.id.txtCourse);
            viewHolder.txtCourse.setTag(position);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final String course = getItem(position);
        viewHolder.txtCourse.setText(course);

        if (course.length() > 3) {
            viewHolder.txtCourse.setGravity(Gravity.LEFT);
            viewHolder.txtCourse.setTextSize(12);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!course.isEmpty() && !courseWeek.contains(course)) {
                    ToastUtils.showToast(mContext, course);
                }
            }
        });

        int rowHeight = rowHeights.get(position / CourseHelper.GRIVEW_COLUMN_NUMS);
        if (rowHeight == 0) {
            //统计最高行
            initKeyTextView(viewHolder.txtCourse, position);
        } else {
            //重新绘制
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    rowHeight);
            lp.setMargins(5, 5, 5, 5);
            viewHolder.txtCourse.setLayoutParams(lp);
        }
        return convertView;
    }

    class ViewHolder {
        RelativeLayout rlContent;
        TextView txtCourse;
    }

    public void initKeyTextView(final View courseView, final int position) {
        ViewTreeObserver vto2 = courseView.getViewTreeObserver();
        vto2.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                courseView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                if (position % CourseHelper.GRIVEW_COLUMN_NUMS == 0) {
                    CourseHelper.GRIVIEW_COLUMN_HEIGHT = 0;
                }
                if (courseView.getHeight() > CourseHelper.GRIVIEW_COLUMN_HEIGHT) {
                    CourseHelper.GRIVIEW_COLUMN_HEIGHT = courseView.getHeight();
                }
                rowHeights.set(position / CourseHelper.GRIVEW_COLUMN_NUMS, CourseHelper.GRIVIEW_COLUMN_HEIGHT);

                if (position == getCount() - 1) {
                    if (!reDraw) {
                        reDraw = true;
                        notifyDataSetChanged();
                    }
                }
            }
        });
    }


}
