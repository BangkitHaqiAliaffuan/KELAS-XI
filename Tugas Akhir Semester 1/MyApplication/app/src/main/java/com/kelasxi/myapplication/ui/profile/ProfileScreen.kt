package com.kelasxi.myapplication.ui.profile

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.kelasxi.myapplication.data.MockData
import com.kelasxi.myapplication.model.UserProfile
import com.kelasxi.myapplication.ui.theme.*



@Composable
fun ProfileScreen(
    onLogout: () -> Unit = {},
    onMyOrders: () -> Unit = {},
    onWishlist: () -> Unit = {}
) {
    val user = MockData.currentUser
    var isDarkMode by remember { mutableStateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGreen)
            .verticalScroll(rememberScrollState())
    ) {
        // Profile Header with gradient
        ProfileHeader(user = user)

        Spacer(modifier = Modifier.height(16.dp))

        // Stats Row
        ProfileStatsRow(user = user)

        Spacer(modifier = Modifier.height(16.dp))

        // Account Section
        ProfileMenuSection(
            title = "Akun",
            items = listOf(
                MenuItemData("My Orders", "📦", Color.Unspecified) { onMyOrders() },
                MenuItemData("My Listings", "🏷️", Color.Unspecified) {},
                MenuItemData("Wishlist", "🧡", Color.Unspecified) { onWishlist() },
                MenuItemData("Addresses", "📍", Color.Unspecified) {}
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Settings Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                Text(
                    text = "Pengaturan",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                HorizontalDivider(color = DividerColor, modifier = Modifier.padding(horizontal = 16.dp))

                MenuRow(
                    emoji = "🔔",
                    title = "Notifikasi",
                    trailing = {
                        Switch(
                            checked = notificationsEnabled,
                            onCheckedChange = { notificationsEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = GreenDeep
                            )
                        )
                    }
                )
                HorizontalDivider(color = DividerColor, modifier = Modifier.padding(horizontal = 16.dp, vertical = 0.dp))
                MenuRow(
                    emoji = "🌐",
                    title = "Bahasa",
                    subtitle = "Indonesia",
                    onClick = {}
                )
                HorizontalDivider(color = DividerColor, modifier = Modifier.padding(horizontal = 16.dp))
                MenuRow(
                    emoji = "🌙",
                    title = "Mode Gelap",
                    trailing = {
                        Switch(
                            checked = isDarkMode,
                            onCheckedChange = { isDarkMode = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = GreenDeep
                            )
                        )
                    }
                )
                HorizontalDivider(color = DividerColor, modifier = Modifier.padding(horizontal = 16.dp))
                MenuRow(
                    emoji = "🔒",
                    title = "Privasi & Ketentuan",
                    onClick = {}
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Danger Zone
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            MenuRow(
                emoji = "🚪",
                title = "Keluar",
                titleColor = StatusCancelled,
                onClick = { showLogoutDialog = true }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // App version
        Text(
            text = "TrashCare v1.0.0 · 🌱 Bersama Jaga Bumi",
            style = MaterialTheme.typography.bodySmall,
            color = TextHint,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }

    // Logout Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            icon = { Text("🚪", fontSize = 40.sp) },
            title = {
                Text("Keluar dari Akun?", fontWeight = FontWeight.Bold, color = TextPrimary)
            },
            text = {
                Text(
                    "Kamu akan keluar dari akun TrashCare. Yakin?",
                    color = TextSecondary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusCancelled),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("Keluar", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showLogoutDialog = false },
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, GreenDeep)
                ) {
                    Text("Batal", color = GreenDeep)
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }
}

@Composable
fun ProfileHeader(user: UserProfile) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(GreenDeep, GreenMedium)
                )
            )
    ) {
        // Decorative circles
        Box(
            modifier = Modifier
                .size(160.dp)
                .offset(x = 250.dp, y = (-30).dp)
                .background(GreenLight.copy(alpha = 0.25f), CircleShape)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Profil",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(GreenLight.copy(alpha = 0.3f), CircleShape)
                            .border(3.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = user.name.take(1).uppercase(),
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = user.name,
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = user.email,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                        Text(
                            text = "Bergabung ${user.memberSince}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
                OutlinedIconButton(
                    onClick = {},
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.7f)),
                    colors = IconButtonDefaults.outlinedIconButtonColors(
                        contentColor = Color.White
                    )
                ) {
                    Icon(Icons.Outlined.Edit, contentDescription = "Edit")
                }
            }
        }
    }
}

@Composable
fun ProfileStatsRow(user: UserProfile) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(value = "${user.totalPickups}", label = "Total\nPickup", emoji = "🚛")
            VerticalDivider(modifier = Modifier.height(50.dp), color = DividerColor)
            StatItem(value = "${user.itemsSold}", label = "Items\nTerjual", emoji = "🛒")
            VerticalDivider(modifier = Modifier.height(50.dp), color = DividerColor)
            StatItem(value = "${user.co2Saved}kg", label = "CO₂\nDihemat", emoji = "🌍")
        }
    }
}

@Composable
fun StatItem(value: String, label: String, emoji: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(emoji, fontSize = 22.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = GreenDeep
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

data class MenuItemData(
    val title: String,
    val emoji: String,
    val titleColor: Color,
    val onClick: () -> Unit
)

@Composable
fun ProfileMenuSection(title: String, items: List<MenuItemData>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            HorizontalDivider(color = DividerColor, modifier = Modifier.padding(horizontal = 16.dp))
            items.forEachIndexed { idx, item ->
                MenuRow(
                    emoji = item.emoji,
                    title = item.title,
                    titleColor = item.titleColor,
                    onClick = item.onClick
                )
                if (idx < items.size - 1) {
                    HorizontalDivider(
                        color = DividerColor,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MenuRow(
    emoji: String,
    title: String,
    subtitle: String? = null,
    titleColor: Color = Color.Unspecified,
    trailing: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    val modifier = if (onClick != null) {
        Modifier.clickable { onClick() }
    } else Modifier

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(SurfaceVariant, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(emoji, fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    color = if (titleColor == Color.Unspecified) TextPrimary else titleColor
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }
        if (trailing != null) {
            trailing()
        } else if (onClick != null) {
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = TextHint,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Preview(showBackground = true, name = "Profile Screen")
@Composable
fun ProfileScreenPreview() {
    TrashCareTheme {
        ProfileScreen()
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES, name = "Profile Dark")
@Composable
fun ProfileScreenDarkPreview() {
    TrashCareTheme(darkTheme = true) {
        ProfileScreen()
    }
}
