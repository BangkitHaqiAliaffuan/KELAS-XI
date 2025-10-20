package com.trashbin.app.data.model

import com.google.gson.annotations.SerializedName

data class MarketplaceListing(
    @SerializedName("id") val id: Int,
    @SerializedName("seller_id") val sellerId: Int,
    @SerializedName("waste_category_id") val categoryId: Int,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("quantity") val quantity: Double,
    @SerializedName("unit") val unit: String,
    @SerializedName("price_per_unit") val pricePerUnit: Double,
    @SerializedName("total_price") val totalPrice: Double,
    @SerializedName("condition") val condition: String, // clean, needs_cleaning, mixed
    @SerializedName("location") val location: String,
    @SerializedName("lat") val lat: String,
    @SerializedName("lng") val lng: String,
    @SerializedName("status") val status: String,
    @SerializedName("photos") val photos: List<String>,
    @SerializedName("views_count") val views: Int,
    @SerializedName("expires_at") val expiresAt: String?,
    @SerializedName("waste_category") val category: WasteCategory,
    @SerializedName("seller") val seller: SellerInfo,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?
)

data class SellerInfo(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("avatar") val avatar: String?,
    @SerializedName("points") val points: Int
)