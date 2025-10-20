package com.trashbin.app.data.model

import com.google.gson.annotations.SerializedName

data class CreateOrderRequest(
    @SerializedName("listing_id") val listingId: Int,
    @SerializedName("quantity") val quantity: Double,
    @SerializedName("shipping_address") val shippingAddress: String,
    @SerializedName("notes") val notes: String? = null
)
