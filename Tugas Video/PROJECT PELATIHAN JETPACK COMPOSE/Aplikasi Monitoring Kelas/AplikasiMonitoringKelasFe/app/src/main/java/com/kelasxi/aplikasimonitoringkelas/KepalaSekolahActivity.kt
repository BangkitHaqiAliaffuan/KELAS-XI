package com.kelasxi.aplikasimonitoringkelas

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CompletionHandler
import com.kelasxi.aplikasimonitoringkelas.data.api.RetrofitClient
import com.kelasxi.aplikasimonitoringkelas.data.model.*
import com.kelasxi.aplikasimonitoringkelas.data.repository.AppRepositoryNew
import com.kelasxi.aplikasimonitoringkelas.utils.SharedPrefManager
import com.kelasxi.aplikasimonitoringkelas.viewmodel.*
import com.kelasxi.aplikasimonitoringkelas.ui.theme.AplikasiMonitoringKelasTheme
import com.kelasxi.aplikasimonitoringkelas.ui.theme.*
import com.kelasxi.aplikasimonitoringkelas.ui.components.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

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

// Helper functions for formatting
private fun formatDate(dateString: String): String {
    return try {
        val date = LocalDate.parse(dateString)
        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale("id", "ID"))
        date.format(formatter)
    } catch (e: Exception) {
        dateString
    }
}

private fun formatTime(timeString: String?): String {
    return try {
        if (timeString == null) return "-"
        // Handle different time formats
        val time = when {
            timeString.contains("T") -> {
                // ISO 8601 format: 2025-10-16T02:30:00.000000Z
                timeString.substringAfter("T").substringBefore(".")
            }
            timeString.length > 5 -> {
                // Format: HH:MM:SS
                timeString.substring(0, 5)
            }
            else -> timeString
        }

        // Parse and format to HH:mm
        val parsed = LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm:ss"))
        parsed.format(DateTimeFormatter.ofPattern("HH:mm"))
    } catch (e: Exception) {
        timeString ?: "-"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KepalaSekolahScreen() {
    val context = LocalContext.current
    val navController = rememberNavController()
    val sharedPrefManager = remember { SharedPrefManager.getInstance(context) }

    // Check if user is kepala sekolah
    LaunchedEffect(Unit) {
        val userRole = sharedPrefManager.getUserRole()
        if (userRole != "kepala_sekolah") {
            Toast.makeText(context, "Akses ditolak. Anda bukan kepala sekolah.", Toast.LENGTH_LONG).show()
            context.startActivity(Intent(context, MainActivity::class.java))
            if (context is ComponentActivity) {
                context.finish()
            }
        }
    }

    Scaffold(
        topBar = {
            SchoolTopBar(
                title = "Dashboard Kepala Sekolah",
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                    ) {
                        SchoolAvatar(
                            name = sharedPrefManager.getUserName() ?: "Kepala Sekolah",
                            size = AvatarSize.Small,
                            backgroundColor = SMKPrimary
                        )
                        Text(
                            text = sharedPrefManager.getUserName() ?: "Kepala Sekolah",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = SMKOnSurface
                        )
                        IconButton(
                            onClick = {
                                sharedPrefManager.logout()
                                context.startActivity(Intent(context, MainActivity::class.java))
                                if (context is ComponentActivity) {
                                    context.finish()
                                }
                            }
                        ) {
                            Icon(
                                Icons.Default.Logout,
                                contentDescription = "Logout",
                                tint = SMKPrimary
                            )
                        }
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
            startDestination = "kelas_kosong",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("kelas_kosong") {
                KepalaSekolahKelasKosongPage()
            }
            composable("guru_pengganti") {
                KepalaSekolahGuruPenggantiPage()
            }
            composable("absensi_guru") {
                KepalaSekolahAbsensiGuruPage()
            }
        }
    }
}

@Composable
fun KepalaSekolahBottomNavigation(navController: NavController) {
    val items = listOf(
        Triple("kelas_kosong", "Kelas Kosong", Icons.Default.EventBusy),
        Triple("guru_pengganti", "Guru Pengganti", Icons.Default.PersonAdd),
        Triple("absensi_guru", "Absensi Guru", Icons.Default.List)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = SMKSurface,
        contentColor = SMKPrimary,
        tonalElevation = Elevation.small
    ) {
        items.forEach { (route, label, icon) ->
            NavigationBarItem(
                icon = {
                    Icon(
                        icon,
                        contentDescription = label,
                        modifier = Modifier.size(Dimensions.iconSizeMedium)
                    )
                },
                label = {
                    Text(
                        text = label,
                        style = SchoolTypography.navigationLabel,
                        fontWeight = if (currentRoute == route) FontWeight.Bold else FontWeight.Medium
                    )
                },
                selected = currentRoute == route,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = SMKPrimary,
                    selectedTextColor = SMKPrimary,
                    unselectedIconColor = NeutralGray500,
                    unselectedTextColor = NeutralGray500,
                    indicatorColor = SMKPrimaryContainer
                ),
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

// Kelas Kosong Page - Shows empty classes from teacher_attendances
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KepalaSekolahKelasKosongPage() {
    val context = LocalContext.current
    val sharedPrefManager = remember { SharedPrefManager.getInstance(context) }
    val token = sharedPrefManager.getToken()
    val repository = remember { AppRepositoryNew(RetrofitClient.apiService) }
    val scope = rememberCoroutineScope()

    var kelasKosongList by remember { mutableStateOf<List<KelasKosong>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Load kelas kosong - this gets classes that don't have teachers present based on teacher_attendances
    fun loadKelasKosong() {
        if (token != null) {
            scope.launch {
                isLoading = true
                errorMessage = null
                // Get current date from device
                val currentDate = java.time.LocalDate.now().toString() // Format: YYYY-MM-DD
                try {
                    // Fetch empty classes based on teacher attendance records
                    repository.getKelasKosong(token, currentDate)
                        .onSuccess { response ->
                            kelasKosongList = response.data ?: emptyList()
                        }
                        .onFailure { error ->
                            errorMessage = error.message
                        }
                } catch (e: Exception) {
                    errorMessage = e.message
                } finally {
                    isLoading = false
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        loadKelasKosong()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SMKBackground)
    ) {
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SMKBackground),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = SMKPrimary)
            }
        } else if (errorMessage != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SMKBackground),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Error: $errorMessage",
                    color = SMKError,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else if (kelasKosongList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SMKBackground),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = NeutralGray400
                    )
                    Text(
                        text = "Tidak ada kelas kosong",
                        style = MaterialTheme.typography.bodyMedium,
                        color = NeutralGray600
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SMKBackground),
                contentPadding = PaddingValues(Spacing.md),
                verticalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                items(kelasKosongList) { kelas ->
                    KepalaSekolahKelasKosongCard(kelasKosong = kelas)
                }
            }
        }
    }
}

// Guru Pengganti Page - Shows teacher replacements
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KepalaSekolahGuruPenggantiPage() {
    val context = LocalContext.current
    val sharedPrefManager = remember { SharedPrefManager.getInstance(context) }
    val token = sharedPrefManager.getToken()
    val repository = remember { AppRepositoryNew(RetrofitClient.apiService) }
    val scope = rememberCoroutineScope()

    var teacherReplacementList by remember { mutableStateOf<List<TeacherReplacement>>(emptyList()) }
    var kelasKosongList by remember { mutableStateOf<List<KelasKosong>>(emptyList()) }
    var teacherList by remember { mutableStateOf<List<User>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    var selectedStatusFilter by remember { mutableStateOf<String?>(null) }
    val statusOptions = listOf("Semua", "Diganti", "Dibatalkan")

    // Load data
    fun loadData() {
        if (token != null) {
            scope.launch {
                isLoading = true
                try {
                    repository.getTeacherReplacements(token)
                        .onSuccess { response ->
                            teacherReplacementList = response.data ?: emptyList()
                        }
                        .onFailure { error ->
                            errorMessage = error.message
                        }

                    repository.getKelasKosong(token, java.time.LocalDate.now().toString())
                        .onSuccess { response ->
                            kelasKosongList = response.data ?: emptyList()
                        }
                        .onFailure { error ->
                            errorMessage = error.message
                        }

                    repository.getTeachers(token)
                        .onSuccess { response ->
                            teacherList = response.data ?: emptyList()
                        }
                        .onFailure { error ->
                            errorMessage = error.message
                        }
                } finally {
                    isLoading = false
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        loadData()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SMKBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md)
        ) {
            Text(
                text = "Filter Status",
                style = MaterialTheme.typography.labelMedium,
                color = SMKOnSurface,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = Spacing.sm)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                statusOptions.forEach { status ->
                    FilterChip(
                        selected = selectedStatusFilter == status,
                        onClick = {
                            selectedStatusFilter = if (selectedStatusFilter == status) null else status
                        },
                        label = {
                            Text(
                                text = status,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Medium
                            )
                        },
                        leadingIcon = if (selectedStatusFilter == status) {
                            {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        } else null,
                        colors = FilterChipDefaults.colors(
                            selectedContainerColor = SMKPrimary,
                            selectedLabelColor = SMKOnPrimary,
                            containerColor = NeutralGray100,
                            labelColor = SMKOnSurface
                        ),
                        modifier = Modifier.height(36.dp)
                    )
                }
            }
        }

        val filteredList = if (selectedStatusFilter != null && selectedStatusFilter != "Semua") {
            teacherReplacementList.filter { replacement ->
                when (selectedStatusFilter) {
                    "Diganti" -> replacement.status == "diganti" || replacement.status == "Diganti"
                    "Dibatalkan" -> replacement.status == "dibatalkan" || replacement.status == "Dibatalkan"
                    else -> true
                }
            }
        } else {
            teacherReplacementList
        }

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SMKBackground),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = SMKPrimary)
            }
        } else if (errorMessage != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SMKBackground),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Error: $errorMessage",
                    color = SMKError,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SMKBackground),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = NeutralGray400
                    )
                    Text(
                        text = "Tidak ada data penggantian guru",
                        style = MaterialTheme.typography.bodyMedium,
                        color = NeutralGray600
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SMKBackground),
                contentPadding = PaddingValues(Spacing.md),
                verticalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                items(filteredList) { replacement ->
                    KepalaSekolahTeacherReplacementCard(
                        replacement = replacement,
                        onCancel = {
                            scope.launch {
                                if (token != null) {
                                    repository.cancelReplacement(token, replacement.id)
                                        .onSuccess {
                                            loadData()
                                            Toast.makeText(context, "Penggantian dibatalkan", Toast.LENGTH_SHORT).show()
                                        }
                                        .onFailure { error ->
                                            Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                                        }
                                }
                            }
                        }
                    )
                }
            }
        }
    }

    // Floating Action Button
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.lg),
        contentAlignment = Alignment.BottomEnd
    ) {
        FloatingActionButton(
            onClick = { showDialog = true },
            containerColor = SMKPrimary,
            contentColor = SMKOnPrimary,
            modifier = Modifier.size(56.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Tambah Penggantian")
        }
    }

    if (showDialog) {
        KepalaSekolahTambahGuruPenggantiDialog(
            kelasKosongList = kelasKosongList,
            guruList = teacherList,
            onDismiss = { showDialog = false },
            onSubmit = { kelasKosongId, guruId, keterangan ->
                scope.launch {
                    if (token != null) {
                        val selectedKelas = kelasKosongList.find { it.attendance_id == kelasKosongId }
                        if (selectedKelas != null) {
                            repository.createGuruPengganti(token, GuruPenggantiRequest(
                                guru_pengganti_id = guruId,
                                guru_asli_id = selectedKelas.guru?.id, // Use the original teacher from the class
                                kelas = selectedKelas.kelas ?: "",
                                mata_pelajaran = selectedKelas.mata_pelajaran ?: "",
                                tanggal = selectedKelas.tanggal ?: java.time.LocalDate.now().toString(),
                                jam_mulai = selectedKelas.jam_mulai ?: "",
                                jam_selesai = selectedKelas.jam_selesai ?: "",
                                ruang = selectedKelas.ruang,
                                keterangan = keterangan
                            ))
                            .onSuccess {
                                showDialog = false
                                loadData()
                                Toast.makeText(context, "Penggantian guru berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                            }
                            .onFailure { error ->
                                Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KepalaSekolahTambahGuruPenggantiDialog(
    kelasKosongList: List<KelasKosong>,
    guruList: List<User>,
    onDismiss: () -> Unit,
    onSubmit: (kelasKosongId: Int, guruId: Int, keterangan: String) -> Unit
) {
    var selectedKelasKosongId by remember { mutableStateOf<Int?>(null) }
    var selectedGuruId by remember { mutableStateOf<Int?>(null) }
    var keterangan by remember { mutableStateOf("") }
    
    val selectedKelas = kelasKosongList.find { it.attendance_id == selectedKelasKosongId }
    val selectedGuru = guruList.find { it.id == selectedGuruId }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah Guru Pengganti") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                SchoolDropdownField(
                    value = selectedKelas?.kelas ?: "",
                    onValueChange = { newValue ->
                        val kelas = kelasKosongList.find { it.kelas == newValue }
                        selectedKelasKosongId = kelas?.attendance_id
                    },
                    options = kelasKosongList.map { it.kelas ?: "" }.distinct(),
                    label = "Pilih Kelas"
                )
                
                SchoolDropdownField(
                    value = selectedGuru?.name ?: "",
                    onValueChange = { newValue ->
                        val guru = guruList.find { it.name == newValue }
                        selectedGuruId = guru?.id
                    },
                    options = guruList.map { it.name },
                    label = "Pilih Guru Pengganti"
                )
                
                SchoolTextField(
                    value = keterangan,
                    onValueChange = { keterangan = it },
                    label = "Keterangan",
                    placeholder = "Masukkan keterangan (opsional)"
                )
            }
        },
        confirmButton = {
            SchoolButton(
                onClick = {
                    if (selectedKelasKosongId != null && selectedGuruId != null) {
                        onSubmit(selectedKelasKosongId!!, selectedGuruId!!, keterangan)
                    }
                },
                text = "Simpan",
                enabled = selectedKelasKosongId != null && selectedGuruId != null
            )
        },
        dismissButton = {
            SchoolButton(
                onClick = onDismiss,
                text = "Batal",
                variant = ButtonVariant.Ghost
            )
        }
    )
}

@Composable
fun KepalaSekolahKelasKosongCard(
    kelasKosong: KelasKosong
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.xs),
        shape = RoundedCornerShape(12.dp), // Use standard dp instead of Dimensions.cornerRadiusMedium
        colors = CardDefaults.cardColors(
            containerColor = SMKSurface,
            contentColor = SMKOnSurface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp // Use standard dp instead of Elevation.small
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = SMKPrimary
                    )
                    Column(
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = formatDate(kelasKosong.tanggal ?: ""),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = SMKOnSurface
                        )
                        Text(
                            text = kelasKosong.kelas ?: "-", // Use kelas instead of nama_kelas
                            style = MaterialTheme.typography.labelSmall,
                            color = NeutralGray600
                        )
                    }
                }
            }

            Divider(
                color = NeutralGray200,
                thickness = 1.dp,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = NeutralGray50,
                        shape = RoundedCornerShape(8.dp) // Use dp instead of CornerRadius.small
                    )
                    .padding(Spacing.md),
                horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(24.dp),
                    shape = RoundedCornerShape(6.dp),
                    color = SMKPrimaryContainer
                ) {
                    Icon(
                        Icons.Default.Schedule,
                        contentDescription = null,
                        modifier = Modifier
                            .size(18.dp)
                            .padding(3.dp),
                        tint = SMKPrimary
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(Spacing.xs)
                ) {
                    Text(
                        text = kelasKosong.mata_pelajaran ?: "-",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = SMKOnSurface
                    )

                    Text(
                        text = "${formatTime(kelasKosong.jam_mulai ?: "")} - ${formatTime(kelasKosong.jam_selesai ?: "")}",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeutralGray600
                    )
                }
            }

            if (!kelasKosong.keterangan.isNullOrEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color(0xFFFFF3E0),
                            shape = RoundedCornerShape(8.dp) // Use dp instead of CornerRadius.small
                        )
                        .padding(Spacing.md),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(24.dp),
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFFFE0B2)
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            modifier = Modifier
                                .size(18.dp)
                                .padding(3.dp),
                            tint = Color(0xFFF57C00)
                        )
                    }
                    Text(
                        text = kelasKosong.keterangan ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = SMKOnSurface
                    )
                }
            }
        }
    }
}

@Composable
fun KepalaSekolahTeacherReplacementCard(
    replacement: TeacherReplacement,
    onCancel: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.xs),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = SMKSurface,
            contentColor = SMKOnSurface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = SMKPrimary
                    )
                    Column(
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = formatDate(replacement.tanggal ?: ""),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = SMKOnSurface
                        )
                        Text(
                            text = replacement.kelas ?: "-",
                            style = MaterialTheme.typography.labelSmall,
                            color = NeutralGray600
                        )
                    }
                }

                Surface(
                    modifier = Modifier
                        .padding(Spacing.xs),
                    shape = RoundedCornerShape(8.dp),
                    color = when (replacement.status?.lowercase()) {
                        "diganti" -> Color(0xFF4CAF50)
                        "dibatalkan" -> Color(0xFFEF5350)
                        else -> NeutralGray300
                    },
                    shadowElevation = 2.dp
                ) {
                    Text(
                        text = replacement.status?.uppercase() ?: "UNKNOWN",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = when (replacement.status?.lowercase()) {
                            "diganti" -> Color.White
                            "dibatalkan" -> Color.White
                            else -> SMKOnSurface
                        },
                        modifier = Modifier.padding(
                            horizontal = Spacing.md,
                            vertical = Spacing.sm
                        )
                    )
                }
            }

            Divider(
                color = NeutralGray200,
                thickness = 1.dp,
                modifier = Modifier.fillMaxWidth()
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = NeutralGray50,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(Spacing.md),
                verticalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    Surface(
                        modifier = Modifier.size(24.dp),
                        shape = RoundedCornerShape(6.dp),
                        color = SMKPrimaryContainer
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier
                                .size(18.dp)
                                .padding(3.dp),
                            tint = SMKPrimary
                        )
                    }
                    Text(
                        text = "Guru Asli",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = SMKOnSurface
                    )
                }

                Text(
                    text = replacement.guru_asli?.name ?: "-",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = SMKOnSurface
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = Spacing.xs),
                horizontalArrangement = Arrangement.Center
            ) {
                Surface(
                    modifier = Modifier
                        .size(32.dp),
                    shape = RoundedCornerShape(50),
                    color = SMKPrimaryContainer
                ) {
                    Icon(
                        Icons.Default.ArrowDownward,
                        contentDescription = null,
                        modifier = Modifier
                            .size(18.dp),
                        tint = SMKPrimary
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFFF1F8E9),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(Spacing.md),
                verticalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    Surface(
                        modifier = Modifier.size(24.dp),
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFC8E6C9)
                    ) {
                        Icon(
                            Icons.Default.PersonAdd,
                            contentDescription = null,
                            modifier = Modifier
                                .size(18.dp)
                                .padding(3.dp),
                            tint = Color(0xFF2E7D32)
                        )
                    }
                    Text(
                        text = "Guru Pengganti",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF2E7D32)
                    )
                }

                Text(
                    text = replacement.guru_pengganti.name ?: "-",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = SMKOnSurface
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFFF3E5F5),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(Spacing.md),
                horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(24.dp),
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFE1BEE7)
                ) {
                    Icon(
                        Icons.Default.Schedule,
                        contentDescription = null,
                        modifier = Modifier
                            .size(18.dp)
                            .padding(3.dp),
                        tint = Color(0xFF7B1FA2)
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(Spacing.xs)
                ) {
                    Text(
                        text = replacement.mata_pelajaran ?: "-",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = SMKOnSurface
                    )

                    Text(
                        text = "${formatTime(replacement.jam_mulai ?: "")} - ${formatTime(replacement.jam_selesai ?: "")}",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeutralGray600
                    )
                }
            }

            if (!replacement.keterangan.isNullOrEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color(0xFFFFF3E0),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(Spacing.md),
                    verticalArrangement = Arrangement.spacedBy(Spacing.xs)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                    ) {
                        Surface(
                            modifier = Modifier.size(24.dp),
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFFFE0B2)
                        ) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(18.dp)
                                    .padding(3.dp),
                                tint = Color(0xFFF57C00)
                            )
                        }
                        Text(
                            text = "Keterangan",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFE65100)
                        )
                    }

                    Text(
                        text = replacement.keterangan ?: "-",
                        style = MaterialTheme.typography.bodySmall,
                        color = SMKOnSurface
                    )
                }
            }

            Button(
                onClick = onCancel,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFEF5350),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = null,
                    modifier = Modifier
                        .size(20.dp)
                        .padding(end = Spacing.sm)
                )
                Text(
                    text = "Batalkan Penggantian",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KepalaSekolahAbsensiGuruPage() {
    val context = LocalContext.current
    val sharedPrefManager = remember { SharedPrefManager.getInstance(context) }
    val token = sharedPrefManager.getToken()
    val repository = remember { AppRepositoryNew(RetrofitClient.apiService) }
    val scope = rememberCoroutineScope()
    
    var teacherAttendanceList by remember { mutableStateOf<List<TeacherAttendance>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var selectedDate by remember { mutableStateOf(java.time.LocalDate.now().toString()) }
    
    // Load teacher attendance data
    fun loadTeacherAttendance() {
        if (token != null) {
            scope.launch {
                isLoading = true
                repository.getTeacherAttendances(token, tanggal = selectedDate)
                    .onSuccess { response ->
                        teacherAttendanceList = response.data
                        errorMessage = null
                    }
                    .onFailure { error ->
                        errorMessage = error.message
                        teacherAttendanceList = emptyList()
                        Toast.makeText(context, "Gagal memuat data absensi: ${error.message}", Toast.LENGTH_LONG).show()
                    }
                isLoading = false
            }
        }
    }
    
    LaunchedEffect(selectedDate) {
        loadTeacherAttendance()
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SMKBackground)
            .padding(Spacing.md)
    ) {
        // Date selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Spacing.md),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Tanggal: $selectedDate",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = SMKOnSurface
            )
            
            IconButton(
                onClick = {
                    // In a full implementation, you would show a date picker
                    // For now, we'll just use the current date
                    selectedDate = java.time.LocalDate.now().toString()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Pilih Tanggal",
                    tint = SMKPrimary
                )
            }
        }
        
        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = SMKPrimary)
                }
            }
            
            errorMessage != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(Spacing.md)
                    ) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = null,
                            tint = SMKError,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "Error: $errorMessage",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SMKError,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            
            teacherAttendanceList.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(Spacing.md)
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = NeutralGray400,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "Tidak ada data absensi untuk tanggal ini",
                            style = MaterialTheme.typography.bodyMedium,
                            color = NeutralGray600
                        )
                    }
                }
            }
            
            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    items(teacherAttendanceList) { attendance ->
                        KepalaSekolahTeacherAttendanceCard(attendance = attendance)
                    }
                }
            }
        }
    }
}

@Composable
fun KepalaSekolahTeacherAttendanceCard(attendance: TeacherAttendance) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.xs),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = SMKSurface,
            contentColor = SMKOnSurface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            // Header with teacher name and class
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = attendance.guru?.name ?: "Guru Tidak Diketahui",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SMKOnSurface
                    )
                    Text(
                        text = attendance.schedule?.kelas ?: attendance.schedule?.mata_pelajaran ?: "Kelas/Mapel Tidak Diketahui",
                        style = MaterialTheme.typography.bodyMedium,
                        color = NeutralGray700
                    )
                }
                
                // Status badge
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = when (attendance.status?.lowercase()) {
                        "hadir" -> SuccessGreen
                        "telat" -> WarningYellow
                        "tidak_hadir" -> ErrorRed
                        else -> NeutralGray300
                    }
                ) {
                    Text(
                        text = attendance.status?.uppercase() ?: "STATUS",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.xs)
                    )
                }
            }
            
            Divider(
                color = NeutralGray200,
                thickness = 1.dp,
                modifier = Modifier.fillMaxWidth()
            )
            
            // Schedule and time info
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = when (attendance.status?.lowercase()) {
                            "hadir" -> Color(0xFFE8F5E8)
                            "telat" -> Color(0xFFFFF3E0)
                            "tidak_hadir" -> Color(0xFFFCE4EC)
                            else -> NeutralGray50
                        },
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(Spacing.md),
                verticalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    Icon(
                        Icons.Default.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = SMKPrimary
                    )
                    Column {
                        Text(
                            text = "Jam Jadwal: ${formatTime(attendance.schedule?.jam_mulai)} - ${formatTime(attendance.schedule?.jam_selesai)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = NeutralGray700
                        )
                        if (attendance.jamMasuk != null) {
                            Text(
                                text = "Jam Masuk: ${formatTime(attendance.jamMasuk)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = when (attendance.status?.lowercase()) {
                                    "hadir" -> SuccessGreen
                                    "telat" -> WarningYellow
                                    else -> ErrorRed
                                }
                            )
                        }
                    }
                }
                
                // Keterangan if available
                if (!attendance.keterangan.isNullOrEmpty()) {
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = SMKInfo
                        )
                        Text(
                            text = "Keterangan: ${attendance.keterangan}",
                            style = MaterialTheme.typography.bodySmall,
                            color = NeutralGray700
                        )
                    }
                }
            }
        }
    }
}
