package com.example.planner;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

public class CalendarFragment extends Fragment {

    private MaterialCalendarView calendarView;
    private RecyclerView taskRecyclerView;
    private TextView tvTaskHeader;
    private TaskAdapter taskAdapter;
    private List<Task> taskList = new ArrayList<>();
    private TaskDatabaseHelper dbHelper;
    private String selectedDate;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        calendarView = view.findViewById(R.id.calendarView);
        taskRecyclerView = view.findViewById(R.id.taskRecyclerView);
        tvTaskHeader = view.findViewById(R.id.tvTaskHeader);
        Button addTaskButton = view.findViewById(R.id.button_add_task);

        dbHelper = new TaskDatabaseHelper(requireContext());
        taskAdapter = new TaskAdapter(taskList, requireContext());

        taskRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        taskRecyclerView.setAdapter(taskAdapter);

        // Загрузка задач на сегодня
        selectedDate = dateFormat.format(new Date());
        calendarView.setSelectedDate(CalendarDay.today());
        loadTasksForDate(selectedDate);

        markDaysWithTasks();

        calendarView.setOnDateChangedListener((widget, date, selected) -> {
            selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d",
                    date.getYear(), date.getMonth() + 1, date.getDay());
            loadTasksForDate(selectedDate);
        });

        addTaskButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddTaskActivity.class);
            intent.putExtra("selectedDate", selectedDate);
            startActivity(intent);
        });

        return view;
    }

    private void loadTasksForDate(String date) {
        taskList.clear();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TaskDatabaseHelper.TABLE_NAME, null,
                TaskDatabaseHelper.COLUMN_DATE + "=?", new String[]{date},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(TaskDatabaseHelper.COLUMN_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(TaskDatabaseHelper.COLUMN_TITLE));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(TaskDatabaseHelper.COLUMN_DESCRIPTION));
                String taskDate = cursor.getString(cursor.getColumnIndexOrThrow(TaskDatabaseHelper.COLUMN_DATE));

                taskList.add(new Task(id, title, description, taskDate));
            } while (cursor.moveToNext());

            cursor.close();
        }

        db.close();

        taskAdapter.notifyDataSetChanged();
        tvTaskHeader.setText("Задачи на " + date);
    }

    private void markDaysWithTasks() {
        HashSet<CalendarDay> datesWithTasks = new HashSet<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TaskDatabaseHelper.TABLE_NAME,
                new String[]{TaskDatabaseHelper.COLUMN_DATE}, null, null,
                TaskDatabaseHelper.COLUMN_DATE, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String dateStr = cursor.getString(cursor.getColumnIndexOrThrow(TaskDatabaseHelper.COLUMN_DATE));
                try {
                    Date date = dateFormat.parse(dateStr);
                    if (date != null) {
                        datesWithTasks.add(CalendarDay.from(date));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());

            cursor.close();
        }

        db.close();

        calendarView.addDecorator(new TaskDotDecorator(datesWithTasks));
    }
}