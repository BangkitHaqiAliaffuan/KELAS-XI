package com.kelasxi.waveoffood.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.kelasxi.waveoffood.ui.theme.WaveOfFoodTheme
import com.kelasxi.waveoffood.models.FoodModel

/**
 * Enhanced Cart Fragment with Material 3 Compose UI
 * Features item management, pricing calculations, and checkout flow
 */
class CartFragmentWithCompose : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                WaveOfFoodTheme {
                    CartScreen()
                }
            }
        }
    }
}

data class CartItem(
    val food: FoodModel,
    var quantity: Int = 1
) {
    val totalPrice: Long get() = food.price * quantity
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CartScreen() {
    var cartItems by remember { mutableStateOf(createSampleCartData()) }
    var promoCode by remember { mutableStateOf("") }
    var isPromoApplied by remember { mutableStateOf(false) }
    var deliveryMethod by remember { mutableStateOf("Standard") }

    val subtotal = cartItems.sumOf { it.totalPrice }
    val deliveryFee = when (deliveryMethod) {
        "Express" -> 500L
        "Standard" -> 200L
        else -> 0L
    }
    val discount = if (isPromoApplied) (subtotal * 0.1).toLong() else 0L
    val total = subtotal + deliveryFee - discount

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Header
        CartHeader(itemCount = cartItems.sumOf { it.quantity })
        
        Spacer(modifier = Modifier.height(20.dp))
        
        if (cartItems.isEmpty()) {
            EmptyCartSection()
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Cart Items
                items(cartItems) { cartItem ->
                    CartItemCard(
                        cartItem = cartItem,
                        onQuantityChanged = { newQuantity ->
                            if (newQuantity <= 0) {
                                cartItems = cartItems.filter { it.food.id != cartItem.food.id }
                            } else {
                                cartItems = cartItems.map { item ->
                                    if (item.food.id == cartItem.food.id) {
                                        item.copy(quantity = newQuantity)
                                    } else item
                                }
                            }
                        },
                        onRemove = {
                            cartItems = cartItems.filter { it.food.id != cartItem.food.id }
                        }
                    )
                }
                
                item { Spacer(modifier = Modifier.height(16.dp)) }
                
                // Delivery Options
                item {
                    DeliveryOptionsSection(
                        selectedMethod = deliveryMethod,
                        onMethodSelected = { deliveryMethod = it }
                    )
                }
                
                item { Spacer(modifier = Modifier.height(16.dp)) }
                
                // Promo Code Section
                item {
                    PromoCodeSection(
                        promoCode = promoCode,
                        onPromoCodeChanged = { promoCode = it },
                        isApplied = isPromoApplied,
                        onApplyPromo = {
                            // Simple promo validation
                            isPromoApplied = promoCode.equals("WAVE10", ignoreCase = true)
                        }
                    )
                }
                
                item { Spacer(modifier = Modifier.height(100.dp)) }
            }
            
            // Bottom Summary and Checkout
            CartSummarySection(
                subtotal = subtotal,
                deliveryFee = deliveryFee,
                discount = discount,
                total = total,
                onCheckout = { /* Handle checkout */ }
            )
        }
    }
}

@Composable
private fun CartHeader(itemCount: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "My Cart",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "$itemCount items",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        IconButton(
            onClick = { /* Handle menu */ },
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "More options"
            )
        }
    }
}

@Composable
private fun CartItemCard(
    cartItem: CartItem,
    onQuantityChanged: (Int) -> Unit,
    onRemove: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Food Image
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(cartItem.food.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = cartItem.food.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
            
            // Food Details
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = cartItem.food.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = cartItem.food.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Price
                    Text(
                        text = cartItem.food.getFormattedPrice(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    // Quantity Controls
                    QuantitySelector(
                        quantity = cartItem.quantity,
                        onQuantityChanged = onQuantityChanged,
                        onRemove = onRemove
                    )
                }
            }
        }
    }
}

@Composable
private fun QuantitySelector(
    quantity: Int,
    onQuantityChanged: (Int) -> Unit,
    onRemove: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Decrease button
        IconButton(
            onClick = { 
                if (quantity <= 1) onRemove() else onQuantityChanged(quantity - 1)
            },
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            ),
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = if (quantity <= 1) Icons.Default.Delete else Icons.Default.Remove,
                contentDescription = if (quantity <= 1) "Remove" else "Decrease",
                modifier = Modifier.size(16.dp)
            )
        }
        
        // Quantity display
        Text(
            text = quantity.toString(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        // Increase button
        IconButton(
            onClick = { onQuantityChanged(quantity + 1) },
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Increase",
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun DeliveryOptionsSection(
    selectedMethod: String,
    onMethodSelected: (String) -> Unit
) {
    val deliveryOptions = listOf(
        "Pickup" to "Free",
        "Standard" to "$2.00",
        "Express" to "$5.00"
    )
    
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Delivery Options",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            deliveryOptions.forEach { (method, fee) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedMethod == method,
                        onClick = { onMethodSelected(method) },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = method,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = when (method) {
                                "Pickup" -> "Ready in 15-20 mins"
                                "Standard" -> "30-45 mins"
                                "Express" -> "15-25 mins"
                                else -> ""
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = fee,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun PromoCodeSection(
    promoCode: String,
    onPromoCodeChanged: (String) -> Unit,
    isApplied: Boolean,
    onApplyPromo: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Promo Code",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = promoCode,
                    onValueChange = onPromoCodeChanged,
                    label = { Text("Enter promo code") },
                    placeholder = { Text("WAVE10") },
                    enabled = !isApplied,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    )
                )
                
                if (isApplied) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Applied",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Button(
                        onClick = onApplyPromo,
                        enabled = promoCode.isNotBlank(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Apply")
                    }
                }
            }
            
            if (isApplied) {
                Text(
                    text = "✅ 10% discount applied!",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun CartSummarySection(
    subtotal: Long,
    deliveryFee: Long,
    discount: Long,
    total: Long,
    onCheckout: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Order Summary",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            // Subtotal
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Subtotal",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = formatPrice(subtotal),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            // Delivery Fee
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Delivery Fee",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (deliveryFee == 0L) "Free" else formatPrice(deliveryFee),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            // Discount
            if (discount > 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Discount",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "-${formatPrice(discount)}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            
            // Total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = formatPrice(total),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Checkout Button
            Button(
                onClick = onCheckout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    text = "Proceed to Checkout",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun EmptyCartSection() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "🛒",
                style = MaterialTheme.typography.displayLarge
            )
            Text(
                text = "Your cart is empty",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Add some delicious items to get started",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = { /* Navigate to menu */ },
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Browse Menu")
            }
        }
    }
}

private fun formatPrice(priceInCents: Long): String {
    return "$${String.format("%.2f", priceInCents / 100.0)}"
}

private fun createSampleCartData(): List<CartItem> {
    return listOf(
        CartItem(
            food = FoodModel().apply {
                id = "1"; name = "Margherita Pizza"; description = "Classic Italian pizza with fresh tomatoes and mozzarella"
                imageUrl = "https://picsum.photos/300/200?random=1"; price = 1299L; categoryId = "Pizza"
                isPopular = true; rating = 4.5; isAvailable = true; preparationTime = 20
            },
            quantity = 2
        ),
        CartItem(
            food = FoodModel().apply {
                id = "4"; name = "Classic Beef Burger"; description = "Juicy beef patty with lettuce, tomato, and cheese"
                imageUrl = "https://picsum.photos/300/200?random=4"; price = 899L; categoryId = "Burger"
                isPopular = true; rating = 4.4; isAvailable = true; preparationTime = 15
            },
            quantity = 1
        ),
        CartItem(
            food = FoodModel().apply {
                id = "11"; name = "Chocolate Cake"; description = "Rich chocolate cake with layers of frosting"
                imageUrl = "https://picsum.photos/300/200?random=11"; price = 699L; categoryId = "Dessert"
                isPopular = true; rating = 4.4; isAvailable = true; preparationTime = 5
            },
            quantity = 1
        )
    )
}
