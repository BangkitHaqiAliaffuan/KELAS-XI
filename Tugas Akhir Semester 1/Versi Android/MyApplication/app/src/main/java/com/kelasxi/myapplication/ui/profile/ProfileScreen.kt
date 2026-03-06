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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kelasxi.myapplication.R
import com.kelasxi.myapplication.data.MockData
import com.kelasxi.myapplication.data.network.UserDto
import com.kelasxi.myapplication.data.network.toUserProfile
import com.kelasxi.myapplication.model.UserProfile
import com.kelasxi.myapplication.ui.theme.*
import com.kelasxi.myapplication.util.LanguageManager
import com.kelasxi.myapplication.viewmodel.AuthViewModel
import com.kelasxi.myapplication.viewmodel.HomeViewModel



@Composable
fun ProfileScreen(
    onLogout: () -> Unit = {},
    onMyOrders: () -> Unit = {},
    onWishlist: () -> Unit = {},
    onMyShop: () -> Unit = {},
    onAddresses: () -> Unit = {},
    authViewModel: AuthViewModel = viewModel(),
    homeViewModel: HomeViewModel = viewModel()
) {
    val authState      by authViewModel.uiState.collectAsStateWithLifecycle()
    val totalWeightKg  by homeViewModel.totalWeightKg.collectAsStateWithLifecycle()
    // Refresh user data every time the Profile screen is composed (navigated to)
    LaunchedEffect(Unit) {
        authViewModel.loadUserProfile()
    }
    // Build a UserProfile from live UserDto; fall back to MockData only in previews
    val user = authState.user?.toUserProfile() ?: MockData.currentUser
    var isDarkMode by remember { mutableStateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val currentLang = remember { mutableStateOf(LanguageManager.getSavedLanguage(context)) }
    val currentLangLabel = if (currentLang.value == LanguageManager.LANG_EN) "English" else "Indonesia"

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
        ProfileStatsRow(user = user, totalWeightKg = totalWeightKg)

        Spacer(modifier = Modifier.height(16.dp))

        // Account Section
        ProfileMenuSection(
            title = stringResource(R.string.section_account),
            items = listOf(
                MenuItemData(stringResource(R.string.menu_my_orders), "📦", Color.Unspecified) { onMyOrders() },
                MenuItemData(stringResource(R.string.menu_my_shop), "🛍️", Color.Unspecified) { onMyShop() },
                MenuItemData(stringResource(R.string.menu_wishlist), "🧡", Color.Unspecified) { onWishlist() },
                MenuItemData(stringResource(R.string.menu_addresses), "📍", Color.Unspecified) { onAddresses() }
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
                    text = stringResource(R.string.section_settings),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                HorizontalDivider(color = DividerColor, modifier = Modifier.padding(horizontal = 16.dp))

                MenuRow(
                    emoji = "🔔",
                    title = stringResource(R.string.settings_notifications),
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
                    title = stringResource(R.string.settings_language),
                    subtitle = currentLangLabel,
                    onClick = { showLanguageDialog = true }
                )
                HorizontalDivider(color = DividerColor, modifier = Modifier.padding(horizontal = 16.dp))
                MenuRow(
                    emoji = "🌙",
                    title = stringResource(R.string.settings_dark_mode),
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
                    title = stringResource(R.string.settings_privacy),
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
                title = stringResource(R.string.menu_logout),
                titleColor = StatusCancelled,
                onClick = { showLogoutDialog = true }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // App version
        Text(
            text = stringResource(R.string.app_version),
            style = MaterialTheme.typography.bodySmall,
            color = TextHint,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }

    // ── Language Picker Dialog ─────────────────────────────────────
    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            icon = { Text("🌐", fontSize = 36.sp) },
            title = {
                Text(
                    stringResource(R.string.lang_dialog_title),
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        stringResource(R.string.lang_dialog_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    // Indonesian option
                    LanguageOption(
                        label = stringResource(R.string.lang_indonesian),
                        isSelected = currentLang.value == LanguageManager.LANG_ID,
                        onClick = {
                            if (currentLang.value != LanguageManager.LANG_ID) {
                                LanguageManager.saveLanguage(context, LanguageManager.LANG_ID)
                                currentLang.value = LanguageManager.LANG_ID
                                showLanguageDialog = false
                                // Restart activity so locale takes effect immediately
                                (context as? android.app.Activity)?.let { activity ->
                                    activity.finish()
                                    activity.startActivity(activity.intent)
                                }
                            } else {
                                showLanguageDialog = false
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    // English option
                    LanguageOption(
                        label = stringResource(R.string.lang_english),
                        isSelected = currentLang.value == LanguageManager.LANG_EN,
                        onClick = {
                            if (currentLang.value != LanguageManager.LANG_EN) {
                                LanguageManager.saveLanguage(context, LanguageManager.LANG_EN)
                                currentLang.value = LanguageManager.LANG_EN
                                showLanguageDialog = false
                                (context as? android.app.Activity)?.let { activity ->
                                    activity.finish()
                                    activity.startActivity(activity.intent)
                                }
                            } else {
                                showLanguageDialog = false
                            }
                        }
                    )
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text(stringResource(R.string.btn_cancel), color = TextSecondary)
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }

    // Logout Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            icon = { Text("🚪", fontSize = 40.sp) },
            title = {
                Text(stringResource(R.string.logout_title), fontWeight = FontWeight.Bold, color = TextPrimary)
            },
            text = {
                Text(
                    stringResource(R.string.logout_body),
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
                    Text(stringResource(R.string.btn_logout), color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showLogoutDialog = false },
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, GreenDeep)
                ) {
                    Text(stringResource(R.string.btn_cancel), color = GreenDeep)
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }
}

// ── Language Option Row ────────────────────────────────────────────
@Composable
fun LanguageOption(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) GreenDeep else DividerColor
    val bgColor     = if (isSelected) GreenDeep.copy(alpha = 0.08f) else Color.Transparent

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(1.5.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) GreenDeep else TextPrimary
        )
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .background(GreenDeep, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("✓", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
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
                text = stringResource(R.string.profile_title),
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
                            text = stringResource(R.string.profile_joined, user.memberSince),
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
                    Icon(Icons.Outlined.Edit, contentDescription = stringResource(R.string.profile_edit))
                }
            }
        }
    }
}

@Composable
fun ProfileStatsRow(user: UserProfile, totalWeightKg: Double = 0.0) {
    val weightLabel = when {
        totalWeightKg == 0.0       -> "0 kg"
        totalWeightKg < 1.0        -> "${(totalWeightKg * 1000).toInt()} g"
        totalWeightKg % 1.0 == 0.0 -> "${totalWeightKg.toInt()} kg"
        else                       -> String.format("%.1f kg", totalWeightKg)
    }
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
            StatItem(value = "${user.totalPickups}", label = stringResource(R.string.stat_pickups), emoji = "🚛")
            VerticalDivider(modifier = Modifier.height(50.dp), color = DividerColor)
            StatItem(value = "${user.itemsSold}", label = stringResource(R.string.stat_sold), emoji = "🛒")
            VerticalDivider(modifier = Modifier.height(50.dp), color = DividerColor)
            StatItem(value = weightLabel, label = stringResource(R.string.stat_recycled), emoji = "♻️")
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
