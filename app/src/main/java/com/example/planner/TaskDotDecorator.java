package com.example.planner;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.Collection;
import java.util.HashSet;

public class TaskDotDecorator implements DayViewDecorator {

    private final HashSet<com.prolificinteractive.materialcalendarview.CalendarDay> dates;

    public TaskDotDecorator(Collection<com.prolificinteractive.materialcalendarview.CalendarDay> dates) {
        this.dates = new HashSet<>(dates);
    }

    @Override
    public boolean shouldDecorate(com.prolificinteractive.materialcalendarview.CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new DotSpan(8, Color.parseColor("#251249"))); // фиолетовая точка
    }
}