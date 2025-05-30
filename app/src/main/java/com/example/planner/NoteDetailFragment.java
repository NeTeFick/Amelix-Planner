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

import jp.wasabeef.richeditor.RichEditor;

public class NoteDetailFragment extends Fragment {

    private static final String ARG_ID = "id";
    private static final String ARG_TITLE = "title";
    private static final String ARG_CONTENT = "content";

    private long noteId = -1;
    private boolean isNewNote = true;

    private EditText etTitle;
    private RichEditor etContent;
    private NoteDatabaseHelper dbHelper;

    public static NoteDetailFragment newInstance(@Nullable Note note) {
        NoteDetailFragment fragment = new NoteDetailFragment();
        Bundle args = new Bundle();

        if (note != null) {
            args.putLong(ARG_ID, note.getId());
            args.putString(ARG_TITLE, note.getTitle());
            args.putString(ARG_CONTENT, note.getContent());
        }

        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note_detail, container, false);

        etTitle = view.findViewById(R.id.etTitle);
        etContent = view.findViewById(R.id.etContent);
        Button btnSave = view.findViewById(R.id.btnSave);
        Button btnBold = view.findViewById(R.id.btnBold);
        Button btnItalic = view.findViewById(R.id.btnItalic);
        Button btnUnderline = view.findViewById(R.id.btnUnderline);

        dbHelper = new NoteDatabaseHelper(requireContext());

        if (getArguments() != null && getArguments().containsKey(ARG_ID)) {
            noteId = getArguments().getLong(ARG_ID, -1);
            etTitle.setText(getArguments().getString(ARG_TITLE, ""));
            etContent.setHtml(getArguments().getString(ARG_CONTENT, ""));
            isNewNote = false;
        }

        btnBold.setOnClickListener(v -> etContent.setBold());
        btnItalic.setOnClickListener(v -> etContent.setItalic());
        btnUnderline.setOnClickListener(v -> etContent.setUnderline());

        btnSave.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String htmlContent = etContent.getHtml();

            if (TextUtils.isEmpty(title)) {
                Toast.makeText(getContext(), "Заголовок не может быть пустым", Toast.LENGTH_SHORT).show();
                return;
            }

            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(NoteDatabaseHelper.COLUMN_TITLE, title);
            values.put(NoteDatabaseHelper.COLUMN_CONTENT, htmlContent);

            if (isNewNote) {
                db.insert(NoteDatabaseHelper.TABLE_NAME, null, values);
                Toast.makeText(getContext(), "Заметка добавлена", Toast.LENGTH_SHORT).show();
            } else {
                db.update(NoteDatabaseHelper.TABLE_NAME, values,
                        NoteDatabaseHelper.COLUMN_ID + "=?",
                        new String[]{String.valueOf(noteId)});
                Toast.makeText(getContext(), "Заметка обновлена", Toast.LENGTH_SHORT).show();
            }

            db.close();
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        return view;
    }
}