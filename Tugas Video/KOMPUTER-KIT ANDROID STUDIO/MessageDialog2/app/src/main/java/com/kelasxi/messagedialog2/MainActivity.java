package com.kelasxi.messagedialog2;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Toast;

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

    // Fungsi untuk menampilkan Toast
    public void showToast(String pesan) {
        Toast.makeText(this, pesan, Toast.LENGTH_SHORT).show();
    }

    // Handler tombol TOAST
    public void btnToastClick(View view) {
        showToast("Belajar membuat pesan");
    }

    // Fungsi untuk menampilkan Alert Dialog sederhana
    public void showAlert(String pesan) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("PERHATIAN!");
        alertDialogBuilder.setMessage(pesan);
        alertDialogBuilder.setCancelable(true);
        alertDialogBuilder.show();
    }

    // Handler tombol ALERT DIALOG
    public void btnAlertClick(View view) {
        showAlert("Silahkan Dicoba!");
    }

    // Fungsi untuk menampilkan Alert Dialog dengan tombol YA dan TIDAK
    public void showAlertButton(String pesan) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("PERINGATAN!");
        alertDialogBuilder.setMessage(pesan);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("YA", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                showToast("Data Sudah Dihapus!");
            }
        });
        alertDialogBuilder.setNegativeButton("TIDAK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                showToast("Data Tidak Dihapus!");
            }
        });
        alertDialogBuilder.show();
    }

    // Handler tombol ALERT DIALOG BUTTON
    public void btnAlertDialogButtonClick(View view) {
        showAlertButton("Yakin Akan Menghapus?");
    }
}