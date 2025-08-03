package com.kelasxi.waveoffood.models

/**
 * Data class untuk item di dalam keranjang belanja
 */
data class CartItemModel(
    val id: String? = null,
    val foodName: String = "",
    val foodPrice: String = "",
    val foodDescription: String = "",
    val foodImage: String = "",
    val foodCategory: String? = null,
    val quantity: Int = 1
) {
    // Constructor kosong untuk kompatibilitas dengan Firebase Firestore
    constructor() : this(null, "", "", "", "", null, 1)
    
    // Constructor dari FoodItemModel
    constructor(foodItem: FoodItemModel, quantity: Int = 1) : this(
        id = foodItem.id,
        foodName = foodItem.foodName,
        foodPrice = foodItem.foodPrice,
        foodDescription = foodItem.foodDescription,
        foodImage = foodItem.foodImage,
        foodCategory = foodItem.foodCategory,
        quantity = quantity
    )
}
