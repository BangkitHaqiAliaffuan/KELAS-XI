package com.kelasxi.myapplication.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kelasxi.myapplication.model.Product
import com.kelasxi.myapplication.model.ProductCategory
import com.kelasxi.myapplication.model.ProductCondition
import com.kelasxi.myapplication.ui.theme.*
import com.kelasxi.myapplication.viewmodel.MarketplaceViewModel

// ─── Price formatter ──────────────────────────────────────────────────────────
private fun formatPrice(price: Long): String {
    val str = price.toString()
    val result = StringBuilder()
    str.forEachIndexed { i, c ->
        if (i > 0 && (str.length - i) % 3 == 0) result.append('.')
        result.append(c)
    }
    return "Rp $result"
}

// ─── Category emoji ───────────────────────────────────────────────────────────
private fun ProductCategory.wishlistEmoji(): String = when (this) {
    ProductCategory.ELECTRONICS -> "💻"
    ProductCategory.FURNITURE   -> "🪑"
    ProductCategory.CLOTHING    -> "👗"
    ProductCategory.BOOKS       -> "📚"
    ProductCategory.ALL         -> "🛍️"
    ProductCategory.OTHERS      -> "📦"
}

// ─── Condition helpers ────────────────────────────────────────────────────────
private fun ProductCondition.displayLabel(): String = when (this) {
    ProductCondition.LIKE_NEW -> "Seperti Baru"
    ProductCondition.GOOD     -> "Bekas Baik"
    ProductCondition.FAIR     -> "Bekas Layak"
}

private fun ProductCondition.displayColor(): Color = when (this) {
    ProductCondition.LIKE_NEW -> Color(0xFF4CAF50)
    ProductCondition.GOOD     -> Color(0xFF03A9F4)
    ProductCondition.FAIR     -> Color(0xFFFF9800)
}

// ─────────────────────────────────────────────────────────────────────────────
//  WishlistScreen
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistScreen(
    viewModel: MarketplaceViewModel = viewModel(),
    onBack: () -> Unit = {},
    onProductClick: (Product) -> Unit = {}
) {
    val wishlistProducts  by viewModel.wishlistProducts.collectAsStateWithLifecycle()
    val isLoading         by viewModel.isLoadingWishlist.collectAsStateWithLifecycle()
    val wishlistError     by viewModel.wishlistError.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) { viewModel.loadWishlist() }

    LaunchedEffect(wishlistError) {
        if (wishlistError != null) {
            snackbarHostState.showSnackbar(wishlistError!!)
            viewModel.dismissWishlistError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Wishlist ❤\uFE0F",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            "${wishlistProducts.size} produk disimpan",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = GreenDeep)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Memuat wishlist...", color = TextSecondary)
                    }
                }
            }
            wishlistProducts.isEmpty() -> {
                WishlistEmpty(modifier = Modifier.padding(padding))
            }
            else -> {
                Column(modifier = Modifier.padding(padding)) {
                    WishlistBanner(count = wishlistProducts.size)
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(wishlistProducts, key = { it.id }) { product ->
                            WishlistProductCard(
                                product = product,
                                onCardClick = { onProductClick(product) },
                                onRemoveWishlist = { viewModel.toggleWishlist(product.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─── Stats Banner ─────────────────────────────────────────────────────────────

@Composable
private fun WishlistBanner(count: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xFFE91E63), Color(0xFFF48FB1))
                )
            )
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.Favorite,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
            Column {
                Text(
                    "Produk Favorit Kamu ❤\uFE0F",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Text(
                    "$count produk menunggu untuk dibeli",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 12.sp
                )
            }
        }
    }
}

// ─── Product Card ─────────────────────────────────────────────────────────────

@Composable
private fun WishlistProductCard(
    product: Product,
    onCardClick: () -> Unit,
    onRemoveWishlist: () -> Unit
) {
    var addedToCart by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onCardClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Image placeholder
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFFF3F4F6), Color(0xFFE5E7EB))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(product.category.wishlistEmoji(), fontSize = 52.sp)
                }

                // Info
                Column(
                    modifier = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        product.name,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 16.sp
                    )

                    // Condition badge
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = product.condition.displayColor().copy(alpha = 0.12f)
                    ) {
                        Text(
                            product.condition.displayLabel(),
                            color = product.condition.displayColor(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    // Price
                    Text(
                        formatPrice(product.price),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(Modifier.height(2.dp))

                    // Add to cart button
                    Button(
                        onClick = { addedToCart = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (addedToCart) Color(0xFF4CAF50)
                            else MaterialTheme.colorScheme.primary
                        ),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            Icons.Filled.ShoppingCart,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            if (addedToCart) "Ditambahkan ✓" else "Tambah Keranjang",
                            fontSize = 11.sp,
                            maxLines = 1
                        )
                    }
                }
            }

            // Remove (heart) button
            IconButton(
                onClick = onRemoveWishlist,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.85f))
            ) {
                Icon(
                    Icons.Filled.Favorite,
                    contentDescription = "Hapus dari Wishlist",
                    tint = Color(0xFFE91E63),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

// ─── Empty State ──────────────────────────────────────────────────────────────

@Composable
private fun WishlistEmpty(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🤍", fontSize = 72.sp)
        Spacer(Modifier.height(16.dp))
        Text(
            "Wishlist Kosong",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Tambahkan produk favoritmu ke wishlist\nagar mudah ditemukan nanti",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )
    }
}

// ─── Preview ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true, name = "Wishlist Empty")
@Composable
private fun WishlistEmptyPreview() {
    TrashCareTheme {
        WishlistEmpty()
    }
}

