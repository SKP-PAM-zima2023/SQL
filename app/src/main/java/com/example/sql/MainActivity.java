package com.example.sql;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = openOrCreateDatabase("Aplikacja", MODE_PRIVATE, null);
       // db.execSQL("DROP TABLE IF EXISTS Osoby");
        String createTable = "CREATE TABLE IF NOT EXISTS Osoby (id INTEGER, Imie VARCHAR, Nazwisko VARCHAR)";
        db.execSQL(createTable);
        insertIntoDb();

        Button buttonShow = findViewById(R.id.buttonShow);
        buttonShow.setOnClickListener((view) ->{
            // 1) odczyt danych z bazy
            ArrayList<String> results = new ArrayList<>();
            Cursor cursor = db.rawQuery("SELECT * FROM Osoby", null);
            if(cursor.moveToFirst()){
                do{
                    int id  = cursor.getInt(0);
                    String name = cursor.getString(1);
                    String surname = cursor.getString(2);
                    results.add(String.format("%d. %s %s", id, name, surname));
                }while (cursor.moveToNext());
            }
            cursor.close();

            // 2) Wyświetlenie
            ListView listView = findViewById(R.id.listView);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getBaseContext(),
                    android.R.layout.simple_list_item_1, results);
            listView.setAdapter(adapter);
        });


        ActivityResultLauncher<Intent> getResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {
                        // to wykonuje się jak otrzymamy dane z aktywnosci
                        Intent intent = o.getData();
                        if(intent != null){
                            CharSequence name = intent.getCharSequenceExtra("name") == null ? "": intent.getCharSequenceExtra("name");
                            CharSequence surname = intent.getCharSequenceExtra("surname");

                            //jakie jest max id
                            Cursor cursor = db.rawQuery("SELECT MAX(id) FROM Osoby", null);
                            cursor.moveToFirst();
                            int id = cursor.getInt(0);
                            cursor.close();

                            String sqlStatment = "INSERT INTO Osoby VALUES (?, ?, ?)";
                            SQLiteStatement statement = db.compileStatement(sqlStatment);
                            statement.bindLong(1, ++id);
                            statement.bindString(2, name.toString());
                            statement.bindString(3, surname.toString());
                            statement.executeInsert();
                            buttonShow.callOnClick();

                        }
                    }
                }
        );

        Button add = findViewById(R.id.buttonAdd);
        add.setOnClickListener((view) -> {
            Intent intent =new Intent(getBaseContext(), Add.class);
            getResult.launch(intent);
        });
    }

    private void insertIntoDb(){
        String sqlCount = "SELECT COUNT(*) FROM Osoby";
        Cursor cursor = db.rawQuery(sqlCount, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();

        if(count == 0){
            String sqlInsert = "INSERT INTO Osoby VALUES (?, ?, ?)";
            SQLiteStatement statement = db.compileStatement(sqlInsert);

            statement.bindLong(1, 1);
            statement.bindString(2, "Jan");
            statement.bindString(3, "Kowalski");
            statement.executeInsert();

            statement.bindLong(1, 2);
            statement.bindString(2, "Anna");
            statement.bindString(3, "Nowak");
            statement.executeInsert();
        }
    }
}