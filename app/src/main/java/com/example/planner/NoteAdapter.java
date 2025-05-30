package com.example.planner;

import android.app.AlertDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private final Context context;
    private final List<Note> noteList;
    private final NoteDatabaseHelper dbHelper;

    public NoteAdapter(Context context, List<Note> noteList, NoteDatabaseHelper dbHelper) {
        this.context = context;
        this.noteList = noteList;
        this.dbHelper = dbHelper;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = noteList.get(position);
        holder.noteTitle.setText(note.getTitle());
        holder.noteContent.setText(note.getContent());

        holder.itemView.setOnClickListener(v -> {
            NoteDetailFragment fragment = NoteDetailFragment.newInstance(note);
            ((AppCompatActivity) context).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        holder.itemView.setOnLongClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Удалить заметку?");
            builder.setMessage(note.getTitle());

            builder.setPositiveButton("Удалить", (dialog, which) -> {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.delete(NoteDatabaseHelper.TABLE_NAME,
                        NoteDatabaseHelper.COLUMN_ID + "=?",
                        new String[]{String.valueOf(note.getId())});
                db.close();

                noteList.remove(position);
                notifyItemRemoved(position);
                Toast.makeText(context, "Удалено", Toast.LENGTH_SHORT).show();
            });

            builder.setNegativeButton("Отмена", (dialog, which) -> dialog.dismiss());
            builder.show();
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView noteTitle, noteContent;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            noteTitle = itemView.findViewById(R.id.noteTitle);
            noteContent = itemView.findViewById(R.id.noteContent);
        }
    }
}