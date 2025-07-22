package com.kelasxi.datepicker;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void etTanggal(View view) {
        // Get Calendar instance for current date
        Calendar calendar = Calendar.getInstance();
        
        // Declare and initialize variables for day, month, and year
        int tgl = calendar.get(Calendar.DAY_OF_MONTH);
        int bln = calendar.get(Calendar.MONTH);
        int thn = calendar.get(Calendar.YEAR);
        
        // Initialize the etTanggal EditText by finding its ID
        EditText etTanggal = findViewById(R.id.etTanggal);
        
        // Set the current date as the text of etTanggal EditText
        etTanggal.setText(tgl + "-" + (bln + 1) + "-" + thn);
        
        // Create and configure DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    // Update the text of etTanggal EditText with selected date
                    // Add 1 to month as DatePickerDialog month is 0-indexed
                    etTanggal.setText(dayOfMonth + "-" + (month + 1) + "-" + year);
                }
            },
            thn, bln, tgl
        );
        
        // Show the DatePickerDialog
        datePickerDialog.show();
    }
}