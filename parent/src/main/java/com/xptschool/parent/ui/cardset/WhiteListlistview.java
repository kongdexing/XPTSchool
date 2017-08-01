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
import android.widget.Toast;

import com.xptschool.parent.R;
import com.xptschool.parent.common.BroadcastAction;
import com.xptschool.parent.common.CommonUtil;
import com.xptschool.parent.util.CardWhiteListClickListener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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

        View buttonView = LayoutInflater.from(context).inflate(R.layout.content_card_whitelist_button, null, false);
//        buttonView.setLayoutParams(lp);
        buttonView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.sendBroadcast(new Intent(BroadcastAction.WHITELIST_OKCLICK));
            }
        });
        listWhite = (ListView) view.findViewById(R.id.list_white);
//        listWhite.addFooterView(buttonView);

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
//                List<WhiteCardView> views = adapter.getCardWhiteViews();
//                int size = views.size();

                HashMap<Integer, String> whiteNumbers = adapter.getWhiteNumbers();
                Iterator iter = whiteNumbers.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    int key = (int) entry.getKey();
                    String value = (String) entry.getValue();
                    if (value == null || !value.contains(":")) {
                        continue;
                    }
                    String[] nameNums = value.split(":");

                    String name = "";
                    String phone = "";

                    if (nameNums.length > 0) {
                        name = nameNums[0];
                    }
                    if (nameNums.length > 1) {
                        phone = nameNums[1];
                    }

                    if (name.isEmpty() && phone.isEmpty()) {
                        continue;
                    }

                    if (name.isEmpty() && !phone.isEmpty()
                            || !name.isEmpty() && phone.isEmpty()) {
                        //其中一个为空
                        Toast.makeText(mContext, R.string.toast_contract_other_empty, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (!CommonUtil.isPhone(phone)) {
                        Toast.makeText(mContext, R.string.input_error_phone, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (!value.isEmpty()) {
                        values += value + ",";
                    }
                    Log.i("white", " value " + value);
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
