package com.trashbin.app.data.model

import com.google.gson.annotations.SerializedName

data class Order(
    @SerializedName("id") val id: Int,
    @SerializedName("buyer_id") val buyerId: Int,
    @SerializedName("seller_id") val sellerId: Int,
    @SerializedName("listing_id") val listingId: Int,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("total_price") val totalPrice: Double,
    @SerializedName("shipping_address") val shippingAddress: String,
    @SerializedName("notes") val notes: String?,
    @SerializedName("status") val status: String, // pending, confirmed, shipped, completed, cancelled
    @SerializedName("buyer_rating") val buyerRating: Int?,
    @SerializedName("buyer_review") val buyerReview: String?,
    @SerializedName("seller_rating") val sellerRating: Int?,
    @SerializedName("seller_review") val sellerReview: String?,
    @SerializedName("buyer") val buyer: User,
    @SerializedName("seller") val seller: User,
    @SerializedName("listing") val listing: MarketplaceListing,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String
)