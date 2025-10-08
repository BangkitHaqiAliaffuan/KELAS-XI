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
import androidx.compose.ui.text.input.PasswordVisualTransformation
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

class AdminActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AplikasiMonitoringKelasTheme {
                AdminScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen() {
    val context = LocalContext.current
    val navController = rememberNavController()
    val sharedPrefManager = remember { SharedPrefManager.getInstance(context) }
    
    // Check if user is admin
    LaunchedEffect(Unit) {
        val userRole = sharedPrefManager.getUserRole()
        if (userRole != "admin") {
            Toast.makeText(context, "Akses ditolak. Anda bukan admin.", Toast.LENGTH_LONG).show()
            context.startActivity(Intent(context, MainActivity::class.java))
            if (context is ComponentActivity) {
                context.finish()
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Dashboard - ${sharedPrefManager.getUserName() ?: "Admin"}") },
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
            AdminBottomNavigation(navController = navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "dashboard",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("users_list") {
                UsersListPage()
            }
            composable("schedules") {
                SchedulesPage()
            }
            composable("monitoring") {
                MonitoringPage()
            }
            composable("dashboard") {
                AdminDashboardPage()
            }
        }
    }
}

@Composable
fun AdminBottomNavigation(navController: NavController) {
    val items = listOf(
        Triple("dashboard", "Dashboard", Icons.Default.Dashboard),
        Triple("users_list", "Users", Icons.Default.People),
        Triple("schedules", "Jadwal", Icons.Default.Schedule),
        Triple("monitoring", "Monitoring", Icons.Default.Visibility)
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

// Users List Page - Admin dapat melihat dan manage semua users
@Composable
fun UsersListPage(viewModel: UsersViewModel = viewModel()) {
    val context = LocalContext.current
    val sharedPrefManager = remember { SharedPrefManager.getInstance(context) }
    val token = sharedPrefManager.getToken()
    
    val users by viewModel.users
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage
    val updateSuccess by viewModel.updateSuccess
    
    // Load users when page opens
    LaunchedEffect(Unit) {
        token?.let { viewModel.loadUsers(it) }
    }
    
    // Handle success and error messages
    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }
    
    LaunchedEffect(updateSuccess) {
        if (updateSuccess) {
            Toast.makeText(context, "Role berhasil diupdate", Toast.LENGTH_SHORT).show()
            viewModel.clearUpdateSuccess()
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Manajemen Users",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn {
                items(users) { user ->
                    UserCard(
                        user = user,
                        onRoleUpdate = { newRole: String ->
                            token?.let { viewModel.updateUserRole(it, user.id, newRole) }
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserCard(user: User, onRoleUpdate: (String) -> Unit) {
    var showRoleDialog by remember { mutableStateOf(false) }
    var selectedRole by remember { mutableStateOf(user.role) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = user.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(text = "Email: ${user.email}")
            Text(text = "Role: ${user.role}")
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = { showRoleDialog = true }
                ) {
                    Text("Update Role")
                }
            }
        }
    }
    
    if (showRoleDialog) {
        AlertDialog(
            onDismissRequest = { showRoleDialog = false },
            title = { Text("Update Role User") },
            text = {
                Column {
                    Text("Pilih role baru untuk ${user.name}:")
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    val roles = listOf("admin", "guru", "siswa")
                    roles.forEach { role ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedRole == role,
                                onClick = { selectedRole = role }
                            )
                            Text(
                                text = role.replaceFirstChar { it.uppercase() },
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onRoleUpdate(selectedRole)
                        showRoleDialog = false
                    }
                ) {
                    Text("Update")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showRoleDialog = false }
                ) {
                    Text("Batal")
                }
            }
        )
    }
}

// Schedules Page - Admin dapat melihat semua jadwal
@Composable
fun SchedulesPage(viewModel: ScheduleViewModel = viewModel()) {
    val context = LocalContext.current
    val sharedPrefManager = remember { SharedPrefManager.getInstance(context) }
    val token = sharedPrefManager.getToken()
    
    val schedules by viewModel.schedules
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage
    
    // Load schedules when page opens
    LaunchedEffect(Unit) {
        token?.let { viewModel.loadSchedules(it) }
    }
    
    // Handle error messages
    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            viewModel.clearError()
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
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn {
                items(schedules) { schedule ->
                    ScheduleCard(schedule = schedule)
                }
            }
        }
    }
}

@Composable
fun ScheduleCard(schedule: Schedule) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = schedule.mata_pelajaran,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(text = "Hari: ${schedule.hari}")
            Text(text = "Kelas: ${schedule.kelas}")
            Text(text = "Guru: ${schedule.guru.name}")
            Text(text = "Waktu: ${schedule.jam_mulai} - ${schedule.jam_selesai}")
            schedule.ruang?.let { ruang ->
                Text(text = "Ruang: $ruang")
            }
        }
    }
}

// Monitoring Page - Admin dapat melihat semua monitoring
@Composable
fun MonitoringPage(viewModel: MonitoringViewModel = viewModel()) {
    val context = LocalContext.current
    val sharedPrefManager = remember { SharedPrefManager.getInstance(context) }
    val token = sharedPrefManager.getToken()
    
    val monitoringList by viewModel.monitoringList
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage
    
    // Load monitoring when page opens
    LaunchedEffect(Unit) {
        token?.let { viewModel.loadMonitoring(it) }
    }
    
    // Handle error messages
    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Data Monitoring",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn {
                items(monitoringList) { monitoring ->
                    MonitoringCard(monitoring = monitoring)
                }
            }
        }
    }
}

@Composable
fun MonitoringCard(monitoring: Monitoring) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "${monitoring.mata_pelajaran} - ${monitoring.kelas}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(text = "Guru: ${monitoring.guru.name}")
            Text(text = "Status: ${monitoring.status_hadir}")
            Text(text = "Tanggal: ${monitoring.tanggal}")
            Text(text = "Jam Laporan: ${monitoring.jam_laporan}")
            Text(text = "Pelapor: ${monitoring.pelapor.name}")
            monitoring.catatan?.let { catatan ->
                Text(text = "Catatan: $catatan")
            }
        }
    }
}

// Admin Dashboard Page
@Composable
fun AdminDashboardPage() {
    val context = LocalContext.current
    val sharedPrefManager = remember { SharedPrefManager.getInstance(context) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Dashboard Admin",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Selamat Datang, ${sharedPrefManager.getUserName()}!",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Email: ${sharedPrefManager.getUserEmail()}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Role: ${sharedPrefManager.getUserRole()}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Fitur yang Tersedia:",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text("• Manajemen Users - Lihat dan update role pengguna")
                Text("• Jadwal Pelajaran - Lihat semua jadwal yang ada")
                Text("• Data Monitoring - Lihat laporan monitoring kehadiran guru")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntriJadwalPage() {
    var selectedHari by remember { mutableStateOf("") }
    var selectedKelas by remember { mutableStateOf("") }
    var selectedMataPelajaran by remember { mutableStateOf("") }
    var selectedGuru by remember { mutableStateOf("") }
    var jam by remember { mutableStateOf("") }
    var jadwalList by remember { mutableStateOf(listOf<Schedule>()) }
    
    var isHariDropdownExpanded by remember { mutableStateOf(false) }
    var isKelasDropdownExpanded by remember { mutableStateOf(false) }
    var isMataPelajaranDropdownExpanded by remember { mutableStateOf(false) }
    var isGuruDropdownExpanded by remember { mutableStateOf(false) }
    
    val hariList = listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu")
    val kelasList = listOf("X RPL", "XI RPL", "XII RPL")
    val mataPelajaranList = listOf("IPA", "IPS", "Bahasa")
    val guruList = listOf("Siti", "Budi", "Adi", "Agus")
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Entri Jadwal",
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Spinner Hari
        ExposedDropdownMenuBox(
            expanded = isHariDropdownExpanded,
            onExpandedChange = { isHariDropdownExpanded = !isHariDropdownExpanded }
        ) {
            OutlinedTextField(
                value = selectedHari,
                onValueChange = { },
                readOnly = true,
                label = { Text("Hari") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isHariDropdownExpanded)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = isHariDropdownExpanded,
                onDismissRequest = { isHariDropdownExpanded = false }
            ) {
                hariList.forEach { hari ->
                    DropdownMenuItem(
                        text = { Text(hari) },
                        onClick = {
                            selectedHari = hari
                            isHariDropdownExpanded = false
                        }
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Spinner Kelas
        ExposedDropdownMenuBox(
            expanded = isKelasDropdownExpanded,
            onExpandedChange = { isKelasDropdownExpanded = !isKelasDropdownExpanded }
        ) {
            OutlinedTextField(
                value = selectedKelas,
                onValueChange = { },
                readOnly = true,
                label = { Text("Kelas") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isKelasDropdownExpanded)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = isKelasDropdownExpanded,
                onDismissRequest = { isKelasDropdownExpanded = false }
            ) {
                kelasList.forEach { kelas ->
                    DropdownMenuItem(
                        text = { Text(kelas) },
                        onClick = {
                            selectedKelas = kelas
                            isKelasDropdownExpanded = false
                        }
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Spinner Mata Pelajaran
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
        
        // Spinner Guru
        ExposedDropdownMenuBox(
            expanded = isGuruDropdownExpanded,
            onExpandedChange = { isGuruDropdownExpanded = !isGuruDropdownExpanded }
        ) {
            OutlinedTextField(
                value = selectedGuru,
                onValueChange = { },
                readOnly = true,
                label = { Text("Guru") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isGuruDropdownExpanded)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = isGuruDropdownExpanded,
                onDismissRequest = { isGuruDropdownExpanded = false }
            ) {
                guruList.forEach { guru ->
                    DropdownMenuItem(
                        text = { Text(guru) },
                        onClick = {
                            selectedGuru = guru
                            isGuruDropdownExpanded = false
                        }
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Text Field Jam
        OutlinedTextField(
            value = jam,
            onValueChange = { jam = it },
            label = { Text("Jam Ke") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Button Simpan
        Button(
            onClick = {
                if (selectedHari.isNotEmpty() && selectedKelas.isNotEmpty() && 
                    selectedMataPelajaran.isNotEmpty() && selectedGuru.isNotEmpty() && jam.isNotEmpty()) {
                    jadwalList = jadwalList + Schedule(
                        id = 0,
                        hari = selectedHari,
                        kelas = selectedKelas,
                        mata_pelajaran = selectedMataPelajaran,
                        guru_id = 0,
                        jam_mulai = jam,
                        jam_selesai = "",
                        ruang = null,
                        created_at = "",
                        updated_at = "",
                        guru = Guru(0, selectedGuru, "")
                    )
                    // Reset form
                    selectedHari = ""
                    selectedKelas = ""
                    selectedMataPelajaran = ""
                    selectedGuru = ""
                    jam = ""
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = selectedHari.isNotEmpty() && selectedKelas.isNotEmpty() && 
                     selectedMataPelajaran.isNotEmpty() && selectedGuru.isNotEmpty() && jam.isNotEmpty()
        ) {
            Text("Simpan")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // List Jadwal yang Sudah Disimpan
        if (jadwalList.isNotEmpty()) {
            Text(
                text = "Jadwal Tersimpan:",
                fontSize = 18.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            LazyColumn {
                items(jadwalList) { jadwal ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text("${jadwal.hari} - ${jadwal.kelas}")
                            Text("${jadwal.mata_pelajaran} - Jam ke ${jadwal.jam_mulai}")
                            Text("Guru: ${jadwal.guru.name}")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UbahJadwalPage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.Edit,
            contentDescription = null,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Ubah Jadwal",
            fontSize = 24.sp
        )
        Text(
            text = "Halaman untuk mengubah jadwal",
            fontSize = 16.sp
        )
    }
}

@Composable
fun AdminListPage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.List,
            contentDescription = null,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "List",
            fontSize = 24.sp
        )
        Text(
            text = "Halaman untuk melihat daftar data",
            fontSize = 16.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AdminScreenPreview() {
    AplikasiMonitoringKelasTheme {
        AdminScreen()
    }
}