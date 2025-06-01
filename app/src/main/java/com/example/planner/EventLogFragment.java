
package com.example.planner;

import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class EventLogFragment extends Fragment {

    private EventDatabaseHelper dbHelper;
    private EventAdapter adapter;
    private List<Event> eventList = new ArrayList<>();
    private String selectedDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_log, container, false);

        if (getArguments() != null) {
            selectedDate = getArguments().getString("selectedDate");
        }

        dbHelper = new EventDatabaseHelper(requireContext());

        RecyclerView recyclerView = view.findViewById(R.id.recyclerEvents);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new EventAdapter(eventList, new EventAdapter.OnEventClickListener() {
            @Override
            public void onEventLongClick(Event event) {
                // Здесь можешь реализовать удаление события, показ диалога и т.п.
                // Простой пример — сообщение:
                Toast.makeText(requireContext(), "Событие: " + event.getTitle(), Toast.LENGTH_SHORT).show();
            }
        });
        recyclerView.setAdapter(adapter);

        loadEventsForDate(selectedDate);

        return view;
    }

    private void loadEventsForDate(String date) {
        eventList.clear();
        Cursor cursor = dbHelper.getEventsByDate(date);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(EventDatabaseHelper.COLUMN_ID));
                String eventDate = cursor.getString(cursor.getColumnIndexOrThrow(EventDatabaseHelper.COLUMN_DATE));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(EventDatabaseHelper.COLUMN_TITLE));
                eventList.add(new Event(id, eventDate, title));
            } while (cursor.moveToNext());
            cursor.close();
        }
        adapter.notifyDataSetChanged();
    }
}
