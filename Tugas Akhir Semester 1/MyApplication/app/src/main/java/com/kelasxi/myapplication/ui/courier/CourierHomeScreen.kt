package com.kelasxi.myapplication.ui.courier

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kelasxi.myapplication.data.network.CourierPickupDto
import com.kelasxi.myapplication.data.network.CourierProfileDto
import com.kelasxi.myapplication.ui.theme.*
import com.kelasxi.myapplication.viewmodel.CourierViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourierHomeScreen(
    viewModel: CourierViewModel,
    onLogout: () -> Unit,
    onNavigateRoute: (lat: Double, lng: Double, address: String) -> Unit = { _, _, _ -> }
) {
    val uiState by viewModel.uiState.collectAsState()

    // Handle logout event
    LaunchedEffect(Unit) {
        viewModel.logoutEvent.collect { onLogout() }
    }

    // Snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }
    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = BackgroundGreen
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = { viewModel.refresh() },
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
            // ── Header ───────────────────────────────────────────
            item {
                CourierHeader(
                    profile = uiState.profile,
                    isAvailable = uiState.isAvailable,
                    onToggleAvailability = { viewModel.toggleAvailability(it) },
                    onLogout = { viewModel.logout() }
                )
            }

            // ── Stats Row ────────────────────────────────────────
            item {
                CourierStatsRow(
                    profile = uiState.profile,
                    activeCount = uiState.pickups.count { it.status == "pending" || it.status == "on_the_way" },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }

            // ── Available Pickups (searching) ────────────────────
            if (uiState.availablePickups.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 8.dp, top = 12.dp, bottom = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(OrangeAccent, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Pickup Menunggu Kurir",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = OrangeAccent.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "${uiState.availablePickups.size}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OrangeAccent,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                        IconButton(onClick = {
                            viewModel.loadAvailablePickups()
                        }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = OrangeAccent)
                        }
                    }
                }
                items(uiState.availablePickups, key = { "avail_${it.id}" }) { pickup ->
                    AvailablePickupCard(
                        pickup = pickup,
                        onAccept = { viewModel.acceptPickup(pickup.id) },
                        onIgnore = { viewModel.ignorePickup(pickup.id) },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
                item {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        color = SurfaceVariant
                    )
                }
            }

            // ── Pickup List Header ───────────────────────────────
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Pickup Saya",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TextPrimary
                    )
                    IconButton(onClick = {
                        viewModel.loadPickups()
                        viewModel.loadAvailablePickups()
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = GreenDeep)
                    }
                }
            }

            // ── Loading ──────────────────────────────────────────
            if (uiState.isLoading) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = GreenDeep)
                    }
                }
            }

            // ── Empty State ───────────────────────────────────────
            if (!uiState.isLoading && uiState.pickups.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.LocalShipping,
                            contentDescription = null,
                            tint = GreenPale,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Belum ada pickup ditugaskan",
                            color = TextHint,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // ── Pickup Cards ─────────────────────────────────────
            val activePickups = uiState.pickups.filter { it.status == "on_the_way" || it.status == "pending" }
            val donePickups = uiState.pickups.filter { it.status == "done" || it.status == "cancelled" }

            if (activePickups.isNotEmpty()) {
                item {
                    Text(
                        "  Aktif",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(start = 16.dp, bottom = 4.dp)
                    )
                }
                items(activePickups, key = { it.id }) { pickup ->
                    CourierPickupCard(
                        pickup = pickup,
                        onUpdateStatus = { newStatus -> viewModel.updateStatus(pickup.id, newStatus) },
                        onNavigateRoute = onNavigateRoute,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
            }

            if (donePickups.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "  Selesai / Dibatalkan",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(start = 16.dp, bottom = 4.dp)
                    )
                }
                items(donePickups, key = { it.id }) { pickup ->
                    CourierPickupCard(
                        pickup = pickup,
                        onUpdateStatus = {},
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
        } // end PullToRefreshBox
    }
}

// ─────────────────────────────────────────────────────────────────
// Header
// ─────────────────────────────────────────────────────────────────
@Composable
private fun CourierHeader(
    profile: CourierProfileDto?,
    isAvailable: Boolean,
    onToggleAvailability: (Boolean) -> Unit,
    onLogout: () -> Unit
) {
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(GreenDeep, GreenMedium)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(gradientBrush)
            .padding(top = 48.dp, bottom = 20.dp, start = 16.dp, end = 16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Avatar + Name
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(GreenLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Halo, Kurir!",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Text(
                            text = profile?.name ?: "—",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                        if (profile != null) {
                            Text(
                                text = "${profile.vehicle_type ?: ""} • ${profile.vehicle_plate ?: ""}",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.75f)
                            )
                        }
                    }
                }

                // Logout button
                IconButton(onClick = onLogout) {
                    Icon(
                        Icons.Default.ExitToApp,
                        contentDescription = "Logout",
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Availability toggle
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White.copy(alpha = 0.15f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isAvailable) "Status: Online" else "Status: Offline",
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            fontSize = 14.sp
                        )
                        Text(
                            text = if (isAvailable) "Siap menerima pickup" else "Tidak menerima pickup",
                            color = Color.White.copy(alpha = 0.75f),
                            fontSize = 12.sp
                        )
                    }
                    Switch(
                        checked = isAvailable,
                        onCheckedChange = onToggleAvailability,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = GreenLight,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color.Gray
                        )
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────
// Stats Row
// ─────────────────────────────────────────────────────────────────
@Composable
private fun CourierStatsRow(
    profile: CourierProfileDto?,
    activeCount: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            label = "Total Antar",
            value = profile?.total_deliveries?.toString() ?: "—",
            icon = Icons.Default.CheckCircle,
            iconTint = StatusDone,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            label = "Rating",
            value = if (profile != null) "%.1f ★".format(profile.rating) else "—",
            icon = Icons.Default.Star,
            iconTint = OrangeAccent,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            label = "Aktif",
            value = activeCount.toString(),
            icon = Icons.Default.DirectionsBike,
            iconTint = StatusOnTheWay,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
            Text(label, fontSize = 11.sp, color = TextHint)
        }
    }
}

// ─────────────────────────────────────────────────────────────────
// Pickup Card
// ─────────────────────────────────────────────────────────────────
@Composable
fun CourierPickupCard(
    pickup: CourierPickupDto,
    onUpdateStatus: (String) -> Unit,
    modifier: Modifier = Modifier,
    onNavigateRoute: (lat: Double, lng: Double, address: String) -> Unit = { _, _, _ -> }
) {
    val statusColor = when (pickup.status) {
        "pending" -> StatusPending
        "on_the_way" -> StatusOnTheWay
        "done" -> StatusDone
        "cancelled" -> StatusCancelled
        else -> TextHint
    }
    val statusLabel = when (pickup.status) {
        "pending" -> "Menunggu"
        "on_the_way" -> "Dalam Perjalanan"
        "done" -> "Selesai"
        "cancelled" -> "Dibatalkan"
        else -> pickup.status
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // ── Top row: status badge + pickup ID ──────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "#${pickup.id}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = TextSecondary
                )
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = statusColor.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = statusLabel,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ── Customer ────────────────────────────────────────
            if (pickup.customer != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = GreenMedium,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = pickup.customer.name,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = TextPrimary
                    )
                    pickup.customer.phone?.let { phone ->
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = phone,
                            fontSize = 12.sp,
                            color = TextHint
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            // ── Address ─────────────────────────────────────────
            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = GreenMedium,
                    modifier = Modifier.size(16.dp).padding(top = 1.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = pickup.address,
                    fontSize = 13.sp,
                    color = TextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // ── Date / Time ──────────────────────────────────────
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.CalendarToday,
                    contentDescription = null,
                    tint = TextHint,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = "${pickup.pickup_date ?: "—"} ${pickup.pickup_time ?: ""}".trim(),
                    fontSize = 12.sp,
                    color = TextHint
                )
                if (pickup.estimated_weight_kg != null) {
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(
                        Icons.Default.Scale,
                        contentDescription = null,
                        tint = TextHint,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "≈ ${"%.1f".format(pickup.estimated_weight_kg)} kg",
                        fontSize = 12.sp,
                        color = TextHint
                    )
                }
            }

            // ── Trash Types ─────────────────────────────────────
            if (pickup.trash_types.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    pickup.trash_types.forEach { trash ->
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = SurfaceVariant
                        ) {
                            Text(
                                text = trash.label,
                                fontSize = 10.sp,
                                color = GreenDeep,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }

            // ── Action Buttons ───────────────────────────────────
            if (pickup.status == "pending" || pickup.status == "on_the_way") {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = SurfaceVariant)
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (pickup.status == "pending") {
                        Button(
                            onClick = { onUpdateStatus("on_the_way") },
                            colors = ButtonDefaults.buttonColors(containerColor = StatusOnTheWay),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            Icon(
                                Icons.Default.DirectionsBike,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Mulai Antar", fontSize = 13.sp)
                        }
                        OutlinedButton(
                            onClick = { onUpdateStatus("cancelled") },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusCancelled),
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            Text("Batalkan", fontSize = 13.sp)
                        }
                    }
                }

                // on_the_way: Lihat Rute + Selesaikan Pickup
                if (pickup.status == "on_the_way") {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (pickup.latitude != null && pickup.longitude != null) {
                            OutlinedButton(
                                onClick = {
                                    onNavigateRoute(
                                        pickup.latitude,
                                        pickup.longitude,
                                        pickup.address
                                    )
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = GreenDeep),
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(vertical = 8.dp)
                            ) {
                                Icon(
                                    Icons.Default.Map,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Lihat Rute", fontSize = 13.sp)
                            }
                        }
                        Button(
                            onClick = { onUpdateStatus("done") },
                            colors = ButtonDefaults.buttonColors(containerColor = StatusDone),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Selesaikan Pickup", fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────
// Available Pickup Card — for unassigned 'searching' pickups
// ─────────────────────────────────────────────────────────────────
@Composable
fun AvailablePickupCard(
    pickup: CourierPickupDto,
    onAccept: () -> Unit,
    onIgnore: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, OrangeAccent.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // ── Header row ──────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        tint = OrangeAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "Mencari Kurir",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = OrangeAccent
                    )
                }
                Text(
                    text = "#${pickup.id}",
                    fontSize = 12.sp,
                    color = TextHint
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ── Customer ────────────────────────────────────────
            if (pickup.customer != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = GreenMedium,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = pickup.customer.name,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            // ── Address ─────────────────────────────────────────
            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = GreenMedium,
                    modifier = Modifier
                        .size(16.dp)
                        .padding(top = 1.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = pickup.address,
                    fontSize = 13.sp,
                    color = TextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // ── Date / Weight ────────────────────────────────────
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.CalendarToday,
                    contentDescription = null,
                    tint = TextHint,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${pickup.pickup_date ?: "—"} ${pickup.pickup_time ?: ""}".trim(),
                    fontSize = 12.sp,
                    color = TextHint
                )
                if (pickup.estimated_weight_kg != null) {
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(
                        Icons.Default.Scale,
                        contentDescription = null,
                        tint = TextHint,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "≈ ${"%.1f".format(pickup.estimated_weight_kg)} kg",
                        fontSize = 12.sp,
                        color = TextHint
                    )
                }
            }

            // ── Trash Types ─────────────────────────────────────
            if (pickup.trash_types.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    pickup.trash_types.forEach { trash ->
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = SurfaceVariant
                        ) {
                            Text(
                                text = "${trash.emoji} ${trash.label}",
                                fontSize = 10.sp,
                                color = GreenDeep,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = SurfaceVariant)
            Spacer(modifier = Modifier.height(10.dp))

            // ── Action Buttons ───────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onIgnore,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextHint),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(vertical = 10.dp)
                ) {
                    Text("Lewati", fontSize = 13.sp)
                }
                Button(
                    onClick = onAccept,
                    colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(2f),
                    contentPadding = PaddingValues(vertical = 10.dp)
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Terima Pickup", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
