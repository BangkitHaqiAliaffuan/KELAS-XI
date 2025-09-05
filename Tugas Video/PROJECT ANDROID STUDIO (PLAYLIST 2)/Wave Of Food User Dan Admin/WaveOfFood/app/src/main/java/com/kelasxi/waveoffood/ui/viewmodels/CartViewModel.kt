package com.kelasxi.waveoffood.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kelasxi.waveoffood.data.models.CartItem
import com.kelasxi.waveoffood.data.models.Food
import com.kelasxi.waveoffood.data.repository.CartManager
import com.kelasxi.waveoffood.data.repository.CartSummary
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CartViewModel : ViewModel() {
    private val cartManager = CartManager()
    
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
}
