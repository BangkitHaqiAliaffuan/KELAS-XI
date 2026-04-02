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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kelasxi.myapplication.model.*
import com.kelasxi.myapplication.ui.theme.*
import com.kelasxi.myapplication.viewmodel.MarketplaceViewModel
import java.text.SimpleDateFormat
import java.util.Locale as JavaLocale
import java.util.Locale.forLanguageTag
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import java.text.NumberFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyShopScreen(
    viewModel: MarketplaceViewModel = viewModel(),
    onBack: () -> Unit = {},
    onAddListing: () -> Unit = {},
    onManage: (String) -> Unit = {}
) {
    val myListings       by viewModel.myListings.collectAsStateWithLifecycle()
    val isLoading        by viewModel.isLoadingMyListings.collectAsStateWithLifecycle()
    val error            by viewModel.myListingsError.collectAsStateWithLifecycle()
    val isDeleting       by viewModel.isDeletingListing.collectAsStateWithLifecycle()
    val deleteSuccess    by viewModel.deleteSuccess.collectAsStateWithLifecycle()
    val salesSummary        by viewModel.salesSummary.collectAsStateWithLifecycle()
    val salesTransactions  by viewModel.salesTransactions.collectAsStateWithLifecycle()
    val isLoadingSales     by viewModel.isLoadingSales.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    // ID listing yang sedang minta konfirmasi hapus
    var pendingDeleteId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadMyListings()
        viewModel.loadSalesTransactions()
    }

    LaunchedEffect(error) {
        if (error != null) {
            snackbarHostState.showSnackbar(error!!)
            viewModel.dismissMyListingsError()
        }
    }
    LaunchedEffect(deleteSuccess) {
        if (deleteSuccess != null) {
            snackbarHostState.showSnackbar(deleteSuccess!!)
            viewModel.dismissDeleteSuccess()
        }
    }

    // Confirm delete dialog
    pendingDeleteId?.let { id ->
        val listing = myListings.find { it.id == id }
        AlertDialog(
            onDismissRequest = { pendingDeleteId = null },
            icon = { Text("🗑️", fontSize = 36.sp) },
            title = {
                Text(
                    "Hapus Listing?",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        "Kamu akan menghapus:",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                    Text(
                        listing?.name ?: "",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = TextPrimary
                    )
                    Text(
                        "Listing akan dinonaktifkan dan tidak muncul di marketplace.",
                        fontSize = 12.sp,
                        color = TextHint
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteListing(id)
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

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick           = onAddListing,
                icon              = { Icon(Icons.Outlined.Add, contentDescription = null, tint = Color.White) },
                text              = { Text("Jual Barang", color = Color.White, fontWeight = FontWeight.Bold) },
                containerColor    = GreenDeep,
                contentColor      = Color.White,
                shape             = RoundedCornerShape(14.dp)
            )
        },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "🏪 Toko Saya",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                        Text(
                            "${myListings.size} produk",
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
                    IconButton(onClick = { viewModel.loadMyListings() }) {
                        Icon(
                            Icons.Outlined.Refresh,
                            contentDescription = "Refresh",
                            tint = Color.White
                        )
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
                        Text("Memuat produkmu...", color = TextSecondary)
                    }
                }
            }

            myListings.isEmpty() -> {
                MyShopEmptyState(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
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
                    // Stats summary banner
                    item {
                        MyShopStatsBanner(listings = myListings)
                    }

                    // Revenue banner dari Mayar
                    item {
                        ShopRevenueBanner(
                            summary       = salesSummary,
                            isLoading     = isLoadingSales,
                            onRefresh     = { viewModel.loadSalesTransactions() }
                        )
                    }

                    // Riwayat Transaksi
                    if (salesTransactions.isNotEmpty()) {
                        item {
                            Text(
                                "📋 Riwayat Transaksi",
                                fontWeight = FontWeight.Bold,
                                fontSize   = 14.sp,
                                color      = TextPrimary,
                                modifier   = Modifier.padding(top = 4.dp, bottom = 4.dp)
                            )
                        }
                        items(salesTransactions, key = { "trx_${it.id}" }) { trx ->
                            ShopTransactionCard(trx = trx)
                        }
                    }

                    // Listing cards
                    items(myListings, key = { "listing_${it.id}" }) { product ->
                        MyShopListingCard(
                            product   = product,
                            onManage  = { onManage(product.id) },
                            onDelete  = { pendingDeleteId = product.id }
                        )
                    }

                    item { Spacer(Modifier.height(16.dp)) }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────
// Stats banner
// ─────────────────────────────────────────────────────────────────
@Composable
private fun MyShopStatsBanner(listings: List<Product>) {
    val total  = listings.size
    val active = listings.count { !it.isSold }
    val sold   = listings.count { it.isSold }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = GreenDeep),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ShopStatItem(value = "$total",  label = "Total",  emoji = "📦")
            ShopStatDivider()
            ShopStatItem(value = "$active", label = "Aktif",  emoji = "🟢")
            ShopStatDivider()
            ShopStatItem(value = "$sold",   label = "Terjual", emoji = "✅")
        }
    }
}

@Composable
private fun ShopStatItem(value: String, label: String, emoji: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(emoji, fontSize = 20.sp)
        Spacer(Modifier.height(4.dp))
        Text(
            value,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 22.sp,
            color = Color.White
        )
        Text(
            label,
            fontSize = 11.sp,
            color = Color.White.copy(alpha = 0.8f)
        )
    }
}

@Composable
private fun ShopStatDivider() {
    Box(
        modifier = Modifier
            .height(40.dp)
            .width(1.dp)
            .background(Color.White.copy(alpha = 0.3f))
    )
}

// ─────────────────────────────────────────────────────────────────
// Revenue banner — keuntungan dari Mayar
// ─────────────────────────────────────────────────────────────────
@Composable
private fun ShopRevenueBanner(
    summary: SalesSummary,
    isLoading: Boolean,
    onRefresh: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("💰", fontSize = 18.sp)
                    Text(
                        "Pendapatan Penjualan",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextPrimary
                    )
                }
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = GreenDeep
                    )
                } else {
                    IconButton(
                        onClick = onRefresh,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            Icons.Outlined.Refresh,
                            contentDescription = "Refresh",
                            tint = TextHint,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Total revenue — angka besar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(GreenDeep, GreenMedium)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Column {
                    Text(
                        "Total Pendapatan",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Text(
                        formatRupiahShop(summary.totalRevenue),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 26.sp,
                        color = Color.White
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Breakdown row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                RevenueStatItem(
                    emoji  = "🧾",
                    value  = "${summary.totalTransactions}",
                    label  = "Total Transaksi",
                    color  = TextPrimary
                )
                Box(
                    modifier = Modifier
                        .height(36.dp)
                        .width(1.dp)
                        .background(DividerColor)
                )
                RevenueStatItem(
                    emoji  = "✅",
                    value  = "${summary.totalPaid}",
                    label  = "Lunas",
                    color  = StatusDone
                )
                Box(
                    modifier = Modifier
                        .height(36.dp)
                        .width(1.dp)
                        .background(DividerColor)
                )
                RevenueStatItem(
                    emoji  = "⏳",
                    value  = "${summary.totalUnpaid}",
                    label  = "Pending",
                    color  = StatusPending
                )
            }

            if (summary.totalTransactions == 0 && !isLoading) {
                Spacer(Modifier.height(8.dp))
                Text(
                    "Belum ada transaksi penjualan tercatat.",
                    fontSize = 12.sp,
                    color = TextHint,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun RevenueStatItem(emoji: String, value: String, label: String, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(emoji, fontSize = 16.sp)
        Text(
            value,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = color
        )
        Text(
            label,
            fontSize = 10.sp,
            color = TextSecondary
        )
    }
}

private fun formatRupiahShop(amount: Long): String {
    return try {
        "Rp " + NumberFormat.getNumberInstance(forLanguageTag("id-ID")).format(amount)
    } catch (_: Exception) {
        "Rp $amount"
    }
}

// ─────────────────────────────────────────────────────────────────
// Listing card — one card per product
// ─────────────────────────────────────────────────────────────────
@Composable
fun MyShopListingCard(
    product: Product,
    onManage: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (product.isSold) SurfaceVariant else SurfaceWhite
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                // Product thumbnail
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (product.isSold)
                                DividerColor
                            else
                                SurfaceVariant
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (!product.imageUrl.isNullOrBlank()) {
                        AsyncImage(
                            model              = product.imageUrl,
                            contentDescription = product.name,
                            contentScale       = ContentScale.Crop,
                            modifier           = Modifier.fillMaxSize()
                        )
                    } else {
                        Text(
                            product.category.shopEmoji(),
                            fontSize = 32.sp
                        )
                    }
                }

                // Info column
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Name + sold badge row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            product.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = if (product.isSold) TextHint else TextPrimary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        if (product.isSold) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = StatusDone.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    "TERJUAL",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = StatusDone,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }

                    // Price
                    Text(
                        formatShopRupiah(product.price),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        color = GreenDeep
                    )

                    // Category & condition chips
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        MiniChip(
                            text   = product.category.label,
                            color  = GreenMedium.copy(alpha = 0.15f),
                            tColor = GreenDeep
                        )
                        MiniChip(
                            text   = product.condition.label,
                            color  = OrangeAccent.copy(alpha = 0.12f),
                            tColor = OrangeDark
                        )
                    }

                    // Stock badge
                    Spacer(Modifier.height(2.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (product.stock == 0)
                            Color(0xFFFFEBEE) else Color(0xFFE8F5E9)
                    ) {
                        Text(
                            text = if (product.stock == 0) "⚠️ Sold Out" else "📦 Stok: ${product.stock}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (product.stock == 0) Color(0xFFC62828) else Color(0xFF2E7D32),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 10.dp),
                color = DividerColor
            )

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Manage button
                OutlinedButton(
                    onClick = onManage,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, GreenDeep),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    Icon(
                        Icons.Outlined.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp),
                        tint = GreenDeep
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "Manage",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = GreenDeep
                    )
                }

                // Delete button — disabled if already sold
                Button(
                    onClick = onDelete,
                    enabled = !product.isSold,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = StatusCancelled,
                        disabledContainerColor = DividerColor
                    ),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    Icon(
                        Icons.Outlined.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp),
                        tint = if (product.isSold) TextHint else Color.White
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "Hapus",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (product.isSold) TextHint else Color.White
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────
// Transaction card — riwayat transaksi penjualan
// ─────────────────────────────────────────────────────────────────
@Composable
fun ShopTransactionCard(trx: SalesTransaction) {
    val isPaid = trx.mayarStatus == "paid"
    val statusColor  = if (isPaid) Color(0xFF2E7D32) else Color(0xFFE65100)
    val statusBg     = if (isPaid) Color(0xFFE8F5E9) else Color(0xFFFFF3E0)
    val statusLabel  = if (isPaid) "✅ Lunas" else "⏳ Belum Bayar"

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(14.dp),
        colors    = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier  = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment     = Alignment.Top
        ) {
            // Status indicator
            Box(
                modifier          = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(statusBg),
                contentAlignment  = Alignment.Center
            ) {
                Text(if (isPaid) "✅" else "⏳", fontSize = 20.sp)
            }

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                // Product / description
                Text(
                    trx.description.ifBlank { "Produk" },
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 13.sp,
                    color      = TextPrimary,
                    maxLines   = 1,
                    overflow   = TextOverflow.Ellipsis
                )
                // Customer name
                Text(
                    "Pembeli: ${trx.customerName.ifBlank { "-" }}",
                    fontSize = 11.sp,
                    color    = TextSecondary
                )
                // Date
                if (trx.createdAt.isNotBlank()) {
                    Text(
                        formatTrxDate(trx.createdAt),
                        fontSize = 10.sp,
                        color    = TextHint
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Amount
                Text(
                    formatRupiahShop(trx.amount),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize   = 13.sp,
                    color      = GreenDeep
                )
                // Status badge
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = statusBg
                ) {
                    Text(
                        statusLabel,
                        fontSize  = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color     = statusColor,
                        modifier  = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }
        }
    }
}

private fun formatTrxDate(isoDate: String): String {
    return try {
        val input  = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", JavaLocale.US)
        val output = SimpleDateFormat("dd MMM yyyy, HH:mm", forLanguageTag("id-ID"))
        output.format(input.parse(isoDate)!!)
    } catch (_: Exception) {
        try {
            val input  = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX", JavaLocale.US)
            val output = SimpleDateFormat("dd MMM yyyy", forLanguageTag("id-ID"))
            output.format(input.parse(isoDate)!!)
        } catch (_: Exception) {
            isoDate.take(10)
        }
    }
}

// ─────────────────────────────────────────────────────────────────
// Mini chip helper
// ─────────────────────────────────────────────────────────────────
@Composable
private fun MiniChip(text: String, color: Color, tColor: Color) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = color
    ) {
        Text(
            text,
            fontSize = 10.sp,
            color = tColor,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

// ─────────────────────────────────────────────────────────────────
// Empty state
// ─────────────────────────────────────────────────────────────────
@Composable
private fun MyShopEmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🏪", fontSize = 64.sp)
        Spacer(Modifier.height(16.dp))
        Text(
            "Tokomu Masih Kosong",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = TextPrimary
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Mulai jual barang bekasmu dan\ndapatkan uang tambahan!",
            fontSize = 14.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
    }
}

// ─────────────────────────────────────────────────────────────────
// Helper functions
// ─────────────────────────────────────────────────────────────────
private fun formatShopRupiah(amount: Long): String {
    val chars = amount.toString().reversed()
    val formatted = StringBuilder()
    for ((i, c) in chars.withIndex()) {
        if (i > 0 && i % 3 == 0) formatted.append('.')
        formatted.append(c)
    }
    return "Rp ${formatted.reverse()}"
}

private fun ProductCategory.shopEmoji(): String = when (this) {
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
private fun MyShopScreenPreview() {
    TrashCareTheme {
        Scaffold(
            topBar = {
                @OptIn(ExperimentalMaterial3Api::class)
                TopAppBar(
                    title = {
                        Column {
                            Text("🏪 Toko Saya", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 18.sp)
                            Text("2 produk", fontSize = 12.sp, color = Color.White.copy(alpha = 0.75f))
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = GreenDeep)
                )
            },
            containerColor = BackgroundGreen
        ) { p ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(p),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    val demoListings = listOf(
                        Product(
                            id = "1", name = "Kursi Kayu Antik",
                            price = 250_000, sellerName = "Aku",
                            sellerRating = 5f, description = "",
                            category = ProductCategory.FURNITURE,
                            condition = ProductCondition.GOOD,
                            isSold = false
                        ),
                        Product(
                            id = "2", name = "Laptop Bekas Core i5",
                            price = 3_500_000, sellerName = "Aku",
                            sellerRating = 5f, description = "",
                            category = ProductCategory.ELECTRONICS,
                            condition = ProductCondition.LIKE_NEW,
                            isSold = true
                        )
                    )
                    MyShopStatsBanner(listings = demoListings)
                }
                item {
                    MyShopListingCard(
                        product = Product(
                            id = "1", name = "Kursi Kayu Antik",
                            price = 250_000, sellerName = "Aku",
                            sellerRating = 5f, description = "",
                            category = ProductCategory.FURNITURE,
                            condition = ProductCondition.GOOD,
                            isSold = false
                        ),
                        onManage = {}, onDelete = {}
                    )
                }
                item {
                    MyShopListingCard(
                        product = Product(
                            id = "2", name = "Laptop Bekas Core i5",
                            price = 3_500_000, sellerName = "Aku",
                            sellerRating = 5f, description = "",
                            category = ProductCategory.ELECTRONICS,
                            condition = ProductCondition.LIKE_NEW,
                            isSold = true
                        ),
                        onManage = {}, onDelete = {}
                    )
                }
            }
        }
    }
}
