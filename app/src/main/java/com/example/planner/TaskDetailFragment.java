package com.example.planner;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class TaskDetailFragment extends Fragment {

    private static final String ARG_ID = "id";
    private static final String ARG_TITLE = "title";
    private static final String ARG_DESC = "description";
    private static final String ARG_DATE = "date";

    private long taskId = -1;
    private boolean isNewTask = true;

    private EditText etTitle, etDescription;
    private TaskDatabaseHelper dbHelper;
    private String taskDate;

    public static TaskDetailFragment newInstance(@Nullable Task task) {
        TaskDetailFragment fragment = new TaskDetailFragment();
        Bundle args = new Bundle();

        if (task != null) {
            args.putLong(ARG_ID, task.getId());
            args.putString(ARG_TITLE, task.getTitle());
            args.putString(ARG_DESC, task.getDescription());
            args.putString(ARG_DATE, task.getDate());
        }

        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_task_detail, container, false);

        etTitle = view.findViewById(R.id.etTaskTitle);
        etDescription = view.findViewById(R.id.etTaskDescription);
        Button btnSave = view.findViewById(R.id.btnSaveTask);

        dbHelper = new TaskDatabaseHelper(requireContext());

        if (getArguments() != null && getArguments().containsKey(ARG_ID)) {
            taskId = getArguments().getLong(ARG_ID, -1);
            etTitle.setText(getArguments().getString(ARG_TITLE, ""));
            etDescription.setText(getArguments().getString(ARG_DESC, ""));
            taskDate = getArguments().getString(ARG_DATE, "");
            isNewTask = false;
        } else {
            // Default to today's date if none provided
            taskDate = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                    .format(new java.util.Date());
        }

        btnSave.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String desc = etDescription.getText().toString().trim();

            if (TextUtils.isEmpty(title)) {
                Toast.makeText(getContext(), "Заголовок обязателен", Toast.LENGTH_SHORT).show();
                return;
            }

            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(TaskDatabaseHelper.COLUMN_TITLE, title);
            values.put(TaskDatabaseHelper.COLUMN_DESCRIPTION, desc);
            values.put(TaskDatabaseHelper.COLUMN_DATE, taskDate);

            if (isNewTask) {
                db.insert(TaskDatabaseHelper.TABLE_NAME, null, values);
                Toast.makeText(getContext(), "Задача добавлена", Toast.LENGTH_SHORT).show();
            } else {
                db.update(TaskDatabaseHelper.TABLE_NAME, values,
                        TaskDatabaseHelper.COLUMN_ID + "=?",
                        new String[]{String.valueOf(taskId)});
                Toast.makeText(getContext(), "Задача обновлена", Toast.LENGTH_SHORT).show();
            }

            db.close();
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        return view;
    }
}