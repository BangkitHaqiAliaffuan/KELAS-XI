package com.kelasxi.myapplication.ui.profile

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kelasxi.myapplication.data.network.Address
import com.kelasxi.myapplication.ui.theme.*
import com.kelasxi.myapplication.viewmodel.AddressViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressScreen(
    viewModel: AddressViewModel = viewModel(),
    onBack: () -> Unit = {}
) {
    val addresses       by viewModel.addresses.collectAsStateWithLifecycle()
    val isLoading       by viewModel.isLoading.collectAsStateWithLifecycle()
    val error           by viewModel.error.collectAsStateWithLifecycle()
    val isDeleting      by viewModel.isDeleting.collectAsStateWithLifecycle()
    val deleteSuccess   by viewModel.deleteSuccess.collectAsStateWithLifecycle()
    val addSuccess      by viewModel.addSuccess.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    // Dialog state
    var showAddDialog        by remember { mutableStateOf(false) }
    var pendingDeleteId      by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) { viewModel.loadAddresses() }

    LaunchedEffect(error) {
        if (error != null) {
            snackbarHostState.showSnackbar(error!!)
            viewModel.dismissError()
        }
    }
    LaunchedEffect(deleteSuccess) {
        if (deleteSuccess != null) {
            snackbarHostState.showSnackbar(deleteSuccess!!)
            viewModel.dismissDeleteSuccess()
        }
    }
    LaunchedEffect(addSuccess) {
        if (addSuccess != null) {
            snackbarHostState.showSnackbar(addSuccess!!)
            viewModel.dismissAddSuccess()
        }
    }

    // ── Confirm delete dialog ─────────────────────────────────────
    pendingDeleteId?.let { id ->
        val addr = addresses.find { it.id == id }
        AlertDialog(
            onDismissRequest = { pendingDeleteId = null },
            icon  = { Text("🗑️", fontSize = 36.sp) },
            title = {
                Text(
                    "Hapus Alamat?",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            },
            text  = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        "Kamu akan menghapus alamat:",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                    Text(
                        "🏷️ ${addr?.label ?: ""}",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = TextPrimary
                    )
                    Text(
                        addr?.fullAddress ?: "",
                        fontSize = 12.sp,
                        color = TextHint
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteAddress(id)
                        pendingDeleteId = null
                    },
                    enabled = !isDeleting,
                    colors = ButtonDefaults.buttonColors(containerColor = StatusCancelled),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    if (isDeleting) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(Modifier.width(6.dp))
                    }
                    Text("Hapus", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDeleteId = null }) {
                    Text("Batal", color = TextSecondary)
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = SurfaceWhite
        )
    }

    // ── Add address bottom-sheet style dialog ─────────────────────
    if (showAddDialog) {
        AddAddressDialog(
            viewModel = viewModel,
            onDismiss = { showAddDialog = false }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick        = { showAddDialog = true },
                icon           = { Icon(Icons.Outlined.Add, contentDescription = null, tint = Color.White) },
                text           = { Text("Tambah Alamat", color = Color.White, fontWeight = FontWeight.Bold) },
                containerColor = GreenDeep,
                contentColor   = Color.White,
                shape          = RoundedCornerShape(14.dp)
            )
        },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "📍 Alamat Saya",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                        Text(
                            "${addresses.size} alamat tersimpan",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.75f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.loadAddresses() }) {
                        Icon(
                            Icons.Outlined.Refresh,
                            contentDescription = "Refresh",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GreenDeep)
            )
        },
        containerColor = BackgroundGreen
    ) { innerPadding ->
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(color = GreenDeep)
                        Text("Memuat alamat...", color = TextSecondary)
                    }
                }
            }

            addresses.isEmpty() -> {
                AddressEmptyState(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    onAdd = { showAddDialog = true }
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Info banner jika ada lebih dari 1 alamat
                    if (addresses.size > 1) {
                        item {
                            AddressInfoBanner()
                        }
                    }

                    items(addresses, key = { it.id }) { address ->
                        AddressCard(
                            address   = address,
                            onDelete  = { pendingDeleteId = address.id },
                            onSetDefault = {
                                if (!address.isDefault) viewModel.setDefaultAddress(address.id)
                            }
                        )
                    }

                    item { Spacer(Modifier.height(80.dp)) } // FAB space
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────
// Address Card
// ─────────────────────────────────────────────────────────────────
@Composable
private fun AddressCard(
    address: Address,
    onDelete: () -> Unit,
    onSetDefault: () -> Unit
) {
    val isDefault = address.isDefault

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isDefault) 6.dp else 2.dp
        )
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {

            // ── Left accent strip ────────────────────────────────
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .fillMaxHeight()
                    .background(
                        color = if (isDefault) GreenDeep else GreenPale,
                        shape = RoundedCornerShape(topStart = 18.dp, bottomStart = 18.dp)
                    )
            )

            // ── Card body ────────────────────────────────────────
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 14.dp, end = 16.dp, top = 14.dp, bottom = 14.dp)
            ) {

                // ── Header: emoji icon + label/name + default badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Emoji avatar
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                color = if (isDefault) GreenDeep.copy(alpha = 0.12f)
                                else SurfaceVariant,
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = addressLabelEmoji(address.label),
                            fontSize = 22.sp
                        )
                    }

                    Spacer(Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = address.label,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            color = if (isDefault) GreenDeep else TextPrimary
                        )
                        Text(
                            text = address.recipientName,
                            fontSize = 12.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Default badge
                    if (isDefault) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = GreenDeep
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    Icons.Filled.CheckCircle,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    "Utama",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                // ── Info rows ────────────────────────────────────
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isDefault) GreenDeep.copy(alpha = 0.05f) else BackgroundGreen,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(7.dp)
                    ) {
                        AddressDetailRow(
                            icon = Icons.Filled.Phone,
                            iconTint = GreenMedium,
                            text = address.phone
                        )
                        HorizontalDivider(color = DividerColor.copy(alpha = 0.6f), thickness = 0.5.dp)
                        AddressDetailRow(
                            icon = Icons.Filled.Home,
                            iconTint = GreenMedium,
                            text = address.fullAddress
                        )
                        HorizontalDivider(color = DividerColor.copy(alpha = 0.6f), thickness = 0.5.dp)
                        AddressDetailRow(
                            icon = Icons.Filled.LocationOn,
                            iconTint = OrangeAccent,
                            text = "${address.city}, ${address.province} ${address.postalCode}"
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                // ── Action buttons ────────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Set default button
                    if (isDefault) {
                        // Already default — show a disabled filled pill
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = GreenDeep.copy(alpha = 0.08f),
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 9.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Filled.Star,
                                    contentDescription = null,
                                    tint = GreenDeep,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(Modifier.width(5.dp))
                                Text(
                                    "Alamat Utama",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = GreenDeep
                                )
                            }
                        }
                    } else {
                        OutlinedButton(
                            onClick = onSetDefault,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, GreenDeep),
                            contentPadding = PaddingValues(vertical = 9.dp)
                        ) {
                            Icon(
                                Icons.Outlined.Star,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = GreenDeep
                            )
                            Spacer(Modifier.width(5.dp))
                            Text(
                                "Jadikan Utama",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = GreenDeep
                            )
                        }
                    }

                    // Delete button
                    Button(
                        onClick = onDelete,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = StatusCancelled
                        ),
                        contentPadding = PaddingValues(vertical = 9.dp)
                    ) {
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = Color.White
                        )
                        Spacer(Modifier.width(5.dp))
                        Text(
                            "Hapus",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AddressDetailRow(
    icon: ImageVector,
    iconTint: Color,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = text,
            fontSize = 13.sp,
            color = TextPrimary,
            modifier = Modifier.weight(1f),
            lineHeight = 18.sp
        )
    }
}

// ─────────────────────────────────────────────────────────────────
// Add Address Dialog
// ─────────────────────────────────────────────────────────────────
@Composable
private fun AddAddressDialog(
    viewModel: AddressViewModel,
    onDismiss: () -> Unit
) {
    val isAdding  by viewModel.isAdding.collectAsStateWithLifecycle()
    val addError  by viewModel.addError.collectAsStateWithLifecycle()

    var label          by remember { mutableStateOf("") }
    var recipientName  by remember { mutableStateOf("") }
    var phone          by remember { mutableStateOf("") }
    var fullAddress    by remember { mutableStateOf("") }
    var city           by remember { mutableStateOf("") }
    var province       by remember { mutableStateOf("") }
    var postalCode     by remember { mutableStateOf("") }
    var isDefault      by remember { mutableStateOf(false) }

    // Validation errors
    var labelErr       by remember { mutableStateOf<String?>(null) }
    var nameErr        by remember { mutableStateOf<String?>(null) }
    var phoneErr       by remember { mutableStateOf<String?>(null) }
    var addressErr     by remember { mutableStateOf<String?>(null) }
    var cityErr        by remember { mutableStateOf<String?>(null) }
    var provinceErr    by remember { mutableStateOf<String?>(null) }
    var postalErr      by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(addError) {
        // errors will show inline via supportingText, but also trigger dismiss on success
    }

    fun validate(): Boolean {
        labelErr    = if (label.isBlank()) "Label wajib diisi (contoh: Rumah)" else null
        nameErr     = if (recipientName.isBlank()) "Nama penerima wajib diisi" else null
        phoneErr    = if (phone.isBlank()) "Nomor telepon wajib diisi" else null
        addressErr  = if (fullAddress.isBlank()) "Alamat lengkap wajib diisi" else null
        cityErr     = if (city.isBlank()) "Kota wajib diisi" else null
        provinceErr = if (province.isBlank()) "Provinsi wajib diisi" else null
        postalErr   = when {
            postalCode.isBlank()     -> "Kode pos wajib diisi"
            postalCode.length < 5    -> "Kode pos minimal 5 digit"
            else                     -> null
        }
        return listOf(labelErr, nameErr, phoneErr, addressErr, cityErr, provinceErr, postalErr)
            .all { it == null }
    }

    AlertDialog(
        onDismissRequest = { if (!isAdding) onDismiss() },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("📍", fontSize = 24.sp)
                Text(
                    "Tambah Alamat Baru",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    fontSize = 16.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Error banner from API
                if (addError != null) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = StatusCancelled.copy(alpha = 0.1f)
                    ) {
                        Text(
                            addError!!,
                            fontSize = 12.sp,
                            color = StatusCancelled,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                // Label
                AddressTextField(
                    value = label,
                    onValueChange = { label = it; labelErr = null },
                    label = "Label *",
                    placeholder = "Rumah / Kantor / Kos",
                    error = labelErr,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
                )

                // Recipient name
                AddressTextField(
                    value = recipientName,
                    onValueChange = { recipientName = it; nameErr = null },
                    label = "Nama Penerima *",
                    placeholder = "Nama lengkap penerima",
                    error = nameErr,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
                )

                // Phone
                AddressTextField(
                    value = phone,
                    onValueChange = { phone = it.filter { c -> c.isDigit() || c == '+' }; phoneErr = null },
                    label = "No. Telepon *",
                    placeholder = "08xxxxxxxxxx",
                    error = phoneErr,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )

                // Full address
                OutlinedTextField(
                    value = fullAddress,
                    onValueChange = { fullAddress = it; addressErr = null },
                    label = { Text("Alamat Lengkap *", fontSize = 12.sp) },
                    placeholder = { Text("Jalan, nomor rumah, RT/RW, kelurahan, kecamatan", color = TextHint, fontSize = 12.sp) },
                    isError = addressErr != null,
                    supportingText = addressErr?.let { { Text(it, color = MaterialTheme.colorScheme.error, fontSize = 11.sp) } },
                    minLines = 2,
                    maxLines = 4,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = addressFieldColors(),
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
                )

                // City & Province row
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AddressTextField(
                        value = city,
                        onValueChange = { city = it; cityErr = null },
                        label = "Kota *",
                        placeholder = "Kota",
                        error = cityErr,
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
                    )
                    AddressTextField(
                        value = postalCode,
                        onValueChange = { postalCode = it.filter { c -> c.isDigit() }.take(10); postalErr = null },
                        label = "Kode Pos *",
                        placeholder = "12345",
                        error = postalErr,
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }

                // Province
                AddressTextField(
                    value = province,
                    onValueChange = { province = it; provinceErr = null },
                    label = "Provinsi *",
                    placeholder = "Provinsi",
                    error = provinceErr,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
                )

                // Is default toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isDefault = !isDefault }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            "Jadikan Alamat Utama",
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp,
                            color = TextPrimary
                        )
                        Text(
                            "Alamat ini akan menjadi default saat checkout",
                            fontSize = 11.sp,
                            color = TextHint
                        )
                    }
                    Switch(
                        checked = isDefault,
                        onCheckedChange = { isDefault = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = GreenDeep
                        )
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (validate()) {
                        viewModel.addAddress(
                            label         = label.trim(),
                            recipientName = recipientName.trim(),
                            phone         = phone.trim(),
                            fullAddress   = fullAddress.trim(),
                            city          = city.trim(),
                            province      = province.trim(),
                            postalCode    = postalCode.trim(),
                            isDefault     = isDefault,
                            onSuccess     = { onDismiss() }
                        )
                    }
                },
                enabled = !isAdding,
                colors = ButtonDefaults.buttonColors(containerColor = GreenDeep),
                shape = RoundedCornerShape(10.dp)
            ) {
                if (isAdding) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(6.dp))
                }
                Text(
                    if (isAdding) "Menyimpan..." else "Simpan Alamat",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = { if (!isAdding) onDismiss() }
            ) {
                Text("Batal", color = TextSecondary)
            }
        },
        shape = RoundedCornerShape(20.dp),
        containerColor = SurfaceWhite
    )
}

// ─────────────────────────────────────────────────────────────────
// Info banner (multiple addresses hint)
// ─────────────────────────────────────────────────────────────────
@Composable
private fun AddressInfoBanner() {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = GreenDeep.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, GreenDeep.copy(alpha = 0.25f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("💡", fontSize = 16.sp)
            Text(
                "Tekan ★ Jadikan Utama untuk memilih alamat pengiriman default.",
                fontSize = 12.sp,
                color = GreenDeep,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────
// Empty state
// ─────────────────────────────────────────────────────────────────
@Composable
private fun AddressEmptyState(modifier: Modifier = Modifier, onAdd: () -> Unit) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("📍", fontSize = 64.sp)
        Spacer(Modifier.height(16.dp))
        Text(
            "Belum Ada Alamat",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = TextPrimary
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Tambahkan alamat pengiriman\nuntuk memudahkan proses checkout",
            fontSize = 14.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = onAdd,
            colors = ButtonDefaults.buttonColors(containerColor = GreenDeep),
            shape = RoundedCornerShape(14.dp)
        ) {
            Icon(Icons.Outlined.Add, contentDescription = null, tint = Color.White)
            Spacer(Modifier.width(8.dp))
            Text("Tambah Alamat Sekarang", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

// ─────────────────────────────────────────────────────────────────
// Helper composables
// ─────────────────────────────────────────────────────────────────
@Composable
private fun AddressTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    error: String? = null,
    modifier: Modifier = Modifier.fillMaxWidth(),
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 12.sp) },
        placeholder = { Text(placeholder, color = TextHint, fontSize = 12.sp) },
        isError = error != null,
        supportingText = error?.let { { Text(it, color = MaterialTheme.colorScheme.error, fontSize = 11.sp) } },
        singleLine = true,
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = addressFieldColors(),
        keyboardOptions = keyboardOptions
    )
}

@Composable
private fun addressFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor      = GreenDeep,
    focusedLabelColor       = GreenDeep,
    unfocusedBorderColor    = DividerColor,
    cursorColor             = GreenDeep,
    focusedContainerColor   = SurfaceWhite,
    unfocusedContainerColor = SurfaceWhite
)

private fun addressLabelEmoji(label: String): String {
    return when (label.lowercase()) {
        "rumah", "home"        -> "🏠"
        "kantor", "office"     -> "🏢"
        "kos", "kosan"         -> "🏡"
        "apartemen", "apt"     -> "🏙️"
        "toko", "store"        -> "🏪"
        else                   -> "📍"
    }
}

// ─────────────────────────────────────────────────────────────────
// Preview
// ─────────────────────────────────────────────────────────────────
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AddressScreenPreview() {
    com.kelasxi.myapplication.ui.theme.TrashCareTheme {
        AddressScreen()
    }
}
