package com.kelasxi.aplikasimonitoringkelas

import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
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
import com.kelasxi.aplikasimonitoringkelas.data.model.User
import com.kelasxi.aplikasimonitoringkelas.ui.components.SchoolButton
import com.kelasxi.aplikasimonitoringkelas.ui.components.SchoolCard
import com.kelasxi.aplikasimonitoringkelas.ui.components.SchoolDropdownField
import com.kelasxi.aplikasimonitoringkelas.ui.components.SchoolTextField
import com.kelasxi.aplikasimonitoringkelas.ui.components.SchoolTopBar
import com.kelasxi.aplikasimonitoringkelas.ui.components.getRoleIcon
import com.kelasxi.aplikasimonitoringkelas.ui.theme.AplikasiMonitoringKelasTheme
import com.kelasxi.aplikasimonitoringkelas.ui.theme.Dimensions
import com.kelasxi.aplikasimonitoringkelas.ui.theme.Spacing
import com.kelasxi.aplikasimonitoringkelas.utils.SharedPrefManager
import com.kelasxi.aplikasimonitoringkelas.viewmodel.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntriUserPage(viewModel: UsersViewModel = viewModel()) {
    val context = LocalContext.current
    val sharedPrefManager = remember { SharedPrefManager.getInstance(context) }
    val token = sharedPrefManager.getToken()
    
    var selectedRole by remember { mutableStateOf("") }
    var nama by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var userList by remember { mutableStateOf(listOf<User>()) }
    var isRoleDropdownExpanded by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    // Load the existing users list once
    LaunchedEffect(Unit) {
        token?.let { viewModel.loadUsers(it) }
    }
    
    // Listen to changes in the ViewModel users list
    val usersFromViewModel by viewModel.users
    val viewModelErrorMessage by viewModel.errorMessage
    val updateSuccess by viewModel.updateSuccess
    
    // Handle error messages
    LaunchedEffect(viewModelErrorMessage) {
        viewModelErrorMessage?.let { message ->
            errorMessage = message
            viewModel.clearError()
        }
    }
    
    // Handle success messages
    LaunchedEffect(updateSuccess) {
        if (updateSuccess) {
            errorMessage = "User berhasil ditambahkan"
            viewModel.clearUpdateSuccess()
        }
    }
    
    val roles = listOf("siswa", "guru", "admin", "kepala_sekolah")
    
    Scaffold(
        topBar = {
            SchoolTopBar(
                title = "Entri User Baru"
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(Dimensions.screenPadding),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
        
        // Role Selection Card
        SchoolCard {
            Text(
                text = "Pilih Role User",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = Spacing.md)
            )
            
            SchoolDropdownField(
                value = when (selectedRole) {
                    "siswa" -> "Siswa"
                    "guru" -> "Guru"
                    "admin" -> "Admin"
                    "kepala_sekolah" -> "Kepala Sekolah"
                    else -> selectedRole
                },
                onValueChange = { 
                    selectedRole = when (it) {
                        "Siswa" -> "siswa"
                        "Guru" -> "guru"
                        "Admin" -> "admin"
                        "Kepala Sekolah" -> "kepala_sekolah"
                        else -> it
                    }
                },
                options = roles.map { role ->
                    when (role) {
                        "siswa" -> "Siswa"
                        "guru" -> "Guru"
                        "admin" -> "Admin"
                        "kepala_sekolah" -> "Kepala Sekolah"
                        else -> role
                    }
                },
                label = "Role Pengguna",
                placeholder = "Pilih role untuk pengguna baru",
                leadingIcon = getRoleIcon(selectedRole)
            )
        }

        // Personal Information Card
        SchoolCard {
            Text(
                text = "Informasi Personal",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = Spacing.md)
            )
            
            SchoolTextField(
                value = nama,
                onValueChange = { nama = it },
                label = "Nama Lengkap",
                placeholder = "Masukkan nama lengkap pengguna",
                leadingIcon = Icons.Default.Person,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(Spacing.md))
            
            SchoolTextField(
                value = email,
                onValueChange = { 
                    email = it
                    emailError = !Patterns.EMAIL_ADDRESS.matcher(it).matches() && it.isNotEmpty()
                },
                label = "Email",
                placeholder = "contoh@sekolah.edu",
                leadingIcon = Icons.Default.Email,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = emailError,
                errorMessage = if (emailError) "Format email tidak valid" else null,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(Spacing.md))
            
            SchoolTextField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                placeholder = "Minimal 6 karakter",
                leadingIcon = Icons.Default.Lock,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        // Action Buttons Card
        SchoolCard {
            SchoolButton(
                onClick = {
                    if (selectedRole.isNotEmpty() && nama.isNotEmpty() && 
                        email.isNotEmpty() && password.isNotEmpty() && !emailError) {
                        if (token != null) {
                            // This would be implemented with a create user API call
                            errorMessage = "Fitur ini akan diimplementasikan - memerlukan endpoint API untuk menambah user"
                        } else {
                            errorMessage = "Token tidak ditemukan, harap login terlebih dahulu"
                        }
                    }
                },
                text = "Tambah User",
                loading = isLoading,
                enabled = selectedRole.isNotEmpty() && nama.isNotEmpty() && 
                         email.isNotEmpty() && password.isNotEmpty() && !emailError && !isLoading,
                leadingIcon = Icons.Default.PersonAdd,
                modifier = Modifier.fillMaxWidth()
            )
            
            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(Spacing.md))
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.errorContainer,
                            RoundedCornerShape(Dimensions.surfaceCornerRadius)
                        )
                        .padding(Spacing.md),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.size(Dimensions.iconSizeSmall)
                    )
                    
                    Spacer(modifier = Modifier.width(Spacing.sm))
                    
                    Text(
                        text = errorMessage!!,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
            
            // Users List Section
            SchoolCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Daftar Pengguna",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    Text(
                        text = "${usersFromViewModel.size} pengguna",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Spacer(modifier = Modifier.height(Spacing.md))
                
                if (usersFromViewModel.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Spacing.xl),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.outline
                            )
                            Spacer(modifier = Modifier.height(Spacing.md))
                            Text(
                                text = "Belum ada pengguna",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(Spacing.sm)
                    ) {
                        items(usersFromViewModel) { user ->
                            UserCard(user = user)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UserCard(user: User) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = Dimensions.elevationSmall),
        shape = RoundedCornerShape(Dimensions.cardCornerRadius),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Role Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        RoundedCornerShape(Dimensions.surfaceCornerRadius)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getRoleIcon(user.role),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(Dimensions.iconSize)
                )
            }
            
            Spacer(modifier = Modifier.width(Spacing.md))
            
            // User Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = user.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(Spacing.xs))
                
                Text(
                    text = user.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(Spacing.xs))
                
                // Role Chip
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = when (user.role.lowercase()) {
                        "admin", "kepala_sekolah" -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        "guru" -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                        else -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f)
                    }
                ) {
                    Text(
                        text = when (user.role) {
                            "siswa" -> "Siswa"
                            "guru" -> "Guru"
                            "admin" -> "Admin"
                            "kepala_sekolah" -> "Kepala Sekolah"
                            else -> user.role
                        },
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = when (user.role.lowercase()) {
                            "admin", "kepala_sekolah" -> MaterialTheme.colorScheme.primary
                            "guru" -> MaterialTheme.colorScheme.secondary
                            else -> MaterialTheme.colorScheme.tertiary
                        },
                        modifier = Modifier.padding(horizontal = Spacing.sm, vertical = Spacing.xs)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Chip(
    onClick: () -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            label()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EntriUserPagePreview() {
    AplikasiMonitoringKelasTheme {
        EntriUserPage()
    }
}