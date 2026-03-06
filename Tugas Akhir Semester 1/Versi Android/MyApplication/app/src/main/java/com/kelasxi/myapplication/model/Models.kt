package com.kelasxi.myapplication.model

data class PickupRequest(
    val id: String,
    val date: String,
    val time: String,
    val address: String,
    val trashTypes: List<TrashType>,
    val status: PickupStatus,
    val notes: String = "",
    val estimatedWeightKg: Double? = null,
    val pointsAwarded: Int = 0,
    val completedAt: String? = null,
    val cancelledAt: String? = null,
    val cancellationReason: String? = null,
    val createdAt: String? = null,
    val courier: Courier? = null
)

data class Courier(
    val id: Long,
    val name: String,
    val phone: String? = null,
    val avatarUrl: String? = null,
    val vehicleType: String? = null,
    val vehiclePlate: String? = null,
    val rating: Double? = null,
    val status: String? = null
)

enum class PickupStatus(val label: String, val emoji: String) {
    SEARCHING("Mencari Kurir", "🔍"),
    PENDING("Menunggu", "🟡"),
    ON_THE_WAY("Dalam Perjalanan", "🔵"),
    DONE("Selesai", "✅"),
    CANCELLED("Dibatalkan", "❌")
}

enum class TrashType(val label: String, val emoji: String) {
    ORGANIC("Organic", "🌿"),
    PLASTIC("Plastic", "♻️"),
    ELECTRONIC("Electronic", "💻"),
    GLASS("Glass", "🫙")
}

data class Product(
    val id: String,
    val name: String,
    val price: Long,
    val sellerName: String,
    val sellerRating: Float,
    val description: String,
    val category: ProductCategory,
    val condition: ProductCondition,
    val imageUrl: String = "",
    val isWishlisted: Boolean = false,
    val isSold: Boolean = false
)

enum class ProductCategory(val label: String) {
    ALL("All"),
    FURNITURE("Furniture"),
    ELECTRONICS("Electronics"),
    CLOTHING("Clothing"),
    BOOKS("Books"),
    OTHERS("Others")
}

enum class ProductCondition(val label: String) {
    LIKE_NEW("Seperti Baru"),
    GOOD("Bekas Baik"),
    FAIR("Bekas Layak")
}

enum class OrderStatus(val label: String, val emoji: String) {
    WAITING_PAYMENT("Menunggu Pembayaran", "⏳"),
    PROCESSING("Diproses", "🔄"),
    SHIPPED("Dikirim", "🚚"),
    DELIVERED("Selesai", "✅"),
    CANCELLED("Dibatalkan", "❌")
}

data class Order(
    val id: String,
    val product: Product,
    val quantity: Int,
    val totalPrice: Long,
    val status: OrderStatus,
    val orderedAt: String,
    val estimatedArrival: String = "",
    val shippingAddress: String = "",
    // ── Mayar payment ─────────────────────────────────────────────
    val paymentStatus: String = "unpaid",
    val mayarPaymentLink: String? = null,
    val mayarPaymentId: String? = null,
    val paidAt: String? = null
)

data class UserProfile(
    val name: String,
    val email: String,
    val memberSince: String,
    val totalPickups: Int,
    val itemsSold: Int,
    val totalWeightKg: Float = 0f,
    val avatarUrl: String = ""
)

data class StatCard(
    val value: String,
    val label: String,
    val emoji: String
)

data class OnboardingPage(
    val emoji: String,
    val title: String,
    val description: String
)
