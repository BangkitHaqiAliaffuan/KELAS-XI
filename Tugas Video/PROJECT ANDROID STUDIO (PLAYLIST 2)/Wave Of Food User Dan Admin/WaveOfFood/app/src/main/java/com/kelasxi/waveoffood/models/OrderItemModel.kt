package com.kelasxi.waveoffood.models

/**
 * Data class untuk item dalam pesanan
 */
data class OrderItemModel(
    val foodId: String = "",
    val name: String = "",
    val foodName: String = "",
    val imageUrl: String = "",
    val foodImage: String = "",
    val price: Double = 0.0,
    val quantity: Int = 1,
    val specialInstructions: String = "",
    val notes: String = ""
) {
    // Constructor kosong untuk kompatibilitas dengan Firebase Firestore
    constructor() : this("", "", "", "", "", 0.0, 1, "", "")
    
    fun getTotalPrice(): Double {
        return price * quantity
    }
}
