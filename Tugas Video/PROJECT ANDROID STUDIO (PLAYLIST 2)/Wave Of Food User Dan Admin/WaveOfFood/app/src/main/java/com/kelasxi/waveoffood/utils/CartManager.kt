package com.kelasxi.waveoffood.utils

import com.kelasxi.waveoffood.model.CartItemModel
import com.kelasxi.waveoffood.model.FoodModel

object CartManager {
    private val cartItems = mutableListOf<CartItemModel>()
    private val listeners = mutableListOf<CartUpdateListener>()
    
    interface CartUpdateListener {
        fun onCartUpdated()
    }
    
    fun addToCart(cartItem: CartItemModel) {
        val existingItem = cartItems.find { it.foodId == cartItem.foodId }
        
        if (existingItem != null) {
            existingItem.quantity += cartItem.quantity
        } else {
            cartItems.add(cartItem)
        }
        
        notifyListeners()
    }
    
    fun removeFromCart(cartItem: CartItemModel) {
        cartItems.remove(cartItem)
        notifyListeners()
    }
    
    fun updateQuantity(cartItem: CartItemModel, newQuantity: Int) {
        if (newQuantity <= 0) {
            removeFromCart(cartItem)
        } else {
            cartItem.quantity = newQuantity
            notifyListeners()
        }
    }
    
    fun getCartItems(): List<CartItemModel> {
        return cartItems.toList()
    }
    
    fun getCartItemCount(): Int {
        return cartItems.sumOf { it.quantity }
    }
    
    fun getCartTotal(): Long {
        return cartItems.sumOf { it.calculateSubtotal() }
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
