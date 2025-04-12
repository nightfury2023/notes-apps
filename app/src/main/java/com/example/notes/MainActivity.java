package com.example.notes;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NoteAdapter.OnNoteClickListener {

    private ListView noteListView;
    private Button newNoteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        noteListView = findViewById(R.id.noteListView);
        newNoteButton = findViewById(R.id.newNoteButton);

        final DatabaseHelper databaseHelper = new DatabaseHelper(this);
        final List<Note> notes = fetchNotesFromDatabase(databaseHelper);

        final NoteAdapter adapter = new NoteAdapter(this, notes, this);
        noteListView.setAdapter(adapter);

        newNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NoteDetailActivity.class);
                startActivity(intent);
            }
        });

        noteListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                showDeleteDialog(adapter, position);
                return true;
            }
        });
    }

    private List<Note> fetchNotesFromDatabase(DatabaseHelper databaseHelper) {
        return databaseHelper.getAllNotes();
    }

    private void showDeleteDialog(final NoteAdapter adapter, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete this note?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Note noteToDelete = adapter.getItem(position);
                        deleteNoteFromDatabase(noteToDelete.getId());
                        adapter.remove(noteToDelete);
                        adapter.notifyDataSetChanged();
                        // Inform the user that the note has been deleted
                        showToast("Note deleted");
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User canceled the operation
                    }
                })
                .show();
    }

    private void deleteNoteFromDatabase(long noteId) {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        databaseHelper.deleteNoteById(noteId);
    }

    private void showToast(String message) {
    }

    @Override
    public void onNoteClick(Note note) {
        Intent intent = new Intent(MainActivity.this, NoteDetailActivity.class);
        intent.putExtra("noteId", note.getId());
        startActivity(intent);
    }
}
