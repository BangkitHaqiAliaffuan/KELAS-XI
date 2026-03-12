package com.kelasxi.myapplication.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kelasxi.myapplication.model.Courier
import com.kelasxi.myapplication.model.PickupRequest
import com.kelasxi.myapplication.model.PickupStatus
import com.kelasxi.myapplication.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickupDetailScreen(
    pickup: PickupRequest,
    onBack: () -> Unit,
    onRatePickup: (courierRating: Int, courierReview: String?) -> Unit = { _, _ -> },
    isRatingPickup: Boolean = false,
    rateSuccessMessage: String? = null,
    onRateSuccessDismissed: () -> Unit = {}
) {
    var showRatingDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(rateSuccessMessage) {
        if (rateSuccessMessage != null) {
            snackbarHostState.showSnackbar(rateSuccessMessage)
            onRateSuccessDismissed()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detail Pickup #${pickup.id}",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GreenDeep,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = BackgroundGreen
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 24.dp)
        ) {
            // ── Status Hero Banner ────────────────────────────────
            StatusHeroBanner(status = pickup.status)

            Spacer(modifier = Modifier.height(16.dp))

            // ── Trash Types Card ──────────────────────────────────
            DetailCard(title = "Jenis Sampah", emoji = "🗑️") {
                if (pickup.trashTypes.isEmpty()) {
                    Text(
                        text = "Tidak ada data",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextHint
                    )
                } else {
                    pickup.trashTypes.forEach { type ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(SurfaceVariant, RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = type.emoji, fontSize = 18.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = type.label,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = TextPrimary
                            )
                        }
                    }
                }
            }

            // ── Schedule Card ─────────────────────────────────────
            DetailCard(title = "Jadwal Penjemputan", emoji = "📅") {
                DetailRow(
                    icon = Icons.Outlined.DateRange,
                    label = "Tanggal",
                    value = pickup.date
                )
                Spacer(modifier = Modifier.height(8.dp))
                DetailRow(
                    icon = Icons.Outlined.AccessTime,
                    label = "Waktu",
                    value = pickup.time
                )
                if (!pickup.createdAt.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    DetailRow(
                        icon = Icons.Outlined.Schedule,
                        label = "Dibuat",
                        value = formatIsoDate(pickup.createdAt)
                    )
                }
            }

            // ── Address Card ──────────────────────────────────────
            DetailCard(title = "Alamat Penjemputan", emoji = "📍") {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = GreenDeep,
                        modifier = Modifier
                            .size(20.dp)
                            .padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = pickup.address,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary,
                        lineHeight = 22.sp
                    )
                }
            }

            // ── Weight & Points Card ──────────────────────────────
            DetailCard(title = "Estimasi & Poin", emoji = "⚖️") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Weight
                    WeightPointChip(
                        emoji = "⚖️",
                        label = "Berat Estimasi",
                        value = if (pickup.estimatedWeightKg != null)
                            formatWeightDisplay(pickup.estimatedWeightKg)
                        else "—",
                        modifier = Modifier.weight(1f)
                    )
                    // Points
                    WeightPointChip(
                        emoji = "⭐",
                        label = "Poin Didapat",
                        value = if (pickup.pointsAwarded > 0) "+${pickup.pointsAwarded}" else "—",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // ── Notes Card (only if notes exist) ─────────────────
            if (pickup.notes.isNotBlank()) {
                DetailCard(title = "Catatan", emoji = "📝") {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = Icons.Outlined.Notes,
                            contentDescription = null,
                            tint = GreenDeep,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = pickup.notes,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary,
                            lineHeight = 22.sp
                        )
                    }
                }
            }

            // ── Courier Card ──────────────────────────────────────
            CourierCard(courier = pickup.courier)

            // ── Completion / Cancellation Info ────────────────────
            when (pickup.status) {
                PickupStatus.DONE -> {
                    if (!pickup.completedAt.isNullOrBlank()) {
                        DetailCard(title = "Informasi Selesai", emoji = "✅") {
                            DetailRow(
                                icon = Icons.Outlined.CheckCircle,
                                label = "Diselesaikan",
                                value = formatIsoDate(pickup.completedAt)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    if (pickup.ratedAt == null) {
                        // Show rating button
                        Button(
                            onClick = { showRatingDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GreenDeep)
                        ) {
                            Text("⭐ Beri Rating Kurir", fontWeight = FontWeight.SemiBold)
                        }
                    } else {
                        // Show rating summary
                        DetailCard(title = "Rating Kurir", emoji = "⭐") {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                repeat(5) { index ->
                                    Text(
                                        text = if (index < (pickup.courierRating ?: 0)) "⭐" else "☆",
                                        fontSize = 22.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${pickup.courierRating}/5",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = GreenDeep
                                )
                            }
                            if (!pickup.courierReview.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "\"${pickup.courierReview}\"",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }
                }
                PickupStatus.CANCELLED -> {
                    DetailCard(title = "Informasi Pembatalan", emoji = "❌") {
                        if (!pickup.cancelledAt.isNullOrBlank()) {
                            DetailRow(
                                icon = Icons.Outlined.Cancel,
                                label = "Dibatalkan",
                                value = formatIsoDate(pickup.cancelledAt)
                            )
                        }
                        if (!pickup.cancellationReason.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.Top) {
                                Icon(
                                    imageVector = Icons.Outlined.Info,
                                    contentDescription = null,
                                    tint = StatusCancelled,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = pickup.cancellationReason,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = StatusCancelled,
                                    lineHeight = 22.sp
                                )
                            }
                        }
                    }
                }
                else -> Unit
            }
        }
    }

    // Rating Dialog
    if (showRatingDialog) {
        RatingDialog(
            onDismiss = { showRatingDialog = false },
            onConfirm = { rating, review ->
                showRatingDialog = false
                onRatePickup(rating, review)
            },
            isLoading = isRatingPickup
        )
    }
}

// ─── Status Hero Banner ────────────────────────────────────────────────────────
@Composable
private fun StatusHeroBanner(status: PickupStatus) {
    val (gradientColors, textColor) = when (status) {
        PickupStatus.SEARCHING   -> Pair(
            listOf(OrangeAccent.copy(alpha = 0.85f), OrangeAccent.copy(alpha = 0.4f)),
            Color.White
        )
        PickupStatus.PENDING     -> Pair(
            listOf(StatusPending.copy(alpha = 0.85f), StatusPending.copy(alpha = 0.4f)),
            Color(0xFF5D4037)
        )
        PickupStatus.ON_THE_WAY  -> Pair(
            listOf(StatusOnTheWay.copy(alpha = 0.85f), StatusOnTheWay.copy(alpha = 0.4f)),
            Color.White
        )
        PickupStatus.DONE        -> Pair(
            listOf(GreenDeep.copy(alpha = 0.9f), GreenLight.copy(alpha = 0.5f)),
            Color.White
        )
        PickupStatus.CANCELLED   -> Pair(
            listOf(StatusCancelled.copy(alpha = 0.8f), StatusCancelled.copy(alpha = 0.3f)),
            Color.White
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(gradientColors))
            .padding(vertical = 28.dp, horizontal = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = status.emoji,
                fontSize = 48.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = status.label,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = statusDescription(status),
                style = MaterialTheme.typography.bodySmall,
                color = textColor.copy(alpha = 0.85f),
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun statusDescription(status: PickupStatus): String = when (status) {
    PickupStatus.SEARCHING   -> "Sedang mencari kurir yang tersedia..."
    PickupStatus.PENDING     -> "Kurir telah ditemukan, menunggu konfirmasi"
    PickupStatus.ON_THE_WAY  -> "Kurir sedang dalam perjalanan menuju lokasimu"
    PickupStatus.DONE        -> "Sampah berhasil dijemput. Terima kasih! 🌱"
    PickupStatus.CANCELLED   -> "Permintaan pickup ini telah dibatalkan"
}

// ─── Courier Card ─────────────────────────────────────────────────────────────
@Composable
private fun CourierCard(courier: Courier?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🚛", fontSize = 20.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Informasi Kurir",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = DividerColor)
            Spacer(modifier = Modifier.height(12.dp))

            if (courier == null) {
                // No courier yet
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(SurfaceVariant, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🔍", fontSize = 26.sp)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Belum ada kurir",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Kurir akan ditugaskan segera",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
            } else {
                // Courier info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatar placeholder
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(
                                Brush.radialGradient(listOf(GreenLight, GreenDeep)),
                                CircleShape
                            )
                            .clip(CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = courier.name.firstOrNull()?.uppercase() ?: "K",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = courier.name,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        if (!courier.vehicleType.isNullOrBlank()) {
                            Text(
                                text = courier.vehicleType,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                        // Rating stars
                        if (courier.rating != null && courier.rating > 0) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("⭐", fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = String.format("%.1f", courier.rating),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = OrangeAccent,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                    // Status chip
                    if (!courier.status.isNullOrBlank()) {
                        CourierStatusChip(status = courier.status)
                    }
                }

                // Vehicle plate & phone details
                if (!courier.vehiclePlate.isNullOrBlank() || !courier.phone.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = DividerColor)
                    Spacer(modifier = Modifier.height(12.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        if (!courier.vehiclePlate.isNullOrBlank()) {
                            CourierInfoRow(
                                emoji = "🚗",
                                label = "Plat Kendaraan",
                                value = courier.vehiclePlate
                            )
                        }
                        if (!courier.phone.isNullOrBlank()) {
                            CourierInfoRow(
                                emoji = "📞",
                                label = "Nomor HP",
                                value = courier.phone
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CourierStatusChip(status: String) {
    val (bg, text, label) = when (status.lowercase()) {
        "on_duty"  -> Triple(StatusOnTheWay.copy(alpha = 0.15f), StatusOnTheWay, "On Duty")
        "inactive" -> Triple(TextHint.copy(alpha = 0.15f), TextHint, "Inactive")
        else       -> Triple(StatusDone.copy(alpha = 0.15f), StatusDone, "Active")
    }
    Surface(color = bg, shape = RoundedCornerShape(20.dp)) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = text,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun CourierInfoRow(emoji: String, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(emoji, fontSize = 16.sp)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
    }
}

// ─── Reusable Detail Card ──────────────────────────────────────────────────────
@Composable
private fun DetailCard(
    title: String,
    emoji: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(emoji, fontSize = 20.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = DividerColor)
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

// ─── Detail Row (icon + label + value) ────────────────────────────────────────
@Composable
private fun DetailRow(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = GreenDeep,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
    }
}

// ─── Weight / Points mini chip ─────────────────────────────────────────────────
@Composable
private fun WeightPointChip(
    emoji: String,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(SurfaceVariant, RoundedCornerShape(12.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
            textAlign = TextAlign.Center
        )
    }
}

// ─── Rating Dialog ───────────────────────────────────────────────────────────

@Composable
fun RatingDialog(
    onDismiss: () -> Unit,
    onConfirm: (rating: Int, review: String?) -> Unit,
    isLoading: Boolean = false
) {
    var rating by remember { mutableIntStateOf(0) }
    var review by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        title = {
            Text(
                text = "Beri Rating Kurir",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Bagaimana pelayanan kurir?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                StarRatingRow(rating = rating, onRatingChange = { rating = it })
                OutlinedTextField(
                    value = review,
                    onValueChange = { review = it },
                    label = { Text("Ulasan (opsional)") },
                    placeholder = { Text("Tulis ulasan singkat...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 3,
                    enabled = !isLoading
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (rating > 0) {
                        onConfirm(rating, review.trim().ifBlank { null })
                    }
                },
                enabled = rating > 0 && !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = GreenDeep)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Kirim Rating")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isLoading) {
                Text("Batal")
            }
        },
        containerColor = CardGreen
    )
}

@Composable
fun StarRatingRow(
    rating: Int,
    onRatingChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        (1..5).forEach { star ->
            Text(
                text = if (star <= rating) "⭐" else "☆",
                fontSize = 32.sp,
                modifier = Modifier
                    .size(44.dp)
                    .wrapContentSize(Alignment.Center)
                    .let { mod ->
                        mod.then(
                            Modifier.clickable { onRatingChange(star) }
                        )
                    }
            )
        }
    }
}

// ─── Helper functions ─────────────────────────────────────────────────────────
private fun formatWeightDisplay(kg: Double): String = when {
    kg < 1.0  -> "${(kg * 1000).toInt()} g"
    kg == kg.toLong().toDouble() -> "${kg.toInt()} kg"
    else      -> String.format("%.1f kg", kg)
}

private fun formatIsoDate(iso: String): String {
    return try {
        val inputFmt = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.getDefault())
        inputFmt.timeZone = java.util.TimeZone.getTimeZone("UTC")
        val date = inputFmt.parse(iso)
        val outputFmt = java.text.SimpleDateFormat("dd MMM yyyy, HH:mm", java.util.Locale("id", "ID"))
        outputFmt.format(date ?: return iso)
    } catch (_: Exception) {
        iso
    }
}
