package com.xptschool.teacher.ui.mine;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xptschool.teacher.R;
import com.xptschool.teacher.adapter.BaseRecycleAdapter;
import com.xptschool.teacher.adapter.RecyclerViewHolderBase;
import com.xptschool.teacher.common.ExtraKey;
import com.xptschool.teacher.model.BeanMyClass;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2016/11/09.
 */
public class MyClassAdapter extends BaseRecycleAdapter {

    private List<BeanMyClass> listClasses = new ArrayList<>();

    public MyClassAdapter(Context context) {
        super(context);
    }

    public void refreshData(List<BeanMyClass> myClass) {
        listClasses.clear();
        listClasses.addAll(myClass);
        Log.i(TAG, "refreshData: " + listClasses.size());
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_class, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final MyClassAdapter.ViewHolder mHolder = (MyClassAdapter.ViewHolder) holder;
        Log.i(TAG, "onBindViewHolder: " + position);
        final BeanMyClass beanClass = listClasses.get(position);
        mHolder.txtClassName.setText(beanClass.getG_name() + beanClass.getName());
        mHolder.txtClassAdviser.setText(beanClass.getT_name());
        mHolder.txtStudentCount.setText(beanClass.getStu_count());
        mHolder.txtRemark.setText(beanClass.getMemo());

        mHolder.llClassItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, StudentsActivity.class);
                intent.putExtra(ExtraKey.CLASS_ID, beanClass.getC_id());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.i(TAG, "getItemCount: " + listClasses.size());
        return listClasses.size();
    }

    public class ViewHolder extends RecyclerViewHolderBase {
        private Unbinder unbinder;
        @BindView(R.id.llClassItem)
        LinearLayout llClassItem;

        @BindView(R.id.txtClassName)
        TextView txtClassName;

        @BindView(R.id.txtClassAdviser)
        TextView txtClassAdviser;

        @BindView(R.id.txtStudentCount)
        TextView txtStudentCount;

        @BindView(R.id.txtRemark)
        TextView txtRemark;

        public ViewHolder(View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this, itemView);
        }

    }

}
