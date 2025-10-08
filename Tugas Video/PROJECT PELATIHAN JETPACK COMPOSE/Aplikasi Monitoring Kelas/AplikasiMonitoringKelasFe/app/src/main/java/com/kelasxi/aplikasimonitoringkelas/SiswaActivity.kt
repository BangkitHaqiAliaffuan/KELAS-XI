package com.kelasxi.aplikasimonitoringkelas

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kelasxi.aplikasimonitoringkelas.data.model.*
import com.kelasxi.aplikasimonitoringkelas.ui.theme.AplikasiMonitoringKelasTheme
import com.kelasxi.aplikasimonitoringkelas.utils.SharedPrefManager
import com.kelasxi.aplikasimonitoringkelas.viewmodel.*

class SiswaActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AplikasiMonitoringKelasTheme {
                SiswaScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SiswaScreen() {
    val context = LocalContext.current
    val navController = rememberNavController()
    val sharedPrefManager = remember { SharedPrefManager.getInstance(context) }
    
    // Check if user is siswa
    LaunchedEffect(Unit) {
        val userRole = sharedPrefManager.getUserRole()
        if (userRole != "siswa") {
            Toast.makeText(context, "Akses ditolak. Anda bukan siswa.", Toast.LENGTH_LONG).show()
            context.startActivity(Intent(context, MainActivity::class.java))
            if (context is ComponentActivity) {
                context.finish()
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Siswa Dashboard - ${sharedPrefManager.getUserName() ?: "Siswa"}") },
                actions = {
                    IconButton(
                        onClick = {
                            sharedPrefManager.logout()
                            context.startActivity(Intent(context, MainActivity::class.java))
                            if (context is ComponentActivity) {
                                context.finish()
                            }
                        }
                    ) {
                        Icon(Icons.Default.Logout, contentDescription = "Logout")
                    }
                }
            )
        },
        bottomBar = {
            SiswaBottomNavigation(navController = navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "jadwal_pelajaran",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("jadwal_pelajaran") {
                JadwalPage(userRole = "Siswa")
            }
            composable("entri") {
                EntriPage()
            }
            composable("list") {
                ListPage()
            }
        }
    }
}

@Composable
fun SiswaBottomNavigation(navController: NavController) {
    val items = listOf(
        Triple("jadwal_pelajaran", "Jadwal Pelajaran", Icons.Default.Schedule),
        Triple("entri", "Entri", Icons.Default.Add),
        Triple("list", "List", Icons.Default.List)
    )
    
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    NavigationBar {
        items.forEach { (route, label, icon) ->
            NavigationBarItem(
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label) },
                selected = currentRoute == route,
                onClick = {
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JadwalPage(userRole: String) {
    val context = LocalContext.current
    val scheduleViewModel: ScheduleViewModel = viewModel()
    val sharedPrefManager = remember { SharedPrefManager.getInstance(context) }
    val token = sharedPrefManager.getToken()
    
    val schedules by scheduleViewModel.schedules
    val isLoading by scheduleViewModel.isLoading
    val errorMessage by scheduleViewModel.errorMessage
    
    // Load schedules when page opens
    LaunchedEffect(Unit) {
        token?.let { scheduleViewModel.loadSchedules(it) }
    }
    
    // Handle error messages
    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            scheduleViewModel.clearError()
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Jadwal Pelajaran",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            errorMessage != null -> {
                Text(
                    text = "Error: $errorMessage",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                
                Button(
                    onClick = { token?.let { scheduleViewModel.loadSchedules(it) } },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Retry")
                }
            }
            
            schedules.isEmpty() -> {
                Text(
                    text = "Tidak ada jadwal tersedia",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
            
            else -> {
                LazyColumn {
                    items(schedules) { schedule ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = schedule.mata_pelajaran,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Hari: ${schedule.hari}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "Waktu: ${schedule.jam_mulai} - ${schedule.jam_selesai}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                if (schedule.kelas.isNotEmpty()) {
                                    Text(
                                        text = "Kelas: ${schedule.kelas}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                Text(
                                    text = "Guru: ${schedule.guru.name}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntriPage() {
    val context = LocalContext.current
    val monitoringViewModel: MonitoringViewModel = viewModel()
    val scheduleViewModel: ScheduleViewModel = viewModel()
    val sharedPrefManager = remember { SharedPrefManager.getInstance(context) }
    val token = sharedPrefManager.getToken()
    
    var selectedMataPelajaran by remember { mutableStateOf("") }
    var keterangan by remember { mutableStateOf("") }
    var isMataPelajaranDropdownExpanded by remember { mutableStateOf(false) }
    var isStatusDropdownExpanded by remember { mutableStateOf(false) }
    var statusHadir by remember { mutableStateOf("Hadir") } // Default status
    
    val schedules by scheduleViewModel.schedules
    val isLoading by monitoringViewModel.isSubmitting
    val errorMessage by monitoringViewModel.errorMessage
    val submitSuccess by monitoringViewModel.submitSuccess
    
    // Fetch schedules for dropdown
    LaunchedEffect(Unit) {
        token?.let { scheduleViewModel.loadSchedules(it) }
    }
    
    // Handle error messages
    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            monitoringViewModel.clearError()
        }
    }
    
    // Handle success messages
    LaunchedEffect(submitSuccess) {
        if (submitSuccess) {
            Toast.makeText(context, "Monitoring berhasil disimpan!", Toast.LENGTH_SHORT).show()
            monitoringViewModel.clearSubmitSuccess()
            // Reset form
            selectedMataPelajaran = ""
            keterangan = ""
        }
    }
    
    // Get available subjects from schedules
    val mataPelajaranList = schedules.map { it.mata_pelajaran }.distinct()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Entri Monitoring Kehadiran",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Dropdown Mata Pelajaran
        ExposedDropdownMenuBox(
            expanded = isMataPelajaranDropdownExpanded,
            onExpandedChange = { isMataPelajaranDropdownExpanded = !isMataPelajaranDropdownExpanded }
        ) {
            OutlinedTextField(
                value = selectedMataPelajaran,
                onValueChange = { },
                readOnly = true,
                label = { Text("Mata Pelajaran") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isMataPelajaranDropdownExpanded)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = isMataPelajaranDropdownExpanded,
                onDismissRequest = { isMataPelajaranDropdownExpanded = false }
            ) {
                mataPelajaranList.forEach { mataPelajaran ->
                    DropdownMenuItem(
                        text = { Text(mataPelajaran) },
                        onClick = {
                            selectedMataPelajaran = mataPelajaran
                            isMataPelajaranDropdownExpanded = false
                        }
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Dropdown Status Kehadiran
        ExposedDropdownMenuBox(
            expanded = isStatusDropdownExpanded,
            onExpandedChange = { isStatusDropdownExpanded = !isStatusDropdownExpanded }
        ) {
            OutlinedTextField(
                value = statusHadir,
                onValueChange = { },
                readOnly = true,
                label = { Text("Status Kehadiran") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isStatusDropdownExpanded)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = isStatusDropdownExpanded,
                onDismissRequest = { isStatusDropdownExpanded = false }
            ) {
                listOf("Hadir", "Terlambat", "Tidak Hadir").forEach { status ->
                    DropdownMenuItem(
                        text = { Text(status) },
                        onClick = {
                            statusHadir = status
                            isStatusDropdownExpanded = false
                        }
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Text Field Keterangan
        OutlinedTextField(
            value = keterangan,
            onValueChange = { keterangan = it },
            label = { Text("Keterangan") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Button Submit
        Button(
            onClick = {
                if (selectedMataPelajaran.isNotEmpty() && statusHadir.isNotEmpty()) {
                    val userId = sharedPrefManager.getUserId()
                    if (userId != null) {
                        monitoringViewModel.submitMonitoring(
                            token = token ?: "",
                            guruId = userId, // Assuming user ID is used as guru ID for this user
                            statusHadir = statusHadir,
                            catatan = keterangan,
                            kelas = "X RPL", // This should be dynamic in a real implementation
                            mataPelajaran = selectedMataPelajaran
                        )
                    } else {
                        Toast.makeText(context, "Error: User tidak ditemukan", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = selectedMataPelajaran.isNotEmpty() && statusHadir.isNotEmpty() && !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp))
            } else {
                Text("Simpan Monitoring")
            }
        }
        
        // Display error message if any
        errorMessage?.let { error ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Error: $error",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun ListPage() {
    val context = LocalContext.current
    val monitoringViewModel: MonitoringViewModel = viewModel()
    val sharedPrefManager = remember { SharedPrefManager.getInstance(context) }
    val token = sharedPrefManager.getToken()
    
    val monitoringData by monitoringViewModel.monitoringList
    val isLoading by monitoringViewModel.isLoading
    val errorMessage by monitoringViewModel.errorMessage
    
    // Fetch user's monitoring data
    LaunchedEffect(Unit) {
        token?.let { monitoringViewModel.loadMonitoring(it) }
    }
    
    // Handle error messages
    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            monitoringViewModel.clearError()
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Riwayat Monitoring",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            errorMessage != null -> {
                Text(
                    text = "Error: $errorMessage",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                
                Button(
                    onClick = { 
                        token?.let { monitoringViewModel.loadMonitoring(it) }
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Retry")
                }
            }
            
            monitoringData.isEmpty() -> {
                Text(
                    text = "Belum ada data monitoring",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
            
            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(monitoringData) { monitoring ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = monitoring.mata_pelajaran,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Status: ${monitoring.status_hadir}",
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                                monitoring.catatan?.let { catatan ->
                                    Text(
                                        text = "Catatan: $catatan",
                                        fontSize = 14.sp,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                                Text(
                                    text = "Tanggal: ${monitoring.tanggal}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                                Text(
                                    text = "Kelas: ${monitoring.kelas}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SiswaScreenPreview() {
    AplikasiMonitoringKelasTheme {
        SiswaScreen()
    }
}