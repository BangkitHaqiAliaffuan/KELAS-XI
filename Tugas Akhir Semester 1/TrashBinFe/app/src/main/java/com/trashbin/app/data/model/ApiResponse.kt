package com.trashbin.app.data.model

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    @SerializedName("success") val success: Boolean? = null, // Make success optional
    @SerializedName("message") val message: String?,
    @SerializedName("data") val data: T?
)