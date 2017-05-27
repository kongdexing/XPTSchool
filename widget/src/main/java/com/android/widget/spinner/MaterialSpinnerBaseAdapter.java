/*
 * Copyright (C) 2016 Jared Rummler <jared.rummler@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.android.widget.spinner;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.widget.R;

import java.util.List;

public abstract class MaterialSpinnerBaseAdapter<T> extends BaseAdapter {

    private final Context context;
    private int selectedIndex;
    private int textColor;
    private float textSize;
    private int paddingLeft = 0;

    public MaterialSpinnerBaseAdapter(Context context) {
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.ms__list_item, parent, false);
            viewHolder.textView = (TextView) convertView.findViewById(R.id.tv_tinted_spinner);
            viewHolder.view_line = convertView.findViewById(R.id.view_line);
            viewHolder.textView.setTextColor(textColor);
            viewHolder.textView.setPadding(paddingLeft, 0, paddingLeft, 0);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                Configuration config = context.getResources().getConfiguration();
                if (config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
                    viewHolder.textView.setTextDirection(View.TEXT_DIRECTION_RTL);
                }
            }
            convertView.setTag(viewHolder);
        } else {
            viewHolder = ((ViewHolder) convertView.getTag());
        }
        Object item = getItem(position);
        if (item instanceof String) {
            viewHolder.textView.setText(item.toString());
        } else if (item instanceof SpinnerModel) {
            viewHolder.textView.setText(((SpinnerModel) item).getName());
        }
        if (position == 0) {
            viewHolder.view_line.setVisibility(View.GONE);
        } else {
            viewHolder.view_line.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void notifyItemSelected(int index) {
        selectedIndex = index;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public abstract T getItem(int position);

    @Override
    public abstract int getCount();

    public abstract T get(int position);

    public abstract List<T> getItems();

    public MaterialSpinnerBaseAdapter<T> setTextColor(int textColor) {
        this.textColor = textColor;
        return this;
    }

    public MaterialSpinnerBaseAdapter<T> setTextSize(float textSize) {
        this.textSize = textSize;
        return this;
    }

    public MaterialSpinnerBaseAdapter<T> setPaddingLeft(int paddingLeft) {
        this.paddingLeft = paddingLeft;
        return this;
    }

    private static class ViewHolder {

        private TextView textView;
        private View view_line;

    }

}
