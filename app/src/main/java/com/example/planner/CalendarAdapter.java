package com.example.planner;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.DayViewHolder> {

    public interface OnItemListener {
        void onItemClick(String dayText);
    }

    private final List<String> days;
    private final OnItemListener listener;
    private final Set<String> markedDays;
    public CalendarAdapter(List<String> days, Set<String> markedDays, OnItemListener listener) {
        this.days = days;
        this.markedDays = markedDays;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_calendar_day, parent, false);
        return new DayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {
        final String day = days.get(position);
        holder.dayText.setText(day);
        holder.dayText.setBackgroundColor(Color.TRANSPARENT);

        if (!day.isEmpty() && markedDays.contains(day)) {
            holder.dayText.setBackgroundColor(Color.parseColor("#FFD700")); // золотой
        }

        holder.itemView.setOnClickListener(v -> {
            if (!day.isEmpty()) listener.onItemClick(day);
        });
    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    static class DayViewHolder extends RecyclerView.ViewHolder {
        TextView dayText;

        DayViewHolder(@NonNull View itemView) {
            super(itemView);
            dayText = itemView.findViewById(R.id.dayText);
        }
    }
}
