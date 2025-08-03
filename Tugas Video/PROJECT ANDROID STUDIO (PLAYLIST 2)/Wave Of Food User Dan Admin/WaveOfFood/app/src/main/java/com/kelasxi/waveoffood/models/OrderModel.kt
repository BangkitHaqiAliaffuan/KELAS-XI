package com.kelasxi.waveoffood.models

/**
 * Data class untuk detail pesanan
 */
data class OrderModel(
    val orderId: String? = null,
    val userId: String = "",
    val userName: String = "",
    val userAddress: String = "",
    val userPhone: String? = null,
    val items: List<CartItemModel> = emptyList(),
    val totalPrice: String = "",
    val orderStatus: String = "Diproses",
    val orderTimestamp: Long = System.currentTimeMillis()
) {
    // Constructor kosong untuk kompatibilitas dengan Firebase Firestore
    constructor() : this(
        null, "", "", "", null, 
        emptyList(), "", "Diproses", System.currentTimeMillis()
    )
}
