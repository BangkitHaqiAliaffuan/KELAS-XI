package com.trashbin.app.data.model

import com.google.gson.annotations.SerializedName

data class PointsHistory(
    @SerializedName("id") val id: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("points") val points: Int,
    @SerializedName("type") val type: String, // earned, redeemed
    @SerializedName("reference_type") val referenceType: String, // pickup, marketplace, reward
    @SerializedName("reference_id") val referenceId: Int,
    @SerializedName("description") val description: String,
    @SerializedName("created_at") val createdAt: String
)