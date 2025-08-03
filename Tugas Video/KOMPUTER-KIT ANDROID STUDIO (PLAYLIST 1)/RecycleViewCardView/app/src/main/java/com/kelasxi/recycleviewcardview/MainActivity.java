package com.kelasxi.recycleviewcardview;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // Declare RecyclerView variable
    RecyclerView recyclerView;
    
    // Declare SiswaAdapter variable
    SiswaAdapter adapter;
    
    // Declare List<Siswa> variable
    List<Siswa> siswaList;

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
        
        // Call load() within onCreate()
        load();
        
        // Call isiData() within onCreate()
        isiData();
        
        // Set the Adapter
        adapter = new SiswaAdapter(this, siswaList);
        recyclerView.setAdapter(adapter);
    }
    
    // Create a public void function load()
    public void load() {
        // Initialize recyclerView by finding its ID (rcvSiswa)
        recyclerView = findViewById(R.id.rcvSiswa);
        
        // Set recyclerView's layout manager to a new LinearLayoutManager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
    
    // Create a public void function isiData()
    public void isiData() {
        // Initialize siswaList as a new ArrayList<Siswa>
        siswaList = new ArrayList<Siswa>();
        
        // Add multiple Siswa objects to siswaList with different names and "Surabaya" as address
        siswaList.add(new Siswa("Ahmad Rizki", "Surabaya"));
        siswaList.add(new Siswa("Siti Nurhaliza", "Surabaya"));
        siswaList.add(new Siswa("Budi Santoso", "Surabaya"));
        siswaList.add(new Siswa("Dewi Sartika", "Surabaya"));
        siswaList.add(new Siswa("Eko Prasetyo", "Surabaya"));
        siswaList.add(new Siswa("Fitri Handayani", "Surabaya"));
        siswaList.add(new Siswa("Gunawan Wijaya", "Surabaya"));
        siswaList.add(new Siswa("Hani Rahmawati", "Surabaya"));
        siswaList.add(new Siswa("Indra Kusuma", "Surabaya"));
        siswaList.add(new Siswa("Joko Widodo", "Surabaya"));
    }
    
    // Create onClick method for btnTambah button
    public void btnTambah(View view) {
        // Add new Siswa data to siswaList
        siswaList.add(new Siswa("Joni Rambo", "Jakarta"));
        
        // Call adapter.notifyDataSetChanged() to refresh the RecyclerView
        adapter.notifyDataSetChanged();
    }
}