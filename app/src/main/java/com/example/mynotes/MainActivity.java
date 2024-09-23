package com.example.mynotes;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.media.Image;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;


import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private SQLiteDatabase database;
    public ListView dataList;
    public ImageView threeDotsMenu;
    private FloatingActionButton addNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        threeDotsMenu = (ImageView) findViewById(R.id.threeDotsMenu);
        threeDotsMenu.setOnClickListener(this::showMenu);
        dataList = (ListView) findViewById(R.id.dataList);
        addNote = (FloatingActionButton) findViewById(R.id.addNote);
        addNote.setOnClickListener(this::newNote);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        newDatabase();
        listNotes(0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            listNotes(0);
        }
    }

    private void showMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(MainActivity.this, v);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.menu_opcoes, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(this::onMenuItemClick);
        popupMenu.show();
    }

    private boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.menu_ordenar_prioridade) {
            listNotes(1);
            return true;
        } else if (item.getItemId() == R.id.menu_ordenar_chegada) {
            listNotes(2);
            return true;
        } else if (item.getItemId() == R.id.menu_fechar) {
            finish();
            return true;
        }
        return false;
    }

    private void newNote(View v) {
        Intent intent = new Intent(this, newNoteActivity.class);
        startActivityForResult(intent, 1);
    }

    private void newDatabase() {
        try {
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

    public void listNotes(int ordenacao) {
        try {
            database = openOrCreateDatabase("my-notes", MODE_PRIVATE, null);
            String query = "SELECT * FROM notes";
            if(ordenacao == 1)
                query += " order by priority";
            else if(ordenacao == 2)
                query += " order by id";
            Cursor meuCursor = database.rawQuery(query, null);

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

                dataList.setOnItemClickListener((parent, view, position, id) -> {
                    int idNote = notes.get(position).getId();
                    detalharNota(idNote);
                });

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
                                listNotes(0);
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

    private void detalharNota(int idNote) {
        Intent intent = new Intent(this, ShowNoteActivity.class);
        intent.putExtra("NOTES_ID", idNote);
        startActivity(intent);
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