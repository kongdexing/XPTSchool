package com.xptschool.parent.view.decorators;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.android.widget.calendar.CalendarDay;
import com.android.widget.calendar.DayViewDecorator;
import com.android.widget.calendar.DayViewFacade;
import com.xptschool.parent.R;

/**
 * Use a custom selector
 */
public class MySelectorDecorator implements DayViewDecorator {

    private final Drawable drawable;

    public MySelectorDecorator(Context context) {
        drawable = context.getResources().getDrawable(R.drawable.my_selector);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return true;
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setSelectionDrawable(drawable);
    }
}
