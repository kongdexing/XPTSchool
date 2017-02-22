package com.xptschool.parent.ui.cardset;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.xptschool.parent.R;
import com.xptschool.parent.util.CardWhiteListClickListener;

/**
 * Created by dexing on 2017/1/12.
 * No1
 */

public class MoniterListview extends LinearLayout {

    private TextView txtHelp;
    private TextView txtTip;
    private ListView listWhite;
    private MoniterListAdapter adapter;

    public MoniterListview(Context context) {
        this(context, null);
    }

    public MoniterListview(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.content_card_moniter_listview, this, true);
        listWhite = (ListView) view.findViewById(R.id.list_white);
        txtHelp = (TextView) view.findViewById(R.id.txtHelp);
        txtTip = (TextView) view.findViewById(R.id.txtTip);

        adapter = new MoniterListAdapter(context);
        listWhite.setAdapter(adapter);
    }

    //白名单，监听号码
    public void bindDatas(String datas, String moniter, CardWhiteListClickListener listener) {
        //判断是否有白名单，判断监听号码时候存在于白名单中
        if (datas == null || datas.isEmpty()) {
            txtTip.setVisibility(VISIBLE);
            return;
        }

        boolean exist = false;
        String[] whitelists = datas.split(",");
        if (moniter.contains(",")) {
            String[] moniters = moniter.split(",");
            String moniterName = moniters[0];
            String moniterPhone = moniters[1];
            for (int i = 0; i < whitelists.length; i++) {
                try {
                    String[] whites = whitelists[i].split(":");
                    String name = whites[0];
                    String phone = whites[1];
                    if (name.equals(moniterName) && phone.equals(moniterPhone)) {
                        exist = true;
                        break;
                    }
                } catch (Exception ex) {

                }
            }
        }

        if (!exist) {
            txtTip.setVisibility(VISIBLE);
            txtTip.setText("当前设置的监听号码为：" + moniter + "\r\n 该号码不在白名单中，请重新设置");
        }

        adapter.loadData(whitelists, moniter, listener);
    }

}
