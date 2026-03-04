package com.kelasxi.myapplication.ui.profile

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kelasxi.myapplication.model.*
import com.kelasxi.myapplication.ui.theme.*
import com.kelasxi.myapplication.viewmodel.MarketplaceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyOrdersScreen(
    viewModel: MarketplaceViewModel = viewModel(),
    onBack: () -> Unit = {}
) {
    val orders       by viewModel.orders.collectAsStateWithLifecycle()
    val isLoading    by viewModel.isLoadingOrders.collectAsStateWithLifecycle()
    val ordersError  by viewModel.ordersError.collectAsStateWithLifecycle()
    val isPayingOrder by viewModel.isPayingOrder.collectAsStateWithLifecycle()
    val paySuccess   by viewModel.paySuccess.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    // Order to show payment dialog for
    var payingOrder by remember { mutableStateOf<Order?>(null) }

    // Load on first entry
    LaunchedEffect(Unit) { viewModel.loadOrders() }

    LaunchedEffect(ordersError) {
        if (ordersError != null) {
            snackbarHostState.showSnackbar(ordersError!!)
        }
    }

    LaunchedEffect(paySuccess) {
        if (paySuccess != null) {
            snackbarHostState.showSnackbar(paySuccess!!)
            viewModel.dismissPaySuccess()
        }
    }

    // Filter state
    var selectedStatus by remember { mutableStateOf<OrderStatus?>(null) }
    val filtered = if (selectedStatus == null) orders
    else orders.filter { it.status == selectedStatus }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "My Orders",
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
                actions = {
                    IconButton(onClick = { viewModel.loadOrders() }) {
                        Icon(Icons.Outlined.Refresh, contentDescription = "Refresh", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GreenDeep,
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = BackgroundGreen
    ) { innerPadding ->
        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = GreenDeep)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Memuat pesanan...", color = TextSecondary)
                    }
                }
            }
            else -> {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // ── Summary banner ────────────────────────────────────────
            OrderSummaryBanner(orders = orders)

            // ── Status filter chips ───────────────────────────────────
            OrderStatusFilter(
                selectedStatus = selectedStatus,
                onStatusSelected = { selectedStatus = if (selectedStatus == it) null else it }
            )

            // ── Order list ────────────────────────────────────────────
            if (filtered.isEmpty()) {
                OrderEmptyState()
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filtered, key = { it.id }) { order ->
                        OrderCard(
                            order = order,
                            onPay    = { payingOrder = order },
                            onCancel = { viewModel.cancelOrder(order.id) }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
            } // end else
        }
    }

    // Payment dialog — shown on top of Scaffold
    payingOrder?.let { order ->
        PaymentDialog(
            order    = order,
            isPaying = isPayingOrder,
            onPay    = { method ->
                viewModel.payOrder(order.id, method)
                payingOrder = null
            },
            onDismiss = { payingOrder = null }
        )
    }
}

// ─────────────────────────────────────────────────────────────────
// Summary banner
// ─────────────────────────────────────────────────────────────────
@Composable
fun OrderSummaryBanner(orders: List<Order>) {
    val total     = orders.size
    val active    = orders.count { it.status in listOf(OrderStatus.PROCESSING, OrderStatus.SHIPPED, OrderStatus.WAITING_PAYMENT) }
    val completed = orders.count { it.status == OrderStatus.DELIVERED }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.horizontalGradient(listOf(GreenDeep, GreenMedium))
            )
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            OrderSummaryItem("$total", "Total Order", "📋")
            OrderSummaryDivider()
            OrderSummaryItem("$active", "Aktif", "🔄")
            OrderSummaryDivider()
            OrderSummaryItem("$completed", "Selesai", "✅")
        }
    }
}

@Composable
private fun OrderSummaryItem(value: String, label: String, emoji: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(emoji, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.8f)
        )
    }
}

@Composable
private fun OrderSummaryDivider() {
    Box(
        modifier = Modifier
            .height(40.dp)
            .width(1.dp)
            .background(Color.White.copy(alpha = 0.3f))
    )
}

// ─────────────────────────────────────────────────────────────────
// Status filter chips
// ─────────────────────────────────────────────────────────────────
@Composable
fun OrderStatusFilter(
    selectedStatus: OrderStatus?,
    onStatusSelected: (OrderStatus) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(OrderStatus.entries) { status ->
            val isSelected = selectedStatus == status
            FilterChip(
                selected = isSelected,
                onClick = { onStatusSelected(status) },
                label = {
                    Text(
                        text = "${status.emoji} ${status.label}",
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = GreenDeep,
                    selectedLabelColor = Color.White
                ),
                shape = RoundedCornerShape(20.dp)
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────
// Single order card
// ─────────────────────────────────────────────────────────────────
@Composable
fun OrderCard(order: Order, onPay: () -> Unit = {}, onCancel: () -> Unit = {}) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // ── Header: order id + status badge ──────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = order.id,
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary,
                    fontWeight = FontWeight.SemiBold
                )
                OrderStatusBadge(status = order.status)
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = DividerColor)
            Spacer(modifier = Modifier.height(10.dp))

            // ── Product row ───────────────────────────────────────
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Product emoji/placeholder
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = order.product.category.emoji(),
                        fontSize = 30.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = order.product.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = order.product.condition.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "× ${order.quantity}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                        Text(
                            text = formatRupiah(order.totalPrice),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = GreenDeep
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = DividerColor)
            Spacer(modifier = Modifier.height(10.dp))

            // ── Footer: date + shipping info ──────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Outlined.CalendarMonth,
                            contentDescription = null,
                            tint = TextHint,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = order.orderedAt,
                            style = MaterialTheme.typography.labelSmall,
                            color = TextHint
                        )
                    }
                    if (order.estimatedArrival.isNotBlank() &&
                        order.status !in listOf(OrderStatus.CANCELLED, OrderStatus.DELIVERED)) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Outlined.LocalShipping,
                                contentDescription = null,
                                tint = GreenDeep,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Est. ${order.estimatedArrival}",
                                style = MaterialTheme.typography.labelSmall,
                                color = GreenDeep,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // Action button per status
                OrderActionButton(status = order.status, onPay = onPay, onCancel = onCancel)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────
// Status badge
// ─────────────────────────────────────────────────────────────────
@Composable
fun OrderStatusBadge(status: OrderStatus) {
    val (bg, fg) = when (status) {
        OrderStatus.WAITING_PAYMENT -> Pair(StatusPending.copy(alpha = 0.15f),   StatusPending)
        OrderStatus.PROCESSING      -> Pair(StatusOnTheWay.copy(alpha = 0.15f),  StatusOnTheWay)
        OrderStatus.SHIPPED         -> Pair(GreenDeep.copy(alpha = 0.12f),       GreenDeep)
        OrderStatus.DELIVERED       -> Pair(StatusDone.copy(alpha = 0.15f),      StatusDone)
        OrderStatus.CANCELLED       -> Pair(StatusCancelled.copy(alpha = 0.15f), StatusCancelled)
    }
    Surface(color = bg, shape = RoundedCornerShape(20.dp)) {
        Text(
            text = "${status.emoji} ${status.label}",
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = fg,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// ─────────────────────────────────────────────────────────────────
// Action button
// ─────────────────────────────────────────────────────────────────
@Composable
fun OrderActionButton(status: OrderStatus, onPay: () -> Unit = {}, onCancel: () -> Unit = {}) {
    when (status) {
        OrderStatus.WAITING_PAYMENT ->
            Column(horizontalAlignment = Alignment.End) {
                Button(
                    onClick = onPay,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = StatusPending),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) { Text("Bayar 💳", style = MaterialTheme.typography.labelSmall, color = Color.White) }
                TextButton(
                    onClick = onCancel,
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                ) { Text("Batalkan", style = MaterialTheme.typography.labelSmall, color = StatusCancelled) }
            }

        OrderStatus.SHIPPED ->
            OutlinedButton(
                onClick = {},
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, GreenDeep),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) { Text("Lacak", style = MaterialTheme.typography.labelSmall, color = GreenDeep) }

        OrderStatus.DELIVERED ->
            OutlinedButton(
                onClick = {},
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, GreenDeep),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) { Text("Beli Lagi", style = MaterialTheme.typography.labelSmall, color = GreenDeep) }

        else -> Spacer(modifier = Modifier.width(0.dp))
    }
}

// ─────────────────────────────────────────────────────────────────
// Empty state
// ─────────────────────────────────────────────────────────────────
@Composable
fun OrderEmptyState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("📦", fontSize = 64.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Tidak ada pesanan",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Belum ada pesanan dengan status ini.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────
// Helpers
// ─────────────────────────────────────────────────────────────────
private fun formatRupiah(amount: Long): String {
    val formatted = StringBuilder()
    val s = amount.toString()
    s.reversed().forEachIndexed { i, c ->
        if (i > 0 && i % 3 == 0) formatted.append('.')
        formatted.append(c)
    }
    return "Rp ${formatted.reverse()}"
}

/** Map ProductCategory to a representative emoji for the order card thumbnail */
private fun ProductCategory.emoji(): String = when (this) {
    ProductCategory.FURNITURE   -> "🪑"
    ProductCategory.ELECTRONICS -> "💻"
    ProductCategory.CLOTHING    -> "👗"
    ProductCategory.BOOKS       -> "📚"
    ProductCategory.OTHERS      -> "🛍️"
    ProductCategory.ALL         -> "🛒"
}

// ─────────────────────────────────────────────────────────────────
// Payment Dialog
// ─────────────────────────────────────────────────────────────────
@Composable
fun PaymentDialog(
    order: Order,
    isPaying: Boolean,
    onPay: (method: String) -> Unit,
    onDismiss: () -> Unit
) {
    val methods = listOf(
        Triple("transfer", "💳", "Transfer Bank"),
        Triple("ewallet",  "📱", "E-Wallet (GoPay/OVO/Dana)"),
        Triple("cod",      "🚚", "Bayar di Tempat (COD)")
    )
    var selectedMethod by remember { mutableStateOf("transfer") }

    AlertDialog(
        onDismissRequest = { if (!isPaying) onDismiss() },
        shape = RoundedCornerShape(20.dp),
        containerColor = SurfaceWhite,
        title = {
            Column {
                Text("💳 Pembayaran", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(Modifier.height(2.dp))
                Text(
                    order.id,
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Order summary
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            order.product.name,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            modifier = Modifier.weight(1f),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            formatRupiah(order.totalPrice),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            color = GreenDeep
                        )
                    }
                }

                Spacer(Modifier.height(4.dp))
                Text(
                    "Pilih Metode Pembayaran",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = TextPrimary
                )

                // Payment method selector
                methods.forEach { (value, emoji, label) ->
                    val isSelected = selectedMethod == value
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedMethod = value },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected)
                                GreenDeep.copy(alpha = 0.08f)
                            else SurfaceVariant
                        ),
                        border = if (isSelected)
                            androidx.compose.foundation.BorderStroke(1.5.dp, GreenDeep)
                        else null
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(emoji, fontSize = 20.sp)
                            Text(
                                label,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                fontSize = 14.sp,
                                color = if (isSelected) GreenDeep else TextPrimary,
                                modifier = Modifier.weight(1f)
                            )
                            RadioButton(
                                selected = isSelected,
                                onClick = { selectedMethod = value },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = GreenDeep
                                )
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onPay(selectedMethod) },
                enabled = !isPaying,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenDeep),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isPaying) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Memproses...", color = Color.White, fontWeight = FontWeight.Bold)
                } else {
                    Text("✅ Bayar Sekarang", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isPaying
            ) {
                Text("Batal", color = TextSecondary)
            }
        }
    )
}

// ─────────────────────────────────────────────────────────────────
// Preview
// ─────────────────────────────────────────────────────────────────
@Preview(showBackground = true)
@Composable
fun MyOrdersScreenPreview() {
    TrashCareTheme {
        // Static preview — no ViewModel
        Scaffold(
            topBar = {
                @OptIn(ExperimentalMaterial3Api::class)
                TopAppBar(
                    title = { Text("My Orders", fontWeight = FontWeight.Bold, color = Color.White) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = GreenDeep)
                )
            },
            containerColor = BackgroundGreen
        ) { p ->
            Box(Modifier.padding(p).fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("My Orders Preview", color = TextSecondary)
            }
        }
    }
}
