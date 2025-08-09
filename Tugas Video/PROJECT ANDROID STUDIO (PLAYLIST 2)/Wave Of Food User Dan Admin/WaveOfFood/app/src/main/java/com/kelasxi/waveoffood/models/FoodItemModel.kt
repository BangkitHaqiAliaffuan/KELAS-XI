package com.kelasxi.waveoffood.models

import com.google.firebase.firestore.PropertyName
import java.util.Date

/**
 * Data class untuk item makanan sesuai dengan struktur Firestore
 */
data class FoodItemModel(
    var id: String = "",
    val name: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val categoryId: String = "",
    val price: Long = 0,
    val rating: Double = 0.0,
    val isPopular: Boolean = false,
    val isAvailable: Boolean = true,
    val preparationTime: Long = 0,
    val ingredients: List<String> = emptyList(),
    val nutritionInfo: NutritionInfo = NutritionInfo(),
    
    // Gunakan String untuk menghindari error parsing Date
    val createdAt: String? = null,
    val updatedAt: String? = null
) {
    // Constructor kosong untuk kompatibilitas dengan Firebase Firestore
    constructor() : this("", "", "", "", "", 0, 0.0, false, true, 0, emptyList(), NutritionInfo(), null, null)
    
    // Untuk backward compatibility dengan kode lama
    val foodName: String get() = if (name.isNotEmpty()) name else ""
    val foodDescription: String get() = if (description.isNotEmpty()) description else ""
    val foodImage: String get() = if (imageUrl.isNotEmpty()) imageUrl else ""
    val foodCategory: String get() = if (categoryId.isNotEmpty()) categoryId else ""
    val foodPrice: String get() = price.toString()
}

data class NutritionInfo(
    val calories: Long = 0,
    val protein: Long = 0,
    val carbs: Long = 0,
    val fat: Long = 0
) {
    constructor() : this(0, 0, 0, 0)
}
