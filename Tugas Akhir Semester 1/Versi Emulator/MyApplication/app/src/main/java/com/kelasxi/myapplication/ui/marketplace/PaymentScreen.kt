package com.kelasxi.myapplication.ui.marketplace

import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kelasxi.myapplication.ui.theme.*
import com.kelasxi.myapplication.viewmodel.MarketplaceViewModel
import kotlinx.coroutines.delay

private const val POLL_INTERVAL_MS = 3_000L
private const val TIMEOUT_MS       = 10 * 60 * 1000L  // 10 minutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    orderId:     Long,
    paymentLink: String,
    paymentId:   String,
    viewModel:   MarketplaceViewModel,
    onBack:      () -> Unit,
    onPaid:      () -> Unit
) {
    val context = LocalContext.current
    val polledStatus by viewModel.polledPaymentStatus.collectAsStateWithLifecycle()
    val paySuccess   by viewModel.paySuccess.collectAsStateWithLifecycle()

    var elapsedMs   by remember { mutableLongStateOf(0L) }
    var isTimedOut  by remember { mutableStateOf(false) }
    var isCheckingManually by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    // ── Auto-polling every 3 seconds ──────────────────────────────
    LaunchedEffect(orderId) {
        while (elapsedMs < TIMEOUT_MS) {
            delay(POLL_INTERVAL_MS)
            elapsedMs += POLL_INTERVAL_MS
            viewModel.pollPaymentStatus(orderId)
        }
        isTimedOut = true
    }

    // ── React to paid status ──────────────────────────────────────
    LaunchedEffect(polledStatus) {
        if (polledStatus == "paid") {
            viewModel.clearPolledPaymentStatus()
            viewModel.dismissPaySuccess()
            onPaid()
        }
    }

    LaunchedEffect(paySuccess) {
        if (paySuccess != null) {
            snackbarHostState.showSnackbar(paySuccess!!)
        }
    }

    // Open Mayar payment page in Chrome Custom Tab
    fun openPaymentPage() {
        try {
            CustomTabsIntent.Builder()
                .build()
                .launchUrl(context, Uri.parse(paymentLink))
        } catch (e: Exception) {
            // Fallback to plain browser
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(paymentLink)))
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Pembayaran",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GreenDeep
                )
            )
        },
        containerColor = BackgroundGreen
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // ── Header banner ─────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .background(
                        Brush.horizontalGradient(listOf(GreenDeep, GreenMedium)),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("💳", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Selesaikan Pembayaran",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Klik tombol di bawah untuk membuka halaman pembayaran Mayar",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.85f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Order ID card ─────────────────────────────────────
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(3.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            "ID Pesanan",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                        Text(
                            "#$orderId",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                    PaymentStatusChip(polledStatus ?: "unpaid")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Main pay button ───────────────────────────────────
            Button(
                onClick = { openPaymentPage() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenDeep)
            ) {
                Icon(Icons.Outlined.OpenInBrowser, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Buka Halaman Pembayaran",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ── Manual check button ───────────────────────────────
            OutlinedButton(
                onClick = {
                    isCheckingManually = true
                    viewModel.pollPaymentStatus(orderId)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(48.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.5.dp, GreenDeep)
            ) {
                if (isCheckingManually) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = GreenDeep,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                } else {
                    Icon(Icons.Outlined.Refresh, contentDescription = null, tint = GreenDeep)
                    Spacer(modifier = Modifier.width(6.dp))
                }
                Text("Cek Status Pembayaran", color = GreenDeep, fontWeight = FontWeight.SemiBold)
            }

            // Reset manual check spinner when status arrives
            LaunchedEffect(polledStatus) { isCheckingManually = false }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Polling status indicator ──────────────────────────
            AnimatedVisibility(visible = !isTimedOut) {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(14.dp),
                        color = GreenMedium,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Menunggu konfirmasi pembayaran otomatis...",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }
            }

            // ── Timeout warning ───────────────────────────────────
            AnimatedVisibility(visible = isTimedOut) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = StatusPending.copy(alpha = 0.12f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("⏰", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            "Waktu habis. Klik \"Cek Status\" secara manual atau kembali ke Pesanan.",
                            style = MaterialTheme.typography.bodySmall,
                            color = StatusPending
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Instruction steps ─────────────────────────────────
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Cara Bayar",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    PaymentStep(number = "1", text = "Klik tombol \"Buka Halaman Pembayaran\"")
                    PaymentStep(number = "2", text = "Pilih metode pembayaran yang diinginkan")
                    PaymentStep(number = "3", text = "Selesaikan pembayaran di halaman Mayar")
                    PaymentStep(number = "4", text = "Kembali ke aplikasi — status akan diperbarui otomatis")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun PaymentStatusChip(status: String) {
    val (bg, fg, label) = when (status) {
        "paid"   -> Triple(StatusDone.copy(alpha = 0.15f),      StatusDone,      "✅ Dibayar")
        "closed" -> Triple(StatusCancelled.copy(alpha = 0.15f), StatusCancelled, "❌ Ditutup")
        else     -> Triple(StatusPending.copy(alpha = 0.15f),   StatusPending,   "⏳ Belum Dibayar")
    }
    Surface(color = bg, shape = RoundedCornerShape(20.dp)) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = fg,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun PaymentStep(number: String, text: String) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            shape = RoundedCornerShape(50),
            color = GreenDeep.copy(alpha = 0.12f),
            modifier = Modifier.size(22.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    number,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = GreenDeep
                )
            }
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )
    }
}
