package com.trashbin.app.data.model

import com.google.gson.annotations.SerializedName

data class MarketplaceListing(
    @SerializedName("id") val id: Int,
    @SerializedName("seller_id") val sellerId: Int,
    @SerializedName("category_id") val categoryId: Int,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("price_per_unit") val pricePerUnit: Double,
    @SerializedName("condition") val condition: String, // bersih, perlu_dibersih, campur
    @SerializedName("location") val location: String,
    @SerializedName("lat") val lat: Double,
    @SerializedName("lng") val lng: Double,
    @SerializedName("photos") val photos: List<String>,
    @SerializedName("views") val views: Int,
    @SerializedName("is_active") val isActive: Boolean,
    @SerializedName("seller") val seller: User,
    @SerializedName("category") val category: WasteCategory,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String
)