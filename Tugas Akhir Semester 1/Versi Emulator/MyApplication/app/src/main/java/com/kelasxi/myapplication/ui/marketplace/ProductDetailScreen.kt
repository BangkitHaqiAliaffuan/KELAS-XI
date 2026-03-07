package com.kelasxi.myapplication.ui.marketplace

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.kelasxi.myapplication.model.*
import com.kelasxi.myapplication.ui.common.AddressPickerField
import com.kelasxi.myapplication.ui.theme.*
import com.kelasxi.myapplication.viewmodel.AddressViewModel
import com.kelasxi.myapplication.viewmodel.MarketplaceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    viewModel: MarketplaceViewModel = viewModel(),
    addressViewModel: AddressViewModel = viewModel(),
    onBack: () -> Unit = {},
    onOrderSuccess: () -> Unit = {}
) {
    val product         by viewModel.selectedProduct.collectAsStateWithLifecycle()
    val wishlist        by viewModel.wishlist.collectAsStateWithLifecycle()
    val isSubmitting    by viewModel.isSubmittingOrder.collectAsStateWithLifecycle()
    val isToggling      by viewModel.isTogglingWishlist.collectAsStateWithLifecycle()
    val orderSuccess    by viewModel.orderSuccess.collectAsStateWithLifecycle()
    val orderError      by viewModel.orderError.collectAsStateWithLifecycle()
    val wishlistError   by viewModel.wishlistError.collectAsStateWithLifecycle()
    val cartAddedMessage by viewModel.cartAddedMessage.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    var quantity          by remember { mutableIntStateOf(1) }
    var showBuyDialog     by remember { mutableStateOf(false) }
    var shippingAddress   by remember { mutableStateOf("") }

    // Clear any stale order success state from a previous order when this screen opens.
    // Without this, re-entering ProductDetail would immediately re-trigger navigation to MyOrders.
    DisposableEffect(Unit) {
        viewModel.clearOrderSuccess()
        onDispose { }
    }

    // Show snackbar on errors
    LaunchedEffect(orderError) {
        if (orderError != null) {
            snackbarHostState.showSnackbar(orderError!!)
            viewModel.dismissOrderError()
        }
    }
    LaunchedEffect(wishlistError) {
        if (wishlistError != null) {
            snackbarHostState.showSnackbar(wishlistError!!)
            viewModel.dismissWishlistError()
        }
    }
    LaunchedEffect(cartAddedMessage) {
        if (cartAddedMessage != null) {
            snackbarHostState.showSnackbar(cartAddedMessage!!)
            viewModel.dismissCartMessage()
        }
    }

    // Auto-navigate after order success; clear state so back-navigation doesn't re-trigger
    LaunchedEffect(orderSuccess) {
        if (orderSuccess != null) {
            viewModel.clearOrderSuccess()
            onOrderSuccess()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = BackgroundGreen
    ) { innerPadding ->

    product?.let { p ->
        val isWishlisted = wishlist.contains(p.id)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(BackgroundGreen)
        ) {
            // Top App Bar
            TopAppBar(
                title = {
                    Text(
                        text = p.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = GreenDeep
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleWishlist(p.id) }) {
                        Icon(
                            imageVector = if (isWishlisted) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Wishlist",
                            tint = if (isWishlisted) StatusCancelled else GreenDeep
                        )
                    }
                    IconButton(onClick = {}) {
                        Icon(
                            Icons.Outlined.Share,
                            contentDescription = "Share",
                            tint = GreenDeep
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceWhite
                )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Image Carousel
                ProductImageCarousel(product = p)

                // Content Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        // Price & Condition Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Rp ${formatPrice(p.price)}",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = OrangeDark
                            )
                            ConditionBadge(condition = p.condition)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = p.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Filled.Star,
                                contentDescription = null,
                                tint = OrangeAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${p.sellerRating} · Terjual 24",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = DividerColor)
                        Spacer(modifier = Modifier.height(16.dp))

                        // Seller Info
                        SellerInfoRow(sellerName = p.sellerName, rating = p.sellerRating)

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = DividerColor)
                        Spacer(modifier = Modifier.height(16.dp))

                        // Description
                        Text(
                            text = "Deskripsi Produk",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = p.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            lineHeight = 22.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Eco label
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SurfaceVariant, RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("♻️", fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Barang Daur Ulang",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = GreenDeep
                                )
                                Text(
                                    text = "Beli ini = kurangi sampah & hemat sumber daya",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Quantity Selector
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Jumlah",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            QuantitySelector(
                                quantity = quantity,
                                onDecrease = { if (quantity > 1) quantity-- },
                                onIncrease = { quantity++ }
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Add to Cart Button
                        OutlinedButton(
                            onClick = { viewModel.addToCart(p) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(24.dp),
                            border = BorderStroke(1.5.dp, GreenDeep)
                        ) {
                            Icon(Icons.Filled.AddShoppingCart, contentDescription = null, tint = GreenDeep)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Masukkan Keranjang",
                                color = GreenDeep,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Buy Now Button
                        Button(
                            onClick = { showBuyDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent),
                            enabled = !isSubmitting
                        ) {
                            if (isSubmitting) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(Icons.Filled.ShoppingBag, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Beli Sekarang",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleSmall
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Add to Wishlist Button
                        OutlinedButton(
                            onClick = { viewModel.toggleWishlist(p.id) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(24.dp),
                            border = BorderStroke(1.5.dp, if (isWishlisted) StatusCancelled else GreenDeep),
                            enabled = !isToggling
                        ) {
                            if (isToggling) {
                                CircularProgressIndicator(
                                    color = GreenDeep,
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = if (isWishlisted) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                    contentDescription = null,
                                    tint = if (isWishlisted) StatusCancelled else GreenDeep
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isWishlisted) "Hapus dari Wishlist" else "Tambah ke Wishlist 🤍",
                                    color = if (isWishlisted) StatusCancelled else GreenDeep,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleSmall
                                )
                            }
                        }
                    }
                }
            }
        }

        // ── Shipping Address Dialog ──────────────────────────────
        if (showBuyDialog) {
            AlertDialog(
                onDismissRequest = { if (!isSubmitting) showBuyDialog = false },
                icon = { Text("🛒", fontSize = 40.sp) },
                title = {
                    Text(
                        "Konfirmasi Pembelian",
                        fontWeight = FontWeight.Bold,
                        color = GreenDeep
                    )
                },
                text = {
                    Column {
                        Text(
                            text = "${p.name}  ×$quantity",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Total: Rp ${formatPrice(p.price * quantity)}",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = OrangeDark
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        AddressPickerField(
                            value = shippingAddress,
                            onValueChange = { shippingAddress = it },
                            addressViewModel = addressViewModel,
                            label = "Alamat Pengiriman"
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showBuyDialog = false
                            viewModel.createOrder(
                                productId       = p.id,
                                quantity        = quantity,
                                shippingAddress = shippingAddress,
                                notes           = null
                            )
                        },
                        enabled = shippingAddress.isNotBlank() && !isSubmitting,
                        colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Pesan Sekarang 🛍️", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showBuyDialog = false }) {
                        Text("Batal", color = TextSecondary)
                    }
                },
                shape = RoundedCornerShape(24.dp),
                containerColor = SurfaceWhite
            )
        }

    } ?: run {
        // Fallback if no product selected
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = GreenDeep)
        }
    }

    } // end Scaffold
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProductImageCarousel(product: Product) {
    if (!product.imageUrl.isNullOrBlank()) {
        // Show uploaded image
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .background(Color.White)
        ) {
            AsyncImage(
                model              = product.imageUrl,
                contentDescription = product.name,
                contentScale       = ContentScale.Crop,
                modifier           = Modifier.fillMaxSize()
            )
            // Condition badge
            Surface(
                color  = GreenDeep.copy(alpha = 0.85f),
                shape  = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
            ) {
                Text(
                    text     = product.condition.label,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    style    = MaterialTheme.typography.labelMedium,
                    color    = Color.White
                )
            }
        }
        return
    }

    // Fallback: emoji pager
    val pagerState = rememberPagerState(pageCount = { 3 })

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .background(Color.White)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.radialGradient(
                            colors = when (page) {
                                0 -> listOf(GreenPale, SurfaceVariant)
                                1 -> listOf(GreenLighter.copy(alpha = 0.3f), SurfaceVariant)
                                else -> listOf(GreenMedium.copy(alpha = 0.2f), SurfaceVariant)
                            }
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(product.category.emoji2(), fontSize = 80.sp)
                    if (page == 0) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            color = GreenDeep.copy(alpha = 0.85f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = product.condition.label,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // Page indicators
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            repeat(3) { index ->
                Box(
                    modifier = Modifier
                        .size(if (index == pagerState.currentPage) 20.dp else 8.dp, 8.dp)
                        .clip(CircleShape)
                        .background(
                            if (index == pagerState.currentPage) GreenDeep
                            else GreenPale
                        )
                )
            }
        }
    }
}

private fun ProductCategory.emoji2(): String = when (this) {
    ProductCategory.FURNITURE -> "🪑"
    ProductCategory.ELECTRONICS -> "💻"
    ProductCategory.CLOTHING -> "👕"
    ProductCategory.BOOKS -> "📚"
    ProductCategory.OTHERS -> "📦"
    ProductCategory.ALL -> "🛒"
}

@Composable
fun ConditionBadge(condition: ProductCondition) {
    val (bg, text) = when (condition) {
        ProductCondition.LIKE_NEW -> Pair(StatusDone.copy(alpha = 0.15f), StatusDone)
        ProductCondition.GOOD -> Pair(StatusOnTheWay.copy(alpha = 0.15f), StatusOnTheWay)
        ProductCondition.FAIR -> Pair(StatusPending.copy(alpha = 0.15f), OrangeDark)
    }
    Surface(color = bg, shape = RoundedCornerShape(20.dp)) {
        Text(
            text = condition.label,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            color = text,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun SellerInfoRow(sellerName: String, rating: Float) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(GreenDeep, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    sellerName.take(1).uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = sellerName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Star,
                        contentDescription = null,
                        tint = OrangeAccent,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = " $rating · Penjual Terpercaya",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }
        OutlinedButton(
            onClick = {},
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, GreenDeep),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
        ) {
            Icon(
                Icons.AutoMirrored.Outlined.Chat,
                contentDescription = null,
                tint = GreenDeep,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("Chat", color = GreenDeep, style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
fun QuantitySelector(
    quantity: Int,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(SurfaceVariant, RoundedCornerShape(20.dp))
            .padding(4.dp)
    ) {
        IconButton(
            onClick = onDecrease,
            modifier = Modifier
                .size(32.dp)
                .background(
                    if (quantity > 1) GreenDeep else GreenPale,
                    CircleShape
                )
        ) {
            Icon(
                Icons.Filled.Remove,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
        Text(
            text = "$quantity",
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        IconButton(
            onClick = onIncrease,
            modifier = Modifier
                .size(32.dp)
                .background(GreenDeep, CircleShape)
        ) {
            Icon(
                Icons.Filled.Add,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Preview(showBackground = true, name = "Product Detail")
@Composable
fun ProductDetailPreview() {
    TrashCareTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(androidx.compose.ui.graphics.Color.White),
            contentAlignment = Alignment.Center
        ) {
            Text("Product Detail Preview", style = MaterialTheme.typography.titleMedium)
        }
    }
}
