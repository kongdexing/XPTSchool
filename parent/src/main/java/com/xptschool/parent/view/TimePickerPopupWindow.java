package com.xptschool.parent.view;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.widget.calendar.CalendarDay;
import com.android.widget.calendar.MaterialCalendarView;
import com.android.widget.wheelview.WheelView;
import com.android.widget.wheelview.adapter.NumericWheelAdapter;
import com.xptschool.parent.R;
import com.xptschool.parent.XPTApplication;
import com.xptschool.parent.common.CommonUtil;
import com.xptschool.parent.view.decorators.HighlightWeekendsDecorator;
import com.xptschool.parent.view.decorators.MySelectorDecorator;
import com.xptschool.parent.view.decorators.OneDayDecorator;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class TimePickerPopupWindow extends PopupWindow implements View.OnClickListener {

    public interface OnTimePickerClickListener {
        void onTimePickerResult(String result);
    }

    private String TAG = TimePickerPopupWindow.class.getSimpleName();
    private Context mContext;
    MaterialCalendarView calendarView;
    private WheelView hourWheelView, minuteWheelView;
    private final OneDayDecorator oneDayDecorator = new OneDayDecorator();
    private OnTimePickerClickListener onTimePickerClickListener;

    public TimePickerPopupWindow(Context context, String checkedTime, OnTimePickerClickListener listener) {
        mContext = context;
        onTimePickerClickListener = listener;
        final View view = LayoutInflater.from(context).inflate(R.layout.widget_time, null);
        calendarView = (MaterialCalendarView) view.findViewById(R.id.calendarView);
        hourWheelView = (WheelView) view.findViewById(R.id.hourWheelView);
        minuteWheelView = (WheelView) view.findViewById(R.id.minuteWheelView);
        hourWheelView.setVisibleItems(9);
        minuteWheelView.setVisibleItems(9);

        calendarView.setShowOtherDates(MaterialCalendarView.SHOW_ALL);

//        Calendar instance = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        ParsePosition pos = new ParsePosition(0);
        calendarView.setSelectedDate(formatter.parse(checkedTime, pos));

        Calendar instance1 = Calendar.getInstance();
        instance1.set(instance1.get(Calendar.YEAR) - 2, instance1.get(Calendar.MONTH), 1);

        Calendar instance2 = Calendar.getInstance();
        if ((int) instance2.get(Calendar.MONTH) == Calendar.DECEMBER) {
            //如果是最后一月，则显示到第二年的一月
            instance2.set(instance2.get(Calendar.YEAR) + 1, Calendar.JANUARY, 31);
        } else {
            instance2.set(instance2.get(Calendar.YEAR), Calendar.DECEMBER, 31);
        }
        calendarView.state().edit()
                .setMinimumDate(instance1.getTime())
                .setMaximumDate(instance2.getTime())
                .commit();

        calendarView.addDecorators(
                new MySelectorDecorator(context),
                new HighlightWeekendsDecorator(),
                oneDayDecorator);

        initData(checkedTime);

        Button btnSubmit = (Button) view.findViewById(R.id.btnConfirm);
        Button btnCancel = (Button) view.findViewById(R.id.btnCancel);
        btnSubmit.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        this.setContentView(view);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(XPTApplication.getInstance().getWindowHeight() / 2);
        this.setFocusable(true);
//        this.setAnimationStyle(R.style.AnimBottom);
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
    }

    private void initData(String time) {
        Calendar calendar = Calendar.getInstance();

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        Log.i(TAG, "initData: HH:mm " + hour + ":" + minute);

        if (time.contains(":")) {
            try {
                String[] _time = time.split(" ")[1].split(":");
                hour = Integer.parseInt(_time[0]);
                minute = Integer.parseInt(_time[1]);
            } catch (Exception ex) {
                Log.e(TAG, "initData: parseTime " + ex.getMessage());
            }
        }
        calendarView.setSelectedDate(CommonUtil.strToDateTimeLong(time));

        hourWheelView.setViewAdapter(new DateNumericAdapter(mContext, 0, 23, hour));
        hourWheelView.setCurrentItem(hour);
        hourWheelView.setCyclic(true);

        minuteWheelView.setViewAdapter(new DateNumericAdapter(mContext, 0, 59, minute));
        minuteWheelView.setCurrentItem(minute);
        minuteWheelView.setCyclic(true);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnConfirm:
                if (onTimePickerClickListener != null) {
                    List<CalendarDay> dates = calendarView.getSelectedDates();
                    String month = String.format("%02d", (dates.get(0).getMonth() + 1));
                    String day = String.format("%02d", dates.get(0).getDay());
                    String dateStr = dates.get(0).getYear() + "-" + month + "-" + day;

                    String hour = String.format("%02d", hourWheelView.getCurrentItem());
                    String minute = String.format("%02d", minuteWheelView.getCurrentItem());

                    String time = dateStr + " " + hour + ":" + minute;
                    onTimePickerClickListener.onTimePickerResult(time);
                }
                break;
            case R.id.btnCancel:
                if (onTimePickerClickListener != null) {
                    onTimePickerClickListener.onTimePickerResult("");
                }
                break;
        }
        dismiss();
    }

    /**
     * Adapter for numeric wheels. Highlights the current value.
     */
    private class DateNumericAdapter extends NumericWheelAdapter {
        // Index of current item
        int currentItem;
        // Index of item to be highlighted
        int currentValue;

        /**
         * Constructor
         */
        public DateNumericAdapter(Context context, int minValue, int maxValue, int current) {
            super(context, minValue, maxValue, "%02d");
            this.currentValue = current;
            setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) mContext.getResources().getDimensionPixelOffset(R.dimen.sp_15));
        }

        @Override
        protected void configureTextView(TextView view) {
            super.configureTextView(view);
            if (currentItem == currentValue) {
                view.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
            }
            view.setTypeface(Typeface.SANS_SERIF);
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            currentItem = index;
            return super.getItem(index, cachedView, parent);
        }
    }

}
