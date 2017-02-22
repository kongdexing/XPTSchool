package com.xptschool.parent.ui.cardset;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xptschool.parent.R;
import com.xptschool.parent.common.BroadcastAction;
import com.xptschool.parent.util.CardWhiteListClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.VISIBLE;

/**
 * Created by dexing on 2017/1/11.
 * No1
 */

public class WhiteListAdapter extends BaseAdapter {

    private String[] whitelists = null;
    private Context mContext;
    private CardWhiteListClickListener mListener;
    private List<WhiteCardView> allCardWhite = new ArrayList<>();
    private int maxLength = 10; //最多设置号码个数

    public WhiteListAdapter(Context context) {
        super();
        mContext = context;
        whitelists = new String[maxLength];
        for (int i = 0; i < maxLength; i++) {
            whitelists[i] = "";
        }
    }

    public void loadData(String[] whites, CardWhiteListClickListener listener) {
        if (whites != null) {
            try {
                int length = whites.length;
                if (length > maxLength) {
                    length = maxLength;
                }
                for (int i = 0; i < length; i++) {
                    whitelists[i] = whites[i];
                }
            } catch (Exception ex) {

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
    public View getView(int i, View view, ViewGroup viewGroup) {
        WhiteCardView cardWhiteListView = null;
        if (view == null) {
            cardWhiteListView = new WhiteCardView(mContext);
            cardWhiteListView.bindData(i + 1, getCount(), getItem(i), mListener);
        } else {
            cardWhiteListView = (WhiteCardView)view;
        }

        if (!allCardWhite.contains(cardWhiteListView)) {
            allCardWhite.add(cardWhiteListView);
        }
        return cardWhiteListView;
    }

    public List<WhiteCardView> getCardWhiteViews() {
        return allCardWhite;
    }

    class ViewHolder {

        @BindView(R.id.txtPhoneName1)
        TextView txtPhoneName1;
        @BindView(R.id.edtPhoneName)
        EditText edtPhoneName;
        @BindView(R.id.edtPhone)
        EditText edtPhone;

        @BindView(R.id.imgDel1)
        ImageView imgDel1;
        @BindView(R.id.imgDel2)
        ImageView imgDel2;
        @BindView(R.id.imgContract)
        ImageView imgContract;
        @BindView(R.id.btnOk)
        Button btnOk;

        public ViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
        }
    }

}
