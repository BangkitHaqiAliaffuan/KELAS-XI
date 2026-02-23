package com.kelasxi.myapplication.ui.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kelasxi.myapplication.model.*
import com.kelasxi.myapplication.ui.theme.*
import com.kelasxi.myapplication.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel()) {
    val recentPickups by viewModel.recentPickups.collectAsStateWithLifecycle()
    val statsCards by viewModel.statsCards.collectAsStateWithLifecycle()
    val selectedTrashTypes by viewModel.selectedTrashTypes.collectAsStateWithLifecycle()
    val address by viewModel.address.collectAsStateWithLifecycle()
    val notes by viewModel.notes.collectAsStateWithLifecycle()
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val selectedTime by viewModel.selectedTime.collectAsStateWithLifecycle()
    val showSuccessDialog by viewModel.showSuccessDialog.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize().background(BackgroundGreen)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Gradient Header
            item { HomeHeader() }

            // Stats Banner
            item {
                StatsBanner(stats = statsCards)
            }

            // Pickup Request Card
            item {
                PickupRequestCard(
                    address = address,
                    onAddressChange = viewModel::updateAddress,
                    selectedTrashTypes = selectedTrashTypes,
                    onTrashTypeToggle = viewModel::toggleTrashType,
                    selectedDate = selectedDate,
                    selectedTime = selectedTime,
                    onDateSelected = viewModel::updateDate,
                    onTimeSelected = viewModel::updateTime,
                    notes = notes,
                    onNotesChange = viewModel::updateNotes,
                    onSubmit = viewModel::submitPickup
                )
            }

            // Recent Pickups Header
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Riwayat Pickup",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    TextButton(onClick = {}) {
                        Text("Lihat Semua", color = GreenDeep)
                    }
                }
            }

            // Recent Pickup Cards
            items(recentPickups) { pickup ->
                RecentPickupCard(pickup = pickup)
            }
        }

        // Success Dialog
        if (showSuccessDialog) {
            PickupSuccessDialog(onDismiss = viewModel::dismissSuccessDialog)
        }
    }
}

@Composable
fun HomeHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(GreenDeep, GreenLight)
                )
            )
    ) {
        // Background decorative circles
        Box(
            modifier = Modifier
                .size(200.dp)
                .offset(x = 220.dp, y = (-40).dp)
                .background(
                    color = GreenMedium.copy(alpha = 0.3f),
                    shape = CircleShape
                )
        )
        Box(
            modifier = Modifier
                .size(120.dp)
                .offset(x = (-20).dp, y = 80.dp)
                .background(
                    color = GreenLight.copy(alpha = 0.2f),
                    shape = CircleShape
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // App logo/icon area
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = Color.White.copy(alpha = 0.2f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("♻️", fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "TrashCare",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButton(
                    onClick = {},
                    modifier = Modifier
                        .background(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Notifications",
                        tint = Color.White
                    )
                }
            }

            Column {
                Text(
                    text = "Hi, Budi 👋",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Ready to keep Earth clean?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
        }
    }
}

@Composable
fun StatsBanner(stats: List<StatCard>) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(stats) { stat ->
            StatCardItem(stat = stat)
        }
    }
}

@Composable
fun StatCardItem(stat: StatCard) {
    Card(
        modifier = Modifier
            .width(130.dp)
            .height(90.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = stat.emoji, fontSize = 22.sp)
            Column {
                Text(
                    text = stat.value,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = GreenDeep
                )
                Text(
                    text = stat.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PickupRequestCard(
    address: String,
    onAddressChange: (String) -> Unit,
    selectedTrashTypes: Set<TrashType>,
    onTrashTypeToggle: (TrashType) -> Unit,
    selectedDate: String,
    selectedTime: String,
    onDateSelected: (String) -> Unit,
    onTimeSelected: (String) -> Unit,
    notes: String,
    onNotesChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Card Title
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("📦", fontSize = 24.sp)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Request Pickup",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Jadwalkan penjemputan sampahmu",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = DividerColor)
            Spacer(modifier = Modifier.height(16.dp))

            // Map Placeholder
            MapPlaceholder()

            Spacer(modifier = Modifier.height(16.dp))

            // Address Field
            OutlinedTextField(
                value = address,
                onValueChange = onAddressChange,
                label = { Text("Alamat Penjemputan") },
                leadingIcon = {
                    Icon(
                        Icons.Filled.LocationOn,
                        contentDescription = null,
                        tint = GreenDeep
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GreenDeep,
                    focusedLabelColor = GreenDeep,
                    cursorColor = GreenDeep
                ),
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Trash Type Selector
            Text(
                text = "Jenis Sampah",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TrashType.entries.forEach { type ->
                    val isSelected = selectedTrashTypes.contains(type)
                    FilterChip(
                        selected = isSelected,
                        onClick = { onTrashTypeToggle(type) },
                        label = { Text("${type.emoji} ${type.label}") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GreenDeep,
                            selectedLabelColor = Color.White
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Date & Time Row
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, GreenDeep)
                ) {
                    Icon(
                        Icons.Outlined.DateRange,
                        contentDescription = null,
                        tint = GreenDeep,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = selectedDate,
                        color = GreenDeep,
                        style = MaterialTheme.typography.labelMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                OutlinedButton(
                    onClick = { showTimePicker = true },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, GreenDeep)
                ) {
                    Icon(
                        Icons.Outlined.AccessTime,
                        contentDescription = null,
                        tint = GreenDeep,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = selectedTime,
                        color = GreenDeep,
                        style = MaterialTheme.typography.labelMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Notes Field
            OutlinedTextField(
                value = notes,
                onValueChange = onNotesChange,
                label = { Text("Catatan (opsional)") },
                leadingIcon = {
                    Icon(Icons.Outlined.Edit, contentDescription = null, tint = GreenDeep)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GreenDeep,
                    focusedLabelColor = GreenDeep,
                    cursorColor = GreenDeep
                ),
                maxLines = 3,
                placeholder = { Text("Tambahkan instruksi untuk kurir...") }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Submit Button
            Button(
                onClick = onSubmit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(GreenDeep, GreenMedium, GreenLight)
                            ),
                            shape = RoundedCornerShape(24.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("🚛", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Request Pickup",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }

    // Date Picker Dialog
    if (showDatePicker) {
        SimpleDatePickerDialog(
            onDismiss = { showDatePicker = false },
            onConfirm = { date ->
                onDateSelected(date)
                showDatePicker = false
            }
        )
    }

    // Time Picker Dialog
    if (showTimePicker) {
        SimpleTimePickerDialog(
            onDismiss = { showTimePicker = false },
            onConfirm = { time ->
                onTimeSelected(time)
                showTimePicker = false
            }
        )
    }
}

@Composable
fun MapPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(SurfaceVariant, GreenPale)
                )
            )
            .border(
                width = 1.dp,
                color = GreenPale,
                shape = RoundedCornerShape(16.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Map grid lines decoration
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {
                // Simulated map roads
                repeat(5) { i ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .offset(y = (30 + i * 30).dp)
                            .background(GreenPale.copy(alpha = 0.5f))
                    )
                }
                repeat(8) { i ->
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .fillMaxHeight()
                            .offset(x = (30 + i * 40).dp)
                            .background(GreenPale.copy(alpha = 0.5f))
                    )
                }
            }

            // Location pin
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(GreenDeep, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.LocationOn,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    color = GreenDeep,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "📍 Lokasi Kamu",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleDatePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val millis = datePickerState.selectedDateMillis
                    if (millis != null) {
                        val calendar = java.util.Calendar.getInstance().apply { timeInMillis = millis }
                        val formatted = String.format(
                            "%02d %s %d",
                            calendar.get(java.util.Calendar.DAY_OF_MONTH),
                            listOf("Jan","Feb","Mar","Apr","Mei","Jun","Jul","Agu","Sep","Okt","Nov","Des")[calendar.get(java.util.Calendar.MONTH)],
                            calendar.get(java.util.Calendar.YEAR)
                        )
                        onConfirm(formatted)
                    }
                }
            ) { Text("OK", color = GreenDeep) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    ) {
        DatePicker(
            state = datePickerState,
            colors = DatePickerDefaults.colors(
                selectedDayContainerColor = GreenDeep,
                todayDateBorderColor = GreenDeep
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleTimePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    val timePickerState = rememberTimePickerState(initialHour = 9, initialMinute = 0)

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onConfirm(String.format("%02d:%02d", timePickerState.hour, timePickerState.minute))
            }) { Text("OK", color = GreenDeep) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        },
        text = {
            TimePicker(
                state = timePickerState,
                colors = TimePickerDefaults.colors(
                    clockDialColor = SurfaceVariant,
                    selectorColor = GreenDeep,
                    containerColor = SurfaceWhite,
                    clockDialSelectedContentColor = Color.White,
                    timeSelectorSelectedContainerColor = GreenDeep
                )
            )
        }
    )
}

@Composable
fun RecentPickupCard(pickup: PickupRequest) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                // Trash type emoji
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(SurfaceVariant, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = pickup.trashTypes.firstOrNull()?.emoji ?: "🗑️",
                        fontSize = 22.sp
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = pickup.trashTypes.joinToString(", ") { it.label },
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${pickup.date} · ${pickup.time}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Text(
                        text = pickup.address,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextHint,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            StatusBadge(status = pickup.status)
        }
    }
}

@Composable
fun StatusBadge(status: PickupStatus) {
    val (bgColor, textColor) = when (status) {
        PickupStatus.PENDING -> Pair(StatusPending.copy(alpha = 0.15f), StatusPending)
        PickupStatus.ON_THE_WAY -> Pair(StatusOnTheWay.copy(alpha = 0.15f), StatusOnTheWay)
        PickupStatus.DONE -> Pair(StatusDone.copy(alpha = 0.15f), StatusDone)
        PickupStatus.CANCELLED -> Pair(StatusCancelled.copy(alpha = 0.15f), StatusCancelled)
    }
    Surface(
        color = bgColor,
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            text = "${status.emoji} ${status.label}",
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun PickupSuccessDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Text("🎉", fontSize = 48.sp) },
        title = {
            Text(
                text = "Pickup Dijadwalkan!",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = GreenDeep
            )
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Tim kami akan segera menjemput sampahmu. Terima kasih sudah peduli lingkungan! 🌱",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .background(SurfaceVariant, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    listOf("🚛", "♻️", "🌍").forEach { emoji ->
                        Text(emoji, fontSize = 24.sp)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = GreenDeep),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Oke, Mantap! 🌟", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        shape = RoundedCornerShape(24.dp),
        containerColor = SurfaceWhite
    )
}

@Preview(showBackground = true, name = "Home Screen Light")
@Composable
fun HomeScreenPreview() {
    TrashCareTheme {
        HomeScreen()
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES, name = "Home Screen Dark")
@Composable
fun HomeScreenDarkPreview() {
    TrashCareTheme(darkTheme = true) {
        HomeScreen()
    }
}
