package com.kelasxi.waveoffood.ui.screens.cart

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.kelasxi.waveoffood.data.models.CartItem
import com.kelasxi.waveoffood.data.repository.CartSummary
import com.kelasxi.waveoffood.ui.theme.*
import com.kelasxi.waveoffood.ui.viewmodels.CartViewModel
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCheckout: (CartSummary) -> Unit,
    cartViewModel: CartViewModel = viewModel()
) {
    val cartItems by cartViewModel.cartItems.collectAsState()
    val cartSummary = cartViewModel.getCartSummary()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Keranjang Belanja",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            if (cartItems.isNotEmpty()) {
                CartBottomBar(
                    cartSummary = cartSummary,
                    onCheckout = { onNavigateToCheckout(cartSummary) }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (cartItems.isEmpty()) {
                EmptyCartContent()
            } else {
                CartContent(
                    cartItems = cartItems,
                    cartViewModel = cartViewModel,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun EmptyCartContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.ShoppingCart,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.outline
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Keranjang Kosong",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Text(
            text = "Tambahkan makanan favorit Anda ke keranjang",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
private fun CartContent(
    cartItems: List<CartItem>,
    cartViewModel: CartViewModel,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = cartItems,
            key = { it.foodId }
        ) { cartItem ->
            CartItemCard(
                cartItem = cartItem,
                onQuantityChanged = { newQuantity ->
                    cartViewModel.updateQuantity(cartItem.foodId, newQuantity)
                },
                onRemoveItem = {
                    cartViewModel.removeFromCart(cartItem.foodId)
                }
            )
        }
        
        item {
            Spacer(modifier = Modifier.height(80.dp)) // Space for bottom bar
        }
    }
}

@Composable
private fun CartItemCard(
    cartItem: CartItem,
    onQuantityChanged: (Int) -> Unit,
    onRemoveItem: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Food Image
            AsyncImage(
                model = cartItem.imageUrl,
                contentDescription = cartItem.name,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Food Details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = cartItem.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                if (cartItem.description.isNotEmpty()) {
                    Text(
                        text = cartItem.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = formatCurrency(cartItem.price),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryColor
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // Quantity Controls & Remove Button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Remove button
                IconButton(
                    onClick = onRemoveItem,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Hapus item",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Quantity controls
                QuantitySelector(
                    quantity = cartItem.quantity,
                    onQuantityChanged = onQuantityChanged,
                    minQuantity = 1,
                    maxQuantity = 99
                )
            }
        }
    }
}

@Composable
private fun QuantitySelector(
    quantity: Int,
    onQuantityChanged: (Int) -> Unit,
    minQuantity: Int = 1,
    maxQuantity: Int = 99
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Decrease button
        IconButton(
            onClick = { 
                if (quantity > minQuantity) {
                    onQuantityChanged(quantity - 1)
                }
            },
            enabled = quantity > minQuantity,
            modifier = Modifier
                .size(32.dp)
                .background(
                    color = if (quantity > minQuantity) PrimaryColor else MaterialTheme.colorScheme.outline,
                    shape = CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = "Kurangi",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
        
        // Quantity display
        Text(
            text = quantity.toString(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.widthIn(min = 24.dp)
        )
        
        // Increase button
        IconButton(
            onClick = { 
                if (quantity < maxQuantity) {
                    onQuantityChanged(quantity + 1)
                }
            },
            enabled = quantity < maxQuantity,
            modifier = Modifier
                .size(32.dp)
                .background(
                    color = if (quantity < maxQuantity) PrimaryColor else MaterialTheme.colorScheme.outline,
                    shape = CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Tambah",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun CartBottomBar(
    cartSummary: CartSummary,
    onCheckout: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Order Summary
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${cartSummary.itemCount} item",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = formatCurrency(cartSummary.total),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryColor
                    )
                }
                
                Button(
                    onClick = onCheckout,
                    modifier = Modifier
                        .height(48.dp)
                        .widthIn(min = 120.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryColor
                    ),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text(
                        text = "Checkout",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

private fun formatCurrency(amount: Long): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    return formatter.format(amount)
}
