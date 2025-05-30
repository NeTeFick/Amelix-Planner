package com.example.planner;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jakewharton.threetenabp.AndroidThreeTen;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.ZoneId;

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
        dbHelper = new TaskDatabaseHelper(requireContext());
        AndroidThreeTen.init(getContext());

        taskAdapter = new TaskAdapter(taskList, requireContext());
        taskRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        taskRecyclerView.setAdapter(taskAdapter);

        calendarView.setSelectedDate(CalendarDay.today());

        String today = dateFormat.format(new Date());
        loadTasksForDate(today);

        markDaysWithTasks();

        calendarView.setOnDateChangedListener((@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) -> {
            String selectedDate = dateFormat.format(date.getDate());
            loadTasksForDate(selectedDate);
        });

        return view;
    }

    private void loadTasksForDate(String date) {
        taskList.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TaskDatabaseHelper.TABLE_NAME, null,
                TaskDatabaseHelper.COLUMN_DATE + "=?",
                new String[]{date}, null, null, null);

        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(TaskDatabaseHelper.COLUMN_ID);
            int titleIndex = cursor.getColumnIndex(TaskDatabaseHelper.COLUMN_TITLE);
            int descIndex = cursor.getColumnIndex(TaskDatabaseHelper.COLUMN_DESCRIPTION);
            int dateIndex = cursor.getColumnIndex(TaskDatabaseHelper.COLUMN_DATE);

            do {
                long id = cursor.getLong(idIndex);
                String title = cursor.getString(titleIndex);
                String description = cursor.getString(descIndex);
                String taskDate = cursor.getString(dateIndex);
                taskList.add(new Task(id, title, description, taskDate));
            } while (cursor.moveToNext());
        }

        cursor.close();
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

        if (cursor.moveToFirst()) {
            int dateIndex = cursor.getColumnIndex(TaskDatabaseHelper.COLUMN_DATE);
            do {
                String dateStr = cursor.getString(dateIndex);
                try {
                    Date date = dateFormat.parse(dateStr);
                    if (date != null) {
                        // Преобразование java.util.Date → org.threeten.bp.LocalDate
                        org.threeten.bp.Instant instant = org.threeten.bp.Instant.ofEpochMilli(date.getTime());
                        org.threeten.bp.LocalDate localDate = instant.atZone(org.threeten.bp.ZoneId.systemDefault()).toLocalDate();

                        datesWithTasks.add(CalendarDay.from(localDate));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        calendarView.addDecorator(new TaskDotDecorator(datesWithTasks));
    }

}
