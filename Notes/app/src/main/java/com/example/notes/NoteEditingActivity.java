package com.example.notes;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.HashSet;

public class NoteEditingActivity extends AppCompatActivity {

    int noteID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_editing);

        EditText editText = findViewById(R.id.editText);

        Intent intent = getIntent();

        noteID = intent.getIntExtra("noteID", -1);

        if (noteID != -1) {
            editText.setText(MainActivity.notes.get(noteID));
        } else {
            MainActivity.notes.add("");
            noteID = MainActivity.notes.size() - 1;
        }



        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                MainActivity.notes.set(noteID, String.valueOf(s));
                MainActivity.customAdapter.notifyDataSetChanged();

                saveNotesSharedPreferences(getApplicationContext());

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    static void saveNotesSharedPreferences(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("com.example.notes", Context.MODE_PRIVATE);
        String combinedNotes = "";
        for (int i = 0; i < MainActivity.notes.size(); i++) {
            combinedNotes += MainActivity.notes.get(i);
            combinedNotes += "@@3%";
        }
        sharedPreferences.edit().putString("Notes", combinedNotes).apply();
    }

}
