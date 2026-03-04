package com.kelasxi.myapplication.ui.common

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kelasxi.myapplication.data.network.Address
import com.kelasxi.myapplication.ui.theme.*
import com.kelasxi.myapplication.viewmodel.AddressViewModel

/**
 * A reusable address field that lets the user either:
 *  - type an address manually, OR
 *  - pick one of their saved addresses from a bottom-sheet
 *
 * @param value          Current address string value
 * @param onValueChange  Called whenever the address text changes
 * @param addressViewModel ViewModel that holds the user's saved addresses
 * @param label          Field label (default "Alamat Penjemputan")
 * @param modifier       Modifier for the outer container
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressPickerField(
    value: String,
    onValueChange: (String) -> Unit,
    addressViewModel: AddressViewModel,
    label: String = "Alamat Penjemputan",
    modifier: Modifier = Modifier
) {
    val addresses by addressViewModel.addresses.collectAsStateWithLifecycle()
    val isLoading by addressViewModel.isLoading.collectAsStateWithLifecycle()

    var showSheet by remember { mutableStateOf(false) }

    // Load addresses on first composition if not yet loaded
    LaunchedEffect(Unit) {
        if (addresses.isEmpty()) addressViewModel.loadAddresses()
    }

    Column(modifier = modifier) {
        // Main address text field
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            leadingIcon = {
                Icon(
                    Icons.Filled.LocationOn,
                    contentDescription = null,
                    tint = GreenDeep
                )
            },
            trailingIcon = {
                // "Pilih Alamat" button — only show if user has saved addresses
                if (addresses.isNotEmpty() || isLoading) {
                    TextButton(
                        onClick = { showSheet = true },
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Icon(
                            Icons.Outlined.List,
                            contentDescription = null,
                            tint = GreenDeep,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "Pilih",
                            color = GreenDeep,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
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

        // Quick-select chips if user has saved addresses (max 2 shown inline)
        if (addresses.isNotEmpty()) {
            Spacer(Modifier.height(6.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.horizontalScroll(rememberScrollState())
            ) {
                Text(
                    "Tersimpan:",
                    fontSize = 11.sp,
                    color = TextHint,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                addresses.take(3).forEach { addr ->
                    AddressQuickChip(
                        address = addr,
                        isSelected = value == buildAddressString(addr),
                        onClick = { onValueChange(buildAddressString(addr)) }
                    )
                }
                if (addresses.size > 3) {
                    SuggestionChip(
                        onClick = { showSheet = true },
                        label = {
                            Text(
                                "+${addresses.size - 3} lainnya",
                                fontSize = 11.sp,
                                color = GreenDeep
                            )
                        },
                        shape = RoundedCornerShape(20.dp),
                        border = SuggestionChipDefaults.suggestionChipBorder(
                            enabled = true,
                            borderColor = GreenDeep.copy(alpha = 0.4f)
                        )
                    )
                }
            }
        }
    }

    // ── Address Picker Bottom Sheet ───────────────────────────────
    if (showSheet) {
        AddressPickerBottomSheet(
            addresses = addresses,
            currentValue = value,
            isLoading = isLoading,
            onSelect = { addr ->
                onValueChange(buildAddressString(addr))
                showSheet = false
            },
            onDismiss = { showSheet = false },
            onRefresh = { addressViewModel.loadAddresses() }
        )
    }
}

// ─────────────────────────────────────────────────────────────────
// Bottom Sheet
// ─────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddressPickerBottomSheet(
    addresses: List<Address>,
    currentValue: String,
    isLoading: Boolean,
    onSelect: (Address) -> Unit,
    onDismiss: () -> Unit,
    onRefresh: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfaceWhite,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "📍 Pilih Alamat",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = TextPrimary
                    )
                    Text(
                        "${addresses.size} alamat tersimpan",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
                IconButton(onClick = onRefresh) {
                    Icon(Icons.Outlined.Refresh, contentDescription = "Refresh", tint = GreenDeep)
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                color = DividerColor
            )

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CircularProgressIndicator(color = GreenDeep)
                            Text("Memuat alamat...", color = TextSecondary, fontSize = 13.sp)
                        }
                    }
                }

                addresses.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("📍", fontSize = 40.sp)
                            Text(
                                "Belum ada alamat tersimpan",
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Text(
                                "Tambahkan alamat di menu Profil → Addresses",
                                fontSize = 12.sp,
                                color = TextSecondary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(addresses, key = { it.id }) { addr ->
                            val addressStr = buildAddressString(addr)
                            val isSelected = currentValue == addressStr
                            AddressPickerItem(
                                address = addr,
                                isSelected = isSelected,
                                onClick = { onSelect(addr) }
                            )
                        }
                        item { Spacer(Modifier.height(8.dp)) }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────
// Single address row inside the sheet
// ─────────────────────────────────────────────────────────────────
@Composable
private fun AddressPickerItem(
    address: Address,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) GreenDeep.copy(alpha = 0.08f) else SurfaceWhite
        ),
        border = if (isSelected)
            BorderStroke(1.5.dp, GreenDeep)
        else
            BorderStroke(1.dp, DividerColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Icon box
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        if (isSelected) GreenDeep.copy(alpha = 0.15f) else SurfaceVariant,
                        RoundedCornerShape(10.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(pickAddressEmoji(address.label), fontSize = 18.sp)
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        address.label,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = if (isSelected) GreenDeep else TextPrimary
                    )
                    if (address.isDefault) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = GreenDeep.copy(alpha = 0.12f)
                        ) {
                            Text(
                                "Utama",
                                fontSize = 10.sp,
                                color = GreenDeep,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    address.recipientName,
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Text(
                    buildAddressString(address),
                    fontSize = 12.sp,
                    color = TextHint,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (isSelected) {
                Icon(
                    Icons.Outlined.CheckCircle,
                    contentDescription = null,
                    tint = GreenDeep,
                    modifier = Modifier
                        .size(20.dp)
                        .align(Alignment.CenterVertically)
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────
// Inline quick-select chip
// ─────────────────────────────────────────────────────────────────
@Composable
private fun AddressQuickChip(
    address: Address,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(pickAddressEmoji(address.label), fontSize = 12.sp)
                Text(
                    address.label,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        shape = RoundedCornerShape(20.dp),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = GreenDeep,
            selectedLabelColor = Color.White
        )
    )
}

// ─────────────────────────────────────────────────────────────────
// Helpers
// ─────────────────────────────────────────────────────────────────

/** Builds the full one-line address string that goes into the text field */
fun buildAddressString(address: Address): String =
    "${address.fullAddress}, ${address.city}, ${address.province} ${address.postalCode}"

private fun pickAddressEmoji(label: String): String = when (label.lowercase()) {
    "rumah", "home"      -> "🏠"
    "kantor", "office"   -> "🏢"
    "kos", "kosan"       -> "🏡"
    "apartemen", "apt"   -> "🏙️"
    "toko", "store"      -> "🏪"
    else                 -> "📍"
}
