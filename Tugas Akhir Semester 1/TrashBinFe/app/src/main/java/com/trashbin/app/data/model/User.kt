package com.trashbin.app.data.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String?,
    @SerializedName("role") val role: String,
    @SerializedName("avatar") val avatar: String?,
    @SerializedName("address") val address: String?,
    @SerializedName("lat") val lat: Double?,
    @SerializedName("lng") val lng: Double?,
    @SerializedName("points") val points: Int,
    @SerializedName("trashpay_amount") val trashpayAmount: Double = 0.0,
    @SerializedName("is_verified") val isVerified: Boolean,
    @SerializedName("rating") val rating: Double?
)