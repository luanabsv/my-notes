package com.example.mynotes;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;

public class newNoteActivity extends AppCompatActivity {
    private EditText inputTitle;
    private EditText inputDescription;
    private Spinner spinner;
    private Button saveNoteButton;
    private int prioritySelected;
    private FloatingActionButton addPhotoButton;
    private ImageView notePhoto;
    private String imageString;
    private Button btnBack;

    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note);

        inputTitle = findViewById(R.id.inputTitle);
        inputDescription = findViewById(R.id.inputDescription);
        spinner = findViewById(R.id.spinner);
        saveNoteButton = findViewById(R.id.saveNoteButton);
        addPhotoButton = findViewById(R.id.addPhotoButton);
        notePhoto = findViewById(R.id.notePhoto);
        btnBack = findViewById(R.id.btnBack);
        String[] options = {"Alta", "Média", "Baixa"};

        ArrayAdapter<String> adapters = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
        adapters.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapters);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                prioritySelected = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                prioritySelected = -1;
            }
        });

        saveNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inserirDados();
            }
        });

        addPhotoButton.setOnClickListener((v) -> {
            AlertDialog.Builder selecionaFoto = new AlertDialog.Builder(newNoteActivity.this);
            selecionaFoto.setTitle("Origem da foto");
            selecionaFoto.setMessage("Por favor, selecione a origem da foto");
            selecionaFoto.setPositiveButton("Câmera", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent,1);
                }
            });
            selecionaFoto.setNegativeButton("Galeria", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(intent,2);
                }
            });
            selecionaFoto.create().show();
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void inserirDados() {
        String titulo = inputTitle.getText().toString().trim();
        String texto = inputDescription.getText().toString().trim();

        if (prioritySelected >= 0) { // Verifica se a prioridade é válida
            try {
                database = openOrCreateDatabase("my-notes", MODE_PRIVATE, null);
                String sql = "INSERT INTO notes(title, description, priority, photo) VALUES(?,?,?,?)";

                SQLiteStatement stmt = database.compileStatement(sql);
                stmt.bindString(1, titulo);
                stmt.bindString(2, texto);
                stmt.bindLong(3, prioritySelected);
                stmt.bindString(4, imageString);
                stmt.executeInsert();
                database.close();

                setResult(RESULT_OK);
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent dados)
    {
        super.onActivityResult(requestCode, resultCode, dados);
        if(requestCode == 1) {
            try {
                Bitmap fotoRegistrada = (Bitmap) dados.getExtras().get("data");
                notePhoto.setImageBitmap(fotoRegistrada);
                ByteArrayOutputStream streamDaFotoEmBytes = new ByteArrayOutputStream();
                fotoRegistrada.compress(Bitmap.CompressFormat.PNG, 70, streamDaFotoEmBytes);
                byte[] fotoEmBytes = streamDaFotoEmBytes.toByteArray();
                imageString = Base64.encodeToString(fotoEmBytes, Base64.DEFAULT);
                Log.d("CadastroActivity", "Foto capturada com sucesso.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if(requestCode == 2)
        {
            try {
                Uri imageUri = dados.getData();
                Bitmap fotoRegistrada = MediaStore.Images.Media.getBitmap(this.getContentResolver(),imageUri);
                notePhoto.setImageBitmap(fotoRegistrada);
                ByteArrayOutputStream streamDaFotoEmBytes = new ByteArrayOutputStream();
                fotoRegistrada.compress(Bitmap.CompressFormat.PNG, 70, streamDaFotoEmBytes);
                byte[] fotoEmBytes = streamDaFotoEmBytes.toByteArray();
                imageString = Base64.encodeToString(fotoEmBytes, Base64.DEFAULT);
                Log.d("CadastroActivity", "Foto capturada com sucesso.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
