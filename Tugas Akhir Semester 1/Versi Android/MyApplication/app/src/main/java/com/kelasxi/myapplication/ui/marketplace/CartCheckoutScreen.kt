package com.kelasxi.myapplication.ui.marketplace

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.kelasxi.myapplication.model.CartItem
import com.kelasxi.myapplication.viewmodel.MarketplaceViewModel
import kotlinx.coroutines.delay

private val GreenPrimary = Color(0xFF2E7D32)
private val GreenLight   = Color(0xFFE8F5E9)

/**
 * CartCheckoutScreen — dual role:
 *  1. Address entry mode   : cartCheckoutId == null/blank  → show address + items, call checkoutCart()
 *  2. Payment polling mode : cartCheckoutId != blank       → show payment link + poll status
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartCheckoutScreen(
    viewModel: MarketplaceViewModel,
    onBack: () -> Unit,
    cartCheckoutId: String? = null,
    paymentLink: String? = null,
    onPaymentReady: (cartCheckoutId: String, paymentLink: String) -> Unit = { _, _ -> },
    onPaid: () -> Unit = {},
    onPickLocationClick: () -> Unit = {}
) {
    val context          = LocalContext.current
    val cartItems        by viewModel.cartItems.collectAsState()
    val cartTotal        by viewModel.cartTotal.collectAsState()
    val isCheckingOut    by viewModel.isCheckingOut.collectAsState()
    val checkoutError    by viewModel.checkoutError.collectAsState()
    val polledStatus     by viewModel.cartCheckoutPolledStatus.collectAsState()
    val checkoutLat      by viewModel.checkoutLat.collectAsState()
    val checkoutLng      by viewModel.checkoutLng.collectAsState()

    val isPollingMode    = !cartCheckoutId.isNullOrBlank() && !paymentLink.isNullOrBlank()

    var shippingAddress  by remember { mutableStateOf("") }
    var notes            by remember { mutableStateOf("") }
    var addressError     by remember { mutableStateOf<String?>(null) }
    var paymentOpened    by remember { mutableStateOf(false) }

    // Auto-poll every 4 seconds when in polling mode
    LaunchedEffect(isPollingMode) {
        if (isPollingMode) {
            while (true) {
                delay(4_000)
                viewModel.pollCartCheckoutStatus(cartCheckoutId!!)
            }
        }
    }

    // React to paid status
    LaunchedEffect(polledStatus) {
        if (polledStatus == "paid") {
            viewModel.clearCartCheckoutPolledStatus()
            onPaid()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isPollingMode) "Selesaikan Pembayaran" else "Konfirmasi Pesanan",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GreenPrimary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        if (isPollingMode) {
            // ── PAYMENT POLLING MODE ──────────────────────────────────
            CartPaymentPollingContent(
                modifier        = Modifier.padding(padding),
                paymentLink     = paymentLink!!,
                polledStatus    = polledStatus,
                cartCheckoutId  = cartCheckoutId!!,
                onOpenPayment   = {
                    paymentOpened = true
                    context.startActivity(
                        Intent(Intent.ACTION_VIEW, Uri.parse(paymentLink))
                    )
                },
                onPollNow       = { viewModel.pollCartCheckoutStatus(cartCheckoutId) },
                onCancel        = {
                    viewModel.cancelCartCheckout(cartCheckoutId) { onBack() }
                }
            )
        } else {
            // ── ADDRESS + ITEMS REVIEW MODE ───────────────────────────
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color(0xFFF5F5F5)),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // ── Address card ──────────────────────────────────────
                item {
                    Card(
                        shape  = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.LocationOn, null, tint = GreenPrimary, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Alamat Pengiriman", fontWeight = FontWeight.SemiBold)
                            }

                            // ── Map pin badge ─────────────────────────────
                            if (checkoutLat != null && checkoutLng != null) {
                                Surface(
                                    shape  = RoundedCornerShape(8.dp),
                                    color  = GreenLight,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(Icons.Filled.PinDrop, null, tint = GreenPrimary, modifier = Modifier.size(18.dp))
                                        Column(Modifier.weight(1f)) {
                                            Text(
                                                "Lokasi dipilih dari peta ✅",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = GreenPrimary
                                            )
                                            Text(
                                                "%.5f, %.5f".format(checkoutLat, checkoutLng),
                                                fontSize = 11.sp,
                                                color = Color.Gray
                                            )
                                        }
                                        TextButton(
                                            onClick = { viewModel.clearCheckoutLocation() },
                                            contentPadding = PaddingValues(0.dp)
                                        ) {
                                            Text("Hapus", fontSize = 11.sp, color = MaterialTheme.colorScheme.error)
                                        }
                                    }
                                }
                            }

                            OutlinedTextField(
                                value = shippingAddress,
                                onValueChange = { shippingAddress = it; addressError = null },
                                label = { Text("Alamat lengkap") },
                                placeholder = { Text("Jl. Merpati No.12, Jakarta…") },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 2,
                                maxLines = 4,
                                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                                isError = addressError != null,
                                supportingText = addressError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } }
                            )

                            // ── Pick on map button ────────────────────
                            OutlinedButton(
                                onClick = onPickLocationClick,
                                modifier = Modifier.fillMaxWidth(),
                                shape  = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = GreenPrimary),
                                border = androidx.compose.foundation.BorderStroke(1.dp, GreenPrimary)
                            ) {
                                Icon(Icons.Filled.Map, null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    if (checkoutLat != null) "Ubah Lokasi di Peta" else "📍  Pilih Lokasi di Peta",
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            OutlinedTextField(
                                value = notes,
                                onValueChange = { notes = it },
                                label = { Text("Catatan (opsional)") },
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 2,
                                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
                            )
                        }
                    }
                }

                // ── Cart items ────────────────────────────────────────
                item {
                    Text(
                        "Produk (${cartItems.size} item)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Gray
                    )
                }
                items(cartItems) { ci ->
                    CartCheckoutItemRow(ci)
                }

                // ── Summary ───────────────────────────────────────────
                item {
                    Card(
                        shape  = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = GreenLight),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Ringkasan Pembayaran", fontWeight = FontWeight.SemiBold)
                            HorizontalDivider()
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Subtotal (${cartItems.sumOf { it.quantity }} item)")
                                Text("Rp ${"%,d".format(cartTotal)}")
                            }
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Total", fontWeight = FontWeight.Bold)
                                Text(
                                    "Rp ${"%,d".format(cartTotal)}",
                                    fontWeight = FontWeight.Bold,
                                    color = GreenPrimary
                                )
                            }
                        }
                    }
                }

                // ── Error ─────────────────────────────────────────────
                if (checkoutError != null) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                            shape  = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                checkoutError!!,
                                color = Color(0xFFC62828),
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }

                // ── Pay button ────────────────────────────────────────
                item {
                    Button(
                        onClick = {
                            if (shippingAddress.isBlank()) {
                                addressError = "Alamat pengiriman wajib diisi"
                                return@Button
                            }
                            viewModel.checkoutCart(
                                shippingAddress = shippingAddress,
                                notes = notes.ifBlank { null }
                            ) { link, ccId ->
                                onPaymentReady(ccId, link)
                            }
                        },
                        enabled = !isCheckingOut && cartItems.isNotEmpty(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                        shape  = RoundedCornerShape(12.dp)
                    ) {
                        if (isCheckingOut) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Memproses…")
                        } else {
                            Text("🛒  Bayar Sekarang", fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(Modifier.height(32.dp))
                }
            }
        }
    }
}

// ── Sub-composables ──────────────────────────────────────────────────────────

@Composable
private fun CartCheckoutItemRow(ci: CartItem) {
    Card(
        shape  = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AsyncImage(
                model = ci.product.imageUrl,
                contentDescription = ci.product.name,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFEEEEEE)),
                contentScale = ContentScale.Crop
            )
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    ci.product.name,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    "Rp ${"%,d".format(ci.product.price)} × ${ci.quantity}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            Text(
                "Rp ${"%,d".format(ci.subtotal)}",
                fontWeight = FontWeight.SemiBold,
                color = GreenPrimary,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun CartPaymentPollingContent(
    modifier: Modifier = Modifier,
    paymentLink: String,
    polledStatus: String?,
    cartCheckoutId: String,
    onOpenPayment: () -> Unit,
    onPollNow: () -> Unit,
    onCancel: () -> Unit
) {
    var showCancelDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically)
    ) {
        // Status badge
        val isPaid = polledStatus == "paid"
        val statusIcon  = if (isPaid) Icons.Filled.CheckCircle else Icons.Filled.Refresh
        val statusColor = if (isPaid) GreenPrimary else Color(0xFFFFA000)
        val statusText  = when (polledStatus) {
            "paid"    -> "Pembayaran Berhasil! 🎉"
            "expired" -> "Pembayaran Kadaluarsa"
            else      -> "Menunggu Pembayaran…"
        }

        Icon(statusIcon, null, tint = statusColor, modifier = Modifier.size(72.dp))
        Text(statusText, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        Text(
            "ID Checkout: $cartCheckoutId",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )

        if (!isPaid) {
            // Open Mayar link
            Button(
                onClick = onOpenPayment,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                shape  = RoundedCornerShape(12.dp)
            ) {
                Text("💳  Buka Halaman Pembayaran", fontWeight = FontWeight.Bold)
            }

            // Manual refresh
            OutlinedButton(
                onClick = onPollNow,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape  = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Filled.Refresh, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Cek Status Pembayaran")
            }

            // Cancel
            TextButton(onClick = { showCancelDialog = true }) {
                Text("Batalkan Pesanan", color = MaterialTheme.colorScheme.error)
            }
        }

        // Polling indicator
        if (!isPaid) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(4.dp)),
                color = GreenPrimary
            )
            Text(
                "Halaman ini otomatis memperbarui status setiap 4 detik",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }

    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title   = { Text("Batalkan Pesanan?") },
            text    = { Text("Semua pesanan dalam checkout ini akan dibatalkan.") },
            confirmButton = {
                TextButton(onClick = { showCancelDialog = false; onCancel() }) {
                    Text("Ya, Batalkan", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) { Text("Tidak") }
            }
        )
    }
}
