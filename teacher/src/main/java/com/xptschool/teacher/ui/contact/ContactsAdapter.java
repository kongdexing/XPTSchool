package com.xptschool.teacher.ui.contact;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xptschool.teacher.R;
import com.xptschool.teacher.bean.ContactType;
import com.xptschool.teacher.common.ExtraKey;
import com.xptschool.teacher.model.ContactStudent;
import com.xptschool.teacher.model.ContactTeacher;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ContactsAdapter extends BaseExpandableListAdapter {

    private String TAG = getClass().getSimpleName();
    private final Context mContext;
    private final LayoutInflater mLayoutInflater;
    private ArrayList<String> keys = new ArrayList();

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
            ArrayList<Object> teachers = new ArrayList<Object>();
            ArrayList<Object> students = new ArrayList<Object>();
            Iterator iter = allListContacts.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                String key = (String) entry.getKey();
                List<Object> val = (ArrayList) entry.getValue();
                if (key.equals(ContactType.TEACHER.toString())) {
                    for (int i = 0; i < val.size(); i++) {
                        ContactTeacher teacher = (ContactTeacher) val.get(i);
                        if (teacher.getName().contains(name)) {
                            teachers.add(teacher);
                        }
                    }
                } else {
                    for (int i = 0; i < val.size(); i++) {
                        ContactStudent student = (ContactStudent) val.get(i);
                        if (student.getStu_name().contains(name)) {
                            students.add(student);
                        }
                    }
                }
            }
            LinkedHashMap<String, ArrayList<Object>> mSearchContacts = new LinkedHashMap<>();
            if (teachers.size() > 0) {
                mSearchContacts.put(ContactType.TEACHER.toString(), teachers);
            }
            if (students.size() > 0) {
                mSearchContacts.put(ContactType.STUDENT.toString(), students);
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
//        return listContacts.size();
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
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.item_contacts_group, parent, false);
        }

        final TextView text = (TextView) convertView.findViewById(R.id.text);
        text.setText(keys.get(groupPosition));
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
            viewHolder = new ChildrenViewHolder();
            viewHolder.llContacts = (LinearLayout) convertView.findViewById(R.id.llContacts);
            viewHolder.text = (TextView) convertView.findViewById(R.id.text);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ChildrenViewHolder) convertView.getTag();
        }

        if (getGroup(groupPosition).equals(ContactType.TEACHER.toString())) {
            final ContactTeacher teacher = (ContactTeacher) getChild(groupPosition, childPosition);
            viewHolder.text.setText(teacher.getName());

            viewHolder.llContacts.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, ContactsDetailActivity.class);
                    intent.putExtra(ExtraKey.CONTACT_TYPE, ExtraKey.CONTACT_TEACHER);
                    intent.putExtra(ExtraKey.CONTACT, teacher);
                    mContext.startActivity(intent);
                }
            });
        } else {
            final ContactStudent student = (ContactStudent) getChild(groupPosition, childPosition);
            viewHolder.text.setText(student.getStu_name());

            viewHolder.llContacts.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, ContactsDetailActivity.class);
                    intent.putExtra(ExtraKey.CONTACT_TYPE, ExtraKey.CONTACT_STUDENT);
                    intent.putExtra(ExtraKey.CONTACT, student);
                    mContext.startActivity(intent);
                }
            });
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

    class ChildrenViewHolder {
        LinearLayout llContacts;
        TextView text;
    }

}
