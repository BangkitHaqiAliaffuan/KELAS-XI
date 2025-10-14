package com.trashbin.app.data.model

import com.google.gson.annotations.SerializedName

data class PickupRequest(
    @SerializedName("pickup_address") val address: String,
    @SerializedName("pickup_lat") val lat: Double,
    @SerializedName("pickup_lng") val lng: Double,
    @SerializedName("scheduled_date") val scheduledDate: String,
    @SerializedName("items") val items: List<PickupItemRequest>,
    @SerializedName("notes") val notes: String?
)

data class PickupItemRequest(
    @SerializedName("category_id") val categoryId: Int,
    @SerializedName("estimated_weight") val estimatedWeight: Double,
    @SerializedName("photo_url") val photoUrl: String?
)