package com.example.mynotes;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;


import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private SQLiteDatabase database;
    public ListView dataList;
    private FloatingActionButton addNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        dataList = (ListView) findViewById(R.id.dataList);
        addNote = (FloatingActionButton) findViewById(R.id.addNote);
        addNote.setOnClickListener(this::newNote);

        System.out.println("CCCCCCCC");
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        System.out.println("BBBBBBBBBBB");
        newDatabase();
        listNotes();
    }

    private void newNote(View v){
        Intent intent = new Intent(this, newNoteActivity.class);
        startActivity(intent);
    }

    private void newDatabase() {
        try {
            System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
            database = openOrCreateDatabase("my-notes", MODE_PRIVATE, null);
            database.execSQL("CREATE TABLE IF NOT EXISTS notes (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "title VARCHAR," +
                    "description VARCHAR," +
                    "priority INT NOT NULL," +
                    "photo TEXT)");
            database.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void listNotes() {
        try {
            database = openOrCreateDatabase("my-notes", MODE_PRIVATE, null);
            Cursor meuCursor = database.rawQuery("SELECT * FROM notes", null);

            ArrayList<Note> notes = new ArrayList<>();

            if (meuCursor.moveToFirst()) {
                do {
                    Note myNote = new Note(meuCursor.getInt(0), meuCursor.getString(1), meuCursor.getString(2), meuCursor.getInt(3), meuCursor.getString(4));
                    notes.add(myNote);
                } while(meuCursor.moveToNext());

                meuCursor.close();
                database.close();
                CustomAdapter adapter = new CustomAdapter(this, notes);
                dataList.setAdapter(adapter);
                dataList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                        int idNote = notes.get(i).getId();
                        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                        dialog.setTitle("Excluir Nota");
                        dialog.setMessage("Deseja excluir essa nota?");

                        dialog.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteNote(idNote);
                                listNotes();
                            }
                        });
                        dialog.setNegativeButton("Não", null);
                        dialog.show();
                        return false;
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteNote(int idNote) {
        try {
            database = openOrCreateDatabase("my-notes", MODE_PRIVATE, null);
            database.execSQL("DELETE FROM notes WHERE id = " + idNote);
            database.close();
            Toast.makeText(this, "Nota excluída com sucesso", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}