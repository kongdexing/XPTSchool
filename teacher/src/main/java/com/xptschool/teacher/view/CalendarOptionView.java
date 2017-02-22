package com.xptschool.teacher.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.widget.calendar.CalendarDay;
import com.android.widget.calendar.MaterialCalendarView;
import com.android.widget.calendar.OnDateSelectedListener;
import com.xptschool.teacher.R;
import com.xptschool.teacher.common.CommonUtil;
import com.xptschool.teacher.view.decorators.HighlightWeekendsDecorator;
import com.xptschool.teacher.view.decorators.MySelectorDecorator;
import com.xptschool.teacher.view.decorators.OneDayDecorator;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/11/17.
 */

public class CalendarOptionView extends LinearLayout implements View.OnClickListener, OnDateSelectedListener {

    LinearLayout llCalendarContainer, llStart, llEnd;
    TextView txtStartTime, txtEndTime, btnSearch;
    MaterialCalendarView calendarView;
    private final OneDayDecorator oneDayDecorator = new OneDayDecorator();
    private CalendarViewSelectedListener selectedListener;
    private String startDate, endDate;
    private boolean checkedStartDate = true;

    public CalendarOptionView(Context context) {
        this(context, null);
    }

    public CalendarOptionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.widget_calendar_option, this, true);
        llCalendarContainer = (LinearLayout) view.findViewById(R.id.llCalendarContainer);
        llStart = (LinearLayout) view.findViewById(R.id.llStart);
        llEnd = (LinearLayout) view.findViewById(R.id.llEnd);

        txtStartTime = (TextView) view.findViewById(R.id.txtStartTime);
        txtEndTime = (TextView) view.findViewById(R.id.txtEndTime);
        btnSearch = (TextView) view.findViewById(R.id.btnSearch);

        llStart.setOnClickListener(this);
        llEnd.setOnClickListener(this);
        btnSearch.setOnClickListener(this);

        calendarView = (MaterialCalendarView) view.findViewById(R.id.calendarView);
        calendarView.setOnDateChangedListener(this);
        calendarView.setShowOtherDates(MaterialCalendarView.SHOW_ALL);
        calendarView.setTileHeight(LinearLayout.LayoutParams.MATCH_PARENT);

        Calendar instance1 = Calendar.getInstance();
        instance1.set(instance1.get(Calendar.YEAR) - 1, Calendar.JANUARY, 1);

        Calendar instance2 = Calendar.getInstance();
        instance2.set(instance2.get(Calendar.YEAR), Calendar.DECEMBER, 31);

        calendarView.state().edit()
                .setMinimumDate(instance1.getTime())
                .setMaximumDate(instance2.getTime())
                .commit();

        calendarView.addDecorators(
                new MySelectorDecorator(context),
                new HighlightWeekendsDecorator(),
                oneDayDecorator);

        txtStartTime.setText(CommonUtil.getDate2StrBefore(7));
        txtEndTime.setText(CommonUtil.getCurrentDate());

        startDate = txtStartTime.getText().toString();
        endDate = txtEndTime.getText().toString();

        calendarView.setSelectedDate(CommonUtil.getDateBefore(7));

    }

    public void setSelectedListener(CalendarViewSelectedListener selectedListener) {
        this.selectedListener = selectedListener;
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        //If you change a decorate, you need to invalidate decorators
        List<CalendarDay> dates = widget.getSelectedDates();
        String month = String.format("%02d", dates.get(0).getMonth() + 1);
        String day = String.format("%02d", dates.get(0).getDay());
        String dateStr = dates.get(0).getYear() + "-" + month + "-" + day;

        if (checkedStartDate) {
            startDate = dateStr;
            txtStartTime.setText(startDate);
        } else {
            endDate = dateStr;
            txtEndTime.setText(endDate);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.llStart:
                checkedStartDate = true;
                llStart.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                llEnd.setBackgroundColor(0);
                if (startDate.contains("-")) {
                    calendarView.gotoDate(CommonUtil.strToDateLong(startDate));
                    calendarView.setSelectedDate(CommonUtil.strToDateLong(startDate));
                } else {
                    Calendar instance = Calendar.getInstance();
                    calendarView.setSelectedDate(instance.getTime());
                }
                break;
            case R.id.llEnd:
                checkedStartDate = false;
                llEnd.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                llStart.setBackgroundColor(0);
                if (endDate.contains("-")) {
                    calendarView.gotoDate(CommonUtil.strToDateLong(endDate));
                    calendarView.setSelectedDate(CommonUtil.strToDateLong(endDate));
                } else {
                    Calendar instance = Calendar.getInstance();
                    calendarView.setSelectedDate(instance.getTime());
                }
                break;
            case R.id.btnSearch:
                if (!startDate.contains("-") || !endDate.contains("-")) {
                    Toast.makeText(getContext(), R.string.msg_miss_time, Toast.LENGTH_SHORT).show();
                    return;
                }
                //比较开始时间与结束时间大小
                Date start = CommonUtil.strToDateLong(startDate);
                Date end = CommonUtil.strToDateLong(endDate);
                if (end.getTime() < start.getTime()) {
                    Toast.makeText(getContext(), R.string.msg_large_endtime, Toast.LENGTH_SHORT).show();
                    return;
                }
                //是否为当月日期
//                if (start.getMonth() != end.getMonth()) {
//                    Toast.makeText(getContext(), R.string.msg_month_cannot_overstep, Toast.LENGTH_SHORT).show();
//                    return;
//                }

                if (selectedListener != null)
                    selectedListener.onCalendarSelected(startDate, endDate);
                break;
        }
    }

    public interface CalendarViewSelectedListener {
        void onCalendarSelected(String sDate, String eDate);
    }
}
