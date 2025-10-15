package com.kelasxi.aplikasimonitoringkelas

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
import com.kelasxi.aplikasimonitoringkelas.data.api.RetrofitClient
import com.kelasxi.aplikasimonitoringkelas.data.model.*
import com.kelasxi.aplikasimonitoringkelas.data.repository.AppRepositoryNew
import com.kelasxi.aplikasimonitoringkelas.ui.theme.AplikasiMonitoringKelasTheme
import com.kelasxi.aplikasimonitoringkelas.ui.theme.*
import com.kelasxi.aplikasimonitoringkelas.ui.components.*
import com.kelasxi.aplikasimonitoringkelas.utils.SharedPrefManager
import com.kelasxi.aplikasimonitoringkelas.viewmodel.*
import kotlinx.coroutines.launch

// Helper functions
private fun getCurrentDay(): String {
    val days = arrayOf("Minggu", "Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu")
    val calendar = java.util.Calendar.getInstance()
    return days[calendar.get(java.util.Calendar.DAY_OF_WEEK) - 1]
}

private fun getSubjectStatus(schedule: Schedule): SubjectStatus {
    // Simple logic to determine subject status
    val currentTime = java.util.Calendar.getInstance()
    val currentHour = currentTime.get(java.util.Calendar.HOUR_OF_DAY)
    val currentMinute = currentTime.get(java.util.Calendar.MINUTE)
    val currentTimeMinutes = currentHour * 60 + currentMinute

    // Parse start time (assuming format HH:mm)
    try {
        val startParts = schedule.jam_mulai.split(":")
        val startHour = startParts[0].toInt()
        val startMinute = startParts[1].toInt()
        val startTimeMinutes = startHour * 60 + startMinute

        val endParts = schedule.jam_selesai.split(":")
        val endHour = endParts[0].toInt()
        val endMinute = endParts[1].toInt()
        val endTimeMinutes = endHour * 60 + endMinute

        return when {
            currentTimeMinutes < startTimeMinutes -> SubjectStatus.Scheduled
            currentTimeMinutes in startTimeMinutes..endTimeMinutes -> SubjectStatus.Ongoing
            else -> SubjectStatus.Completed
        }
    } catch (e: Exception) {
        return SubjectStatus.Scheduled
    }
}

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
            Toast.makeText(context, "Akses ditolak. Anda bukan siswa.", Toast.LENGTH_LONG)
                .show()
            context.startActivity(Intent(context, MainActivity::class.java))
            if (context is ComponentActivity) {
                context.finish()
            }
        }
    }

    Scaffold(
        topBar = {
            SchoolTopBar(
                title = "Dashboard Siswa",
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                    ) {
                        SchoolAvatar(
                            name = sharedPrefManager.getUserName() ?: "Siswa",
                            size = AvatarSize.Small,
                            backgroundColor = SMKPrimary
                        )
                        Text(
                            text = sharedPrefManager.getUserName() ?: "Siswa",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = SMKOnSurface
                        )
                        IconButton(
                            onClick = {
                                sharedPrefManager.logout()
                                context.startActivity(
                                    Intent(
                                        context,
                                        MainActivity::class.java
                                    )
                                )
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
            // REMOVED: Tugas and Nilai routes - no longer needed for siswa
            composable("entri") {
                EntriPage()
            }
            composable("list") {
                ListPage()
            }
            composable("my_reports") {
                MyReportsPage()
            }
        }
    }
}

@Composable
fun SiswaBottomNavigation(navController: NavController) {
    val items = listOf(
        Triple("jadwal_pelajaran", "Jadwal", Icons.Default.Schedule),
        Triple("entri", "Entri", Icons.Default.Add),
        Triple("list", "List", Icons.Default.List),
        Triple("my_reports", "Laporan Saya", Icons.Default.Description)
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

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Subtle gradient background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            SMKPrimary.copy(alpha = 0.02f),
                            SMKSurface,
                            SMKAccent.copy(alpha = 0.01f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.lg)
        ) {
            // Enhanced Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Jadwal Pelajaran",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = SMKOnSurface
                    )
                    Text(
                        text = "Hari ini - ${getCurrentDay()}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = NeutralGray600
                    )
                }

                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = SMKPrimary,
                    modifier = Modifier.size(Dimensions.iconSize)
                )
            }

            Spacer(modifier = Modifier.height(Spacing.xl))

            when {
                isLoading -> {
                    SchoolLoadingCard(
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                errorMessage != null -> {
                    SchoolEmptyState(
                        title = "Terjadi Kesalahan",
                        subtitle = errorMessage ?: "Tidak dapat memuat jadwal",
                        icon = Icons.Default.Error,
                        actionText = "Coba Lagi",
                        onActionClick = { token?.let { scheduleViewModel.loadSchedules(it) } }
                    )
                }

                schedules.isEmpty() -> {
                    SchoolEmptyState(
                        title = "Belum Ada Jadwal",
                        subtitle = "Jadwal pelajaran belum tersedia untuk hari ini",
                        icon = Icons.Default.EventBusy,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(Spacing.md)
                    ) {
                        items(schedules) { schedule ->
                            SchoolSubjectCard(
                                subjectName = schedule.mata_pelajaran,
                                teacherName = schedule.guru.name,
                                time = "${schedule.jam_mulai} - ${schedule.jam_selesai}",
                                status = getSubjectStatus(schedule),
                                onClick = {
                                    // Handle schedule item click if needed
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

// My Reports Page - Siswa dapat melihat laporan monitoring yang telah mereka buat
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyReportsPage() {
    val context = LocalContext.current
    val sharedPrefManager = remember { SharedPrefManager.getInstance(context) }
    val token = sharedPrefManager.getToken()
    val repository = remember { AppRepositoryNew(RetrofitClient.apiService) }
    val scope = rememberCoroutineScope()
    
    var monitoringList by remember { mutableStateOf<List<Monitoring>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    // Load my reports when page opens
    LaunchedEffect(Unit) {
        if (token != null) {
            scope.launch {
                isLoading = true
                repository.getMyReports(token)
                    .onSuccess { response ->
                        monitoringList = response.data
                    }
                    .onFailure { error ->
                        errorMessage = error.message
                        Toast.makeText(context, "Gagal memuat laporan: ${error.message}", Toast.LENGTH_LONG).show()
                    }
                isLoading = false
            }
        }
    }
    
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            SMKPrimary.copy(alpha = 0.02f),
                            SMKSurface,
                            SMKAccent.copy(alpha = 0.01f)
                        )
                    )
                )
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.lg)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Laporan Saya",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = SMKOnSurface
                    )
                    Text(
                        text = "Riwayat monitoring yang telah dibuat",
                        style = MaterialTheme.typography.bodyMedium,
                        color = NeutralGray600
                    )
                }
                
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = null,
                    tint = SMKPrimary,
                    modifier = Modifier.size(Dimensions.iconSize)
                )
            }
            
            Spacer(modifier = Modifier.height(Spacing.xl))
            
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
                    SchoolEmptyState(
                        title = "Terjadi Kesalahan",
                        subtitle = errorMessage ?: "Tidak dapat memuat laporan",
                        icon = Icons.Default.Error,
                        actionText = "Coba Lagi",
                        onActionClick = {
                            if (token != null) {
                                scope.launch {
                                    isLoading = true
                                    repository.getMyReports(token)
                                        .onSuccess { response ->
                                            monitoringList = response.data
                                            errorMessage = null
                                        }
                                        .onFailure { error ->
                                            errorMessage = error.message
                                        }
                                    isLoading = false
                                }
                            }
                        }
                    )
                }
                
                monitoringList.isEmpty() -> {
                    SchoolEmptyState(
                        title = "Belum Ada Laporan",
                        subtitle = "Anda belum membuat laporan monitoring. Buat laporan dari menu Entri.",
                        icon = Icons.Default.EventBusy,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                
                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(Spacing.md)
                    ) {
                        items(monitoringList) { monitoring ->
                            MyReportCard(monitoring = monitoring)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MyReportCard(monitoring: Monitoring) {
    SchoolCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                // Date and Time
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = SMKPrimary,
                        modifier = Modifier.size(Dimensions.iconSizeSmall)
                    )
                    Text(
                        text = monitoring.tanggal,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = SMKOnSurface
                    )
                    
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = SMKSecondary,
                        modifier = Modifier.size(Dimensions.iconSizeSmall)
                    )
                    Text(
                        text = monitoring.jam_laporan,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = SMKSecondary
                    )
                }
                
                Spacer(modifier = Modifier.height(Spacing.sm))
                
                // Mata Pelajaran
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                ) {
                    Icon(
                        imageVector = Icons.Default.Book,
                        contentDescription = null,
                        tint = NeutralGray600,
                        modifier = Modifier.size(Dimensions.iconSizeSmall)
                    )
                    Text(
                        text = monitoring.mata_pelajaran,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SMKOnSurface
                    )
                }
                
                Spacer(modifier = Modifier.height(Spacing.sm))
                
                // Keterangan
                Text(
                    text = monitoring.keterangan,
                    style = MaterialTheme.typography.bodyMedium,
                    color = NeutralGray700
                )
                
                Spacer(modifier = Modifier.height(Spacing.md))
                
                // Status Badge
                Surface(
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                    color = when (monitoring.status.lowercase()) {
                        "hadir" -> SuccessGreen.copy(alpha = 0.1f)
                        "terlambat" -> WarningYellow.copy(alpha = 0.1f)
                        else -> ErrorRed.copy(alpha = 0.1f)
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = Spacing.sm, vertical = Spacing.xs),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                    ) {
                        Icon(
                            imageVector = when (monitoring.status.lowercase()) {
                                "hadir" -> Icons.Default.CheckCircle
                                "terlambat" -> Icons.Default.Warning
                                else -> Icons.Default.Cancel
                            },
                            contentDescription = null,
                            tint = when (monitoring.status.lowercase()) {
                                "hadir" -> SuccessGreen
                                "terlambat" -> WarningYellow
                                else -> ErrorRed
                            },
                            modifier = Modifier.size(Dimensions.iconSizeSmall)
                        )
                        Text(
                            text = monitoring.status,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = when (monitoring.status.lowercase()) {
                                "hadir" -> SuccessGreen
                                "terlambat" -> WarningYellow
                                else -> ErrorRed
                            }
                        )
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
            Toast.makeText(context, "Monitoring berhasil disimpan!", Toast.LENGTH_SHORT)
                .show()
            monitoringViewModel.clearSubmitSuccess()
            // Reset form
            selectedMataPelajaran = ""
            keterangan = ""
        }
    }

    // Get available subjects from schedules
    val mataPelajaranList = schedules.map { it.mata_pelajaran }.distinct()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            SMKAccent.copy(alpha = 0.02f),
                            SMKSurface,
                            SMKPrimary.copy(alpha = 0.01f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(Spacing.lg)
        ) {
            // Enhanced Header
            SchoolCard(
                variant = CardVariant.Gradient,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = SMKPrimary,
                        modifier = Modifier.size(Dimensions.iconSize)
                    )
                    Column {
                        Text(
                            text = "Entri Monitoring Kehadiran",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = SMKOnSurface
                        )
                        Text(
                            text = "Catat kehadiran Anda untuk mata pelajaran hari ini",
                            style = MaterialTheme.typography.bodyMedium,
                            color = NeutralGray600
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.xl))

            // Form Card
            SchoolCard(
                variant = CardVariant.Default,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Informasi Kehadiran",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = SMKOnSurface
                )

                Spacer(modifier = Modifier.height(Spacing.lg))

                // Dropdown Mata Pelajaran
                SchoolDropdownField(
                    value = selectedMataPelajaran,
                    onValueChange = { selectedMataPelajaran = it },
                    options = mataPelajaranList,
                    label = "Mata Pelajaran",
                    leadingIcon = Icons.Default.Book,
                    placeholder = "Pilih mata pelajaran",
                    modifier = Modifier.fillMaxWidth()
                )


                Spacer(modifier = Modifier.height(Spacing.lg))

                // Dropdown Status Kehadiran
                SchoolDropdownField(
                    value = statusHadir,
                    onValueChange = { statusHadir = it },
                    options = listOf("Hadir", "Terlambat", "Tidak Hadir"),
                    label = "Status Kehadiran",
                    leadingIcon = Icons.Default.Check,
                    placeholder = "Pilih status kehadiran",
                    modifier = Modifier.fillMaxWidth()
                )


                Spacer(modifier = Modifier.height(Spacing.lg))

                // Text Field Keterangan
                SchoolTextField(
                    value = keterangan,
                    onValueChange = { keterangan = it },
                    label = "Keterangan",
                    placeholder = "Tambahkan keterangan (opsional)",
                    leadingIcon = Icons.Default.Notes,
                    singleLine = false,
                    minLines = 3,
                    maxLines = 5,
                    supportingText = "Berikan keterangan tambahan jika diperlukan",
                    modifier = Modifier.fillMaxWidth()
                )
            }


            Spacer(modifier = Modifier.height(Spacing.xl))

            // Submit Button
            SchoolButton(
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
                            Toast.makeText(
                                context,
                                "Error: User tidak ditemukan",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                },
                text = "SIMPAN MONITORING",
                loading = isLoading,
                leadingIcon = Icons.Default.Save,
                variant = ButtonVariant.Primary,
                enabled = selectedMataPelajaran.isNotEmpty() && statusHadir.isNotEmpty() && !isLoading,
                modifier = Modifier.fillMaxWidth()
            )


            // Display error message if any
            errorMessage?.let { error ->
                Spacer(modifier = Modifier.height(Spacing.md))
                SchoolCard(
                    variant = CardVariant.Danger,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = null,
                            tint = SMKError,
                            modifier = Modifier.size(Dimensions.iconSizeSmall)
                        )
                        Text(
                            text = error,
                            style = MaterialTheme.typography.bodySmall,
                            color = SMKError
                        )
                    }
                }
            }
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

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            SMKSuccess.copy(alpha = 0.02f),
                            SMKSurface,
                            SMKInfo.copy(alpha = 0.01f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.lg)
        ) {
            // Enhanced Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Riwayat Monitoring",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = SMKOnSurface
                    )
                    Text(
                        text = "Lihat riwayat kehadiran Anda",
                        style = MaterialTheme.typography.bodyMedium,
                        color = NeutralGray600
                    )
                }

                Icon(
                    imageVector = Icons.Default.List,
                    contentDescription = null,
                    tint = SMKPrimary,
                    modifier = Modifier.size(Dimensions.iconSize)
                )
            }

            Spacer(modifier = Modifier.height(Spacing.xl))

            when {
                isLoading -> {
                    SchoolLoadingCard(
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                errorMessage != null -> {
                    SchoolEmptyState(
                        title = "Terjadi Kesalahan",
                        subtitle = errorMessage ?: "Tidak dapat memuat riwayat monitoring",
                        icon = Icons.Default.Error,
                        actionText = "Coba Lagi",
                        onActionClick = { token?.let { monitoringViewModel.loadMonitoring(it) } }
                    )
                }

                monitoringData.isEmpty() -> {
                    SchoolEmptyState(
                        title = "Belum Ada Riwayat",
                        subtitle = "Anda belum memiliki riwayat monitoring kehadiran",
                        icon = Icons.Default.HistoryEdu,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(monitoringData) { monitoring ->
                            SchoolCard(
                                variant = CardVariant.Default,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = monitoring.mata_pelajaran
                                                ?: "Mata Pelajaran",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = SMKOnSurface
                                        )

                                        Spacer(modifier = Modifier.height(Spacing.xs))
                                        
                                        // Display guru name and subject
                                        Text(
                                            text = "${monitoring.guru.name}${
                                                if (!monitoring.guru.mata_pelajaran.isNullOrEmpty()) 
                                                    " - ${monitoring.guru.mata_pelajaran}" 
                                                else ""
                                            }",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Medium
                                        )

                                        Spacer(modifier = Modifier.height(Spacing.xs))

                                        Text(
                                            text = monitoring.kelas ?: "Kelas",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = NeutralGray600
                                        )

                                        monitoring.catatan?.let { catatan ->
                                            if (catatan.isNotEmpty()) {
                                                Spacer(modifier = Modifier.height(Spacing.xs))
                                                Text(
                                                    text = catatan,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = NeutralGray500
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(Spacing.sm))

                                        Text(
                                            text = "${monitoring.tanggal ?: ""} • ${monitoring.jam_laporan}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = NeutralGray400
                                        )
                                    }

                                    SchoolStatusBadge(
                                        text = monitoring.status_hadir ?: "Status",
                                        variant = when (monitoring.status_hadir?.lowercase()) {
                                            "hadir" -> BadgeVariant.Success
                                            "terlambat" -> BadgeVariant.Warning
                                            "tidak hadir" -> BadgeVariant.Danger
                                            else -> BadgeVariant.Default
                                        }
                                    )
                                }
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