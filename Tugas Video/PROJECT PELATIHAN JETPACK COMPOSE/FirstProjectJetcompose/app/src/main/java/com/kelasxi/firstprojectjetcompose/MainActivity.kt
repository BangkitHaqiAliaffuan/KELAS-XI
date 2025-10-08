package com.kelasxi.firstprojectjetcompose

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kelasxi.firstprojectjetcompose.ui.theme.FirstProjectJetcomposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FirstProjectJetcomposeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    FormProfile(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun FormProfile(modifier: Modifier = Modifier) {
    // State untuk menyimpan input user
    var nama by remember { mutableStateOf("") }
    var alamat by remember { mutableStateOf("") }
    var hasil by remember { mutableStateOf("") }
    
    val context = LocalContext.current
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Image
        Image(
            painter = painterResource(id = android.R.drawable.ic_menu_camera),
            contentDescription = "Profile Image",
            modifier = Modifier
                .size(120.dp)
                .padding(bottom = 16.dp)
        )
        
        // Text "Nama"
        Text(
            text = "Nama",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        // TextField untuk input nama
        TextField(
            value = nama,
            onValueChange = { nama = it },
            label = { Text("Masukkan Nama") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )
        
        // Text "Alamat"
        Text(
            text = "Alamat",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        // TextField untuk input alamat
        TextField(
            value = alamat,
            onValueChange = { alamat = it },
            label = { Text("Masukkan Alamat") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        )
        
        // Button Simpan
        Button(
            onClick = {

                Toast.makeText(context, "Data berhasil disimpan!", Toast.LENGTH_SHORT).show()
                hasil = "Nama: $nama\nAlamat: $alamat"
                // Aksi ketika button diklik
                // Bisa ditambahkan logika penyimpanan data
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(
                text = "Simpan",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
        
        // Text untuk menampilkan hasil
        if (hasil.isNotEmpty()) {
            Text(
                text = hasil,
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FormProfilePreview() {
    FirstProjectJetcomposeTheme {
        FormProfile()
    }
}