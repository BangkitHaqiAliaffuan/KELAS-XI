package com.kelasxi.waveoffood.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import com.kelasxi.waveoffood.data.models.CartItem
import com.kelasxi.waveoffood.data.models.Food
import com.kelasxi.waveoffood.data.models.Order
import com.kelasxi.waveoffood.data.models.OrderItem
import com.kelasxi.waveoffood.data.repository.CartManager
import com.kelasxi.waveoffood.data.repository.CartSummary
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CartViewModel : ViewModel() {
    private val cartManager = CartManager()
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    
    val cartItems: StateFlow<List<CartItem>> = cartManager.cartItems
    val cartCount: StateFlow<Int> = cartManager.cartCount
    val cartTotal: StateFlow<Long> = cartManager.cartTotal
    
    fun addToCart(item: CartItem) {
        cartManager.addToCart(item)
    }
    
    fun removeFromCart(foodId: String) {
        cartManager.removeFromCart(foodId)
    }
    
    fun updateQuantity(foodId: String, quantity: Int) {
        cartManager.updateQuantity(foodId, quantity)
    }
    
    fun clearCart() {
        cartManager.clearCart()
    }
    
    fun getCartItem(foodId: String): CartItem? {
        return cartManager.getCartItem(foodId)
    }
    
    fun isInCart(foodId: String): Boolean {
        return cartManager.isInCart(foodId)
    }
    
    fun getCartSummary(): CartSummary {
        return cartManager.getCartSummary()
    }
    
    // Helper functions for easy integration
    fun addToCart(food: Food) {
        val cartItem = CartItem(
            foodId = food.id,
            name = food.name,
            price = food.price,
            quantity = 1,
            imageUrl = food.imageUrl,
            description = food.description
        )
        addToCart(cartItem)
    }
    
    fun addSingleItem(
        foodId: String,
        name: String,
        price: Long,
        imageUrl: String,
        description: String = ""
    ) {
        val cartItem = CartItem(
            foodId = foodId,
            name = name,
            price = price,
            quantity = 1,
            imageUrl = imageUrl,
            description = description
        )
        addToCart(cartItem)
    }
    
    fun incrementQuantity(foodId: String) {
        val currentItem = getCartItem(foodId)
        if (currentItem != null) {
            updateQuantity(foodId, currentItem.quantity + 1)
        }
    }
    
    fun decrementQuantity(foodId: String) {
        val currentItem = getCartItem(foodId)
        if (currentItem != null) {
            updateQuantity(foodId, currentItem.quantity - 1)
        }
    }
    
    // Place Order Function
    suspend fun placeOrder(
        customerName: String,
        customerPhone: String,
        customerAddress: String,
        paymentMethod: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val currentUser = auth.currentUser
            if (currentUser == null) {
                onError("User not authenticated")
                return
            }
            
            val cartSummary = getCartSummary()
            if (cartSummary.items.isEmpty()) {
                onError("Cart is empty")
                return
            }
            
            // Generate order ID
            val orderId = "ORDER_${System.currentTimeMillis()}"
            
            // Convert CartItems to OrderItems
            val orderItems = cartSummary.items.map { cartItem ->
                OrderItem(
                    name = cartItem.name,
                    price = cartItem.price,
                    quantity = cartItem.quantity,
                    imageUrl = cartItem.imageUrl
                )
            }
            
            // Calculate totals
            val subtotal = cartSummary.subtotal
            val serviceFee = 2000L
            val deliveryFee = 10000L
            val total = subtotal + serviceFee + deliveryFee
            
            // Create order object
            val order = Order(
                orderId = orderId,
                userId = currentUser.uid,
                customerName = customerName,
                customerPhone = customerPhone,
                customerAddress = customerAddress,
                items = orderItems,
                subtotal = subtotal,
                serviceFee = serviceFee,
                deliveryFee = deliveryFee,
                total = total,
                paymentMethod = paymentMethod,
                status = "pending",
                timestamp = FieldValue.serverTimestamp()
            )
            
            // Save order to Firestore
            firestore.collection("orders")
                .document(orderId)
                .set(order)
                .await()
            
            // Clear cart after successful order
            clearCart()
            
            onSuccess(orderId)
            
        } catch (e: Exception) {
            onError("Failed to place order: ${e.message}")
        }
    }
}
