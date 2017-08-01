package com.xptschool.parent.ui.score;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.android.widget.roundcornerprogressbar.RoundCornerProgressBar;
import com.xptschool.parent.R;
import com.xptschool.parent.bean.BeanExam;
import com.xptschool.parent.bean.BeanScore;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ScoreAdapter extends BaseExpandableListAdapter {

    private String TAG = getClass().getSimpleName();
    private final Context mContext;
    private final LayoutInflater mLayoutInflater;
    private List<BeanExam> listExams = new ArrayList<>();

    public ScoreAdapter(Context context) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void initData(List<BeanExam> datas) {
        listExams.clear();
        listExams.addAll(datas);
        notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        Log.i(TAG, "getGroupCount: " + listExams.size());
        return listExams.size();
    }

    @Override
    public BeanExam getGroup(int groupPosition) {
        return listExams.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.item_exam_group, parent, false);
            viewHolder = new GroupViewHolder();
            viewHolder.txtExamName = (TextView) convertView.findViewById(R.id.txtExamName);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (GroupViewHolder) convertView.getTag();
        }
        BeanExam exam = getGroup(groupPosition);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        ParsePosition pos = new ParsePosition(0);
        Date ringDate = formatter.parse(exam.getStart_time(), pos);
        viewHolder.txtExamName.setText(formatter.format(ringDate) + "—" + exam.getExam_com_type() + "—" + exam.getExam_name());
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {

        return listExams.get(groupPosition).getScores().size();
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return listExams.get(groupPosition).getScores().get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildrenViewHolder viewHolder = null;
//        if (convertView == null) {
//            convertView.setTag(viewHolder);
//        } else {
//            viewHolder = (ChildrenViewHolder) convertView.getTag();
//        }
        convertView = mLayoutInflater.inflate(R.layout.item_exam_childen, parent, false);
        viewHolder = new ChildrenViewHolder();
        viewHolder.txtScoreName = (TextView) convertView.findViewById(R.id.txtScoreName);
        viewHolder.txtScore = (TextView) convertView.findViewById(R.id.txtScore);
        viewHolder.progressBar = (RoundCornerProgressBar) convertView.findViewById(R.id.roundCornerProgressBar);

        BeanScore score = (BeanScore) getChild(groupPosition, childPosition);
        if (score != null) {
            String course_name = score.getCourse_name();
            viewHolder.txtScoreName.setText(course_name);
            viewHolder.txtScore.setText(score.getCourse_score());
            if (!course_name.equals("总分")) {
                String course = course_name.substring(0, 1);
                Log.i(TAG, "getChildView: course " + course);
                int color = R.color.color_other;
                if (course.equals("语")) {
                    color = R.color.color_yuwen;
                } else if (course.equals("数")) {
                    color = R.color.color_shuxue;
                } else if (course.equals("英")) {
                    color = R.color.color_yingyu;
                } else if (course.equals("物")) {
                    color = R.color.color_wuli;
                } else if (course.equals("地")) {
                    color = R.color.color_dili;
                } else if (course.equals("化")) {
                    color = R.color.color_huaxue;
                } else if (course.equals("政") || course.equals("品") || course.equals("思")) {
                    color = R.color.color_zhengzhi;
                } else if (course.equals("生")) {
                    color = R.color.color_shengwu;
                } else if (course.equals("体")) {
                    color = R.color.color_tiyu;
                }

                viewHolder.progressBar.setProgressBackgroundColor(mContext.getResources().getColor(color));

                viewHolder.progressBar.setMax(Float.parseFloat(score.getCourse_score_total()));
                viewHolder.progressBar.setSecondaryProgress(viewHolder.progressBar.getMax());
                viewHolder.progressBar.setSecondaryProgressColor(mContext.getResources().getColor(R.color.white));

                viewHolder.progressBar.setProgressColor(mContext.getResources().getColor(color));
                viewHolder.progressBar.setProgress(Float.parseFloat(score.getCourse_score()));
            } else {
                viewHolder.progressBar.setVisibility(View.GONE);
            }
        }
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    class GroupViewHolder {
        TextView txtExamName;
    }

    class ChildrenViewHolder {
        TextView txtScoreName;
        TextView txtScore;
        RoundCornerProgressBar progressBar;
    }

}
