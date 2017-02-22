package com.xptschool.parent.ui.cardset;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.xptschool.parent.R;
import com.xptschool.parent.common.BroadcastAction;
import com.xptschool.parent.util.CardWhiteListClickListener;

import java.util.List;

/**
 * Created by dexing on 2017/1/11.
 * No1
 */
public class WhiteListlistview extends LinearLayout {

    private Context mContext;
    private ListView listWhite;
    private WhiteListAdapter adapter;
    private CardWhiteListClickListener mListener;

    public WhiteListlistview(Context context) {
        this(context, null);
        mContext = context;
        context.registerReceiver(btnOkClickReceiver, new IntentFilter(BroadcastAction.WHITELIST_OKCLICK));
    }

    public WhiteListlistview(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.content_card_whitelist_listview, this, true);
        listWhite = (ListView) view.findViewById(R.id.list_white);
        adapter = new WhiteListAdapter(context);
        listWhite.setAdapter(adapter);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        try {
            mContext.unregisterReceiver(btnOkClickReceiver);
        } catch (Exception ex) {

        }
    }

    public void bindDatas(String datas, CardWhiteListClickListener listener) {
        mListener = listener;
        String[] whitelists = null;
        if (datas != null) {
            whitelists = datas.split(",");
        }
        adapter.loadData(whitelists, listener);
    }

    BroadcastReceiver btnOkClickReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BroadcastAction.WHITELIST_OKCLICK)) {
                String values = "";
                List<WhiteCardView> views = adapter.getCardWhiteViews();
                int size = views.size();
                Log.i("white", "onReceive: child size " + size);

                for (int i = 0; i < size; i++) {
                    WhiteCardView cardWhiteList = views.get(i);
                    String value = cardWhiteList.getInputValue();
                    if (value == null) {
                        return;
                    }
                    if (!value.isEmpty()) {
                        values += value + ",";
                    }
                    Log.i("white", i + " value " + value);
                }

                if (values.length() > 1) {
                    values = values.substring(0, values.length() - 1);
                }

                if (mListener != null) {
                    mListener.onBtnOkClick(values);
                }
            }
        }
    };

}
