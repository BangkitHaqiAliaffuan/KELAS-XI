package com.kelasxi.konversisuhu;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText editTextSuhu;
    private Spinner spinnerKonversi;
    private Button buttonKonversi;
    private TextView textViewHasil;

    private String[] opsiKonversi = {
            "Celsius ke Fahrenheit",
            "Fahrenheit ke Celsius",
            "Celsius ke Kelvin",
            "Kelvin ke Celsius",
            "Fahrenheit ke Kelvin",
            "Kelvin ke Fahrenheit"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inisialisasi views
        editTextSuhu = findViewById(R.id.editTextSuhu);
        spinnerKonversi = findViewById(R.id.spinnerKonversi);
        buttonKonversi = findViewById(R.id.buttonKonversi);
        textViewHasil = findViewById(R.id.textViewHasil);

        // Setup Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, opsiKonversi);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerKonversi.setAdapter(adapter);

        // Event listener untuk tombol konversi
        buttonKonversi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                konversiSuhu();
            }
        });
    }

    private void konversiSuhu() {
        String inputText = editTextSuhu.getText().toString().trim();

        if (inputText.isEmpty()) {
            Toast.makeText(this, "Masukkan nilai suhu terlebih dahulu!",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double suhuInput = Double.parseDouble(inputText);
            int pilihanKonversi = spinnerKonversi.getSelectedItemPosition();
            double hasil = 0;
            String satuanHasil = "";

            switch (pilihanKonversi) {
                case 0: // Celsius ke Fahrenheit
                    hasil = (suhuInput * 9/5) + 32;
                    satuanHasil = "°F";
                    break;
                case 1: // Fahrenheit ke Celsius
                    hasil = (suhuInput - 32) * 5/9;
                    satuanHasil = "°C";
                    break;
                case 2: // Celsius ke Kelvin
                    hasil = suhuInput + 273.15;
                    satuanHasil = "K";
                    break;
                case 3: // Kelvin ke Celsius
                    hasil = suhuInput - 273.15;
                    satuanHasil = "°C";
                    break;
                case 4: // Fahrenheit ke Kelvin
                    hasil = ((suhuInput - 32) * 5/9) + 273.15;
                    satuanHasil = "K";
                    break;
                case 5: // Kelvin ke Fahrenheit
                    hasil = ((suhuInput - 273.15) * 9/5) + 32;
                    satuanHasil = "°F";
                    break;
            }

            textViewHasil.setText(String.format("%.2f %s", hasil, satuanHasil));

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Masukkan angka yang valid!",
                    Toast.LENGTH_SHORT).show();
        }
    }
}