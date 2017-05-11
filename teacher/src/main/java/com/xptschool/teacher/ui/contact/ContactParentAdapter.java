package com.xptschool.teacher.ui.contact;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xptschool.teacher.R;
import com.xptschool.teacher.adapter.BaseRecycleAdapter;
import com.xptschool.teacher.adapter.RecyclerViewHolderBase;
import com.xptschool.teacher.model.ContactParent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by dexing on 2016/12/09.
 */
public class ContactParentAdapter extends BaseRecycleAdapter {

    private List<ContactParent> contactParents = new ArrayList<>();
    private Context mContext;
    private OnParentPhoneClickListener phoneClickListener;

    public ContactParentAdapter(Context context) {
        super(context);
        mContext = context;
    }

    public void setPhoneClickListener(OnParentPhoneClickListener phoneClickListener) {
        this.phoneClickListener = phoneClickListener;
    }

    public void refreshDate(List<ContactParent> list) {
        contactParents = list;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.contact_parent, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ContactParentAdapter.ViewHolder mHolder = (ContactParentAdapter.ViewHolder) holder;
        final ContactParent parent = contactParents.get(position);

        mHolder.txtParentName.setText(parent.getName());
        mHolder.txtParentPhone.setText(parent.getPhone());

        mHolder.llParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (phoneClickListener != null) {
                    phoneClickListener.onPhoneClickListener(parent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactParents.size();
    }

    public class ViewHolder extends RecyclerViewHolderBase {
        private Unbinder unbinder;
        @BindView(R.id.llParent)
        LinearLayout llParent;

        @BindView(R.id.txtParentName)
        TextView txtParentName;

        @BindView(R.id.txtParentPhone)
        TextView txtParentPhone;

        public ViewHolder(View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this, itemView);
        }

    }

    public interface OnParentPhoneClickListener {
        void onPhoneClickListener(ContactParent parent);
    }

}
