package com.kelasxi.waveoffood.data.models

data class Food(
    val id: String = "",
    val categoryId: String = "",
    val createdAt: Any? = null, // Can handle both String and Timestamp
    val description: String = "",
    val imageUrl: String = "",
    val ingredients: List<String> = emptyList(),
    val isAvailable: Boolean = true,
    val isPopular: Boolean = false,
    val name: String = "",
    val nutritionInfo: NutritionInfo = NutritionInfo(),
    val preparationTime: Int = 0,
    val price: Long = 0,
    val rating: Double = 0.0,
    val updatedAt: Any? = null // Can handle both String and Timestamp
)

data class NutritionInfo(
    val calories: Int = 0,
    val carbs: Int = 0,
    val fat: Int = 0,
    val protein: Int = 0
)

data class Category(
    val id: String = "",
    val createdAt: Any? = null, // Can handle both String and Timestamp
    val imageUrl: String = "",
    val isActive: Boolean = true,
    val name: String = ""
)

data class User(
    val uid: String = "",
    val email: String = "",
    val name: String = "",
    val address: String? = null,
    val phone: String? = null,
    val profileImage: String? = null,
    val favoritefoods: List<String> = emptyList(), // List of food IDs
    val createdAt: Any? = null,
    val updatedAt: Any? = null
)

data class Order(
    val orderId: String = "",
    val userId: String = "",
    val customerName: String = "",
    val customerPhone: String = "",
    val customerAddress: String = "",
    val items: List<OrderItem> = emptyList(),
    val subtotal: Long = 0,
    val serviceFee: Long = 2000, // Service fee sebesar 2000
    val deliveryFee: Long = 10000, // Delivery fee sebesar 10000
    val total: Long = 0,
    val paymentMethod: String = "",
    val status: String = "pending",
    val timestamp: Any? = null // Can handle both String and Timestamp
)

data class OrderItem(
    val name: String = "",
    val price: Long = 0,
    val quantity: Int = 0,
    val imageUrl: String = ""
)

// Cart Item untuk session lokal (tidak disimpan ke Firebase)
data class CartItem(
    val foodId: String,
    val name: String,
    val price: Long,
    val quantity: Int,
    val imageUrl: String,
    val description: String = ""
)
