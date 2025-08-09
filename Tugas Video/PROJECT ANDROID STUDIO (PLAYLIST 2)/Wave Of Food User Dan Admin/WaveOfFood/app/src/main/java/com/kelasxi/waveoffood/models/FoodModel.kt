package com.kelasxi.waveoffood.models

/**
 * Data class untuk item makanan (untuk adapter)
 */
data class FoodModel (
    var id: String = "",
    var name: String = "",
    var description: String = "",
    var imageUrl: String = "",
    var price: Long = 0,
    var categoryId: String = "",
    var isPopular: Boolean = false,
    var rating: Double = 0.0,
    var isAvailable: Boolean = true,
    var preparationTime: Int = 0
 ) {
    // Constructor kosong untuk kompatibilitas dengan Firebase Firestore
    constructor() : this("", "", "", "", 0, "", false, 0.0, true, 15)
    
    // Helper method for price formatting
    fun getFormattedPrice(): String = "Rp ${String.format("%,d", price)}"
}
