package com.kelasxi.myapplication.ui.marketplace

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.kelasxi.myapplication.model.CartItem
import com.kelasxi.myapplication.model.Product
import com.kelasxi.myapplication.ui.theme.*
import com.kelasxi.myapplication.viewmodel.MarketplaceViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    viewModel: MarketplaceViewModel,
    onBack: () -> Unit = {},
    onCheckout: (List<CartItem>) -> Unit = {}
) {
    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
    val cartTotal by viewModel.cartTotal.collectAsStateWithLifecycle()

    var showClearDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Keranjang Belanja",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        if (cartItems.isNotEmpty()) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                color = Color.White.copy(alpha = 0.25f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "${cartItems.sumOf { it.quantity }} item",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Kembali", tint = Color.White)
                    }
                },
                actions = {
                    if (cartItems.isNotEmpty()) {
                        IconButton(onClick = { showClearDialog = true }) {
                            Icon(Icons.Filled.DeleteSweep, contentDescription = "Kosongkan", tint = Color.White)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GreenDeep
                )
            )
        },
        bottomBar = {
            if (cartItems.isNotEmpty()) {
                CartBottomBar(
                    total = cartTotal,
                    itemCount = cartItems.sumOf { it.quantity },
                    onCheckout = { onCheckout(cartItems) }
                )
            }
        },
        containerColor = BackgroundGreen
    ) { padding ->
        if (cartItems.isEmpty()) {
            EmptyCartState(modifier = Modifier.padding(padding))
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(cartItems, key = { it.product.id }) { item ->
                    CartItemCard(
                        item = item,
                        onIncrease = { viewModel.updateCartQuantity(item.product.id, item.quantity + 1) },
                        onDecrease = { viewModel.updateCartQuantity(item.product.id, item.quantity - 1) },
                        onRemove = { viewModel.removeFromCart(item.product.id) }
                    )
                }
                item {
                    CartSummaryCard(items = cartItems, total = cartTotal)
                }
                item { Spacer(modifier = Modifier.height(8.dp)) }
            }
        }
    }

    // Confirm clear dialog
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            icon = { Text("🗑️", fontSize = 32.sp) },
            title = { Text("Kosongkan Keranjang?") },
            text = { Text("Semua item akan dihapus dari keranjang.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearCart()
                        showClearDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusCancelled)
                ) { Text("Hapus Semua") }
            },
            dismissButton = {
                OutlinedButton(onClick = { showClearDialog = false }) { Text("Batal") }
            }
        )
    }
}

// ── CartItemCard ───────────────────────────────────────────────────
@Composable
private fun CartItemCard(
    item: CartItem,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Thumbnail
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                if (!item.product.imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = item.product.imageUrl,
                        contentDescription = item.product.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text(
                        text = item.product.category.emoji(),
                        fontSize = 32.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Name + remove button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = item.product.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = onRemove,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            Icons.Filled.Close,
                            contentDescription = "Hapus",
                            tint = TextHint,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Seller
                Text(
                    text = "Oleh ${item.product.sellerName}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Price + qty control
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Rp ${formatPrice(item.product.price)}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = OrangeDark
                    )

                    // Quantity control
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        FilledIconButton(
                            onClick = onDecrease,
                            modifier = Modifier.size(28.dp),
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = if (item.quantity <= 1) StatusCancelled.copy(alpha = 0.15f)
                                                 else GreenPale,
                                contentColor = if (item.quantity <= 1) StatusCancelled else GreenDeep
                            )
                        ) {
                            Icon(
                                if (item.quantity <= 1) Icons.Filled.Delete else Icons.Filled.Remove,
                                contentDescription = "Kurangi",
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Text(
                            text = "${item.quantity}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            modifier = Modifier.widthIn(min = 24.dp),
                            textAlign = TextAlign.Center
                        )
                        FilledIconButton(
                            onClick = onIncrease,
                            modifier = Modifier.size(28.dp),
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = GreenDeep,
                                contentColor = Color.White
                            )
                        ) {
                            Icon(
                                Icons.Filled.Add,
                                contentDescription = "Tambah",
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }

                // Subtotal
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Subtotal: Rp ${formatCartRupiah(item.subtotal)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }
        }
    }
}

// ── CartSummaryCard ────────────────────────────────────────────────
@Composable
private fun CartSummaryCard(items: List<CartItem>, total: Long) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Ringkasan Pesanan",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))

            items.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${item.product.name} ×${item.quantity}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Rp ${formatCartRupiah(item.subtotal)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = GreenPale)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Rp ${formatCartRupiah(total)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = GreenDeep
                )
            }
        }
    }
}

// ── CartBottomBar ──────────────────────────────────────────────────
@Composable
private fun CartBottomBar(total: Long, itemCount: Int, onCheckout: () -> Unit) {
    Surface(
        shadowElevation = 12.dp,
        color = SurfaceWhite
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Total Pembayaran",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary
                    )
                    Text(
                        text = "Rp ${formatCartRupiah(total)}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = GreenDeep
                    )
                }
                Button(
                    onClick = onCheckout,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenDeep),
                    modifier = Modifier.height(48.dp)
                ) {
                    Icon(Icons.Filled.ShoppingCartCheckout, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Checkout ($itemCount)",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

// ── EmptyCartState ─────────────────────────────────────────────────
@Composable
private fun EmptyCartState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.ShoppingCart,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = GreenPale
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Keranjang Kosong",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Tambahkan produk dari Marketplace\nuntuk mulai berbelanja",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
    }
}

// ── Helpers ────────────────────────────────────────────────────────
private fun formatCartRupiah(amount: Long): String {
    return NumberFormat.getNumberInstance(Locale("id", "ID")).format(amount)
}

private fun com.kelasxi.myapplication.model.ProductCategory.emoji(): String = when (this) {
    com.kelasxi.myapplication.model.ProductCategory.FURNITURE    -> "🪑"
    com.kelasxi.myapplication.model.ProductCategory.ELECTRONICS  -> "💻"
    com.kelasxi.myapplication.model.ProductCategory.CLOTHING     -> "👕"
    com.kelasxi.myapplication.model.ProductCategory.BOOKS        -> "📚"
    com.kelasxi.myapplication.model.ProductCategory.OTHERS       -> "📦"
    com.kelasxi.myapplication.model.ProductCategory.ALL          -> "🛒"
}
