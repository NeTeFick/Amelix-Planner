package com.example.planner;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotesFragment extends Fragment {

    private RecyclerView notesRecyclerView;
    private FloatingActionButton btnAddNote;
    private NoteAdapter noteAdapter;
    private final List<Note> noteList = new ArrayList<>();
    private NoteDatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notes, container, false);

        notesRecyclerView = view.findViewById(R.id.notesRecyclerView);
        btnAddNote = view.findViewById(R.id.btnAddNote);
        dbHelper = new NoteDatabaseHelper(requireContext());

        notesRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        noteAdapter = new NoteAdapter(requireContext(), noteList, dbHelper);
        notesRecyclerView.setAdapter(noteAdapter);

        loadNotes();

        btnAddNote.setOnClickListener(v -> {
            NoteDetailFragment fragment = NoteDetailFragment.newInstance(null);
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    private void loadNotes() {
        noteList.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(NoteDatabaseHelper.TABLE_NAME, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(NoteDatabaseHelper.COLUMN_ID);
            int titleIndex = cursor.getColumnIndex(NoteDatabaseHelper.COLUMN_TITLE);
            int contentIndex = cursor.getColumnIndex(NoteDatabaseHelper.COLUMN_CONTENT);

            do {
                long id = cursor.getLong(idIndex);
                String title = cursor.getString(titleIndex);
                String content = cursor.getString(contentIndex);
                noteList.add(new Note(id, title, content));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        Collections.sort(noteList, (a, b) -> a.getTitle().compareToIgnoreCase(b.getTitle()));
        noteAdapter.notifyDataSetChanged();
    }
}