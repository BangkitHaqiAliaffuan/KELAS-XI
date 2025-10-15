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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
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
import com.kelasxi.aplikasimonitoringkelas.data.api.RetrofitClient
import com.kelasxi.aplikasimonitoringkelas.data.model.*
import com.kelasxi.aplikasimonitoringkelas.data.repository.AppRepositoryNew
import com.kelasxi.aplikasimonitoringkelas.utils.SharedPrefManager
import com.kelasxi.aplikasimonitoringkelas.viewmodel.*
import com.kelasxi.aplikasimonitoringkelas.ui.theme.AplikasiMonitoringKelasTheme
import com.kelasxi.aplikasimonitoringkelas.ui.theme.*
import com.kelasxi.aplikasimonitoringkelas.ui.components.*
import kotlinx.coroutines.launch

class KurikulumActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AplikasiMonitoringKelasTheme {
                KurikulumScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KurikulumScreen() {
    val context = LocalContext.current
    val navController = rememberNavController()
    val sharedPrefManager = remember { SharedPrefManager.getInstance(context) }
    
    // Check if user is kurikulum
    LaunchedEffect(Unit) {
        val userRole = sharedPrefManager.getUserRole()
        if (userRole != "kurikulum") {
            Toast.makeText(context, "Akses ditolak. Anda bukan kurikulum.", Toast.LENGTH_LONG).show()
            context.startActivity(Intent(context, MainActivity::class.java))
            if (context is ComponentActivity) {
                context.finish()
            }
        }
    }
    
    Scaffold(
        topBar = {
            SchoolTopBar(
                title = "Dashboard Kurikulum",
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                    ) {
                        SchoolAvatar(
                            name = sharedPrefManager.getUserName() ?: "Kurikulum",
                            size = AvatarSize.Small,
                            backgroundColor = SMKPrimary
                        )
                        Text(
                            text = sharedPrefManager.getUserName() ?: "Kurikulum",
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
            KurikulumBottomNavigation(navController = navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "kelas_kosong",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("kelas_kosong") {
                KelasKosongPage()
            }
            composable("guru_pengganti") {
                GuruPenggantiPage()
            }
        }
    }
}

@Composable
fun KurikulumBottomNavigation(navController: NavController) {
    val items = listOf(
        Triple("kelas_kosong", "Kelas Kosong", Icons.Default.EventBusy),
        Triple("guru_pengganti", "Guru Pengganti", Icons.Default.PersonAdd)
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

// Kelas Kosong Page - Shows empty classes
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KelasKosongPage() {
    val context = LocalContext.current
    val sharedPrefManager = remember { SharedPrefManager.getInstance(context) }
    val token = sharedPrefManager.getToken()
    val repository = remember { AppRepositoryNew(RetrofitClient.apiService) }
    val scope = rememberCoroutineScope()
    
    var kelasKosongList by remember { mutableStateOf<List<KelasKosong>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    // Load kelas kosong
    fun loadKelasKosong() {
        if (token != null) {
            scope.launch {
                isLoading = true
                repository.getKelasKosong(token)
                    .onSuccess { response ->
                        kelasKosongList = response.data
                        errorMessage = null
                    }
                    .onFailure { error ->
                        errorMessage = error.message
                        Toast.makeText(context, "Gagal memuat kelas kosong: ${error.message}", Toast.LENGTH_LONG).show()
                    }
                isLoading = false
            }
        }
    }
    
    LaunchedEffect(Unit) {
        loadKelasKosong()
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
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
                        text = "Kelas Kosong",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = SMKOnSurface
                    )
                    Text(
                        text = "Daftar kelas yang tidak ada guru",
                        style = MaterialTheme.typography.bodyMedium,
                        color = NeutralGray600
                    )
                }
                
                Icon(
                    imageVector = Icons.Default.EventBusy,
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
                        subtitle = errorMessage ?: "Tidak dapat memuat data",
                        icon = Icons.Default.Error,
                        actionText = "Coba Lagi",
                        onActionClick = { loadKelasKosong() }
                    )
                }
                
                kelasKosongList.isEmpty() -> {
                    SchoolEmptyState(
                        title = "Semua Kelas Terisi",
                        subtitle = "Tidak ada kelas yang kosong saat ini",
                        icon = Icons.Default.CheckCircle,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                
                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(Spacing.md)
                    ) {
                        items(kelasKosongList) { kelasKosong ->
                            KelasKosongCard(kelasKosong = kelasKosong)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun KelasKosongCard(kelasKosong: KelasKosong) {
    SchoolCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            // Header with warning badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = kelasKosong.mata_pelajaran,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = SMKOnSurface
                    )
                    Spacer(modifier = Modifier.height(Spacing.xs))
                    Text(
                        text = "Kelas ${kelasKosong.kelas}",
                        style = MaterialTheme.typography.titleMedium,
                        color = SMKSecondary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = ErrorRed.copy(alpha = 0.1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = Spacing.sm, vertical = Spacing.xs),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = ErrorRed,
                            modifier = Modifier.size(Dimensions.iconSizeSmall)
                        )
                        Text(
                            text = "KOSONG",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = ErrorRed
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(Spacing.md))
            
            // Schedule info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.lg)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = NeutralGray600,
                        modifier = Modifier.size(Dimensions.iconSizeSmall)
                    )
                    Text(
                        text = kelasKosong.hari,
                        style = MaterialTheme.typography.bodyMedium,
                        color = NeutralGray700
                    )
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = NeutralGray600,
                        modifier = Modifier.size(Dimensions.iconSizeSmall)
                    )
                    Text(
                        text = "${kelasKosong.jam_mulai} - ${kelasKosong.jam_selesai}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = NeutralGray700
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(Spacing.sm))
            
            // Original teacher info
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = NeutralGray600,
                    modifier = Modifier.size(Dimensions.iconSizeSmall)
                )
                Text(
                    text = "Guru Seharusnya: ${kelasKosong.guru.name}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = NeutralGray700,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// Guru Pengganti Page - CRUD untuk guru pengganti
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuruPenggantiPage() {
    val context = LocalContext.current
    val sharedPrefManager = remember { SharedPrefManager.getInstance(context) }
    val token = sharedPrefManager.getToken()
    val repository = remember { AppRepositoryNew(RetrofitClient.apiService) }
    val scope = rememberCoroutineScope()
    
    var guruPenggantiList by remember { mutableStateOf<List<GuruPengganti>>(emptyList()) }
    var kelasKosongList by remember { mutableStateOf<List<KelasKosong>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showCreateDialog by remember { mutableStateOf(false) }
    
    // Form states
    var selectedKelasKosong by remember { mutableStateOf<KelasKosong?>(null) }
    var selectedGuruPengganti by remember { mutableStateOf("") }
    
    // Load data
    fun loadData() {
        if (token != null) {
            scope.launch {
                isLoading = true
                repository.getGuruPengganti(token)
                    .onSuccess { response ->
                        guruPenggantiList = response.data
                    }
                    .onFailure { error ->
                        errorMessage = error.message
                    }
                    
                repository.getKelasKosong(token)
                    .onSuccess { response ->
                        kelasKosongList = response.data
                    }
                    .onFailure { error ->
                        errorMessage = error.message
                    }
                isLoading = false
            }
        }
    }
    
    LaunchedEffect(Unit) {
        loadData()
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Guru Pengganti",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = SMKOnSurface
                    )
                    Text(
                        text = "Kelola penugasan guru pengganti",
                        style = MaterialTheme.typography.bodyMedium,
                        color = NeutralGray600
                    )
                }
                
                IconButton(
                    onClick = { showCreateDialog = true },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = SMKPrimary,
                        contentColor = SMKOnPrimary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.PersonAdd,
                        contentDescription = "Tambah Guru Pengganti"
                    )
                }
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
                
                guruPenggantiList.isEmpty() -> {
                    SchoolEmptyState(
                        title = "Belum Ada Penugasan",
                        subtitle = "Belum ada guru pengganti yang ditugaskan",
                        icon = Icons.Default.PersonOff,
                        actionText = "Tambah Guru Pengganti",
                        onActionClick = { showCreateDialog = true }
                    )
                }
                
                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(Spacing.md)
                    ) {
                        items(guruPenggantiList) { guruPengganti ->
                            GuruPenggantiCard(
                                guruPengganti = guruPengganti,
                                onDelete = {
                                    if (token != null) {
                                        scope.launch {
                                            isLoading = true
                                            repository.deleteGuruPengganti(token, guruPengganti.id)
                                                .onSuccess {
                                                    Toast.makeText(context, "Berhasil dihapus", Toast.LENGTH_SHORT).show()
                                                    loadData()
                                                }
                                                .onFailure { error ->
                                                    Toast.makeText(context, "Gagal: ${error.message}", Toast.LENGTH_LONG).show()
                                                }
                                            isLoading = false
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
    
    // Create Dialog
    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text("Tambah Guru Pengganti") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    Text("Pilih kelas kosong dan guru pengganti")
                    
                    // TODO: Add proper dropdowns for kelas kosong and guru selection
                    Text(
                        text = "Fitur ini memerlukan UI dropdown yang lebih kompleks",
                        style = MaterialTheme.typography.bodySmall,
                        color = NeutralGray600
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showCreateDialog = false
                        Toast.makeText(context, "Fitur akan dikembangkan lebih lanjut", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Tutup")
                }
            }
        )
    }
}

@Composable
fun GuruPenggantiCard(
    guruPengganti: GuruPengganti,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    SchoolCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = null,
                            tint = SMKPrimary,
                            modifier = Modifier.size(Dimensions.iconSizeSmall)
                        )
                        Text(
                            text = "Pergantian Guru",
                            style = MaterialTheme.typography.labelMedium,
                            color = SMKPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(Spacing.sm))
                    
                                        // Original teacher
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PersonOff,
                            contentDescription = null,
                            tint = ErrorRed,
                            modifier = Modifier.size(Dimensions.iconSizeSmall)
                        )
                        Text(
                            text = "Dari: ${guruPengganti.guruAsli?.name ?: "Tidak ada data"}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = NeutralGray700
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(Spacing.xs))
                    
                    // Substitute teacher
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = SuccessGreen,
                            modifier = Modifier.size(Dimensions.iconSizeSmall)
                        )
                        Text(
                            text = "Ke: ${guruPengganti.guruPengganti.name}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SuccessGreen,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(Spacing.sm))
                    
                    // Date
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = NeutralGray600,
                            modifier = Modifier.size(Dimensions.iconSizeSmall)
                        )
                        Text(
                            text = guruPengganti.tanggal,
                            style = MaterialTheme.typography.bodySmall,
                            color = NeutralGray600
                        )
                    }
                }
                
                IconButton(
                    onClick = { showDeleteDialog = true },
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = ErrorRed
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Hapus"
                    )
                }
            }
        }
    }
    
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = ErrorRed
                )
            },
            title = { Text("Hapus Penugasan?") },
            text = { Text("Apakah Anda yakin ingin menghapus penugasan guru pengganti ini?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ErrorRed
                    )
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}


@Preview(showBackground = true)
@Composable
fun KurikulumScreenPreview() {
    AplikasiMonitoringKelasTheme {
        KurikulumScreen()
    }
}
