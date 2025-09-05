package com.kelasxi.waveoffood.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.kelasxi.waveoffood.data.models.CartItem

class CartManager {
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()
    
    private val _cartCount = MutableStateFlow(0)
    val cartCount: StateFlow<Int> = _cartCount.asStateFlow()
    
    private val _cartTotal = MutableStateFlow(0L)
    val cartTotal: StateFlow<Long> = _cartTotal.asStateFlow()
    
    fun addToCart(item: CartItem) {
        val currentItems = _cartItems.value.toMutableList()
        val existingItemIndex = currentItems.indexOfFirst { it.foodId == item.foodId }
        
        if (existingItemIndex >= 0) {
            // Update quantity if item already exists
            val existingItem = currentItems[existingItemIndex]
            currentItems[existingItemIndex] = existingItem.copy(
                quantity = existingItem.quantity + item.quantity
            )
        } else {
            // Add new item
            currentItems.add(item)
        }
        
        updateCart(currentItems)
    }
    
    fun removeFromCart(foodId: String) {
        val currentItems = _cartItems.value.toMutableList()
        currentItems.removeAll { it.foodId == foodId }
        updateCart(currentItems)
    }
    
    fun updateQuantity(foodId: String, quantity: Int) {
        if (quantity <= 0) {
            removeFromCart(foodId)
            return
        }
        
        val currentItems = _cartItems.value.toMutableList()
        val itemIndex = currentItems.indexOfFirst { it.foodId == foodId }
        
        if (itemIndex >= 0) {
            currentItems[itemIndex] = currentItems[itemIndex].copy(quantity = quantity)
            updateCart(currentItems)
        }
    }
    
    fun clearCart() {
        updateCart(emptyList())
    }
    
    fun getCartItem(foodId: String): CartItem? {
        return _cartItems.value.find { it.foodId == foodId }
    }
    
    fun isInCart(foodId: String): Boolean {
        return _cartItems.value.any { it.foodId == foodId }
    }
    
    private fun updateCart(items: List<CartItem>) {
        _cartItems.value = items
        _cartCount.value = items.sumOf { it.quantity }
        _cartTotal.value = items.sumOf { it.price * it.quantity }
    }
    
    // Get cart summary for checkout
    fun getCartSummary(): CartSummary {
        val items = _cartItems.value
        val subtotal = items.sumOf { it.price * it.quantity }
        val deliveryFee = if (subtotal > 50000) 0L else 10000L // Free delivery over 50k
        val tax = (subtotal * 0.1).toLong() // 10% tax
        val total = subtotal + deliveryFee + tax
        
        return CartSummary(
            items = items,
            subtotal = subtotal,
            deliveryFee = deliveryFee,
            tax = tax,
            total = total,
            itemCount = items.sumOf { it.quantity }
        )
    }
}

data class CartSummary(
    val items: List<CartItem>,
    val subtotal: Long,
    val deliveryFee: Long,
    val tax: Long,
    val total: Long,
    val itemCount: Int
)
