package com.kelasxi.waveoffood.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.kelasxi.waveoffood.ui.theme.*
import com.kelasxi.waveoffood.ui.viewmodels.ProfileViewModel
import com.kelasxi.waveoffood.ui.viewmodels.ProfileViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToEditProfile: () -> Unit,
    onNavigateToOrderHistory: () -> Unit,
    onNavigateToFavorites: () -> Unit,
    onNavigateToSettings: () -> Unit = {},
    onSignOut: () -> Unit,
    profileViewModel: ProfileViewModel
) {
    val user by profileViewModel.user.collectAsState()
    val isLoading by profileViewModel.isLoading.collectAsState()
    val errorMessage by profileViewModel.errorMessage.collectAsState()
    
    var showLogoutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        profileViewModel.loadCurrentUser()
    }
    
    // Show error message if any
    LaunchedEffect(errorMessage) {
        if (errorMessage.isNotEmpty()) {
            // You can show a snackbar or toast here
            // For now, we'll clear it after showing
            profileViewModel.clearError()
        }
    }
    
    // Logout confirmation dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Text("Konfirmasi Logout")
            },
            text = {
                Text("Apakah Anda yakin ingin keluar dari akun?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        profileViewModel.signOut()
                        onSignOut()
                    }
                ) {
                    Text("Ya, Keluar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showLogoutDialog = false }
                ) {
                    Text("Batal")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Profile",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Header
            item {
                ProfileHeader(
                    user = user,
                    isLoading = isLoading,
                    onEditProfile = onNavigateToEditProfile
                )
            }

            // Menu Items
            item {
                ProfileMenuSection(
                    onNavigateToOrderHistory = onNavigateToOrderHistory,
                    onNavigateToFavorites = onNavigateToFavorites,
                    onNavigateToSettings = onNavigateToSettings,
                    onSignOut = { showLogoutDialog = true }
                )
            }
        }
    }
}

@Composable
private fun ProfileHeader(
    user: com.kelasxi.waveoffood.data.models.User?,
    isLoading: Boolean,
    onEditProfile: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Picture
            Box(
                modifier = Modifier.size(120.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(40.dp),
                        color = PrimaryColor
                    )
                } else {
                    AsyncImage(
                        model = user?.profileImage ?: "",
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)),
                        contentScale = ContentScale.Crop
                    )
                    
                    // Default avatar if no image
                    if (user?.profileImage.isNullOrBlank()) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Default Avatar",
                            modifier = Modifier.size(60.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                    }
                }
                
                // Edit button
                IconButton(
                    onClick = onEditProfile,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(36.dp)
                        .background(
                            PrimaryColor,
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Profile",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // User Info
            Text(
                text = user?.name ?: "Loading...",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = user?.email ?: "",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline
            )
            
            if (!user?.phone.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = user?.phone ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

@Composable
private fun ProfileMenuSection(
    onNavigateToOrderHistory: () -> Unit,
    onNavigateToFavorites: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onSignOut: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Menu",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            ProfileMenuItem(
                icon = Icons.Default.History,
                title = "Order History",
                subtitle = "Lihat riwayat pesanan Anda",
                onClick = onNavigateToOrderHistory
            )
            
            ProfileMenuItem(
                icon = Icons.Default.Favorite,
                title = "Favorite Foods",
                subtitle = "Makanan favorit Anda",
                onClick = onNavigateToFavorites
            )
            
            ProfileMenuItem(
                icon = Icons.Default.Settings,
                title = "Settings",
                subtitle = "Pengaturan aplikasi",
                onClick = onNavigateToSettings
            )
            
            Divider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            )
            
            ProfileMenuItem(
                icon = Icons.Default.Logout,
                title = "Sign Out",
                subtitle = "Keluar dari akun",
                onClick = onSignOut,
                textColor = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    textColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = if (textColor == MaterialTheme.colorScheme.error) textColor else PrimaryColor,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = textColor
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
        
        Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = "Navigate",
            tint = MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(20.dp)
        )
    }
}
