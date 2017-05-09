package com.xptschool.teacher.ui.mine;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xptschool.teacher.R;
import com.xptschool.teacher.bean.BeanStudent;
import com.xptschool.teacher.view.FilterImageView;

import java.util.ArrayList;
import java.util.List;

public class StudentAdapter extends BaseAdapter {
    private Context mContext;

    public List<BeanStudent> students = new ArrayList<>();
    private MyGridViewClickListener myGridViewClickListener;

    public StudentAdapter(Context mContext) {
        super();
        this.mContext = mContext;
//        for (int i = 0; i < 50; i++) {
//            BeanStudent student = new BeanStudent();
//            student.setStudentSex("1");
//            student.setStudentImg("http://h.hiphotos.bdimg.com/imgad/pic/item/d4628535e5dde7112fe2b5f5a0efce1b9d166185.jpg");
//            student.setStudentName("王小丫" + i);
//            student.setStudentCode("" + i);
//            students.add(student);
//        }
    }

    public void loadStudent(List<BeanStudent> _students) {
        this.students = _students;
        notifyDataSetChanged();
    }

    public void setMyGridViewClickListener(MyGridViewClickListener listener) {
        this.myGridViewClickListener = listener;
    }

    @Override
    public int getCount() {
        return students.size();
    }

    @Override
    public BeanStudent getItem(int position) {
        return students.get(position);
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
                    R.layout.item_student, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.llStudent = (LinearLayout) convertView.findViewById(R.id.llStudent);
            viewHolder.imageView = (FilterImageView) convertView.findViewById(R.id.imgStudent);
            viewHolder.txtStudentCode = (TextView) convertView.findViewById(R.id.txtStudentCode);
            viewHolder.txtStudentName = (TextView) convertView.findViewById(R.id.txtStudentName);
            viewHolder.imgSex = (ImageView) convertView.findViewById(R.id.imgSex);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final BeanStudent student = getItem(position);
        viewHolder.llStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myGridViewClickListener != null) {
                    myGridViewClickListener.onGridViewItemClick(student);
                }
            }
        });
        if (student.getSex().equals("0")) {
            viewHolder.imgSex.setBackgroundResource(R.drawable.ico_female);
        } else {
            viewHolder.imgSex.setBackgroundResource(R.drawable.ico_male);
        }

        viewHolder.txtStudentName.setText(student.getStu_name());
        viewHolder.txtStudentCode.setText("学号:" + student.getStu_no());
        return convertView;
    }

    class ViewHolder {
        LinearLayout llStudent;
        FilterImageView imageView;
        TextView txtStudentName;
        TextView txtStudentCode;
        ImageView imgSex;
    }

    public interface MyGridViewClickListener {
        void onGridViewItemClick(BeanStudent student);
    }

}
