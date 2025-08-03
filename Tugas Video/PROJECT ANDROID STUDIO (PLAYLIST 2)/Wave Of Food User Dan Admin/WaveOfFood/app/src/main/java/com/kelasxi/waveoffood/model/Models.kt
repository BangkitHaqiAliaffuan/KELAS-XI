package com.kelasxi.waveoffood.model

import com.google.firebase.Timestamp
import java.util.*

/**
 * Enhanced Models for Firebase Integration
 * Compatible with professional design system
 */

// Category Model (Enhanced)
data class CategoryModel(
    val id: String = "",
    val name: String = "",
    val imageUrl: String = "",
    val isActive: Boolean = true,
    val createdAt: Timestamp? = null
)

// Food Model (Enhanced) 
data class FoodModel(
    val id: String = "",
    val name: String = "",
    val price: Long = 0, // Changed to Long for Firestore compatibility
    val description: String = "",
    val imageUrl: String = "",
    val categoryId: String = "",
    val isPopular: Boolean = false,
    val rating: Double = 0.0,
    val preparationTime: Int = 15, // in minutes
    val isAvailable: Boolean = true,
    val ingredients: List<String> = emptyList(),
    val nutritionInfo: NutritionInfo = NutritionInfo(),
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
) {
    // Helper method for price formatting
    fun getFormattedPrice(): String = "Rp ${String.format("%,d", price)}"
}

// Nutrition Info
data class NutritionInfo(
    val calories: Int = 0,
    val protein: Int = 0,
    val carbs: Int = 0,
    val fat: Int = 0
)

// Enhanced Cart Item Model
data class CartItemModel(
    val id: String = "",
    val foodId: String = "",
    val name: String = "",
    val price: Long = 0,
    val imageUrl: String = "",
    var quantity: Int = 1,
    val selectedSize: String = "Regular", // Small, Medium, Large
    val selectedExtras: List<ExtraItem> = emptyList(),
    val subtotal: Long = 0,
    val addedAt: Timestamp? = null
) {
    fun calculateSubtotal(): Long {
        val extrasTotal = selectedExtras.sumOf { it.price }
        return (price + extrasTotal) * quantity
    }
    
    fun getFormattedSubtotal(): String = "Rp ${String.format("%,d", calculateSubtotal())}"
}

// Extra Item (for add-ons)
data class ExtraItem(
    val name: String = "",
    val price: Long = 0
)

// Order Item Model (for order detail display)
data class OrderItemModel(
    val foodId: String = "",
    val foodName: String = "",
    val foodImage: String = "",
    val price: Long = 0,
    val quantity: Int = 1,
    val specialInstructions: String = ""
) {
    fun getTotalPrice(): Long = price * quantity
    fun getFormattedPrice(): String = "Rp ${String.format("%,d", price)}"
    fun getFormattedTotalPrice(): String = "Rp ${String.format("%,d", getTotalPrice())}"
}

// Enhanced User Model
data class UserModel(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val address: String = "",
    val phone: String = "",
    val profileImageUrl: String = "",
    val favoriteCategories: List<String> = emptyList(),
    val totalOrders: Int = 0,
    val loyaltyPoints: Int = 0,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
)

// Address Model
data class AddressModel(
    val id: String = "",
    val label: String = "", // Home, Office, etc.
    val address: String = "",
    val coordinates: GeoPoint = GeoPoint(),
    val isDefault: Boolean = false,
    val createdAt: Timestamp? = null
)

// GeoPoint for coordinates
data class GeoPoint(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)

// Enhanced Order Model
data class OrderModel(
    val orderId: String = "",
    val userId: String = "",
    val userName: String = "",
    val userPhone: String = "",
    val deliveryAddress: DeliveryAddress = DeliveryAddress(),
    val items: List<CartItemModel> = emptyList(),
    val subtotal: Long = 0,
    val deliveryFee: Long = 5000,
    val serviceFee: Long = 2000,
    val discount: Long = 0,
    val totalAmount: Long = 0,
    val paymentMethod: String = "Cash on Delivery",
    val orderStatus: String = "pending", // pending, confirmed, preparing, delivering, completed, cancelled
    val estimatedDelivery: Timestamp? = null,
    val actualDelivery: Timestamp? = null,
    val driverInfo: DriverInfo? = null,
    val rating: Double = 0.0,
    val review: String = "",
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
) {
    fun calculateTotal(): Long {
        return subtotal + deliveryFee + serviceFee - discount
    }
    
    fun getFormattedTotal(): String = "Rp ${String.format("%,d", calculateTotal())}"
    
    fun getOrderStatusDisplay(): String {
        return when (orderStatus) {
            "pending" -> "Menunggu Konfirmasi"
            "confirmed" -> "Dikonfirmasi"
            "preparing" -> "Sedang Disiapkan"
            "delivering" -> "Dalam Perjalanan"
            "completed" -> "Selesai"
            "cancelled" -> "Dibatalkan"
            else -> "Unknown"
        }
    }
}

// Delivery Address
data class DeliveryAddress(
    val address: String = "",
    val coordinates: GeoPoint = GeoPoint(),
    val instructions: String = ""
)

// Driver Info
data class DriverInfo(
    val name: String = "",
    val phone: String = "",
    val vehicleInfo: String = ""
)

// Promotion Model
data class PromotionModel(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val discountType: String = "percentage", // percentage, fixed
    val discountValue: Int = 0,
    val minOrder: Long = 0,
    val maxDiscount: Long = 0, // for percentage discounts
    val validFrom: Timestamp? = null,
    val validUntil: Timestamp? = null,
    val isActive: Boolean = true,
    val applicableCategories: List<String> = emptyList(),
    val usageLimit: Int = 0,
    val usedCount: Int = 0
) {
    fun isValidNow(): Boolean {
        val now = Date()
        val validFromDate = validFrom?.toDate()
        val validUntilDate = validUntil?.toDate()
        
        return isActive && 
               (validFromDate == null || now.after(validFromDate)) &&
               (validUntilDate == null || now.before(validUntilDate)) &&
               (usageLimit == 0 || usedCount < usageLimit)
    }
    
    fun getFormattedDiscount(): String {
        return if (discountType == "percentage") {
            "$discountValue%"
        } else {
            "Rp ${String.format("%,d", discountValue.toLong())}"
        }
    }
}

// Review Model
data class ReviewModel(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val foodId: String = "",
    val orderId: String = "",
    val rating: Double = 0.0,
    val comment: String = "",
    val images: List<String> = emptyList(),
    val isVerified: Boolean = false,
    val createdAt: Timestamp? = null
)
