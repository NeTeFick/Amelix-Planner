package com.example.planner;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class EventLogFragment extends Fragment {

    private EventDatabaseHelper dbHelper;
    private EventAdapter adapter;
    private List<Event> eventList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_log, container, false);

        dbHelper = new EventDatabaseHelper(requireContext());

        RecyclerView recyclerView = view.findViewById(R.id.recyclerEvents);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new EventAdapter(eventList, event -> confirmDelete(event));
        recyclerView.setAdapter(adapter);

        loadEvents();
        return view;
    }

    private void loadEvents() {
        eventList.clear();

        Cursor cursor = dbHelper.getReadableDatabase().query(
                EventDatabaseHelper.TABLE_NAME,
                null, null, null, null, null,
                EventDatabaseHelper.COLUMN_DATE + " DESC"
        );

        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(EventDatabaseHelper.COLUMN_ID));
            String date = cursor.getString(cursor.getColumnIndexOrThrow(EventDatabaseHelper.COLUMN_DATE));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(EventDatabaseHelper.COLUMN_TITLE));
            eventList.add(new Event(id, date, title));
        }

        cursor.close();
        adapter.notifyDataSetChanged();
    }

    private void confirmDelete(Event event) {
        new AlertDialog.Builder(getContext())
                .setTitle("Удалить событие?")
                .setMessage(event.getTitle() + "\n" + event.getDate())
                .setPositiveButton("Удалить", (dialog, which) -> {
                    dbHelper.getWritableDatabase().delete(
                            EventDatabaseHelper.TABLE_NAME,
                            EventDatabaseHelper.COLUMN_ID + " = ?",
                            new String[]{String.valueOf(event.getId())}
                    );
                    loadEvents();
                })
                .setNegativeButton("Отмена", null)
                .show();
    }
}
