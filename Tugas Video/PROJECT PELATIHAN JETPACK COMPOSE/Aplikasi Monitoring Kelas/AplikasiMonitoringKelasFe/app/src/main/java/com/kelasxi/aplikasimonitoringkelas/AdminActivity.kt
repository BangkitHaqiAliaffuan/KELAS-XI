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
import com.kelasxi.aplikasimonitoringkelas.data.api.RetrofitClient
import com.kelasxi.aplikasimonitoringkelas.data.model.*
import com.kelasxi.aplikasimonitoringkelas.data.repository.AppRepositoryNew
import com.kelasxi.aplikasimonitoringkelas.ui.theme.AplikasiMonitoringKelasTheme
import com.kelasxi.aplikasimonitoringkelas.utils.SharedPrefManager
import com.kelasxi.aplikasimonitoringkelas.viewmodel.*
import kotlinx.coroutines.launch
import androidx.compose.ui.text.input.VisualTransformation

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
    val repository = remember { AppRepositoryNew(RetrofitClient.apiService) }
    val scope = rememberCoroutineScope()
    
    var userList by remember { mutableStateOf<List<User>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showCreateDialog by remember { mutableStateOf(false) }
    
    // Function to load users
    fun loadUsers() {
        if (token != null) {
            scope.launch {
                isLoading = true
                repository.getUsers(token)
                    .onSuccess { response ->
                        userList = response.data
                    }
                    .onFailure { error ->
                        errorMessage = error.message
                        Toast.makeText(context, "Gagal memuat users: ${error.message}", Toast.LENGTH_LONG).show()
                    }
                isLoading = false
            }
        }
    }
    
    // Load users when page opens
    LaunchedEffect(Unit) {
        loadUsers()
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Manajemen Users",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                
                // Tombol Tambah User
                IconButton(
                    onClick = { showCreateDialog = true },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.PersonAdd,
                        contentDescription = "Tambah User"
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn {
                    items(userList) { user ->
                        UserCardWithActions(
                        user = user,
                        onBanToggle = {
                            if (token != null) {
                                scope.launch {
                                    isLoading = true
                                    val result = if (user.is_banned) {
                                        repository.unbanUser(token, user.id)
                                    } else {
                                        repository.banUser(token, user.id)
                                    }
                                    
                                    result.onSuccess {
                                        Toast.makeText(
                                            context,
                                            if (user.is_banned) "User berhasil di-unban" else "User berhasil di-ban",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        loadUsers() // Reload list
                                    }.onFailure { error ->
                                        Toast.makeText(context, "Gagal: ${error.message}", Toast.LENGTH_LONG).show()
                                    }
                                    isLoading = false
                                }
                            }
                        },
                        onDelete = {
                            if (token != null) {
                                scope.launch {
                                    isLoading = true
                                    repository.deleteUser(token, user.id)
                                        .onSuccess {
                                            Toast.makeText(context, "User berhasil dihapus", Toast.LENGTH_SHORT).show()
                                            loadUsers() // Reload list
                                        }
                                        .onFailure { error ->
                                            Toast.makeText(context, "Gagal menghapus: ${error.message}", Toast.LENGTH_LONG).show()
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateUserDialog(
    onDismiss: () -> Unit,
    onUserCreated: () -> Unit
) {
    val context = LocalContext.current
    val sharedPrefManager = remember { SharedPrefManager.getInstance(context) }
    val token = sharedPrefManager.getToken()
    val repository = remember { AppRepositoryNew(RetrofitClient.apiService) }
    val scope = rememberCoroutineScope()
    
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("siswa") }
    var mataPelajaran by remember { mutableStateOf("") }
    var expandedRole by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    
    val roleList = listOf(
        "siswa" to "Siswa",
        "guru" to "Guru",
        "kurikulum" to "Kurikulum",
        "kepala_sekolah" to "Kepala Sekolah",
        "admin" to "Admin"
    )
    
    AlertDialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        modifier = Modifier.fillMaxWidth(0.95f)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Tambah User Baru",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Isi data user yang akan ditambahkan",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    if (!isLoading) {
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Tutup",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Form Fields
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Lengkap") },
                    placeholder = { Text("Masukkan nama lengkap") },
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = null)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    placeholder = { Text("contoh@email.com") },
                    leadingIcon = {
                        Icon(Icons.Default.Email, contentDescription = null)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    placeholder = { Text("Min. 6 karakter") },
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = null)
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (passwordVisible) "Sembunyikan password" else "Tampilkan password"
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) androidx.compose.ui.text.input.VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Konfirmasi Password") },
                    placeholder = { Text("Ulangi password") },
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = null)
                    },
                    trailingIcon = {
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(
                                imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (confirmPasswordVisible) "Sembunyikan password" else "Tampilkan password"
                            )
                        }
                    },
                    visualTransformation = if (confirmPasswordVisible) androidx.compose.ui.text.input.VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    singleLine = true,
                    isError = confirmPassword.isNotEmpty() && password != confirmPassword
                )
                
                if (confirmPassword.isNotEmpty() && password != confirmPassword) {
                    Text(
                        text = "Password tidak cocok",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Role Dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedRole,
                    onExpandedChange = { if (!isLoading) expandedRole = !expandedRole }
                ) {
                    OutlinedTextField(
                        value = roleList.find { it.first == selectedRole }?.second ?: "Pilih Role",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Role") },
                        leadingIcon = {
                            Icon(Icons.Default.AdminPanelSettings, contentDescription = null)
                        },
                        trailingIcon = {
                            Icon(
                                imageVector = if (expandedRole) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                contentDescription = null
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        enabled = !isLoading
                    )
                    
                    ExposedDropdownMenu(
                        expanded = expandedRole,
                        onDismissRequest = { expandedRole = false }
                    ) {
                        roleList.forEach { (value, label) ->
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = when (value) {
                                                "admin", "kepala_sekolah" -> Icons.Default.AdminPanelSettings
                                                "kurikulum" -> Icons.Default.School
                                                "guru" -> Icons.Default.Person
                                                else -> Icons.Default.PersonOutline
                                            },
                                            contentDescription = null,
                                            tint = when (value) {
                                                "admin", "kepala_sekolah" -> MaterialTheme.colorScheme.primary
                                                "kurikulum" -> MaterialTheme.colorScheme.secondary
                                                else -> MaterialTheme.colorScheme.tertiary
                                            }
                                        )
                                        Text(label)
                                    }
                                },
                                onClick = {
                                    selectedRole = value
                                    expandedRole = false
                                    // Reset mata pelajaran jika bukan guru
                                    if (value != "guru") {
                                        mataPelajaran = ""
                                    }
                                }
                            )
                        }
                    }
                }
                
                // Mata Pelajaran field (only for guru)
                if (selectedRole == "guru") {
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    OutlinedTextField(
                        value = mataPelajaran,
                        onValueChange = { mataPelajaran = it },
                        label = { Text("Mata Pelajaran") },
                        placeholder = { Text("Contoh: Matematika") },
                        leadingIcon = {
                            Icon(Icons.Default.Book, contentDescription = null)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading,
                        singleLine = true
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        enabled = !isLoading
                    ) {
                        Text("Batal")
                    }
                    
                    Button(
                        onClick = {
                            // Validasi
                            when {
                                name.isBlank() -> {
                                    Toast.makeText(context, "Nama tidak boleh kosong", Toast.LENGTH_SHORT).show()
                                }
                                email.isBlank() -> {
                                    Toast.makeText(context, "Email tidak boleh kosong", Toast.LENGTH_SHORT).show()
                                }
                                !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                                    Toast.makeText(context, "Format email tidak valid", Toast.LENGTH_SHORT).show()
                                }
                                password.isBlank() -> {
                                    Toast.makeText(context, "Password tidak boleh kosong", Toast.LENGTH_SHORT).show()
                                }
                                password.length < 6 -> {
                                    Toast.makeText(context, "Password minimal 6 karakter", Toast.LENGTH_SHORT).show()
                                }
                                password != confirmPassword -> {
                                    Toast.makeText(context, "Password tidak cocok", Toast.LENGTH_SHORT).show()
                                }
                                selectedRole == "guru" && mataPelajaran.isBlank() -> {
                                    Toast.makeText(context, "Mata pelajaran harus diisi untuk guru", Toast.LENGTH_SHORT).show()
                                }
                                else -> {
                                    // Submit
                                    if (token != null) {
                                        scope.launch {
                                            isLoading = true
                                            val request = CreateUserRequest(
                                                name = name.trim(),
                                                email = email.trim().lowercase(),
                                                password = password,
                                                role = selectedRole,
                                                mata_pelajaran = if (selectedRole == "guru") mataPelajaran.trim() else null
                                            )
                                            
                                            repository.createUser(token, request)
                                                .onSuccess {
                                                    Toast.makeText(context, "User berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                                                    onUserCreated()
                                                }
                                                .onFailure { error ->
                                                    Toast.makeText(context, "Gagal: ${error.message}", Toast.LENGTH_LONG).show()
                                                }
                                            isLoading = false
                                        }
                                    }
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = !isLoading && name.isNotBlank() && email.isNotBlank() && 
                                 password.isNotBlank() && password == confirmPassword &&
                                 (selectedRole != "guru" || mataPelajaran.isNotBlank())
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Simpan")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserCardWithActions(
    user: User,
    onBanToggle: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showBanDialog by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (user.is_banned) 
                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
            else 
                MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = user.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (user.is_banned) 
                            MaterialTheme.colorScheme.onErrorContainer
                        else 
                            MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Email: ${user.email}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (user.is_banned) 
                            MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.7f)
                        else 
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // Role Chip
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = when (user.role.lowercase()) {
                            "admin", "kepala_sekolah" -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            "kurikulum" -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                            else -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f)
                        }
                    ) {
                        Text(
                            text = when (user.role) {
                                "siswa" -> "Siswa"
                                "kurikulum" -> "Kurikulum"
                                "admin" -> "Admin"
                                "kepala_sekolah" -> "Kepala Sekolah"
                                else -> user.role
                            },
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium,
                            color = when (user.role.lowercase()) {
                                "admin", "kepala_sekolah" -> MaterialTheme.colorScheme.primary
                                "kurikulum" -> MaterialTheme.colorScheme.secondary
                                else -> MaterialTheme.colorScheme.tertiary
                            },
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    
                    // Banned Badge
                    if (user.is_banned) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Block,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "BANNED",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
            
            // Action Buttons Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Ban/Unban Button
                if (user.role.lowercase() != "admin") {
                    IconButton(
                        onClick = { showBanDialog = true },
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = if (user.is_banned) 
                                MaterialTheme.colorScheme.primary 
                            else 
                                MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(
                            imageVector = if (user.is_banned) Icons.Default.CheckCircle else Icons.Default.Block,
                            contentDescription = if (user.is_banned) "Unban User" else "Ban User"
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    // Delete Button
                    IconButton(
                        onClick = { showDeleteDialog = true },
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete User"
                        )
                    }
                } else {
                    // Show protected badge for admin
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Protected",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
    
    // Ban/Unban Confirmation Dialog
    if (showBanDialog) {
        AlertDialog(
            onDismissRequest = { showBanDialog = false },
            icon = {
                Icon(
                    imageVector = if (user.is_banned) Icons.Default.CheckCircle else Icons.Default.Block,
                    contentDescription = null,
                    tint = if (user.is_banned) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
            },
            title = { 
                Text(if (user.is_banned) "Unban User?" else "Ban User?") 
            },
            text = {
                Text(
                    if (user.is_banned)
                        "Apakah Anda yakin ingin mengaktifkan kembali akses untuk ${user.name}? User akan dapat login kembali."
                    else
                        "Apakah Anda yakin ingin menonaktifkan akses untuk ${user.name}? User tidak akan dapat login."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onBanToggle()
                        showBanDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (user.is_banned) 
                            MaterialTheme.colorScheme.primary 
                        else 
                            MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(if (user.is_banned) "Unban" else "Ban")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBanDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
    
    // Delete Confirmation Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = { Text("Hapus User?") },
            text = {
                Column {
                    Text("Apakah Anda yakin ingin menghapus user berikut?")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Nama: ${user.name}\nEmail: ${user.email}",
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "⚠️ Tindakan ini tidak dapat dibatalkan!",
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
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

// Legacy UserCard - kept for backward compatibility but not used
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
            Text(text = "Guru: ${monitoring.guru.name}${
                if (!monitoring.guru.mata_pelajaran.isNullOrEmpty()) 
                    " (${monitoring.guru.mata_pelajaran})" 
                else ""
            }")
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
    
    // Dialog untuk Tambah User Baru
    if (showCreateDialog) {
        CreateUserDialog(
            onDismiss = { showCreateDialog = false },
            onUserCreated = {
                showCreateDialog = false
                loadUsers() // Reload list setelah berhasil menambah user
            }
        )
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