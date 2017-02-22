package com.xptschool.parent.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.widget.calendar.CalendarDay;
import com.android.widget.calendar.MaterialCalendarView;
import com.android.widget.calendar.OnDateSelectedListener;
import com.xptschool.parent.R;
import com.xptschool.parent.view.decorators.HighlightWeekendsDecorator;
import com.xptschool.parent.view.decorators.MySelectorDecorator;
import com.xptschool.parent.view.decorators.OneDayDecorator;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Administrator on 2016/10/27.
 */

public class CalendarView extends LinearLayout implements OnDateSelectedListener, View.OnClickListener {

    private String TAG = CalendarView.class.getSimpleName();
    private final OneDayDecorator oneDayDecorator = new OneDayDecorator();
    MaterialCalendarView calendarView;
    private LinearLayout llCalendarContainer;
    private Button btnConfirm;
    public static final int SELECTION_MODE_NONE = 0;
    public static final int SELECTION_MODE_SINGLE = 1;
    /**
     * Selection mode which allows more than one selected date at one time.
     */
    public static final int SELECTION_MODE_MULTIPLE = 2;

    private int selectionMode = SELECTION_MODE_SINGLE;
    private CalendarViewSelectedListener selectedListener;

    public CalendarView(Context context, int mode) {
        this(context, null, mode);
    }

    public CalendarView(Context context, AttributeSet attrs, int mode) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.widget_calendar, this, true);
        llCalendarContainer = (LinearLayout) view.findViewById(R.id.llCalendarContainer);
        btnConfirm = (Button) view.findViewById(R.id.btnConfirm);
        btnConfirm.setOnClickListener(this);
        calendarView = (MaterialCalendarView) view.findViewById(R.id.calendarView);
        calendarView.setOnDateChangedListener(this);
        selectionMode = mode;
        if (selectionMode == SELECTION_MODE_MULTIPLE) {
            calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_MULTIPLE);
        } else if (selectionMode == SELECTION_MODE_SINGLE) {
            calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_SINGLE);
        }
        calendarView.setShowOtherDates(MaterialCalendarView.SHOW_ALL);

        Calendar instance = Calendar.getInstance();
        calendarView.setSelectedDate(instance.getTime());

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
    }

    public void setCalendarWidth(int width) {
        calendarView.setTileWidthDp(width);
    }

    public void setCalendarHeight(int height) {
        calendarView.setTileHeightDp(height);
    }

    public void setContainerGravity(int gravity) {
        llCalendarContainer.setGravity(gravity);
        requestLayout();
    }

    public void setSelectedListener(CalendarViewSelectedListener selectedListener) {
        this.selectedListener = selectedListener;
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        //If you change a decorate, you need to invalidate decorators
        List<CalendarDay> dates = widget.getSelectedDates();

        if (selectionMode == CalendarView.SELECTION_MODE_MULTIPLE) {
            if (dates.size() == 2) {
                if (selectedListener != null) {
                    String month1 = String.format("%02d", dates.get(0).getMonth() + 1);
                    String day1 = String.format("%02d", dates.get(0).getDay());
                    String month2 = String.format("%02d", dates.get(1).getMonth() + 1);
                    String day2 = String.format("%02d", dates.get(1).getDay());

                    String dateStr1 = dates.get(0).getYear() + "-" + month1 + "-" + day1;
                    String dateStr2 = dates.get(1).getYear() + "-" + month2 + "-" + day2;
                    int res = dateStr1.compareTo(dateStr2);
                    if (res > 0) {
                        String temp = dateStr1;
                        dateStr1 = dateStr2;
                        dateStr2 = temp;
                    }
                    selectedListener.onCalendarSelected(selectionMode, dateStr1, dateStr2);
                }
            } else if (dates.size() > 2) {
                Toast.makeText(getContext(), "最多只能选择两个日期！", Toast.LENGTH_SHORT).show();
                widget.setDateSelected(dates.get(2), false);
            }
        } else if (selectionMode == CalendarView.SELECTION_MODE_SINGLE) {
            if (selectedListener != null) {
                String month = String.format("%02d", dates.get(0).getMonth() + 1);
                String day = String.format("%02d", dates.get(0).getDay());

                String dateStr = dates.get(0).getYear() + "-" + month + "-" + day;
                selectedListener.onCalendarSelected(selectionMode, dateStr);
            }
        }
    }

    public void setSubmitVisibility(int visibility) {
        btnConfirm.setVisibility(visibility);
    }

    @Override
    public void onClick(View view) {
        List<CalendarDay> dates = calendarView.getSelectedDates();
        if (dates.size() == 0) {
            if (selectedListener != null) {
                selectedListener.onCalendarSelected(SELECTION_MODE_NONE, "");
            }
        } else if (dates.size() == 1) {
            if (selectedListener != null) {
                String dateStr = dates.get(0).getYear() + "-" + dates.get(0).getMonth() + "-" + dates.get(0).getDay();
                selectedListener.onCalendarSelected(SELECTION_MODE_SINGLE, dateStr);
            }
        }
    }

    public interface CalendarViewSelectedListener {
        void onCalendarSelected(int mode, String... date);
    }

}
