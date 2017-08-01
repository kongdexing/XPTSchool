package com.xptschool.parent.ui.fence;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.common.VolleyHttpParamsEntity;
import com.android.volley.common.VolleyHttpResult;
import com.android.volley.common.VolleyHttpService;
import com.xptschool.parent.R;
import com.xptschool.parent.bean.BeanRail;
import com.xptschool.parent.common.BroadcastAction;
import com.xptschool.parent.common.CommonUtil;
import com.xptschool.parent.common.ExtraKey;
import com.xptschool.parent.http.HttpAction;
import com.xptschool.parent.http.MyVolleyRequestListener;
import com.xptschool.parent.model.BeanStudent;
import com.xptschool.parent.view.CustomDialog;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FencesAdapter extends BaseExpandableListAdapter {

    private String TAG = getClass().getSimpleName();
    private final Context mContext;
    private final LayoutInflater mLayoutInflater;
    private ArrayList<String> keys = new ArrayList();
    private ArrayList<LinearLayout> llDels = new ArrayList<>();
    private LinkedHashMap<String, ArrayList<Object>> listFences = new LinkedHashMap<>();
    private LinkedHashMap<String, ArrayList<Object>> allListFences = new LinkedHashMap<>();
    private ArrayList<Boolean> groupExpandedStatus = new ArrayList<>();

    public FencesAdapter(Context context) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        IntentFilter filter = new IntentFilter();
        filter.addAction(BroadcastAction.FENCE_MODIFY);
        filter.addAction(BroadcastAction.FENCE_CANCEL);
        mContext.registerReceiver(fenceStatusReceiver, filter);
    }

    public void loadContacts(LinkedHashMap<String, ArrayList<Object>> contacts) {
        allListFences = contacts;
        initData(allListFences);
    }

    private void initData(LinkedHashMap<String, ArrayList<Object>> contacts) {
        listFences = contacts;
        keys = new ArrayList<>(listFences.keySet());
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
        return listFences.get(keys.get(groupPosition)).size();
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return listFences.get(keys.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildrenViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.item_fences, parent, false);
            viewHolder = new ChildrenViewHolder();
            viewHolder.llDel = (LinearLayout) convertView.findViewById(R.id.llDel);
            viewHolder.text = (TextView) convertView.findViewById(R.id.text);
            viewHolder.llAdd = (LinearLayout) convertView.findViewById(R.id.llAdd);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ChildrenViewHolder) convertView.getTag();
        }

        final Object object = getChild(groupPosition, childPosition);

        if (object instanceof BeanStudent) {
            final BeanStudent student = (BeanStudent) object;
            viewHolder.text.setVisibility(View.GONE);
            viewHolder.llAdd.setVisibility(View.VISIBLE);
            viewHolder.llAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, FenceDrawActivity.class);
                    //学生id
                    intent.putExtra(ExtraKey.STUDENT_ID, student);
                    ((FenceListActivity) mContext).startActivityForResult(intent, 1);
                }
            });

        } else if (object instanceof BeanRail) {
            final BeanRail rail = (BeanRail) object;
            viewHolder.text.setVisibility(View.VISIBLE);
            viewHolder.llAdd.setVisibility(View.GONE);
            viewHolder.text.setText(rail.getName());
            viewHolder.text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, FenceShowActivity.class);
                    intent.putExtra(ExtraKey.FENCE_ID, rail);
                    mContext.startActivity(intent);
                }
            });
            llDels.add(viewHolder.llDel);
            viewHolder.llDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CustomDialog dialog = new CustomDialog(mContext);
                    dialog.setTitle(R.string.label_fence_list);
                    dialog.setMessage(R.string.msg_fence_delete);
                    dialog.setAlertDialogClickListener(new CustomDialog.DialogClickListener() {
                        @Override
                        public void onPositiveClick() {
                            deleteFence(rail.getSr_id());
                        }
                    });
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

    private void deleteFence(final String sr_id) {

        VolleyHttpService.getInstance().sendPostRequest(HttpAction.Track_deleteStudentFence,
                new VolleyHttpParamsEntity()
                        .addParam("sr_id", sr_id)
                        .addParam("token", CommonUtil.encryptToken(HttpAction.Track_deleteStudentFence)),
                new MyVolleyRequestListener() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        ((FenceListActivity) mContext).showProgress(R.string.msg_fence_deleteing);
                    }

                    @Override
                    public void onResponse(VolleyHttpResult volleyHttpResult) {
                        super.onResponse(volleyHttpResult);
                        ((FenceListActivity) mContext).hideProgress();
                        Toast.makeText(mContext, volleyHttpResult.getInfo(), Toast.LENGTH_LONG).show();
                        if (volleyHttpResult.getStatus() == HttpAction.SUCCESS) {
                            Iterator iter = allListFences.entrySet().iterator();
                            while (iter.hasNext()) {
                                Map.Entry entry = (Map.Entry) iter.next();
                                String key = (String) entry.getKey();
                                List<Object> val = (ArrayList) entry.getValue();
                                for (int i = 0; i < val.size(); i++) {
                                    Object object = val.get(i);
                                    if (object instanceof BeanRail) {
                                        BeanRail rail = (BeanRail) object;
                                        if (rail.getSr_id().equals(sr_id)) {
                                            val.remove(rail);
                                            break;
                                        }
                                    }
                                }
                            }
                            initData(allListFences);
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        super.onErrorResponse(volleyError);
                        ((FenceListActivity) mContext).hideProgress();
                    }
                });
    }

    BroadcastReceiver fenceStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BroadcastAction.FENCE_MODIFY)) {
                for (int i = 0; i < llDels.size(); i++) {
                    TranslateAnimation mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 1.0f,
                            Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                            0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
                    mShowAction.setDuration(300);
                    llDels.get(i).startAnimation(mShowAction);
                    llDels.get(i).setVisibility(View.VISIBLE);
                }
            } else if (action.equals(BroadcastAction.FENCE_CANCEL)) {
                for (int i = 0; i < llDels.size(); i++) {

                    TranslateAnimation mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                            0.0f, Animation.RELATIVE_TO_SELF, 1.0f,
                            Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                            0.0f);
                    mHiddenAction.setDuration(300);
                    llDels.get(i).startAnimation(mHiddenAction);

                    llDels.get(i).setVisibility(View.GONE);
                }
            }
        }
    };

    class GroupViewHolder {
        TextView groupName;
        ImageView imgArrow;
    }

    class ChildrenViewHolder {
        LinearLayout llDel;
        TextView text;
        LinearLayout llAdd;
    }

}
