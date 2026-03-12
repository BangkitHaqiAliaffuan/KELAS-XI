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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kelasxi.myapplication.model.*
import com.kelasxi.myapplication.ui.common.AddressPickerField
import com.kelasxi.myapplication.ui.theme.*
import com.kelasxi.myapplication.viewmodel.AddressViewModel
import com.kelasxi.myapplication.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    addressViewModel: AddressViewModel = viewModel(),
    onPickupClick: (PickupRequest) -> Unit = {},
    onPickLocationClick: () -> Unit = {}
) {
    val recentPickups       by viewModel.recentPickups.collectAsStateWithLifecycle()
    val statsCards          by viewModel.statsCards.collectAsStateWithLifecycle()
    val selectedTrashTypes  by viewModel.selectedTrashTypes.collectAsStateWithLifecycle()
    val address             by viewModel.address.collectAsStateWithLifecycle()
    val notes               by viewModel.notes.collectAsStateWithLifecycle()
    val showSuccessDialog   by viewModel.showSuccessDialog.collectAsStateWithLifecycle()
    val isLoadingPickups    by viewModel.isLoadingPickups.collectAsStateWithLifecycle()
    val pickupsError        by viewModel.pickupsError.collectAsStateWithLifecycle()
    val isSubmitting        by viewModel.isSubmitting.collectAsStateWithLifecycle()
    val submitError         by viewModel.submitError.collectAsStateWithLifecycle()
    val estimatedWeightKg   by viewModel.estimatedWeightKg.collectAsStateWithLifecycle()
    val userName            by viewModel.userName.collectAsStateWithLifecycle()
    val latitude            by viewModel.latitude.collectAsStateWithLifecycle()
    val longitude           by viewModel.longitude.collectAsStateWithLifecycle()

    // Show submit-error snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(submitError) {
        if (submitError != null) {
            snackbarHostState.showSnackbar(submitError!!)
            viewModel.dismissError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = BackgroundGreen
    ) { innerPadding ->
    Box(modifier = Modifier.fillMaxSize().padding(innerPadding).background(BackgroundGreen)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Gradient Header
            item { HomeHeader(userName = userName) }

            // Stats Banner
            item {
                StatsBanner(stats = statsCards)
            }

            // Pickup Request Card
            item {
                PickupRequestCard(
                    address = address,
                    onAddressChange = viewModel::updateAddress,
                    addressViewModel = addressViewModel,
                    selectedTrashTypes = selectedTrashTypes,
                    onTrashTypeToggle = viewModel::toggleTrashType,
                    notes = notes,
                    onNotesChange = viewModel::updateNotes,
                    estimatedWeightKg = estimatedWeightKg,
                    onWeightChange = viewModel::updateEstimatedWeight,
                    onSubmit = viewModel::submitPickup,
                    isSubmitting = isSubmitting,
                    latitude = latitude,
                    longitude = longitude,
                    onPickLocationClick = onPickLocationClick
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
                    IconButton(onClick = viewModel::loadPickups) {
                        Icon(
                            imageVector = Icons.Outlined.Refresh,
                            contentDescription = "Refresh",
                            tint = GreenDeep
                        )
                    }
                }
            }

            // Loading / error / list
            when {
                isLoadingPickups -> item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator(color = GreenDeep) }
                }
                pickupsError != null -> item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "⚠️ ${pickupsError}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = StatusCancelled
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = viewModel::loadPickups,
                            border = BorderStroke(1.dp, GreenDeep)
                        ) { Text("Coba Lagi", color = GreenDeep) }
                    }
                }
                recentPickups.isEmpty() -> item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🗑️", fontSize = 40.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Belum ada riwayat pickup.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                        }
                    }
                }
                else -> items(recentPickups) { pickup ->
                    RecentPickupCard(
                        pickup = pickup,
                        onClick = { onPickupClick(pickup) }
                    )
                }
            }
        }

        // Success Dialog
        if (showSuccessDialog) {
            PickupSuccessDialog(onDismiss = viewModel::dismissSuccessDialog)
        }
    }
    } // end Scaffold
}

@Composable
fun HomeHeader(userName: String = "") {
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
                    text = if (userName.isNotBlank()) "Hi, $userName 👋" else "Hi, Selamat Datang 👋",
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
    addressViewModel: AddressViewModel = viewModel(),
    selectedTrashTypes: Set<TrashType>,
    onTrashTypeToggle: (TrashType) -> Unit,
    notes: String,
    onNotesChange: (String) -> Unit,
    estimatedWeightKg: Double? = null,
    onWeightChange: (Double?) -> Unit = {},
    onSubmit: () -> Unit,
    isSubmitting: Boolean = false,
    latitude: Double? = null,
    longitude: Double? = null,
    onPickLocationClick: () -> Unit = {}
) {
    // Weight picker local state
    val weightPresets = listOf(1.0, 2.0, 5.0, 10.0, 20.0)
    var showCustomWeight by remember { mutableStateOf(false) }
    var customWeightText by remember { mutableStateOf("") }
    LaunchedEffect(estimatedWeightKg) {
        // Sync showCustomWeight flag when state is reset externally
        if (estimatedWeightKg == null) {
            showCustomWeight = false
            customWeightText = ""
        }
    }

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

            // Map Preview / Picker Button
            if (latitude != null && longitude != null) {
                // Show mini osmdroid preview
                MapMiniPreview(
                    lat = latitude,
                    lng = longitude,
                    onClick = onPickLocationClick
                )
            } else {
                // No location selected yet — show placeholder with "Pilih di Peta" button
                MapPlaceholderWithButton(onClick = onPickLocationClick)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Address Field
            AddressPickerField(
                value = address,
                onValueChange = onAddressChange,
                addressViewModel = addressViewModel,
                label = "Alamat Penjemputan"
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

            Spacer(modifier = Modifier.height(16.dp))

            // ── Estimated Weight Section ──────────────────────────────
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Estimasi Berat Sampah",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "(opsional)",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextHint
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                weightPresets.forEach { kg ->
                    val isSelected = !showCustomWeight && estimatedWeightKg == kg
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            showCustomWeight = false
                            customWeightText = ""
                            onWeightChange(if (isSelected) null else kg)
                        },
                        label = { Text("${kg.toInt()} kg") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GreenDeep,
                            selectedLabelColor = Color.White
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                }
                // "Lainnya" chip
                FilterChip(
                    selected = showCustomWeight,
                    onClick = {
                        showCustomWeight = !showCustomWeight
                        if (!showCustomWeight) {
                            customWeightText = ""
                            onWeightChange(null)
                        }
                    },
                    label = { Text("Lainnya") },
                    leadingIcon = {
                        Icon(
                            Icons.Outlined.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GreenDeep,
                        selectedLabelColor = Color.White
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
            }
            if (showCustomWeight) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = customWeightText,
                    onValueChange = { raw ->
                        customWeightText = raw
                        onWeightChange(raw.toDoubleOrNull())
                    },
                    label = { Text("Berat (kg)") },
                    placeholder = { Text("Contoh: 7.5") },
                    suffix = { Text("kg") },
                    leadingIcon = {
                        Icon(Icons.Outlined.Scale, contentDescription = null, tint = GreenDeep)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GreenDeep,
                        focusedLabelColor = GreenDeep,
                        cursorColor = GreenDeep
                    ),
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal
                    ),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Submit Button
            Button(
                onClick = onSubmit,
                enabled = !isSubmitting,
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
                                colors = if (isSubmitting)
                                    listOf(GreenDeep.copy(alpha = 0.5f), GreenLight.copy(alpha = 0.5f))
                                else
                                    listOf(GreenDeep, GreenMedium, GreenLight)
                            ),
                            shape = RoundedCornerShape(24.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
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
    }

}

@Composable
fun MapPlaceholderWithButton(onClick: () -> Unit = {}) {
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
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
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
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = GreenDeep),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Icon(
                    Icons.Filled.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Pilih di Peta", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            }
        }
    }
}

@Composable
fun MapMiniPreview(lat: Double, lng: Double, onClick: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, GreenMedium.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
    ) {
        AndroidView(
            factory = { ctx ->
                org.osmdroid.config.Configuration.getInstance()
                    .load(ctx, ctx.getSharedPreferences("osmdroid", android.content.Context.MODE_PRIVATE))
                org.osmdroid.config.Configuration.getInstance().userAgentValue = ctx.packageName
                org.osmdroid.views.MapView(ctx).also { mv ->
                    mv.setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK)
                    mv.setMultiTouchControls(false)
                    mv.isClickable = false
                    mv.controller.setZoom(16.0)
                    mv.controller.setCenter(org.osmdroid.util.GeoPoint(lat, lng))
                    val marker = org.osmdroid.views.overlay.Marker(mv)
                    marker.position = org.osmdroid.util.GeoPoint(lat, lng)
                    marker.setAnchor(
                        org.osmdroid.views.overlay.Marker.ANCHOR_CENTER,
                        org.osmdroid.views.overlay.Marker.ANCHOR_BOTTOM
                    )
                    mv.overlays.add(marker)
                }
            },
            update = { mv ->
                mv.controller.setCenter(org.osmdroid.util.GeoPoint(lat, lng))
                mv.overlays.filterIsInstance<org.osmdroid.views.overlay.Marker>()
                    .firstOrNull()?.position = org.osmdroid.util.GeoPoint(lat, lng)
                mv.invalidate()
            },
            modifier = Modifier.fillMaxSize()
        )
        // "Ubah Lokasi" chip overlay
        Surface(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(10.dp),
            color = GreenDeep,
            shape = RoundedCornerShape(20.dp),
            onClick = onClick
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Filled.LocationOn,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Ubah Lokasi", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

// Keep original MapPlaceholder for Preview usage
@Composable
fun MapPlaceholder() {
    MapPlaceholderWithButton()
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
fun RecentPickupCard(pickup: PickupRequest, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        onClick = onClick
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
        PickupStatus.SEARCHING  -> Pair(OrangeAccent.copy(alpha = 0.15f), OrangeAccent)
        PickupStatus.PENDING    -> Pair(StatusPending.copy(alpha = 0.15f), StatusPending)
        PickupStatus.ON_THE_WAY -> Pair(StatusOnTheWay.copy(alpha = 0.15f), StatusOnTheWay)
        PickupStatus.DONE       -> Pair(StatusDone.copy(alpha = 0.15f), StatusDone)
        PickupStatus.CANCELLED  -> Pair(StatusCancelled.copy(alpha = 0.15f), StatusCancelled)
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
        // Preview-safe: render the static parts without a real ViewModel
        Box(modifier = Modifier.fillMaxSize().background(BackgroundGreen)) {
            LazyColumn(contentPadding = PaddingValues(bottom = 24.dp)) {
                item { HomeHeader() }
                item { StatsBanner(stats = listOf(
                    StatCard("12", "Pickups Done", "🚛"),
                    StatCard("5", "Items Sold", "🛒")
                )) }
                item {
                    PickupRequestCard(
                        address = "Jl. Sudirman No. 12",
                        onAddressChange = {},
                        selectedTrashTypes = setOf(TrashType.PLASTIC),
                        onTrashTypeToggle = {},
                        notes = "",
                        onNotesChange = {},
                        estimatedWeightKg = null,
                        onWeightChange = {},
                        onSubmit = {}
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES, name = "Home Screen Dark")
@Composable
fun HomeScreenDarkPreview() {
    TrashCareTheme(darkTheme = true) {
        Box(modifier = Modifier.fillMaxSize().background(BackgroundGreen)) {
            LazyColumn(contentPadding = PaddingValues(bottom = 24.dp)) {
                item { HomeHeader() }
            }
        }
    }
}
