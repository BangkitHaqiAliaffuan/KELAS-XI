package com.kelasxi.myapplication.ui.profile

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.kelasxi.myapplication.model.*
import com.kelasxi.myapplication.model.CartCheckoutGroup
import com.kelasxi.myapplication.ui.theme.*
import com.kelasxi.myapplication.viewmodel.MarketplaceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyOrdersScreen(
    viewModel: MarketplaceViewModel = viewModel(),
    onBack: () -> Unit = {},
    onPayOrder: (orderId: Long, paymentLink: String, paymentId: String) -> Unit = { _, _, _ -> },
    onPayCartCheckout: (cartCheckoutId: String, paymentLink: String) -> Unit = { _, _ -> }
) {
    val orders        by viewModel.orders.collectAsStateWithLifecycle()
    val isLoading     by viewModel.isLoadingOrders.collectAsStateWithLifecycle()
    val ordersError   by viewModel.ordersError.collectAsStateWithLifecycle()
    val isPayingOrder by viewModel.isPayingOrder.collectAsStateWithLifecycle()
    val paySuccess    by viewModel.paySuccess.collectAsStateWithLifecycle()

    val salesTransactions by viewModel.salesTransactions.collectAsStateWithLifecycle()
    val salesSummary      by viewModel.salesSummary.collectAsStateWithLifecycle()
    val isLoadingSales    by viewModel.isLoadingSales.collectAsStateWithLifecycle()
    val salesError        by viewModel.salesError.collectAsStateWithLifecycle()

    val cartCheckoutGroups    by viewModel.cartCheckoutGroups.collectAsStateWithLifecycle()
    @Suppress("UNUSED_VARIABLE")
    val isLoadingCartCheckouts by viewModel.isLoadingCartCheckouts.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    // 0 = Pembelian (My Orders), 1 = Penjualan (Sales)
    var selectedTab by remember { mutableIntStateOf(0) }

    // Load on first entry
    LaunchedEffect(Unit) {
        viewModel.loadOrders()
        viewModel.loadSalesTransactions()
        viewModel.loadCartCheckouts()
    }

    LaunchedEffect(ordersError) {
        if (ordersError != null) snackbarHostState.showSnackbar(ordersError!!)
    }
    LaunchedEffect(salesError) {
        if (salesError != null) snackbarHostState.showSnackbar(salesError!!)
    }
    LaunchedEffect(paySuccess) {
        if (paySuccess != null) {
            snackbarHostState.showSnackbar(paySuccess!!)
            viewModel.dismissPaySuccess()
        }
    }

    // Filter state (Pembelian tab only)
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
                    IconButton(onClick = {
                        viewModel.loadOrders()
                        viewModel.loadSalesTransactions()
                    }) {
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // ── Tab Row ───────────────────────────────────────────
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor   = GreenDeep,
                contentColor     = Color.White,
                indicator        = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = Color.White
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick  = { selectedTab = 0 },
                    text     = {
                        Text(
                            "🛒 Pembelian",
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                            color      = Color.White
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick  = { selectedTab = 1 },
                    text     = {
                        Text(
                            "💰 Penjualan",
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                            color      = Color.White
                        )
                    }
                )
            }

            // ── Tab Content ───────────────────────────────────────
            when (selectedTab) {
                0 -> PembelianTab(
                    orders        = orders,
                    filtered      = filtered,
                    isLoading     = isLoading,
                    selectedStatus = selectedStatus,
                    onStatusSelected = { selectedStatus = if (selectedStatus == it) null else it },
                    viewModel     = viewModel,
                    isPayingOrder = isPayingOrder,
                    onPayOrder    = onPayOrder,
                    cartCheckoutGroups   = cartCheckoutGroups,
                    onPayCartCheckout    = onPayCartCheckout
                )
                1 -> PenjualanTab(
                    transactions = salesTransactions,
                    summary      = salesSummary,
                    isLoading    = isLoadingSales
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────
// Tab 0: Pembelian (existing orders)
// ─────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PembelianTab(
    orders: List<Order>,
    filtered: List<Order>,
    isLoading: Boolean,
    selectedStatus: OrderStatus?,
    onStatusSelected: (OrderStatus) -> Unit,
    viewModel: MarketplaceViewModel,
    isPayingOrder: Boolean,
    onPayOrder: (Long, String, String) -> Unit,
    cartCheckoutGroups: List<CartCheckoutGroup> = emptyList(),
    onPayCartCheckout: (String, String) -> Unit = { _, _ -> }
) {
    if (isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = GreenDeep)
                Spacer(Modifier.height(12.dp))
                Text("Memuat pesanan...", color = TextSecondary)
            }
        }
        return
    }

    // Menyimpan order yang sedang dibuka detail-nya
    var selectedOrder by remember { mutableStateOf<Order?>(null) }
    val sheetState    = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Tampilkan bottom sheet detail jika ada order yang dipilih
    selectedOrder?.let { order ->
        OrderDetailBottomSheet(
            order         = order,
            sheetState    = sheetState,
            onDismiss     = { selectedOrder = null },
            onPay         = {
                viewModel.payOrder(order.id) { ordId, link, pid ->
                    onPayOrder(ordId, link, pid)
                }
                selectedOrder = null
            },
            onCancel      = {
                viewModel.cancelOrder(order.id)
                selectedOrder = null
            },
            isPayingThis  = isPayingOrder && order.status == OrderStatus.WAITING_PAYMENT
        )
    }

    Column(Modifier.fillMaxSize()) {
        OrderSummaryBanner(orders = orders)
        OrderStatusFilter(
            selectedStatus   = selectedStatus,
            onStatusSelected = onStatusSelected
        )
        if (filtered.isEmpty() && cartCheckoutGroups.isEmpty()) {
            OrderEmptyState()
        } else {
            LazyColumn(
                contentPadding      = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // ── Cart Checkout Groups (multi-item cart transactions) ──
                if (cartCheckoutGroups.isNotEmpty()) {
                    item {
                        Text(
                            "🛒 Pembelian Cart (${cartCheckoutGroups.size})",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF2E7D32),
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                    items(cartCheckoutGroups, key = { "cg_" + it.cartCheckoutId }) { group ->
                        CartGroupCard(
                            group    = group,
                            onPay    = {
                                val link = group.paymentLink ?: ""
                                if (link.isNotBlank()) onPayCartCheckout(group.cartCheckoutId, link)
                            },
                            onCancel = { viewModel.cancelCartCheckout(group.cartCheckoutId) }
                        )
                    }
                    if (filtered.isNotEmpty()) {
                        item { HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp)) }
                    }
                }

                // ── Regular single-item orders ──────────────────────────
                items(filtered, key = { it.id }) { order ->
                    OrderCard(
                        order        = order,
                        onOrderClick = { selectedOrder = order },
                        onPay        = {
                            viewModel.payOrder(order.id) { ordId, link, pid ->
                                onPayOrder(ordId, link, pid)
                            }
                        },
                        onCancel     = { viewModel.cancelOrder(order.id) },
                        isPayingThis = isPayingOrder && order.status == OrderStatus.WAITING_PAYMENT
                    )
                }
                item { Spacer(Modifier.height(16.dp)) }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────
// Tab 1: Penjualan — Mayar transactions
// ─────────────────────────────────────────────────────────────────
@Composable
private fun PenjualanTab(
    transactions: List<SalesTransaction>,
    summary: SalesSummary,
    isLoading: Boolean
) {
    if (isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = GreenDeep)
                Spacer(Modifier.height(12.dp))
                Text("Memuat data penjualan...", color = TextSecondary)
            }
        }
        return
    }

    LazyColumn(
        contentPadding      = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ── Revenue Summary Card ──────────────────────────────────
        item {
            SalesRevenueBanner(summary = summary)
        }

        // ── Section Header ────────────────────────────────────────
        item {
            Text(
                "Riwayat Transaksi",
                style      = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color      = TextPrimary
            )
        }

        if (transactions.isEmpty()) {
            item {
                Box(
                    modifier         = Modifier.fillMaxWidth().padding(top = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("💸", fontSize = 56.sp)
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "Belum ada transaksi penjualan",
                            style      = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color      = TextPrimary
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "Transaksi dari pembeli akan muncul di sini.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
            }
        } else {
            items(transactions, key = { it.id }) { trx ->
                SalesTransactionCard(trx = trx)
            }
        }

        item { Spacer(Modifier.height(16.dp)) }
    }
}

// ─────────────────────────────────────────────────────────────────
// Revenue banner card
// ─────────────────────────────────────────────────────────────────
@Composable
private fun SalesRevenueBanner(summary: SalesSummary) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(containerColor = GreenDeep),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Ringkasan Penjualan",
                fontWeight = FontWeight.Bold,
                fontSize   = 14.sp,
                color      = Color.White.copy(alpha = 0.85f)
            )
            Spacer(Modifier.height(12.dp))
            // Total Revenue highlight
            Text(
                formatRupiah(summary.totalRevenue),
                fontWeight = FontWeight.ExtraBold,
                fontSize   = 28.sp,
                color      = Color.White
            )
            Text(
                "Total Keuntungan Bersih",
                fontSize = 11.sp,
                color    = Color.White.copy(alpha = 0.75f)
            )
            Spacer(Modifier.height(16.dp))
            // Stats row
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SalesStatItem("${summary.totalTransactions}", "Total Trx", "📊")
                SalesDivider()
                SalesStatItem("${summary.totalPaid}", "Lunas", "✅")
                SalesDivider()
                SalesStatItem("${summary.totalUnpaid}", "Pending", "⏳")
            }
        }
    }
}

@Composable
private fun SalesStatItem(value: String, label: String, emoji: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(emoji, fontSize = 18.sp)
        Spacer(Modifier.height(2.dp))
        Text(
            value,
            fontWeight = FontWeight.Bold,
            fontSize   = 18.sp,
            color      = Color.White
        )
        Text(
            label,
            fontSize = 10.sp,
            color    = Color.White.copy(alpha = 0.75f)
        )
    }
}

@Composable
private fun SalesDivider() {
    Box(
        modifier = Modifier
            .height(36.dp)
            .width(1.dp)
            .background(Color.White.copy(alpha = 0.3f))
    )
}

// ─────────────────────────────────────────────────────────────────
// Single sales transaction card
// ─────────────────────────────────────────────────────────────────
@Composable
private fun SalesTransactionCard(trx: SalesTransaction) {
    val isPaid = trx.mayarStatus == "paid" || trx.status.uppercase() == "SUCCESS"
    val statusColor  = if (isPaid) StatusDone else StatusPending
    val statusLabel  = if (isPaid) "Lunas ✅" else "Pending ⏳"
    val statusBg     = if (isPaid) StatusDone.copy(alpha = 0.12f) else StatusPending.copy(alpha = 0.12f)

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(14.dp),
        colors    = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header row: customer name + status badge
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Avatar circle
                    Box(
                        modifier         = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(GreenDeep.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            trx.customerName.take(1).uppercase().ifBlank { "?" },
                            fontWeight = FontWeight.Bold,
                            color      = GreenDeep,
                            fontSize   = 16.sp
                        )
                    }
                    Column {
                        Text(
                            trx.customerName.ifBlank { "Pembeli" },
                            style      = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color      = TextPrimary,
                            maxLines   = 1,
                            overflow   = TextOverflow.Ellipsis
                        )
                        if (trx.customerEmail.isNotBlank()) {
                            Text(
                                trx.customerEmail,
                                style   = MaterialTheme.typography.labelSmall,
                                color   = TextHint,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
                // Status badge
                Surface(color = statusBg, shape = RoundedCornerShape(20.dp)) {
                    Text(
                        statusLabel,
                        modifier  = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        fontSize  = 11.sp,
                        color     = statusColor,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(Modifier.height(10.dp))
            HorizontalDivider(color = DividerColor)
            Spacer(Modifier.height(10.dp))

            // Amount + description row
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    if (trx.description.isNotBlank()) {
                        Text(
                            trx.description,
                            style    = MaterialTheme.typography.bodySmall,
                            color    = TextSecondary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(Modifier.height(4.dp))
                    }
                    // Transaction ID (short)
                    Text(
                        "ID: ${trx.transactionId.take(18)}…",
                        style  = MaterialTheme.typography.labelSmall,
                        color  = TextHint
                    )
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    formatRupiah(trx.amount),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize   = 16.sp,
                    color      = if (isPaid) StatusDone else OrangeDark
                )
            }

            // Date
            if (trx.createdAt.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.CalendarMonth,
                        contentDescription = null,
                        tint     = TextHint,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        trx.createdAt.take(10),   // "2025-04-23"
                        style = MaterialTheme.typography.labelSmall,
                        color = TextHint
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────
// Order Detail Bottom Sheet
// ─────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OrderDetailBottomSheet(
    order: Order,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onPay: () -> Unit,
    onCancel: () -> Unit,
    isPayingThis: Boolean
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState       = sheetState,
        shape            = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor   = SurfaceWhite,
        dragHandle       = {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 4.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(DividerColor)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            // ── Judul ─────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    "Detail Pesanan",
                    style      = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color      = TextPrimary
                )
                OrderStatusBadge(status = order.status)
            }

            Text(
                "ID: ${order.id}",
                style = MaterialTheme.typography.labelSmall,
                color = TextHint
            )

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = DividerColor)
            Spacer(Modifier.height(16.dp))

            // ── Produk ────────────────────────────────────────────
            Row(
                modifier          = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(BackgroundGreen),
                    contentAlignment = Alignment.Center
                ) {
                    if (!order.product.imageUrl.isNullOrBlank()) {
                        AsyncImage(
                            model              = order.product.imageUrl,
                            contentDescription = order.product.name,
                            contentScale       = ContentScale.Crop,
                            modifier           = Modifier.fillMaxSize()
                        )
                    } else {
                        Text(order.product.category.emoji(), fontSize = 34.sp)
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        order.product.name,
                        style      = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color      = TextPrimary,
                        maxLines   = 3
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        order.product.condition.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = DividerColor)
            Spacer(Modifier.height(14.dp))

            // ── Info Rows ─────────────────────────────────────────
            OrderDetailRow("📦", "Jumlah",   "${order.quantity} item")
            OrderDetailRow("📅", "Tanggal",  order.orderedAt)
            if (order.shippingAddress.isNotBlank())
                OrderDetailRow("📍", "Alamat", order.shippingAddress)
            if (order.estimatedArrival.isNotBlank() &&
                order.status !in listOf(OrderStatus.CANCELLED, OrderStatus.DELIVERED))
                OrderDetailRow("🚚", "Est. Tiba", order.estimatedArrival)
            if (order.paidAt != null)
                OrderDetailRow("💳", "Dibayar", order.paidAt)

            Spacer(Modifier.height(14.dp))
            HorizontalDivider(color = DividerColor)
            Spacer(Modifier.height(14.dp))

            // ── Total Harga ───────────────────────────────────────
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    "Total Pembayaran",
                    style      = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color      = TextPrimary
                )
                Text(
                    formatRupiah(order.totalPrice),
                    style      = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color      = GreenDeep
                )
            }

            Spacer(Modifier.height(20.dp))

            // ── Tombol Aksi ───────────────────────────────────────
            @Suppress("UNUSED_EXPRESSION")
            when (order.status) {
                OrderStatus.WAITING_PAYMENT -> {
                    Column {
                        Button(
                            onClick  = onPay,
                            enabled  = !isPayingThis,
                            modifier = Modifier.fillMaxWidth(),
                            shape    = RoundedCornerShape(14.dp),
                            colors   = ButtonDefaults.buttonColors(containerColor = GreenDeep)
                        ) {
                            if (isPayingThis) {
                                CircularProgressIndicator(
                                    modifier    = Modifier.size(18.dp),
                                    color       = Color.White,
                                    strokeWidth = 2.dp
                                )
                                Spacer(Modifier.width(8.dp))
                                Text("Memproses...", color = Color.White, fontWeight = FontWeight.Bold)
                            } else {
                                Text("💳  Bayar Sekarang", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        OutlinedButton(
                            onClick  = onCancel,
                            modifier = Modifier.fillMaxWidth(),
                            shape    = RoundedCornerShape(14.dp),
                            border   = BorderStroke(1.dp, StatusCancelled)
                        ) {
                            Text(
                                "❌  Batalkan Pesanan",
                                color      = StatusCancelled,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
                OrderStatus.SEARCHING -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = Color(0xFF1565C0).copy(alpha = 0.08f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "🔍 Sedang mencari kurir terdekat...",
                            color      = Color(0xFF1565C0),
                            fontSize   = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                OrderStatus.PROCESSING -> {
                    OutlinedButton(
                        onClick  = onCancel,
                        modifier = Modifier.fillMaxWidth(),
                        shape    = RoundedCornerShape(14.dp),
                        border   = BorderStroke(1.dp, StatusCancelled)
                    ) {
                        Text(
                            "❌  Batalkan Pesanan",
                            color      = StatusCancelled,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                OrderStatus.SHIPPED -> {
                    Button(
                        onClick  = {},
                        modifier = Modifier.fillMaxWidth(),
                        shape    = RoundedCornerShape(14.dp),
                        colors   = ButtonDefaults.buttonColors(containerColor = GreenDeep)
                    ) {
                        Text("🚚  Lacak Pengiriman", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
                OrderStatus.DELIVERED -> {
                    Button(
                        onClick  = onDismiss,
                        modifier = Modifier.fillMaxWidth(),
                        shape    = RoundedCornerShape(14.dp),
                        colors   = ButtonDefaults.buttonColors(containerColor = GreenDeep)
                    ) {
                        Text("✅  Pesanan Selesai", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
                OrderStatus.CANCELLED -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = StatusCancelled.copy(alpha = 0.08f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "❌ Pesanan ini telah dibatalkan",
                            color     = StatusCancelled,
                            fontSize  = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                else -> {}
            }
        }
    }
}

@Composable
private fun OrderDetailRow(emoji: String, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment     = Alignment.Top
    ) {
        Text(emoji, fontSize = 15.sp, modifier = Modifier.width(22.dp))
        Text(
            label,
            style      = MaterialTheme.typography.bodySmall,
            color      = TextSecondary,
            modifier   = Modifier.width(82.dp)
        )
        Text(
            value,
            style      = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color      = TextPrimary,
            modifier   = Modifier.weight(1f)
        )
    }
}

// ─────────────────────────────────────────────────────────────────
// CartGroupCard — single card for a multi-item cart checkout
// ─────────────────────────────────────────────────────────────────
@Composable
private fun CartGroupCard(
    group: CartCheckoutGroup,
    onPay: () -> Unit,
    onCancel: () -> Unit
) {
    val canPay    = group.paymentStatus == "unpaid" && group.orderStatus == OrderStatus.WAITING_PAYMENT
    val canCancel = group.orderStatus !in listOf(OrderStatus.CANCELLED, OrderStatus.DELIVERED)
    var showCancelDialog by remember { mutableStateOf(false) }

    Card(
        shape     = RoundedCornerShape(14.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(3.dp),
        modifier  = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // ── Header ──────────────────────────────────────────────
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🛒", style = MaterialTheme.typography.bodyLarge)
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "Cart Checkout",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                // Payment status badge
                val (badgeColor, badgeText) = when (group.paymentStatus) {
                    "paid"    -> Color(0xFF2E7D32) to "✅ Dibayar"
                    "expired" -> Color(0xFF9E9E9E) to "⏰ Kadaluarsa"
                    else      -> Color(0xFFFFA000) to "⏳ Belum Bayar"
                }
                Surface(
                    shape  = RoundedCornerShape(20.dp),
                    color  = badgeColor.copy(alpha = 0.12f)
                ) {
                    Text(
                        badgeText,
                        color  = badgeColor,
                        style  = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // ── Order status ─────────────────────────────────────────
            Text(
                "${group.orderStatus.emoji} ${group.orderStatus.label}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            HorizontalDivider()

            // ── Product thumbnails ────────────────────────────────────
            Text(
                "${group.orders.size} produk:",
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.horizontalScroll(rememberScrollState())
            ) {
                group.orders.take(5).forEach { order ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.width(64.dp)
                    ) {
                        AsyncImage(
                            model = order.product.imageUrl,
                            contentDescription = order.product.name,
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFEEEEEE)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            order.product.name,
                            style = MaterialTheme.typography.labelSmall,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            color = Color.DarkGray
                        )
                    }
                }
                if (group.orders.size > 5) {
                    Box(
                        Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFEEEEEE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("+${group.orders.size - 5}", fontWeight = FontWeight.Bold, color = Color.Gray)
                    }
                }
            }

            HorizontalDivider()

            // ── Total ─────────────────────────────────────────────────
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Total Pembayaran", fontWeight = FontWeight.SemiBold)
                Text(
                    "Rp ${"%,d".format(group.total)}",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32),
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            // ── Alamat ───────────────────────────────────────────────
            if (group.shippingAddress.isNotBlank()) {
                Text(
                    "📍 ${group.shippingAddress}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // ── Action buttons ────────────────────────────────────────
            if (canPay || canCancel) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (canCancel) {
                        OutlinedButton(
                            onClick = { showCancelDialog = true },
                            modifier = Modifier.weight(1f),
                            shape  = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                        ) { Text("Batalkan") }
                    }
                    if (canPay) {
                        Button(
                            onClick = onPay,
                            modifier = Modifier.weight(1f),
                            shape  = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                        ) { Text("💳 Bayar") }
                    }
                }
            }
        }
    }

    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title   = { Text("Batalkan Semua?") },
            text    = { Text("Semua ${group.orders.size} produk dalam checkout ini akan dibatalkan.") },
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

// ─────────────────────────────────────────────────────────────────
// Summary banner
// ─────────────────────────────────────────────────────────────────
@Composable
fun OrderSummaryBanner(orders: List<Order>) {
    val total     = orders.size
    val active    = orders.count { it.status in listOf(OrderStatus.SEARCHING, OrderStatus.PROCESSING, OrderStatus.SHIPPED, OrderStatus.WAITING_PAYMENT) }
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
fun OrderCard(
    order: Order,
    onPay: () -> Unit = {},
    onCancel: () -> Unit = {},
    onOrderClick: () -> Unit = {},
    isPayingThis: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOrderClick() },
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
                // Product image / emoji fallback
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    if (!order.product.imageUrl.isNullOrBlank()) {
                        AsyncImage(
                            model              = order.product.imageUrl,
                            contentDescription = order.product.name,
                            contentScale       = ContentScale.Crop,
                            modifier           = Modifier.fillMaxSize()
                        )
                    } else {
                        Text(
                            text = order.product.category.emoji(),
                            fontSize = 30.sp
                        )
                    }
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
                OrderActionButton(status = order.status, onPay = onPay, onCancel = onCancel, isPayingThis = isPayingThis)
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
        OrderStatus.SEARCHING       -> Pair(Color(0xFF1565C0).copy(alpha = 0.12f), Color(0xFF1565C0))
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
fun OrderActionButton(
    status: OrderStatus,
    onPay: () -> Unit = {},
    onCancel: () -> Unit = {},
    isPayingThis: Boolean = false
) {
    when (status) {
        OrderStatus.WAITING_PAYMENT ->
            Column(horizontalAlignment = Alignment.End) {
                Button(
                    onClick = onPay,
                    enabled = !isPayingThis,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = StatusPending),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    if (isPayingThis) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(14.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Spacer(Modifier.width(6.dp))
                        Text("Memproses...", style = MaterialTheme.typography.labelSmall, color = Color.White)
                    } else {
                        Text("Bayar 💳", style = MaterialTheme.typography.labelSmall, color = Color.White)
                    }
                }
                TextButton(
                    onClick = onCancel,
                    enabled = !isPayingThis,
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
