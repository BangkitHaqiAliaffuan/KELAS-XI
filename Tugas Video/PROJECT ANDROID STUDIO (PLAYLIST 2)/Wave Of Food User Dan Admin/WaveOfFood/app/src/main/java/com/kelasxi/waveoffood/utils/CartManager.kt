package com.kelasxi.waveoffood.utils

import com.kelasxi.waveoffood.models.CartItemModel
import com.kelasxi.waveoffood.models.FoodItemModel
import android.util.Log

object CartManager {
    private val cartItems = mutableListOf<CartItemModel>()
    private val listeners = mutableListOf<CartUpdateListener>()

    interface CartUpdateListener {
        fun onCartUpdated()
    }

    fun addToCart(cartItem: CartItemModel) {
        Log.d("CartManager", "Adding to cart: ${cartItem.foodName}")
        
        // Use foodName as identifier instead of random ID to prevent duplicates
        val existingItem = cartItems.find { it.foodName == cartItem.foodName }
        
        if (existingItem != null) {
            val index = cartItems.indexOf(existingItem)
            cartItems[index] = existingItem.copy(quantity = existingItem.quantity + cartItem.quantity)
            Log.d("CartManager", "Updated existing item: ${cartItem.foodName}, new quantity: ${cartItems[index].quantity}")
        } else {
            cartItems.add(cartItem)
            Log.d("CartManager", "Added new item: ${cartItem.foodName}")
        }
        
        Log.d("CartManager", "Total items in cart: ${cartItems.size}")
        notifyListeners()
    }    fun removeFromCart(cartItem: CartItemModel) {
        cartItems.remove(cartItem)
        notifyListeners()
    }
    
    fun updateQuantity(cartItem: CartItemModel, newQuantity: Int) {
        if (newQuantity <= 0) {
            removeFromCart(cartItem)
        } else {
            val index = cartItems.indexOf(cartItem)
            if (index != -1) {
                cartItems[index] = cartItem.copy(quantity = newQuantity)
            }
            notifyListeners()
        }
    }
    
    fun getCartItems(): List<CartItemModel> {
        Log.d("CartManager", "Getting cart items: ${cartItems.size} items")
        cartItems.forEachIndexed { index, item ->
            Log.d("CartManager", "Item $index: ${item.foodName} x ${item.quantity}")
        }
        return cartItems.toList()
    }
    
    fun getCartItemCount(): Int {
        return cartItems.sumOf { it.quantity }
    }
    
    fun getCartTotal(): Double {
        return cartItems.sumOf { 
            val price = it.foodPrice.replace("Rp", "").replace(",", "").replace(".", "").trim().toDoubleOrNull() ?: 0.0
            price * it.quantity
        }
    }
    
    fun clearCart() {
        cartItems.clear()
        notifyListeners()
    }
    
    fun addListener(listener: CartUpdateListener) {
        listeners.add(listener)
    }
    
    fun removeListener(listener: CartUpdateListener) {
        listeners.remove(listener)
    }
    
    private fun notifyListeners() {
        listeners.forEach { it.onCartUpdated() }
    }
}
