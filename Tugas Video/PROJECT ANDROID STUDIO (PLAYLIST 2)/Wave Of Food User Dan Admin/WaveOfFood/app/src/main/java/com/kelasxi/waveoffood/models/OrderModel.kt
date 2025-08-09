package com.kelasxi.waveoffood.models

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

/**
 * Data class untuk detail pesanan
 */
data class OrderModel(
    val id: String = "",
    val orderId: String = "",
    val userId: String = "",
    val userName: String = "",
    val userEmail: String = "",
    val userAddress: String = "",
    val userPhone: String = "",
    val items: List<OrderItemModel> = emptyList(),
    val subtotal: Double = 0.0,
    val deliveryFee: Double = 0.0,
    val totalAmount: Double = 0.0,
    val deliveryAddress: String = "",
    val paymentMethod: String = "",
    val orderStatus: String = "pending",
    val status: String = "processing",
    val createdAt: Timestamp = Timestamp.now(),
    val estimatedDelivery: String = "",
    val notes: String = "",
    val orderTimestamp: Long = System.currentTimeMillis(),
    val orderDate: Long = System.currentTimeMillis()
) {
    // Constructor kosong untuk kompatibilitas dengan Firebase Firestore
    constructor() : this("", "", "", "", "", "", "", emptyList(), 0.0, 0.0, 0.0, "", "", "pending", "processing", Timestamp.now(), "", "", System.currentTimeMillis(), System.currentTimeMillis())
    
    fun getOrderStatusDisplay(): String {
        return when (orderStatus.lowercase()) {
            "pending" -> "Menunggu Konfirmasi"
            "confirmed" -> "Dikonfirmasi"
            "preparing" -> "Sedang Diproses"
            "ready" -> "Siap Diantar"
            "delivering" -> "Sedang Diantar"
            "delivered" -> "Terkirim"
            "cancelled" -> "Dibatalkan"
            else -> orderStatus.replaceFirstChar { it.uppercase() }
        }
    }
    
    fun getFormattedCreatedAt(): String {
        return try {
            val date = createdAt.toDate()
            val formatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
            formatter.format(date)
        } catch (e: Exception) {
            ""
        }
    }
}
