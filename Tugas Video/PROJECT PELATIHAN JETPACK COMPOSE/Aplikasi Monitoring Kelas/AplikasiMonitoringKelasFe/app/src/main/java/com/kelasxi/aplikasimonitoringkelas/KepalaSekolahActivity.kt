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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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
import com.kelasxi.aplikasimonitoringkelas.utils.SharedPrefManager
import com.kelasxi.aplikasimonitoringkelas.viewmodel.*
import com.kelasxi.aplikasimonitoringkelas.ui.theme.AplikasiMonitoringKelasTheme

class KepalaSekolahActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AplikasiMonitoringKelasTheme {
                KepalaSekolahScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KepalaSekolahScreen() {
    val context = LocalContext.current
    val navController = rememberNavController()
    val sharedPrefManager = remember { SharedPrefManager.getInstance(context) }
    
    // Check if user is admin/kepala sekolah
    LaunchedEffect(Unit) {
        val userRole = sharedPrefManager.getUserRole()
        if (userRole != "admin") {
            Toast.makeText(context, "Akses ditolak. Anda bukan kepala sekolah.", Toast.LENGTH_LONG).show()
            context.startActivity(Intent(context, MainActivity::class.java))
            if (context is ComponentActivity) {
                context.finish()
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard Kepala Sekolah - ${sharedPrefManager.getUserName() ?: "Admin"}") },
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
            KepalaSekolahBottomNavigation(navController = navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "jadwal_pelajaran",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("jadwal_pelajaran") {
                KepsekJadwalPage()
            }
            composable("kelas_kosong") {
                KelasKosongPage()
            }
            composable("list") {
                KepsekListPage()
            }
        }
    }
}

@Composable
fun KepalaSekolahBottomNavigation(navController: NavController) {
    val items = listOf(
        Triple("jadwal_pelajaran", "Jadwal Pelajaran", Icons.Default.Schedule),
        Triple("kelas_kosong", "Kelas Kosong", Icons.Default.MeetingRoom),
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



@Composable
fun KepsekJadwalPage() {
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
            text = "Jadwal Pelajaran - Overview",
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

@Composable
fun KelasKosongPage() {
    val context = LocalContext.current
    val usersViewModel: UsersViewModel = viewModel()
    val scheduleViewModel: ScheduleViewModel = viewModel()
    val sharedPrefManager = remember { SharedPrefManager.getInstance(context) }
    val token = sharedPrefManager.getToken()
    
    val users by usersViewModel.users
    val schedules by scheduleViewModel.schedules
    val isLoading by usersViewModel.isLoading
    val errorMessage by usersViewModel.errorMessage
    
    // Fetch data
    LaunchedEffect(Unit) {
        token?.let { 
            usersViewModel.loadUsers(it)
            scheduleViewModel.loadSchedules(it)
        }
    }
    
    // Handle error messages
    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            usersViewModel.clearError()
        }
    }
    
    // Calculate statistics
    val totalStudents = users.count { it.role == "siswa" }
    val totalTeachers = users.count { it.role == "guru" }
    val totalSchedules = schedules.size
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Dashboard Kepala Sekolah",
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
                        token?.let {
                            usersViewModel.loadUsers(it)
                            scheduleViewModel.loadSchedules(it)
                        }
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Retry")
                }
            }
            
            else -> {
                // Statistik Section
                Text(
                    text = "📊 Statistik Sekolah:",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f))
                        ) {
                            Text(
                                text = "Total Siswa: $totalStudents siswa",
                                modifier = Modifier.padding(12.dp),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f))
                        ) {
                            Text(
                                text = "Total Guru: $totalTeachers guru",
                                modifier = Modifier.padding(12.dp),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f))
                        ) {
                            Text(
                                text = "Total Jadwal: $totalSchedules mata pelajaran",
                                modifier = Modifier.padding(12.dp),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun KepsekListPage() {
    val context = LocalContext.current
    val monitoringViewModel: MonitoringViewModel = viewModel()
    val usersViewModel: UsersViewModel = viewModel()
    val sharedPrefManager = remember { SharedPrefManager.getInstance(context) }
    val token = sharedPrefManager.getToken()
    
    val monitoringData by monitoringViewModel.monitoringList
    val users by usersViewModel.users
    val isLoading by monitoringViewModel.isLoading
    val errorMessage by monitoringViewModel.errorMessage
    
    // Fetch all data
    LaunchedEffect(Unit) {
        token?.let { 
            monitoringViewModel.loadMonitoring(it)
            usersViewModel.loadUsers(it)
        }
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
            text = "Laporan Monitoring Sekolah",
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
                        token?.let {
                            monitoringViewModel.loadMonitoring(it)
                            usersViewModel.loadUsers(it)
                        }
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
                // Statistics summary
                val siswaCount = users.count { it.role == "siswa" }
                val activeUsers = monitoringData.map { it.guru_id }.distinct().size
                
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Ringkasan Aktivitas",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Guru Aktif: $activeUsers dari ${users.size}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Total Monitoring: ${monitoringData.size}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                
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
                                    text = "Guru: ${monitoring.guru.name}${
                                        if (!monitoring.guru.mata_pelajaran.isNullOrEmpty()) 
                                            " (${monitoring.guru.mata_pelajaran})" 
                                        else ""
                                    }",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Mata Pelajaran: ${monitoring.mata_pelajaran}",
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                                Text(
                                    text = "Kelas: ${monitoring.kelas}",
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                                Text(
                                    text = "Status: ${monitoring.status_hadir}",
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                                Text(
                                    text = "Jam Laporan: ${monitoring.jam_laporan}",
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
fun KepalaSekolahScreenPreview() {
    AplikasiMonitoringKelasTheme {
        KepalaSekolahScreen()
    }
}