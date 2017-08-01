package com.xptschool.parent.ui.contact;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.widget.view.CircularImageView;
import com.xptschool.parent.R;
import com.xptschool.parent.common.ExtraKey;
import com.xptschool.parent.model.ContactSchool;
import com.xptschool.parent.model.ContactTeacher;
import com.xptschool.parent.model.GreenDaoHelper;
import com.xptschool.parent.ui.chat.ChatActivity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContactsAdapter extends BaseExpandableListAdapter {

    private String TAG = getClass().getSimpleName();
    private final Context mContext;
    private final LayoutInflater mLayoutInflater;
    private ArrayList<String> keys = new ArrayList();
    private ArrayList<Boolean> groupExpandedStatus = new ArrayList<>();

    private LinkedHashMap<String, ArrayList<Object>> listContacts = new LinkedHashMap<>();
    private LinkedHashMap<String, ArrayList<Object>> allListContacts = new LinkedHashMap<>();

    public ContactsAdapter(Context context) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void reloadByName(String name) {
        if (name.isEmpty()) {
            initData(allListContacts);
        } else {
            LinkedHashMap<String, ArrayList<Object>> mSearchContacts = new LinkedHashMap<>();
            Iterator iter = allListContacts.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                String key = (String) entry.getKey();
                List<Object> val = (ArrayList) entry.getValue();
                ArrayList<Object> teachers = new ArrayList<>();

                for (int i = 0; i < val.size(); i++) {
                    Object object = val.get(i);
                    if (object instanceof ContactTeacher) {
                        ContactTeacher teacher = (ContactTeacher) object;
                        if (teacher.getName().contains(name)) {
                            teachers.add(teacher);
                        }
                    }
                }
                if (teachers.size() > 0) {
                    mSearchContacts.put(key, teachers);
                }
            }

            initData(mSearchContacts);
        }
    }

    public void loadContacts(LinkedHashMap<String, ArrayList<Object>> contacts) {
        allListContacts = contacts;
        initData(allListContacts);
    }

    private void initData(LinkedHashMap<String, ArrayList<Object>> contacts) {
        listContacts = contacts;
        keys = new ArrayList<>(listContacts.keySet());
        notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        Log.i(TAG, "getGroupCount: " + keys.size());
        return keys.size();
    }

    @Override
    public String getGroup(int groupPosition) {
        return keys.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new GroupViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.item_contacts_group, parent, false);
            viewHolder.groupName = (TextView) convertView.findViewById(R.id.text);
            viewHolder.imgArrow = (ImageView) convertView.findViewById(R.id.imgArrow);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (GroupViewHolder) convertView.getTag();
        }

        boolean expanded = isExpanded;

        if (groupExpandedStatus.size() > groupPosition) {
            expanded = groupExpandedStatus.get(groupPosition);
        } else {
            groupExpandedStatus.add(isExpanded);
        }

        viewHolder.groupName.setText(keys.get(groupPosition));
        Animation rotateAnimation = null;

        if (expanded != isExpanded) {
            if (isExpanded) {
                rotateAnimation = new
                        RotateAnimation(-90f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            } else {
                rotateAnimation = new
                        RotateAnimation(0f, -90f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            }
            rotateAnimation.setFillAfter(true); // 设置保持动画最后的状态
            rotateAnimation.setDuration(300); // 设置动画时间
            viewHolder.imgArrow.startAnimation(rotateAnimation);
            groupExpandedStatus.set(groupPosition, isExpanded);
        }
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return listContacts.get(keys.get(groupPosition)).size();
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return listContacts.get(keys.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildrenViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.item_contacts, parent, false);
            viewHolder = new ChildrenViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ChildrenViewHolder) convertView.getTag();
        }

        final Object object = getChild(groupPosition, childPosition);
        if (object instanceof ContactSchool) {
            final ContactSchool school = (ContactSchool) object;
            viewHolder.imgHead.setImageResource(R.drawable.contacts_school);
            viewHolder.text.setText(school.getS_name() + school.getA_name());
            viewHolder.llContacts.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, ContactsDetailActivity.class);
                    intent.putExtra(ExtraKey.CONTACT_TYPE, ExtraKey.CONTACT_SCHOOL);
                    intent.putExtra(ExtraKey.CONTACT, school);
                    mContext.startActivity(intent);
                }
            });
            viewHolder.txtUnReadNum.setVisibility(View.GONE);
        } else {
            final ContactTeacher teacher = (ContactTeacher) object;
            if (teacher.getSex().equals("1")) {
                viewHolder.imgHead.setImageResource(R.drawable.teacher_man);
            } else {
                viewHolder.imgHead.setImageResource(R.drawable.teacher_woman);
            }
            viewHolder.text.setText(teacher.getName());
            viewHolder.llContacts.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, ChatActivity.class);
                    intent.putExtra(ExtraKey.CHAT_TEACHER, teacher);
                    mContext.startActivity(intent);

//                    Intent intent = new Intent(mContext, ContactsDetailActivity.class);
//                    intent.putExtra(ExtraKey.CONTACT_TYPE, ExtraKey.CONTACT_TEACHER);
//                    intent.putExtra(ExtraKey.CONTACT, teacher);
//                    mContext.startActivity(intent);
                }
            });
            //读取老师未读消息
            int unReadNum = GreenDaoHelper.getInstance().getUnReadNumByTeacherId(teacher.getU_id());
            if (unReadNum > 0) {
                viewHolder.txtUnReadNum.setText(unReadNum + "");
                viewHolder.txtUnReadNum.setVisibility(View.VISIBLE);
            } else {
                viewHolder.txtUnReadNum.setVisibility(View.GONE);
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
        TextView groupName;
        ImageView imgArrow;
    }

    class ChildrenViewHolder {
        @BindView(R.id.llContacts)
        RelativeLayout llContacts;
        @BindView(R.id.imgHead)
        CircularImageView imgHead;
        @BindView(R.id.txtUnReadNum)
        TextView txtUnReadNum;
        @BindView(R.id.text)
        TextView text;

        public ChildrenViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
        }
    }

}
