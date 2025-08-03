package com.kelasxi.firstproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView counterTextView;
    private Button buttonUp;
    private Button buttonDown;
    private int counter = 0; // Variabel untuk menyimpan nilai counter

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Opsional: Jika Anda ingin menyembunyikan AppBar default sistem
        // dan hanya menggunakan Toolbar yang Anda tambahkan di XML,
        // pastikan tema Anda di res/values/themes.xml adalah NoActionBar.
        // Contoh: getSupportActionBar().hide(); // Ini akan menyembunyikan AppBar sistem

        // Inisialisasi komponen UI dari layout XML
        counterTextView = findViewById(R.id.text_counter);
        buttonUp = findViewById(R.id.button_up);
        buttonDown = findViewById(R.id.button_down);

        // Set listener untuk tombol "COUNTER UP"
        buttonUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counter++; // Menambah nilai counter
                counterTextView.setText(String.valueOf(counter)); // Memperbarui teks TextView
            }
        });

        // Set listener untuk tombol "COUNTER DOWN"
        buttonDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counter--; // Mengurangi nilai counter
                counterTextView.setText(String.valueOf(counter)); // Memperbarui teks TextView
            }
        });
    }
}