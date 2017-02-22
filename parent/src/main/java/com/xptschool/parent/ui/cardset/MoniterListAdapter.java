package com.xptschool.parent.ui.cardset;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xptschool.parent.R;
import com.xptschool.parent.util.CardWhiteListClickListener;

/**
 * Created by dexing on 2017/1/12.
 * No1
 */

public class MoniterListAdapter extends BaseAdapter {

    private String[] whitelists = new String[]{};
    private String mMoniter;
    private Context mContext;
    private CardWhiteListClickListener mListener;
    private String TAG = MoniterListAdapter.class.getSimpleName();
    private int checkedPisition = -1;

    public MoniterListAdapter(Context context) {
        super();
        mContext = context;
    }

    public void loadData(String[] whites, String moniter, CardWhiteListClickListener listener) {
        whitelists = whites;
        mMoniter = moniter;
        if (whitelists == null) {
            whitelists = new String[]{};
        }

        if (mMoniter != null && mMoniter.contains(",")) {
            String moniterPhone = mMoniter.replace(",", ":");
            for (int i = 0; i < whitelists.length; i++) {
                if (whitelists[i].equals(moniterPhone)) {
                    checkedPisition = i;
                    break;
                }
            }
        }

        mListener = listener;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return whitelists.length;
    }

    @Override
    public String getItem(int i) {
        return whitelists[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View contentView, ViewGroup viewGroup) {
        contentView = LayoutInflater.from(mContext).inflate(R.layout.content_card_moniter, viewGroup, false);
        RelativeLayout rlWhite = (RelativeLayout) contentView.findViewById(R.id.rlWhite);
        TextView txtPhoneName = (TextView) contentView.findViewById(R.id.txtPhoneName);
        TextView txtPhone = (TextView) contentView.findViewById(R.id.txtPhone);
        final CheckBox checkBox = (CheckBox) contentView.findViewById(R.id.checkBox);
        Button btnOk = (Button) contentView.findViewById(R.id.btnOk);

        String white = getItem(position);
        String[] datas = white.split(":");
        txtPhoneName.setText(datas[0]);
        txtPhone.setText(datas[1]);
        String moniterPhone = mMoniter.replace(",", ":");
        if (moniterPhone.equals(white)) {
            rlWhite.setBackgroundColor(mContext.getResources().getColor(R.color.transparent_gray_map));
        }

        if (position == checkedPisition) {
            checkBox.setChecked(true);
        } else {
            checkBox.setChecked(false);
        }

        rlWhite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkedPisition != position) {
                    checkedPisition = position;
                    notifyDataSetChanged();
                } else {
                    checkedPisition = -1;
                    checkBox.setChecked(!checkBox.isChecked());
                }
            }
        });

        if (position == getCount() - 1) {
            btnOk.setVisibility(View.VISIBLE);
        } else {
            btnOk.setVisibility(View.GONE);
        }

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onBtnOkClick(checkedPisition == -1 ? "" : getItem(checkedPisition).replace(":", ","));
                }
            }
        });
        return contentView;
    }

}
