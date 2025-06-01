package com.example.planner;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddTaskActivity extends AppCompatActivity {

    private EditText etTitle, etDescription;
    private Button btnSave;
    private TaskDatabaseHelper dbHelper;
    private String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        btnSave = findViewById(R.id.btnSave);
        dbHelper = new TaskDatabaseHelper(this);

        selectedDate = getIntent().getStringExtra("selectedDate");

        btnSave.setOnClickListener(v -> saveTask());
    }

    private void saveTask() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (title.isEmpty()) {
            Toast.makeText(this, "Введите заголовок задачи", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TaskDatabaseHelper.COLUMN_TITLE, title);
        values.put(TaskDatabaseHelper.COLUMN_DESCRIPTION, description);
        values.put(TaskDatabaseHelper.COLUMN_DATE, selectedDate);

        long result = db.insert(TaskDatabaseHelper.TABLE_NAME, null, values);
        db.close();

        if (result != -1) {
            Toast.makeText(this, "Задача добавлена", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Ошибка добавления", Toast.LENGTH_SHORT).show();
        }
    }
}