package com.trashbin.app.data.model

import com.google.gson.annotations.SerializedName

data class PickupResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("collector_id") val collectorId: Int?,
    @SerializedName("address") val address: String,
    @SerializedName("lat") val lat: Double,
    @SerializedName("lng") val lng: Double,
    @SerializedName("scheduled_date") val scheduledDate: String,
    @SerializedName("status") val status: String, // pending, accepted, on_the_way, arrived, completed, cancelled
    @SerializedName("actual_weight") val actualWeight: Double?,
    @SerializedName("total_price") val totalPrice: Double?,
    @SerializedName("notes") val notes: String?,
    @SerializedName("cancellation_reason") val cancellationReason: String?,
    @SerializedName("cancelled_at") val cancelledAt: String?,
    @SerializedName("accepted_at") val acceptedAt: String?,
    @SerializedName("completed_at") val completedAt: String?,
    @SerializedName("items") val items: List<PickupItemResponse>,
    @SerializedName("user") val user: User?,
    @SerializedName("collector") val collector: User?,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String
)

data class PickupItemResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("pickup_id") val pickupId: Int,
    @SerializedName("category_id") val categoryId: Int,
    @SerializedName("estimated_weight") val estimatedWeight: Double,
    @SerializedName("actual_weight") val actualWeight: Double?,
    @SerializedName("price") val price: Double,
    @SerializedName("category") val category: WasteCategory
)