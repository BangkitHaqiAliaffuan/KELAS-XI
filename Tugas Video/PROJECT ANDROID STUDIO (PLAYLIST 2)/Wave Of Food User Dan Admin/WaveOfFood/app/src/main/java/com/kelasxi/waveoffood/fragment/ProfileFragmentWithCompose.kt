package com.kelasxi.waveoffood.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.firebase.auth.FirebaseAuth
import com.kelasxi.waveoffood.MyOrdersActivity
import com.kelasxi.waveoffood.ui.theme.WaveOfFoodTheme

/**
 * Enhanced Profile Fragment with Material 3 Compose UI
 * Features user information, settings, and account management
 */
class ProfileFragmentWithCompose : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                WaveOfFoodTheme {
                    ProfileScreen()
                }
            }
        }
    }
}

data class ProfileMenuItem(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val title: String,
    val subtitle: String? = null,
    val showBadge: Boolean = false,
    val badgeText: String = "",
    val showToggle: Boolean = false,
    val isToggleOn: Boolean = false,
    val onToggle: ((Boolean) -> Unit)? = null,
    val onClick: () -> Unit = {}
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen() {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    
    var showLogoutDialog by remember { mutableStateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var darkModeEnabled by remember { mutableStateOf(false) }
    var locationEnabled by remember { mutableStateOf(true) }

    val menuItems = listOf(
        ProfileMenuItem(
            icon = Icons.Default.Person,
            title = "Edit Profile",
            subtitle = "Update your personal information",
            onClick = { /* Handle edit profile */ }
        ),
        ProfileMenuItem(
            icon = Icons.Default.LocationOn,
            title = "My Addresses",
            subtitle = "Manage delivery addresses",
            onClick = { /* Handle addresses */ }
        ),
        ProfileMenuItem(
            icon = Icons.Default.ShoppingBag,
            title = "Order History",
            subtitle = "View your past orders",
            showBadge = true,
            badgeText = "5",
            onClick = { 
                // Navigate to MyOrdersActivity
                val intent = Intent(context, MyOrdersActivity::class.java)
                context.startActivity(intent)
            }
        ),
        ProfileMenuItem(
            icon = Icons.Default.FavoriteBorder,
            title = "Favorites",
            subtitle = "Your favorite dishes",
            onClick = { /* Handle favorites */ }
        ),
        ProfileMenuItem(
            icon = Icons.Default.Notifications,
            title = "Notifications",
            subtitle = "Manage notification preferences",
            showToggle = true,
            isToggleOn = notificationsEnabled,
            onToggle = { notificationsEnabled = it }
        ),
        ProfileMenuItem(
            icon = Icons.Default.LocationOn,
            title = "Location Services",
            subtitle = "Allow location access for better service",
            showToggle = true,
            isToggleOn = locationEnabled,
            onToggle = { locationEnabled = it }
        ),
        ProfileMenuItem(
            icon = Icons.Default.DarkMode,
            title = "Dark Mode",
            subtitle = "Switch to dark theme",
            showToggle = true,
            isToggleOn = darkModeEnabled,
            onToggle = { darkModeEnabled = it }
        ),
        ProfileMenuItem(
            icon = Icons.Default.Payment,
            title = "Payment Methods",
            subtitle = "Manage your payment options",
            onClick = { /* Handle payment methods */ }
        ),
        ProfileMenuItem(
            icon = Icons.Default.Help,
            title = "Help & Support",
            subtitle = "Get help or contact support",
            onClick = { /* Handle help */ }
        ),
        ProfileMenuItem(
            icon = Icons.Default.Info,
            title = "About",
            subtitle = "App version and information",
            onClick = { /* Handle about */ }
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Header
        ProfileHeader()
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // User Info Card
        UserInfoCard(
            userName = user?.displayName ?: "Guest User",
            userEmail = user?.email ?: "guest@example.com",
            userPhoto = user?.photoUrl?.toString(),
            onEditClick = { /* Handle edit profile */ }
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Stats Cards
        StatsSection()
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Menu Items
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            items(menuItems) { menuItem ->
                ProfileMenuItemCard(menuItem = menuItem)
            }
            
            item {
                Spacer(modifier = Modifier.height(16.dp))
                
                // Logout Button
                OutlinedButton(
                    onClick = { showLogoutDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.error
                        ).brush
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = "Logout",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Logout",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    // Logout Confirmation Dialog
    if (showLogoutDialog) {
        LogoutConfirmationDialog(
            onConfirm = {
                auth.signOut()
                // Handle navigation to login
                showLogoutDialog = false
            },
            onDismiss = { showLogoutDialog = false }
        )
    }
}

@Composable
private fun ProfileHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Profile",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Manage your account settings",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        IconButton(
            onClick = { /* Handle settings */ },
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings"
            )
        }
    }
}

@Composable
private fun UserInfoCard(
    userName: String,
    userEmail: String,
    userPhoto: String?,
    onEditClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile Picture
            Surface(
                modifier = Modifier.size(80.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
                shadowElevation = 4.dp
            ) {
                if (userPhoto != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(userPhoto)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Profile picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Default profile",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
            }
            
            // User Info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = userName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = userEmail,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Membership Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.wrapContentSize()
                ) {
                    Text(
                        text = "Gold Member",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            
            // Edit Button
            IconButton(
                onClick = onEditClick,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit profile"
                )
            }
        }
    }
}

@Composable
private fun StatsSection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatsCard(
            title = "Orders",
            value = "23",
            icon = Icons.Default.ShoppingBag,
            modifier = Modifier.weight(1f)
        )
        StatsCard(
            title = "Favorites",
            value = "8",
            icon = Icons.Default.FavoriteBorder,
            modifier = Modifier.weight(1f)
        )
        StatsCard(
            title = "Reviews",
            value = "15",
            icon = Icons.Default.Star,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatsCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier,
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ProfileMenuItemCard(
    menuItem: ProfileMenuItem
) {
    ElevatedCard(
        onClick = menuItem.onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon with background
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = menuItem.icon,
                        contentDescription = menuItem.title,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            // Content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = menuItem.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    if (menuItem.showBadge) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.error
                        ) {
                            Text(
                                text = menuItem.badgeText,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onError,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                
                if (menuItem.subtitle != null) {
                    Text(
                        text = menuItem.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Action
            if (menuItem.showToggle && menuItem.onToggle != null) {
                Switch(
                    checked = menuItem.isToggleOn,
                    onCheckedChange = menuItem.onToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                        checkedTrackColor = MaterialTheme.colorScheme.primary,
                        uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            } else {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Navigate",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun LogoutConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Logout,
                contentDescription = "Logout",
                tint = MaterialTheme.colorScheme.error
            )
        },
        title = {
            Text(
                text = "Logout",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = "Are you sure you want to logout? You'll need to sign in again to access your account.",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                )
            ) {
                Text("Logout")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
