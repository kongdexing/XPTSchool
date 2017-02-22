package com.xptschool.parent.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

public abstract class BaseRecycleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public Context mContext;
    public String TAG = "";

    public BaseRecycleAdapter(Context context) {
        mContext = context;
//        context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
//        mBackground = mTypedValue.resourceId;
        TAG = getClass().getSimpleName();
//        Log.i(TAG, "BaseRecycleAdapter: ");
    }

}
