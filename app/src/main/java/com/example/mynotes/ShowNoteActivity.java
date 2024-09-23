package com.example.mynotes;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;

public class ShowNoteActivity extends AppCompatActivity {
    private SQLiteDatabase database;

    private TextView titleNote;
    private TextView descriptionNote;
    private TextView priorityNote;
    private ImageView photoNote;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_note);

        titleNote = findViewById(R.id.titleNote);
        descriptionNote = findViewById(R.id.descriptionNote);
        priorityNote = findViewById(R.id.priorityNote);
        photoNote = findViewById(R.id.notePhoto);
        btnBack = findViewById(R.id.btnBack);
        int idNote = getIntent().getIntExtra("NOTES_ID", -1);
        if(idNote != -1) {
            getNote(idNote);
        }
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void getNote(int id) {
        database = openOrCreateDatabase("my-notes", MODE_PRIVATE, null);
        Cursor cursor = database.rawQuery("SELECT title, description, priority, photo FROM notes WHERE id = ?", new String[]{String.valueOf(id)});

        if (cursor.moveToFirst()) {
            titleNote.setText(cursor.getString(0));
            descriptionNote.setText(cursor.getString(1));
            priorityNote.setText(getPrioridadeString(cursor.getInt(2)));
            String fotoBase64 = cursor.getString(3);
            if (fotoBase64 != null && !fotoBase64.isEmpty()) {
                byte[] imagemBytes = Base64.decode(fotoBase64, Base64.DEFAULT);
                Bitmap imagemDecodificada = BitmapFactory.decodeByteArray(imagemBytes, 0, imagemBytes.length);
                photoNote.setImageBitmap(imagemDecodificada);
            } else {
                photoNote.setImageResource(android.R.drawable.ic_menu_camera);
            }
        } else {
            Toast.makeText(this, "Anotação não encontrada", Toast.LENGTH_SHORT).show();
        }
        cursor.close();
        database.close();
    }

    private String getPrioridadeString(int prioridade) {
        switch (prioridade) {
            case 0: return "Alta";
            case 1: return "Normal";
            case 2: return "Baixa";
            default: return "Desconhecida";
        }
    }
}
