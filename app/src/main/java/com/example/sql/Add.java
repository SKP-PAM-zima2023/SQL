package com.example.sql;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class Add extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        Button button = findViewById(R.id.buttonSave);
        button.setOnClickListener((view) -> {
            Intent intent = new Intent();
            EditText name = findViewById(R.id.name);
            EditText surname = findViewById(R.id.surname);
            intent.putExtra("name", name.getText());
            intent.putExtra("surname", surname.getText());
            setResult(Activity.RESULT_OK, intent);
            finish();
        });
    }
}