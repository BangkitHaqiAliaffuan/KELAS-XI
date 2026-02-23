package com.kelasxi.myapplication.model

data class PickupRequest(
    val id: String,
    val date: String,
    val time: String,
    val address: String,
    val trashTypes: List<TrashType>,
    val status: PickupStatus,
    val notes: String = ""
)

enum class PickupStatus(val label: String, val emoji: String) {
    PENDING("Pending", "🟡"),
    ON_THE_WAY("On the Way", "🔵"),
    DONE("Done", "✅"),
    CANCELLED("Cancelled", "❌")
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
    val isWishlisted: Boolean = false
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

data class UserProfile(
    val name: String,
    val email: String,
    val memberSince: String,
    val totalPickups: Int,
    val itemsSold: Int,
    val co2Saved: Float,
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
